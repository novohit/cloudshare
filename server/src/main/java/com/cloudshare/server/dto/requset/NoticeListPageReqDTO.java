package com.cloudshare.server.dto.requset;

/**
 * @author novo
 * @since 2024/3/27
 */
public record NoticeListPageReqDTO(
        Integer page,
        Integer size,
        String title,
        String content
) {
}
