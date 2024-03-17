package com.cloudshare.server.service;

import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.dto.requset.ShareCancelReqDTO;
import com.cloudshare.server.dto.requset.ShareCheckCodeReqDTO;
import com.cloudshare.server.dto.requset.ShareCreateReqDTO;
import com.cloudshare.server.dto.requset.ShareSaveReqDTO;
import com.cloudshare.server.dto.response.ShareCreateRespVO;
import com.cloudshare.server.dto.response.ShareVO;
import com.cloudshare.server.dto.response.SharerRespVO;
import com.cloudshare.server.model.Share;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/30
 */
public interface ShareService {
    ShareCreateRespVO create(ShareCreateReqDTO reqDTO);

    List<ShareVO> list();

    void cancel(ShareCancelReqDTO reqDTO);

    String checkCode(ShareCheckCodeReqDTO reqDTO);

    Share detail(Long shareId);

    List<FileVO> access(Long fileId);

    SharerRespVO sharer(Long shareId);

    void save(ShareSaveReqDTO reqDTO);

    void download(Long fileId, Long userId, HttpServletResponse response);
}
