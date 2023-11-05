package com.cloudshare.cache.caffeine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author novo
 * @since 2023/11/5
 */
@Data
@ConfigurationProperties(prefix = CaffeineCacheProperties.PREFIX)
public class CaffeineCacheProperties {

    public static final String PREFIX = "cache.caffeine";

    /**
     * 缓存初始容量
     * cache.caffeine.initial-capacity
     */
    private Integer initialCapacity = 256;

    /**
     * 缓存最大容量，超过之后 默认策略 ：recently or very often（最近最少）进行缓存剔除
     * cache.caffeine.maximum-size
     */
    private Long maximumSize = 10000L;

    /**
     * 是否允许空值null作为缓存的value
     * cache.caffeine.allow-null-value
     */
    private Boolean allowNullValue = Boolean.TRUE;
}
