package com.cloudshare.server.user.api.request;

/**
 * @author novo
 * @since 2023/10/6
 */
public record UserInfoRepDTO(
        Long id,
        String username,
        String phone
) {
}
