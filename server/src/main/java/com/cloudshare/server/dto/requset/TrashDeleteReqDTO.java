package com.cloudshare.server.dto.requset;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
public record TrashDeleteReqDTO(
        List<Long> fileIds
) {
}
