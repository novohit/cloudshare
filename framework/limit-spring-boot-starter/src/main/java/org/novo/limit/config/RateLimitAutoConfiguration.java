package org.novo.limit.config;

import org.novo.limit.aspect.RateLimiterAspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author novo
 * @since 2023/11/14
 */
public class RateLimitAutoConfiguration {

    @Bean("limitScript")
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> limitScript = new DefaultRedisScript<>();
        // 设置脚本返回的类型
        limitScript.setResultType(Long.class);
        // 设置脚本位置 根目录从resource开始
        limitScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/limit.lua")));
        return limitScript;
    }


    @Bean("limitRedisTemplate")
    public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Long> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Long.class);
        //设置string key和value序列器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    public RateLimiterAspect rateLimiterAspect(@Qualifier("limitRedisTemplate") RedisTemplate<String, Long> redisTemplate,
                                               @Qualifier("limitScript") RedisScript<Long> redisScript) {
        return new RateLimiterAspect(redisTemplate, redisScript);
    }
}
