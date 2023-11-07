package com.cloudshare.pay.ali.config;

import com.cloudshare.pay.ali.AliPayProperties;
import com.cloudshare.pay.ali.AliPayStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author novo
 * @since 2023/11/6
 */
@EnableConfigurationProperties(AliPayProperties.class)
@Slf4j
public class AliPayAutoConfiguration {

    private final AliPayProperties properties;

    public AliPayAutoConfiguration(AliPayProperties properties) {
        this.properties = properties;
    }

    @Bean
    public AliPayStrategy aliPayStrategy() {
        return new AliPayStrategy(properties);
    }
}
