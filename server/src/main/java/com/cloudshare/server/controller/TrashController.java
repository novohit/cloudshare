package com.cloudshare.server.controller;

import com.cloudshare.server.dto.requset.FileDeleteReqDTO;
import com.cloudshare.server.dto.requset.TrashListReqDTO;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.service.TrashService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author novo
 * @since 2023/11/5
 */
@RestController
@RequestMapping("/trash")
public class TrashController {

    private final TrashService trashService;

    public TrashController(TrashService trashService) {
        this.trashService = trashService;
    }

    @PostMapping("/list")
    public Response<List<FileVO>> list(@Validated @RequestBody TrashListReqDTO reqDTO) {
        return Response.success(trashService.list(reqDTO));
    }

    @DeleteMapping
    public Response<Void> physicallyDelete() {
        return Response.success();
    }
}
