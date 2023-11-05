package com.cloudshare.storage.aliyun.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.cloudshare.storage.aliyun.AliyunOssStorageEngine;
import com.cloudshare.storage.core.StorageEngine;
import com.cloudshare.storage.core.constant.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

/**
 * @author novo
 * @since 2023/10/10
 */
@EnableConfigurationProperties(AliyunOssStorageProperties.class)
@Slf4j
public class AliyunOssStorageAutoConfiguration {

    private final AliyunOssStorageProperties properties;

    public AliyunOssStorageAutoConfiguration(AliyunOssStorageProperties properties) {
        this.properties = properties;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(value = "storage.engine", havingValue = Storage.ALI_YUN)
    public StorageEngine storageEngine(CacheManager cacheManager) {
        log.info("启用阿里云对象存储");
        OSS ossClient = new OSSClientBuilder()
                .build(properties.getEndpoint(),
                        properties.getAccessKeyId(),
                        properties.getAccessKeySecret());
        return new AliyunOssStorageEngine(properties, ossClient, cacheManager);
    }
}
