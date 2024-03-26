package com.cloudshare.server.manager.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author novo
 * @since 2024/3/26
 */
@Service
public class RedisManagerImpl implements RedisManager {

    private final int HISTORY_MAX_SIZE = 10;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisManagerImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<String> getRecentHistory(String key, int size) {
        List<String> history = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, size - 1);
        if (!CollectionUtils.isEmpty(tuples)) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                history.add(tuple.getValue());
            }
        }
        return history;
    }

    @Override
    public void saveHistory(String key, String keyword) {
        stringRedisTemplate.opsForZSet().add(key, keyword, System.currentTimeMillis());
        Long size = stringRedisTemplate.opsForZSet().zCard(key);
        if (size > HISTORY_MAX_SIZE) {
            stringRedisTemplate.opsForZSet().removeRange(key, 0, size - HISTORY_MAX_SIZE - 1);
        }
    }
}
