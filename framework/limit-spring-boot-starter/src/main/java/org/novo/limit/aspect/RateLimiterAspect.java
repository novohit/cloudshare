package org.novo.limit.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.novo.limit.annotation.RateLimit;
import org.novo.limit.enums.LimitType;
import org.novo.limit.exception.RateLimitException;
import org.novo.limit.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * @author novo
 * @since 2023/11/14
 */
@Aspect
public class RateLimiterAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);

    private final RedisTemplate<String, Long> redisTemplate;

    private final RedisScript<Long> redisScript;

    public RateLimiterAspect(RedisTemplate<String, Long> redisTemplate, RedisScript<Long> redisScript) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
    }

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint joinPoint, RateLimit rateLimit) {
        int time = rateLimit.time();
        int count = rateLimit.count();
        String combineKey = getCombineKey(rateLimit, joinPoint);
        Long current = redisTemplate.execute(redisScript, Collections.singletonList(combineKey), time, count);
        if (current == null || current.intValue() > count) {
            // 超过限流
            logger.info("当前接口已达到最大限流次数");
            throw new RateLimitException("访问过于频繁，请稍后访问");
        }
        logger.info("一个时间窗内请求次数：{}，当前请求次数：{}，缓存的 key 为{}", count, current, combineKey);
    }

    /**
     * 组合key
     * redis上的key
     * 基于IP:
     * 前缀:IP-方法的唯一标识
     * rate_limit:10.10.1.1-com.wyu.controller.HelloController-hello
     * <p>
     * 默认限流:
     * rate_limit:com.wyu.controller.HelloController-hello
     *
     * @param rateLimit
     * @param joinPoint
     * @return
     */
    private String getCombineKey(RateLimit rateLimit, JoinPoint joinPoint) {
        StringBuilder key = new StringBuilder(rateLimit.key());
        if (rateLimit.limitType() == LimitType.IP) {
            key.append(IpUtil.getIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()))
                    .append("-");
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        key.append(method.getDeclaringClass().getName())
                .append("-") // 类名
                .append(method.getName()); // 方法名
        logger.info(key.toString());
        return key.toString();
    }
}
