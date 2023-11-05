package com.cloudshare.server;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author novo
 * @since 2023/11/5
 */
@SpringBootTest
@Slf4j
public class CacheTest {

    @Autowired
    @Qualifier("caffeineCacheManager")
    private CacheManager cacheManager;

    @Test
    void cacheTest() {
        Cache cache = cacheManager.getCache("");
        cache.put("key", 1);
        Integer value = cache.get("key", Integer.class);
        log.info("value:{}", value);
        try {
            Thread.sleep(6000L);
            log.info("value:{}", cache.get("key", Integer.class));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
