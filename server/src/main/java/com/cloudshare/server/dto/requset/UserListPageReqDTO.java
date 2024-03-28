package com.cloudshare.server.dto.requset;

/**
 * @author novo
 * @since 2024/3/27
 */
public record UserListPageReqDTO(
        Integer page,
        Integer size
) {
}
