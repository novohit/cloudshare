package com.cloudshare.server.file.controller;

import com.cloudshare.server.file.controller.requset.DirAddReqDTO;
import com.cloudshare.server.file.controller.requset.DirRenameReqDTO;
import com.cloudshare.server.file.controller.requset.FileListReqDTO;
import com.cloudshare.server.file.controller.requset.FileSecUploadReqDTO;
import com.cloudshare.server.file.controller.requset.FileSingleUploadReqDTO;
import com.cloudshare.server.file.controller.response.FileListVO;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.web.response.Response;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/dir/name")
    public Response<Void> renameDir(@Validated @RequestBody DirRenameReqDTO reqDTO) {
        fileService.renameDir(reqDTO);
        return Response.success();
    }

    @PostMapping("/list")
    public Response<List<FileListVO>> list(@Validated @RequestBody FileListReqDTO reqDTO) {
        List<FileListVO> response = fileService.list(reqDTO);
        return Response.success(response);
    }

    @PostMapping("/sec-upload")
    public Response<Boolean> secUpload(@Validated @RequestBody FileSecUploadReqDTO reqDTO) {
        return Response.success(fileService.secUpload(reqDTO));
    }


    @PostMapping(path = "/single-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> singleUpload(@Validated FileSingleUploadReqDTO reqDTO) {
        fileService.singleUpload(reqDTO);
        return Response.success();
    }
}
