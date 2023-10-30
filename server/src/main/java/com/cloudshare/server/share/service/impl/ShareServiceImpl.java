package com.cloudshare.server.share.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.cloudshare.common.util.TokenUtil;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.file.model.FileDocument;
import com.cloudshare.server.file.repository.FileRepository;
import com.cloudshare.server.share.controller.request.ShareCancelReqDTO;
import com.cloudshare.server.share.controller.request.ShareCheckCodeReqDTO;
import com.cloudshare.server.share.controller.request.ShareCreateReqDTO;
import com.cloudshare.server.share.controller.request.ShareListReqDTO;
import com.cloudshare.server.share.controller.response.ShareCreateRespVO;
import com.cloudshare.server.share.controller.response.ShareListRespVO;
import com.cloudshare.server.share.controller.response.ShareVO;
import com.cloudshare.server.share.convert.ShareConverter;
import com.cloudshare.server.share.enums.ShareStatus;
import com.cloudshare.server.share.enums.VisibleType;
import com.cloudshare.server.share.model.Share;
import com.cloudshare.server.share.repository.ShareRepository;
import com.cloudshare.server.share.service.ShareService;
import com.cloudshare.web.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    public ShareServiceImpl(ShareRepository shareRepository, FileRepository fileRepository,
                            ShareConverter shareConverter) {
        this.shareRepository = shareRepository;
        this.fileRepository = fileRepository;
        this.shareConverter = shareConverter;
    }


    @Override
    public ShareCreateRespVO createShare(ShareCreateReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        Long fileId = reqDTO.fileId();
        Optional<FileDocument> optional = fileRepository.findById(fileId);
        if (optional.isEmpty()) {
            throw new BizException("文件不存在");
        }
        Share share = new Share();
        share.setUserId(userId);
        share.setFileId(fileId);
        share.setExpiredAt(reqDTO.expiredAt());
        share.setShareStatus(ShareStatus.ACTIVE);
        share.setUrl("http://localhost:8080/share/link?id=" + fileId);
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
    public ShareListRespVO list(ShareListReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        List<Share> shareList = shareRepository.findByUserId(userId);
        List<ShareVO> shareVOList = shareConverter.DOList2VOList(shareList);
        return new ShareListRespVO(shareVOList);
    }

    @Override
    public void delete(ShareCancelReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        List<Share> shareList = shareRepository.findByIdInAndUserId(reqDTO.ids(), userId);
        List<Long> ids = shareList.stream()
                .map(Share::getId)
                .toList();
        shareRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public String checkCode(ShareCheckCodeReqDTO reqDTO) {
        Optional<Share> optional = shareRepository.findById(reqDTO.shareId());
        if (optional.isEmpty()) {
            throw new BizException("分享链接不存在");
        }
        Share share = optional.get();
        if (!share.getCode().equals(reqDTO.code())) {
            throw new BizException("提取码错误");
        }
        return TokenUtil.generateAccessToken(0L);
    }
}
