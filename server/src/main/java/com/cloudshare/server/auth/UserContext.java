package com.cloudshare.server.auth;

import com.cloudshare.server.enums.PlanLevel;

/**
 * @author novo
 * @since 2023/10/6
 */
public record UserContext(
        Long id,
        String username,
        String phone,
        String avatar,
        Integer scope,
        PlanLevel plan,
        Long totalQuota,
        Long usedQuota
) {
}
