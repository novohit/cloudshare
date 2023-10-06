package com.cloudshare.lock.lock;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 缺点
 * 不可重入
 * 不可重试
 * 超时释放
 * Redis 主从一致性
 *
 * @author novo
 * @since 2023/10/5
 */
public class RedisLock implements ILock {

    private static final String KEY_PREFIX = "lock:";

    private static final String GLOBAL_THREAD_ID_PREFIX = UUID.randomUUID().toString().replace("-", "");


    private final StringRedisTemplate stringRedisTemplate;

    private final RedisScript<Long> unlockScript;

    public RedisLock(StringRedisTemplate stringRedisTemplate, @Qualifier("unlock") RedisScript<Long> unlockScript) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.unlockScript = unlockScript;
    }


    @Override
    public boolean tryLock(String key, long timeout) {
        String threadId = "%s-%d".formatted(GLOBAL_THREAD_ID_PREFIX, Thread.currentThread().getId());
        // setnx
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + key, threadId, timeout, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 解锁校验是否为持有锁线程的原因
     * A lock -> A lock expired -> B lock -> A unlock
     * 同一个业务由于 A 业务阻塞原因锁超时被 B 拿到了锁
     * 当 A 阻塞完后再释放锁会导致 A 解除了 B 的锁
     */
    @Override
    public boolean unlock(String key) {
        String threadId = "%s-%d".formatted(GLOBAL_THREAD_ID_PREFIX, Thread.currentThread().getId());
        Long res = stringRedisTemplate.execute(unlockScript, Collections.singletonList(KEY_PREFIX + key), threadId);
        return Long.valueOf(1).equals(res);
    }
}
