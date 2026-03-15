package com.cloud.baowang.common.feign.annotations;

import com.cloud.baowang.common.feign.config.FeignLogConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义feign注解
 * 添加basePackages路径
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
public @interface EnableBwFeignClients {
    @AliasFor(annotation = EnableFeignClients.class, attribute = "value")
    String[] value() default {};
    @AliasFor(annotation = EnableFeignClients.class, attribute = "basePackages")
    String[] basePackages() default {"com.cloud.baowang"};
    @AliasFor(annotation = EnableFeignClients.class, attribute = "basePackageClasses")
    Class<?>[] basePackageClasses() default {};
    @AliasFor(annotation = EnableFeignClients.class, attribute = "defaultConfiguration")
    Class<?>[] defaultConfiguration() default {FeignLogConfiguration.class};
    @AliasFor(annotation = EnableFeignClients.class, attribute = "clients")
    Class<?>[] clients() default {};
}
