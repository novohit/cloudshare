package com.cloudshare.server.enums;

import lombok.Getter;

/**
 * @author novo
 * @since 2023/11/5
 */
@Getter
public enum PlanLevel {

    FREE(1024 * 1024 * 1024L),

    BASE(10 * 1024 * 1024 * 1024L),

    PLUS(100 * 1024 * 1024 * 1024L),

    ;

    private final Long quota;

    PlanLevel(Long quota) {
        this.quota = quota;
    }
}
