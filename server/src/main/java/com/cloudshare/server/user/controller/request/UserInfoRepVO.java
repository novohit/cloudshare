package com.cloudshare.server.user.controller.request;

import com.cloudshare.server.user.enums.PlanLevel;

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
        PlanLevel plan,
        Long totalQuota,
        Long usedQuota
) {
}
