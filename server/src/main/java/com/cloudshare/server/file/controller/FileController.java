package com.cloudshare.server.file.controller;

import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.file.controller.requset.DirCreateReqDTO;
import com.cloudshare.server.file.controller.requset.FileChunkMergeReqDTO;
import com.cloudshare.server.file.controller.requset.FileChunkUploadReqDTO;
import com.cloudshare.server.file.controller.requset.FileDeleteReqDTO;
import com.cloudshare.server.file.controller.requset.FileListReqDTO;
import com.cloudshare.server.file.controller.requset.FileMoveOrCopyReqDTO;
import com.cloudshare.server.file.controller.requset.FileRenameReqDTO;
import com.cloudshare.server.file.controller.requset.FileSecUploadReqDTO;
import com.cloudshare.server.file.controller.requset.FileSingleUploadReqDTO;
import com.cloudshare.server.file.controller.response.DirTreeNode;
import com.cloudshare.server.file.controller.response.FileVO;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.web.response.Response;
import org.novo.limit.annotation.RateLimit;
import org.novo.limit.enums.LimitType;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/7
 */
@RestController
@RequestMapping("/file")
@Validated
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/dir")
    public Response<Void> createDir(@Validated @RequestBody DirCreateReqDTO reqDTO) {
        fileService.createDir(reqDTO);
        return Response.success();
    }

    @GetMapping("/dir/tree")
    public Response<List<DirTreeNode>> dirTree() {
        List<DirTreeNode> tree = fileService.dirTree();
        return Response.success(tree);
    }

    @PutMapping("/name")
    public Response<Void> rename(@Validated @RequestBody FileRenameReqDTO reqDTO) {
        fileService.rename(reqDTO);
        return Response.success();
    }

    @DeleteMapping
    public Response<Void> delete(@RequestBody FileDeleteReqDTO reqDTO) {
        fileService.delete(reqDTO);
        return Response.success();
    }

    @PostMapping("/list")
    public Response<List<FileVO>> list(@Validated @RequestBody FileListReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        List<FileVO> response = fileService.list(reqDTO, userId);
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

    /**
     * 查询已上传的分片数
     *
     * @param md5
     * @return
     */
    @GetMapping("/chunk-upload")
    public Response<List<Integer>> chunkUpload(@NotBlank @RequestParam String md5) {
        List<Integer> received = fileService.chunkUpload(md5);
        return Response.success(received);
    }

    @RateLimit(time = 10, count = 50, limitType = LimitType.IP)
    @PostMapping(path = "/chunk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Boolean> chunkUpload(@Validated FileChunkUploadReqDTO reqDTO) {
        boolean merge = fileService.chunkUpload(reqDTO);
        return Response.success(merge);
    }

    @PostMapping("/chunk-merge")
    public Response<Void> chunkMerge(@Validated @RequestBody FileChunkMergeReqDTO reqDTO) {
        fileService.chunkMerge(reqDTO);
        return Response.success();
    }

    @RateLimit(time = 5, count = 3, limitType = LimitType.IP)
    @GetMapping("/download/{fileId}")
    public void download(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        Long userId = UserContextThreadHolder.getUserId();
        fileService.download(fileId, userId, response);
    }

    @GetMapping("/preview/{fileId}")
    public void preview(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        fileService.preview(fileId, response);
    }

    @PostMapping("/move")
    public Response<Void> move(@Validated @RequestBody FileMoveOrCopyReqDTO reqDTO) {
        fileService.move(reqDTO);
        return Response.success();
    }

    @PostMapping("/copy")
    public Response<Void> copy(@Validated @RequestBody FileMoveOrCopyReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        UserContext userContext = UserContextThreadHolder.getUserContext();
        fileService.copy(reqDTO, userId, userContext);
        return Response.success();
    }
}
