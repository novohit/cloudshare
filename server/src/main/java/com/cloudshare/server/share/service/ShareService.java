package com.cloudshare.server.share.service;

import com.cloudshare.server.file.controller.response.FileVO;
import com.cloudshare.server.share.controller.request.ShareCancelReqDTO;
import com.cloudshare.server.share.controller.request.ShareCheckCodeReqDTO;
import com.cloudshare.server.share.controller.request.ShareCreateReqDTO;
import com.cloudshare.server.share.controller.request.ShareListReqDTO;
import com.cloudshare.server.share.controller.response.ShareCreateRespVO;
import com.cloudshare.server.share.controller.response.ShareListRespVO;
import com.cloudshare.server.share.controller.response.ShareVO;
import com.cloudshare.server.share.controller.response.SharerRespVO;
import com.cloudshare.server.share.model.Share;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/30
 */
public interface ShareService {
    ShareCreateRespVO createShare(ShareCreateReqDTO reqDTO);

    List<ShareVO> list(ShareListReqDTO reqDTO);

    void delete(ShareCancelReqDTO reqDTO);

    String checkCode(ShareCheckCodeReqDTO reqDTO);

    Share detail(Long shareId);

    List<FileVO> access(Long fileId);

    SharerRespVO sharer(Long shareId);
}
