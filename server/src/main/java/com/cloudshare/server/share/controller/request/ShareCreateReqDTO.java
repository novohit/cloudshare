package com.cloudshare.server.share.controller.request;

import com.cloudshare.server.share.enums.VisibleType;

import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareCreateReqDTO(
        Long fileId,
        VisibleType visibleType,
        LocalDateTime expiredAt
) {
}
