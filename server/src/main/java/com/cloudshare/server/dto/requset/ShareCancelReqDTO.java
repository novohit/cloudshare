package com.cloudshare.server.dto.requset;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareCancelReqDTO(
        @NotNull List<Long> shareIds
) {
}
