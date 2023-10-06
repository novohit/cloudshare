package com.cloudshare.server.auth;

/**
 * @author novo
 * @since 2023/10/6
 */
public record UserContext(
        Long id,
        String username,
        String phone,
        Integer scope
) {
}
