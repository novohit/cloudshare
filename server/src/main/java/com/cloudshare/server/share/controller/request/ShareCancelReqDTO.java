package com.cloudshare.server.share.controller.request;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareCancelReqDTO(
        List<Long> ids
) {
}
