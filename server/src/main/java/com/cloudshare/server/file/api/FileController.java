package com.cloudshare.server.file.api;

import com.cloudshare.server.file.api.requset.DirAddReqDTO;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author novo
 * @since 2023/10/7
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/dir")
    public Response<Void> addDir(@Validated @RequestBody DirAddReqDTO reqDTO) {
        fileService.addDir(reqDTO);
        return Response.success();
    }
}
