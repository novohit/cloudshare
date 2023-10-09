package com.cloudshare.server.file.service;

import com.cloudshare.server.file.controller.requset.DirAddReqDTO;
import com.cloudshare.server.file.controller.requset.FileListReqDTO;
import com.cloudshare.server.file.controller.response.FileListRepDTO;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
public interface FileService {
    void addDir(DirAddReqDTO reqDTO);

    List<FileListRepDTO> list(FileListReqDTO reqDTO);
}
