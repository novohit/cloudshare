package org.novo.limit.annotation;

import org.novo.limit.enums.LimitType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author novo
 * @since 2023/11/14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    String key() default "rate_limit:";

    /**
     * 限流时间窗
     *
     * @return
     */
    int time() default 60;

    /**
     * 在时间窗内的限流次数
     *
     * @return
     */
    int count() default 100;

    /**
     * 限流类型
     *
     * @return
     */
    LimitType limitType() default LimitType.DEFAULT;
}
