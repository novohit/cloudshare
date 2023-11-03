package com.cloudshare.server.share.controller.response;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareCreateRespVO(
        Long shareId,
        String url,
        String code
) {
}
