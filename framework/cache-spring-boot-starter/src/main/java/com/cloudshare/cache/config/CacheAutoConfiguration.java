package com.cloudshare.cache.config;

import com.cloudshare.cache.lock.ILock;
import com.cloudshare.cache.lock.RedisLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author novo
 * @since 2023/10/5
 */
public class CacheAutoConfiguration {

    @Bean
    public ILock redisLock(StringRedisTemplate stringRedisTemplate, @Qualifier("unlock") RedisScript<Long> unlockScript) {
        return new RedisLock(stringRedisTemplate, unlockScript);
    }

    @Bean(name = "unlock")
    public DefaultRedisScript<Long> unlockScript() {
        DefaultRedisScript<Long> unlockScript = new DefaultRedisScript<>();
        // 设置脚本返回的类型
        unlockScript.setResultType(Long.class);
        // 设置脚本位置 根目录从resource开始
        unlockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/unlock.lua")));
        return unlockScript;
    }
}
