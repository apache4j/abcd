package com.cloud.baowang.common.redis.annotation;

import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String name(); // 锁前缀名称

    String unique() default Strings.EMPTY; // 锁业务区分标识，可以是SpEL表达式

    boolean fair() default false; // 是否使用公平锁 默认非公平锁

    long waitTime() default 10; // 等待获取锁的最大时间

    long leaseTime() default -1; // 锁的持有时间 默认使用看门狗续期

    TimeUnit timeUnit() default TimeUnit.SECONDS; // 时间单位
}

