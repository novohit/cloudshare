package com.cloudshare.server;

import com.cloudshare.lock.lock.ILock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author novo
 * @since 2023/10/6
 */
@SpringBootTest
@Slf4j
public class LockTest {

    @Autowired
    private ILock lock;

    @Test
    void lockTest() {
        String key = "user-1";
        if (lock.tryLock(key, 1000)) {
            try {
                log.info("获取到锁");
            } finally {
                if (lock.unlock(key)) {
                    log.info("解锁成功");
                }
            }
        }
    }
}
