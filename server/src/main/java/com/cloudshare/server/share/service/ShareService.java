package com.cloudshare.server.share.service;

import com.cloudshare.server.share.controller.request.ShareCancelReqDTO;
import com.cloudshare.server.share.controller.request.ShareCheckCodeReqDTO;
import com.cloudshare.server.share.controller.request.ShareCreateReqDTO;
import com.cloudshare.server.share.controller.request.ShareListReqDTO;
import com.cloudshare.server.share.controller.response.ShareCreateRespVO;
import com.cloudshare.server.share.controller.response.ShareListRespVO;

/**
 * @author novo
 * @since 2023/10/30
 */
public interface ShareService {
    ShareCreateRespVO createShare(ShareCreateReqDTO reqDTO);

    ShareListRespVO list(ShareListReqDTO reqDTO);

    void delete(ShareCancelReqDTO reqDTO);

    String checkCode(ShareCheckCodeReqDTO reqDTO);
}
