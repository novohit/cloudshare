package com.cloudshare.server.share.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.cloudshare.common.util.SnowflakeUtil;
import com.cloudshare.common.util.TokenUtil;
import com.cloudshare.server.auth.ShareContextThreadHolder;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.file.controller.requset.FileListReqDTO;
import com.cloudshare.server.file.controller.response.FileVO;
import com.cloudshare.server.file.converter.FileConverter;
import com.cloudshare.server.file.enums.FileType;
import com.cloudshare.server.file.model.FileDocument;
import com.cloudshare.server.file.repository.FileRepository;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.server.share.controller.request.ShareCancelReqDTO;
import com.cloudshare.server.share.controller.request.ShareCheckCodeReqDTO;
import com.cloudshare.server.share.controller.request.ShareCreateReqDTO;
import com.cloudshare.server.share.controller.response.ShareCreateRespVO;
import com.cloudshare.server.share.controller.response.ShareVO;
import com.cloudshare.server.share.controller.response.SharerRespVO;
import com.cloudshare.server.share.convert.ShareConverter;
import com.cloudshare.server.share.enums.ShareStatus;
import com.cloudshare.server.share.enums.VisibleType;
import com.cloudshare.server.share.model.Share;
import com.cloudshare.server.share.repository.ShareRepository;
import com.cloudshare.server.share.service.ShareService;
import com.cloudshare.web.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public ShareServiceImpl(ShareRepository shareRepository, FileRepository fileRepository,
                            ShareConverter shareConverter, FileService fileService,
                            FileConverter fileConverter) {
        this.shareRepository = shareRepository;
        this.fileRepository = fileRepository;
        this.shareConverter = shareConverter;
        this.fileService = fileService;
        this.fileConverter = fileConverter;
    }


    @Override
    public ShareCreateRespVO create(ShareCreateReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        Long fileId = reqDTO.fileId();
        Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(fileId, userId);
        if (optional.isEmpty()) {
            throw new BizException("文件不存在");
        }
        Share share = new Share();
        long shareId = SnowflakeUtil.nextId();
        share.setShareId(shareId);
        share.setUserId(userId);
        share.setFileId(fileId);
        share.setExpiredAt(reqDTO.expiredAt());
        share.setShareStatus(ShareStatus.ACTIVE);
        share.setUrl("http://127.0.0.1:5173/share/" + shareId);
        if (VisibleType.PUBLIC.equals(reqDTO.visibleType())) {
            share.setVisibleType(VisibleType.PUBLIC);
        } else {
            share.setVisibleType(VisibleType.PRIVATE);
            String code = RandomUtil.randomString(4);
            share.setCode(code);
        }
        shareRepository.save(share);
        return new ShareCreateRespVO(share.getId(), share.getUrl(), share.getCode());
    }

    @Override
    public List<ShareVO> list() {
        Long userId = UserContextThreadHolder.getUserId();
        List<Share> shareList = shareRepository.findByUserId(userId);
        List<ShareVO> resp = shareConverter.DOList2VOList(shareList);
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
            throw new BizException("分享链接不存在");
        }
        Share share = optional.get();
        if (!share.getCode().equals(reqDTO.code())) {
//            throw new BizException("提取码错误");
            return "";
        }
        return TokenUtil.generateAccessToken(shareId);
    }

    @Override
    public Share detail(Long shareId) {
        Optional<Share> optional = shareRepository.findByShareId(shareId);
        if (optional.isEmpty()) {
            throw new BizException("分享链接不存在");
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
            throw new BizException("无权访问");
        }
        FileVO sub = fileService.detail(fileId, shareUserId);
        if (!FileType.DIR.equals(sub.fileType())) {
            throw new BizException("目录不存在");
        }
        resp = fileService.list(new FileListReqDTO(null, sub.path(), null), shareUserId);
        return resp;
    }

    @Override
    public SharerRespVO sharer(Long shareId) {
        Optional<Share> optional = shareRepository.findByShareId(shareId);
        if (optional.isEmpty()) {
            throw new BizException("分享不存在");
        }
        Share share = optional.get();
        return new SharerRespVO(
                shareId,
                share.getFileDocument().getName(),
                share.getUserId(),
                share.getUser().getUsername()
        );
    }
}
