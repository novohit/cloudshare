package com.cloudshare.server.file.controller.requset;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/21
 */
public record FileDeleteReqDTO(
        List<Long> ids
) {
}
