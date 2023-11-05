package com.cloudshare.cache.caffeine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @author novo
 * @since 2023/11/5
 */
@EnableConfigurationProperties(CaffeineCacheProperties.class)
@Slf4j
public class CaffeineCacheAutoConfiguration {

    private final CaffeineCacheProperties properties;

    public CaffeineCacheAutoConfiguration(CaffeineCacheProperties properties) {
        this.properties = properties;
    }

    @Bean("caffeineCacheManager")
    public CacheManager cacheManager() {
        log.info("Caffeine Cache init");
        // TODO 配置多个不同过期时间的实例
        // https://mp.weixin.qq.com/s/8PRz8BxFaiW55EZLaG_4LQ
        // https://www.duidaima.com/Group/Topic/JAVA/17064
        // https://www.bilibili.com/video/BV1G3411Q74k/
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(1, TimeUnit.DAYS)
                // 初始的缓存空间大小
                .initialCapacity(properties.getInitialCapacity())
                // 缓存的最大条数
                .maximumSize(properties.getMaximumSize()));
        cacheManager.setAllowNullValues(properties.getAllowNullValue());
        return cacheManager;
    }
}
