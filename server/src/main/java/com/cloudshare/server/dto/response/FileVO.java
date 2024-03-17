package com.cloudshare.server.dto.response;

import com.cloudshare.server.enums.FileType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/10/8
 */
public record FileVO(
        Long fileId,
        String path,
        String curDirectory,
        FileType fileType,
        String fileName,
        Long size,
        String readableSize,
        String suffix,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt
) {
}
