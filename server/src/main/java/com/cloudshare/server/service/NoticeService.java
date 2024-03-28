package com.cloudshare.server.service;

import com.cloudshare.server.dto.requset.NoticeAddOrUpdateReqDTO;
import com.cloudshare.server.dto.requset.NoticeListPageReqDTO;
import com.cloudshare.server.dto.response.PageResponse;
import com.cloudshare.server.model.Notice;

import java.util.List;


/**
 * @author novo
 * @since 2024/3/27
 */
public interface NoticeService {

    PageResponse<Notice> list(NoticeListPageReqDTO reqDTO);

    void addNotice(NoticeAddOrUpdateReqDTO reqDTO);

    void updateNotice(NoticeAddOrUpdateReqDTO reqDTO, Long noticeId);

    void deleteNotices(List<Long> ids);
}
