package com.cloudshare.server.controller;

import com.cloudshare.server.auth.ShareContextThreadHolder;
import com.cloudshare.server.common.annotation.ShareTokenRequired;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.dto.requset.ShareCancelReqDTO;
import com.cloudshare.server.dto.requset.ShareCheckCodeReqDTO;
import com.cloudshare.server.dto.requset.ShareCreateReqDTO;
import com.cloudshare.server.dto.requset.ShareSaveReqDTO;
import com.cloudshare.server.dto.response.ShareCreateRespVO;
import com.cloudshare.server.dto.response.ShareVO;
import com.cloudshare.server.dto.response.SharerRespVO;
import com.cloudshare.server.service.ShareService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/30
 */
@RestController
@RequestMapping("/share")
@Validated
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    /**
     * 分享文件
     * TODO 支持多文件分享
     *
     * @param reqDTO
     * @return
     */
    @PostMapping
    public Response<ShareCreateRespVO> create(@Validated @RequestBody ShareCreateReqDTO reqDTO) {
        ShareCreateRespVO resp = shareService.create(reqDTO);
        return Response.success(resp);
    }


    /**
     * 我的分享列表
     *
     * @return
     */
    @GetMapping("/list")
    public Response<List<ShareVO>> list() {
        List<ShareVO> resp = shareService.list();
        return Response.success(resp);
    }

    @GetMapping("/access")
    @ShareTokenRequired
    public Response<List<FileVO>> access(@RequestParam(name = "fileId", required = false) Long fileId) {
        List<FileVO> resp = shareService.access(fileId);
        return Response.success(resp);
    }

    @GetMapping("/sharer")
    public Response<SharerRespVO> sharer(@RequestParam("shareId") Long shareId) {
        SharerRespVO resp = shareService.sharer(shareId);
        return Response.success(resp);
    }

    /**
     * 取消分享
     *
     * @param reqDTO
     * @return
     */
    @DeleteMapping
    public Response<Void> cancel(@Validated @RequestBody ShareCancelReqDTO reqDTO) {
        shareService.cancel(reqDTO);
        return Response.success();
    }

    @PostMapping("/check-code")
    public Response<String> checkCode(@Validated @RequestBody ShareCheckCodeReqDTO reqDTO) {
        String token = shareService.checkCode(reqDTO);
        return Response.success(token);
    }

    @PostMapping("/save")
    @ShareTokenRequired
    public Response<Void> save(@Validated @RequestBody ShareSaveReqDTO reqDTO) {
        shareService.save(reqDTO);
        return Response.success();
    }

    @GetMapping("/download/{fileId}")
    @ShareTokenRequired
    public void download(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        Long userId = ShareContextThreadHolder.getShareUserId();
        shareService.download(fileId, userId, response);
    }
}
