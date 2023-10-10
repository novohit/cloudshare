package com.cloudshare.storage.local.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * @author novo
 * @since 2023/10/10
 */
@Data
@ConfigurationProperties(prefix = LocalStorageEngineProperties.PREFIX)
public class LocalStorageEngineProperties {

    public static final String PREFIX = "storage.local";

    private String basePath = System.getProperty("user.dir") + File.separator + "temp";
}
