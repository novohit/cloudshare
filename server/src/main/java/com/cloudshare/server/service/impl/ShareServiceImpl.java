package com.cloudshare.server.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.RandomUtil;
import com.cloudshare.common.util.SnowflakeUtil;
import com.cloudshare.common.util.TokenUtil;
import com.cloudshare.server.auth.ShareContextThreadHolder;
import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.dto.requset.FileListReqDTO;
import com.cloudshare.server.dto.requset.FileMoveOrCopyReqDTO;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.converter.FileConverter;
import com.cloudshare.server.enums.FileType;
import com.cloudshare.server.model.FileDocument;
import com.cloudshare.server.repository.FileRepository;
import com.cloudshare.server.service.FileService;
import com.cloudshare.server.link.service.ShortLinkService;
import com.cloudshare.server.dto.requset.ShareCancelReqDTO;
import com.cloudshare.server.dto.requset.ShareCheckCodeReqDTO;
import com.cloudshare.server.dto.requset.ShareCreateReqDTO;
import com.cloudshare.server.dto.requset.ShareSaveReqDTO;
import com.cloudshare.server.dto.response.ShareCreateRespVO;
import com.cloudshare.server.dto.response.ShareVO;
import com.cloudshare.server.dto.response.ShareInfoVO;
import com.cloudshare.server.converter.ShareConverter;
import com.cloudshare.server.enums.ShareStatus;
import com.cloudshare.server.enums.VisibleType;
import com.cloudshare.server.model.Share;
import com.cloudshare.server.repository.ShareRepository;
import com.cloudshare.server.service.ShareService;
import com.cloudshare.server.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/30
 */
@Service
@Slf4j
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;

    private final FileRepository fileRepository;

    private final ShareConverter shareConverter;

    private final FileService fileService;

    private final FileConverter fileConverter;

    private final ShortLinkService shortLinkService;

    @Value("${cloudshare.share.front-end.url}")
    private String shareFrontendUrl;

    public ShareServiceImpl(ShareRepository shareRepository, FileRepository fileRepository,
                            ShareConverter shareConverter, FileService fileService,
                            FileConverter fileConverter, ShortLinkService shortLinkService) {
        this.shareRepository = shareRepository;
        this.fileRepository = fileRepository;
        this.shareConverter = shareConverter;
        this.fileService = fileService;
        this.fileConverter = fileConverter;
        this.shortLinkService = shortLinkService;
    }


    @Override
    public ShareCreateRespVO create(ShareCreateReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        Long fileId = reqDTO.fileId();
        Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(fileId, userId);
        if (optional.isEmpty()) {
            throw new BadRequestException("文件不存在");
        }
        Share share = new Share();
        long shareId = SnowflakeUtil.nextId();
        share.setShareId(shareId);
        share.setUserId(userId);
        share.setFileId(fileId);
        share.setExpiredAt(reqDTO.expiredAt());
        share.setShareStatus(ShareStatus.ACTIVE);
        share.setUrl(shareFrontendUrl + shareId);
        String shortUrl = shortLinkService.create(shareId, share.getUrl(), share.getExpiredAt());
        if (VisibleType.PUBLIC.equals(reqDTO.visibleType())) {
            share.setVisibleType(VisibleType.PUBLIC);
        } else {
            share.setVisibleType(VisibleType.PRIVATE);
            String code = RandomUtil.randomString(4);
            share.setCode(code);
        }
        shareRepository.save(share);
        return new ShareCreateRespVO(share.getId(), shortUrl, share.getCode());
    }

    @Override
    public List<ShareVO> list() {
        Long userId = UserContextThreadHolder.getUserId();
        List<Share> shareList = shareRepository.findByUserId(userId);
        List<ShareVO> resp = new ArrayList<>();
        for (Share share : shareList) {
            resp.add(shareConverter.DO2VO(share, shortLinkService.getPV(share.getShortLink().getCode())));
        }
//        List<ShareVO> resp = shareConverter.DOList2VOList(shareList);
        return resp;
    }

    @Override
    public void cancel(ShareCancelReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        List<Share> shareList = shareRepository.findByShareIdInAndUserId(reqDTO.shareIds(), userId);
        List<Long> ids = shareList.stream()
                .map(Share::getId)
                .toList();
        shareRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public String checkCode(ShareCheckCodeReqDTO reqDTO) {
        Long shareId = reqDTO.shareId();
        Optional<Share> optional = shareRepository.findByShareId(shareId);
        if (optional.isEmpty()) {
            throw new BadRequestException("分享链接不存在");
        }
        Share share = optional.get();
        if (!share.getCode().equals(reqDTO.code())) {
            throw new BadRequestException("提取码错误");
        }
        return TokenUtil.generateAccessToken(shareId);
    }

    @Override
    public Share detail(Long shareId) {
        Optional<Share> optional = shareRepository.findByShareId(shareId);
        if (optional.isEmpty()) {
            throw new BadRequestException("分享链接不存在");
        }
        return optional.get();
    }

    @Override
    public List<FileVO> access(Long fileId) {
        Share share = ShareContextThreadHolder.getShare();
        Long shareUserId = share.getUserId();
        Long rootFileId = share.getFileId();
        FileDocument fileDocument = share.getFileDocument();
        List<FileVO> resp = new ArrayList<>();
        // 访问分享的根文件/目录
        if (fileId == null) {
            FileVO fileVO = fileConverter.DO2VO(fileDocument);
            resp.add(fileVO);
            return resp;
        }
        // 访问子目录 校验子目录是否在分享范围里
        boolean valid = fileService.isSubFile(rootFileId, fileId, shareUserId);
        if (!valid) {
            throw new BadRequestException("无权访问");
        }
        FileVO sub = fileService.detail(fileId, shareUserId);
        if (!FileType.DIR.equals(sub.fileType())) {
            throw new BadRequestException("目录不存在");
        }
        resp = fileService.list(new FileListReqDTO(null, sub.path(), null), shareUserId, true);
        return resp;
    }

    @Override
    public ShareInfoVO getShareInfo(Long shareId) {
        Optional<Share> optional = shareRepository.findByShareId(shareId);
        if (optional.isEmpty()) {
            throw new BadRequestException("分享不存在");
        }
        Share share = optional.get();
        FileDocument fileDocument = share.getFileDocument();
        return new ShareInfoVO(
                shareId,
                fileDocument.getName(),
                fileDocument.getType(),
                share.getUserId(),
                share.getUser().getUsername()
        );
    }

    @Override
    public void save(ShareSaveReqDTO reqDTO) {
        Long sourceId = ShareContextThreadHolder.getShareUserId();
        UserContext targetUser = UserContextThreadHolder.getUserContext();
        fileService.copy(
                new FileMoveOrCopyReqDTO(
                        reqDTO.sources(),
                        reqDTO.targetId(),
                        reqDTO.target()
                ),
                sourceId,
                targetUser);
    }

    @Override
    public void download(Long fileId, Long userId, HttpServletResponse response) {
        fileService.download(fileId, userId, response);
    }
}
