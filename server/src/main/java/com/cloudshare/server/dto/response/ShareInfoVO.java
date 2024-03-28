package com.cloudshare.server.dto.response;

import com.cloudshare.server.enums.FileType;

/**
 * @author novo
 * @since 2023/10/31
 */
public record ShareInfoVO(
        Long shareId,
        String fileName,
        FileType fileType,
        Long userId,
        String username
) {
}
