package com.cloudshare.storage.local.config;

import com.cloudshare.storage.core.StorageEngine;
import com.cloudshare.storage.local.LocalStorageEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
    @ConditionalOnMissingBean
    public StorageEngine storageEngine() {
        log.info("启动本地存储");
        return new LocalStorageEngine(localStorageEngineProperties);
    }
}
