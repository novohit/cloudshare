package com.cloudshare.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author novo
 * @since 2023/10/4
 */
@SpringBootApplication
@EnableJpaAuditing
public class CloudShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudShareApplication.class, args);
    }
}
