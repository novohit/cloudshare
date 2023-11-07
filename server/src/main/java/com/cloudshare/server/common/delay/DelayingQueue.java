package com.cloudshare.server.common.delay;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author novo
 * @since 2023/11/7
 */
@Component
@Slf4j
public class DelayingQueue {

    private final StringRedisTemplate redisTemplate;

    public DelayingQueue(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean send(String queueName, DelayingMsg message) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(queueName, JSON.toJSONString(message), message.getDelayTime()));
    }

    public List<DelayingMsg> poll(String queueName) {
        Set<String> strings = redisTemplate.opsForZSet().rangeByScore(queueName, 0, System.currentTimeMillis());
        List<DelayingMsg> delayingMsgList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(strings)) {
            delayingMsgList = strings.stream()
                    .map(string -> JSON.parseObject(string, DelayingMsg.class))
                    .toList();
        }
        return delayingMsgList;
    }

    public boolean remove(String queueName, DelayingMsg message) {
        Long remove = redisTemplate.opsForZSet().remove(queueName, JSON.toJSONString(message));
        return Long.valueOf(1).equals(remove);
    }
}
