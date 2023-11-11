package com.cloudshare.server.share.controller.response;

import com.cloudshare.server.share.enums.ShareStatus;
import com.cloudshare.server.share.enums.VisibleType;

import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareVO(
        Long shareId,
        Long fileId,
        VisibleType visibleType,
        ShareStatus shareStatus,
        String url,
        String code,
        LocalDateTime expiredAt,
        String fileName,
        Long pv,
        LocalDateTime createdAt
) {
}
