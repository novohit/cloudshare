package com.cloudshare.server.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author novo
 * @since 2023/11/13
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    /**
     * 核心线程数
     */
    public static final Integer CORE_POOL_SIZE = calculateCoreNum();

    /**
     * 最大线程数
     */
    public static final Integer MAX_POOL_SIZE = CORE_POOL_SIZE + (CORE_POOL_SIZE >> 1);


    @Bean(name = "baseExecutor", destroyMethod = "shutdown")
    public ExecutorService taskExecutor() {
        ThreadPoolExecutor baseExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                30000L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(4096),
                new BasicThreadFactory.Builder().namingPattern("base-thread-%d").build(),
                ((r, executor) -> log.error("The async executor pool is full!"))
        );
        return baseExecutor;
    }

    private static Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(BigDecimal.valueOf(0.2)).intValue();
    }
}
