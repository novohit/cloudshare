package com.cloudshare.server.dto.requset;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareCheckCodeReqDTO(
        Long shareId,
        String code
) {
}
