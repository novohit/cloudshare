package com.cloudshare.server.common.config;

import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.core.executor.DtpExecutor;
import org.dromara.dynamictp.core.support.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author novo
 * @since 2023/11/13
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Bean("tikaExecutor")
    public DtpExecutor eagerDtpExecutor() {
        return ThreadPoolBuilder.newBuilder()
                .threadPoolName("dtpExecutor")
                .threadFactory("test-eager")
                .corePoolSize(32)
                .maximumPoolSize(64)
                .queueCapacity(2000)
                .eager()
                .buildDynamic();
    }
}
