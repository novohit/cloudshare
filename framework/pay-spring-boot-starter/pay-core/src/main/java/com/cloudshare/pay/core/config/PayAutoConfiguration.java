package com.cloudshare.pay.core.config;

import com.cloudshare.pay.core.PayStrategyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author novo
 * @since 2023/11/6
 */
public class PayAutoConfiguration {

    @Bean
    public PayStrategyFactory payStrategyFactory(ApplicationContext applicationContext) {
        return new PayStrategyFactory(applicationContext);
    }
}
