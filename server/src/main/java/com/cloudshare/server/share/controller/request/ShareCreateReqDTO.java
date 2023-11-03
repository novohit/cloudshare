package com.cloudshare.server.share.controller.request;

import com.cloudshare.server.share.enums.VisibleType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareCreateReqDTO(
        @NotNull Long fileId,
        @NotNull VisibleType visibleType,
        @NotNull LocalDateTime expiredAt
) {
}
