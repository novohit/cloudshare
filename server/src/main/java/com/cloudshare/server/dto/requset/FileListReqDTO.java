package com.cloudshare.server.dto.requset;

import com.cloudshare.server.enums.FileType;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
public record FileListReqDTO(
        Long parentId,
        String curDirectory,
        List<FileType> fileTypeList
) {
}
