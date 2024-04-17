package com.cloudshare.server;

import org.dromara.dynamictp.core.spring.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author novo
 * @since 2023/10/4
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableDynamicTp
public class CloudShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudShareApplication.class, args);
    }
}
