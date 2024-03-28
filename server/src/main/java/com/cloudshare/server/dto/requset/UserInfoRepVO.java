package com.cloudshare.server.dto.requset;

import com.cloudshare.server.enums.PlanLevel;
import com.cloudshare.server.enums.RoleEnum;

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
        Long usedQuota,
        RoleEnum role
) {
}
