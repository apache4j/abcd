package com.cloud.baowang.common.data.transfer.i18n.config;


import com.cloud.baowang.common.data.transfer.i18n.DynamicMessageSource;
import com.cloud.baowang.common.data.transfer.i18n.I18nMessageProvider;
import com.cloud.baowang.common.data.transfer.i18n.MessageSourceHierarchicalChanger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;

@AutoConfiguration(after = {CustomMessageSourceAutoConfiguration.class, MessageSourceAutoConfiguration.class})
public class I18nMessageSourceAutoConfiguration {

    @ConditionalOnBean(I18nMessageProvider.class)
    @ConditionalOnMissingBean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    @Bean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    public DynamicMessageSource messageSource(I18nMessageProvider i18nMessageProvider) {
        return new DynamicMessageSource(i18nMessageProvider);
    }

    @ConditionalOnBean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME, value = I18nMessageProvider.class)
    @Bean(name = DynamicMessageSource.DYNAMIC_MESSAGE_SOURCE_BEAN_NAME)
    public DynamicMessageSource dynamicMessageSource(I18nMessageProvider i18nMessageProvider) {
        return new DynamicMessageSource(i18nMessageProvider);
    }

    @ConditionalOnBean(name = {AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME,
            DynamicMessageSource.DYNAMIC_MESSAGE_SOURCE_BEAN_NAME})
    @Bean
    public MessageSourceHierarchicalChanger messageSourceHierarchicalChanger() {
        return new MessageSourceHierarchicalChanger();
    }

}
