package com.cloudshare.server.dto.requset;

import com.cloudshare.server.enums.VisibleType;

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
