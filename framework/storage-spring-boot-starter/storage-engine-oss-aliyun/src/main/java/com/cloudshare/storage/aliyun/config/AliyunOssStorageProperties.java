package com.cloudshare.storage.aliyun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author novo
 * @since 2023/10/10
 */
@Data
@ConfigurationProperties(prefix = AliyunOssStorageProperties.PREFIX)
public class AliyunOssStorageProperties {

    public static final String PREFIX = "storage.aliyun.oss";

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;
}
