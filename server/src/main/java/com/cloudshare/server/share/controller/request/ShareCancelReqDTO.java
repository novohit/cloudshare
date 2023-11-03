package com.cloudshare.server.share.controller.request;

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
