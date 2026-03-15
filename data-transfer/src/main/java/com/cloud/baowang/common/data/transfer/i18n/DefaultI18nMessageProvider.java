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
package com.cloud.baowang.common.data.transfer.i18n;


import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化信息的提供者，使用者实现此接口，用于从数据库或者缓存中读取数据
 */
@Component
public class DefaultI18nMessageProvider implements I18nMessageProvider {

    @Override
    public String getI18nMessage(String code, Locale locale) {
        return I18nMessageUtil.getI18NMessageInAdvice(code, locale);
    }

}