package org.novo.limit.enums;

/**
 * @author novo
 * @since 2023/11/14
 */
public enum LimitType {

    /**
     * 默认限流策略 针对某一个接口
     */
    DEFAULT,

    /**
     * 针对某一个ip
     */
    IP,

    ;
}
