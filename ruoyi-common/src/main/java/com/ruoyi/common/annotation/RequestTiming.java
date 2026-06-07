package com.ruoyi.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要记录方法执行耗时（AOP 日志），加在 Controller 或 Service 方法上。
 *
 * @author daming
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestTiming
{
    /**
     * 业务说明，会出现在日志中便于检索
     */
    String value() default "";

    /**
     * 慢调用阈值（毫秒），大于 0 且实际耗时超过该值时打 WARN，否则打 INFO
     */
    long slowMs() default -1L;
}
