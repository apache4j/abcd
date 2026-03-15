/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloud.baowang.common.core.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注在需要国际化的 String 类型的属性上，用于标记其需要国际化。 必须在拥有 {@link I18nClass} 注解标记的类上
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18nField {

    /**
     * <p>
     * This is an alias for {@link #code}
     * </p>
     *
     * @return String
     */
    @AliasFor("code")
    String code() default "";


    /**
     * 类型 1.字典值；2.spel;
     * {@link com.cloud.baowang.common.core.constants.I18nFieldTypeConstants}
     * @return String
     */
    @AliasFor("type")
    int type() default -1;

    /**
     * @return String
     */
    @AliasFor("value")
    String value() default "";

    /**
     * spel表达式 code拼接
     *
     */
    String condition() default "";

}
