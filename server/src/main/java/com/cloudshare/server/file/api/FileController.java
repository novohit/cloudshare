package com.cloudshare.server.file.api;

import com.cloudshare.server.file.api.requset.DirAddReqDTO;
import com.cloudshare.server.file.api.requset.FileListReqDTO;
import com.cloudshare.server.file.api.response.FileListRepDTO;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/list")
    public Response<List<FileListRepDTO>> list(@Validated @RequestBody FileListReqDTO reqDTO) {
        List<FileListRepDTO> response = fileService.list(reqDTO);
        return Response.success(response);
    }
}
