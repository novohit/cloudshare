package com.cloudshare.server.share.controller.response;

/**
 * @author novo
 * @since 2023/10/31
 */
public record SharerRespVO(
        Long shareId,
        String fileName,
        Long userId,
        String username
) {
}
