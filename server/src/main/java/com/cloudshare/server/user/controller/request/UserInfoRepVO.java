package com.cloudshare.server.user.controller.request;

/**
 * @author novo
 * @since 2023/10/6
 */
public record UserInfoRepVO(
        Long id,
        String username,
        String phone,
        String avatar,
        Long rootId,
        String rootName,
        Long totalQuota,
        Long usedQuota
) {
}
