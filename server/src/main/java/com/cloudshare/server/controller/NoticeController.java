package com.cloudshare.server.controller;

import com.cloudshare.server.dto.requset.NoticeAddOrUpdateReqDTO;
import com.cloudshare.server.dto.requset.NoticeListPageReqDTO;
import com.cloudshare.server.dto.response.PageResponse;
import com.cloudshare.server.model.Notice;
import com.cloudshare.server.service.NoticeService;
import com.cloudshare.server.common.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author novo
 * @since 2024/3/27
 */
@RestController
@RequestMapping("/notice")
@Validated
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/list")
    public Response<PageResponse<Notice>> list(@Validated @RequestBody NoticeListPageReqDTO reqDTO) {
        return Response.success(noticeService.list(reqDTO));
    }

    @PutMapping("/{id}/update")
    public Response<Void> updateNotice(@PathVariable("id") Long noticeId, @Validated @RequestBody NoticeAddOrUpdateReqDTO reqDTO) {
        noticeService.updateNotice(reqDTO, noticeId);
        return Response.success();
    }

    @PostMapping("/add")
    public Response<Void> addNotice(@Validated @RequestBody NoticeAddOrUpdateReqDTO reqDTO) {
        noticeService.addNotice(reqDTO);
        return Response.success();
    }

    @DeleteMapping("/delete")
    public Response<Void> deleteNotices(@Validated @RequestBody List<Long> ids) {
        noticeService.deleteNotices(ids);
        return Response.success();
    }
}
