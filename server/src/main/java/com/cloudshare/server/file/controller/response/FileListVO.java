package com.cloudshare.server.file.controller.response;

import com.cloudshare.server.file.enums.FileType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

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
        String size,
        String suffix,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt
) {
}
