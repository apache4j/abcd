package com.cloud.baowang.common.data.transfer.i18n.config;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.data.transfer.i18n.I18nOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * 设置默认语言
 */
public class LocaleConfig implements WebMvcConfigurer {
    @Autowired
    I18nOptions i18nOptions;

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        String fallbackLanguageTag = i18nOptions.getFallbackLanguageTag();
        if (fallbackLanguageTag != null) {
            String[] arr = fallbackLanguageTag.split(CommonConstant.CENTER_LINE);
            Assert.isTrue(arr.length == 2, "error fallbackLanguageTag!");
            resolver.setDefaultLocale(new Locale(arr[0], arr[1]));
        } else {
            resolver.setDefaultLocale(Locale.US); // 设置默认语言
        }
        return resolver;
    }

}
