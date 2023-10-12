package com.cloudshare.server.file.controller.response;

import com.cloudshare.server.file.enums.FileType;

/**
 * @author novo
 * @since 2023/10/8
 */
public record FileListVO(
        Long id,
        String path,
        String curDirectory,
        FileType fileType,
        String fileName,
        Long size,
        String suffix
) {
}
