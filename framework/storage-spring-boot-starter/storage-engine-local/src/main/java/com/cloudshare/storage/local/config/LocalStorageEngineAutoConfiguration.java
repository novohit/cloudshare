package com.cloudshare.storage.local.config;

import com.cloudshare.storage.core.StorageEngine;
import com.cloudshare.storage.core.constant.Storage;
import com.cloudshare.storage.local.LocalStorageEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

/**
 * @author novo
 * @since 2023/10/10
 */
@EnableConfigurationProperties(LocalStorageEngineProperties.class)
@Slf4j
public class LocalStorageEngineAutoConfiguration {

    private final LocalStorageEngineProperties localStorageEngineProperties;

    public LocalStorageEngineAutoConfiguration(LocalStorageEngineProperties localStorageEngineProperties) {
        this.localStorageEngineProperties = localStorageEngineProperties;
    }


    @Bean
    @ConditionalOnProperty(value = "storage.engine", havingValue = Storage.LOCAL, matchIfMissing = true)
    public StorageEngine storageEngine(CacheManager cacheManager) {
        log.info("启动本地存储");
        return new LocalStorageEngine(localStorageEngineProperties, cacheManager);
    }
}
