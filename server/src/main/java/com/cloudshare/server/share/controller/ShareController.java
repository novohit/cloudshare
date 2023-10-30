package com.cloudshare.server.share.controller;

import com.cloudshare.server.share.controller.request.ShareCancelReqDTO;
import com.cloudshare.server.share.controller.request.ShareCheckCodeReqDTO;
import com.cloudshare.server.share.controller.request.ShareCreateReqDTO;
import com.cloudshare.server.share.controller.request.ShareListReqDTO;
import com.cloudshare.server.share.controller.response.ShareCreateRespVO;
import com.cloudshare.server.share.controller.response.ShareListRespVO;
import com.cloudshare.server.share.service.ShareService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author novo
 * @since 2023/10/30
 */
@RestController
@RequestMapping("/share")
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    public Response<ShareCreateRespVO> createShare(@Validated @RequestBody ShareCreateReqDTO reqDTO) {
        ShareCreateRespVO resp = shareService.createShare(reqDTO);
        return Response.success(resp);
    }

    @GetMapping("/list")
    public Response<ShareListRespVO> list(@Validated @RequestBody ShareListReqDTO reqDTO) {
        ShareListRespVO resp = shareService.list(reqDTO);
        return Response.success(resp);
    }

    @DeleteMapping
    public Response<Void> delete(@Validated @RequestBody ShareCancelReqDTO reqDTO) {
        shareService.delete(reqDTO);
        return Response.success();
    }

    @PostMapping("/check-code")
    public Response<String> checkCode(@Validated @RequestBody ShareCheckCodeReqDTO reqDTO) {
        String token = shareService.checkCode(reqDTO);
        return Response.success(token);
    }
}