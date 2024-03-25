package com.cloudshare.server.dto.response;

/**
 * @author novo
 * @since 2024/3/25
 */
public record StatsKeyValue(
        String type,
        long value
) {
}
