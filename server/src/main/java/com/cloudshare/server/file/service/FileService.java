package com.cloudshare.server.file.service;

import com.cloudshare.server.file.api.requset.DirAddReqDTO;

/**
 * @author novo
 * @since 2023/10/8
 */
public interface FileService {
    void addDir(DirAddReqDTO reqDTO);
}
