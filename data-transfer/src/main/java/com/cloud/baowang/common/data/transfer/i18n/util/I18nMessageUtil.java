package com.cloud.baowang.common.data.transfer.i18n.util;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18NMessageType;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.data.transfer.cache.SystemDictCache;
import com.cloud.baowang.common.data.transfer.i18n.I18nOptions;
import com.cloud.baowang.common.data.transfer.i18n.cache.I18nMessageCache;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.cloud.baowang.common.core.constants.CommonConstant.CENTER_LINE;


/**
 * 获取i18n message静态工具类
 */
@Slf4j
public class I18nMessageUtil {
    private static final I18nMessageCache i18nMessageCache;
    private static final MessageSource messageSource;
    private static final Locale defaultLocal;
    private static final SystemDictCache systemDictCache;
    private final static String DICT_I18N_BASE = "LOOKUP_";

    static {
        i18nMessageCache = SpringUtil.getBean(I18nMessageCache.class);
        messageSource = SpringUtil.getBean("messageSource", MessageSource.class);
        defaultLocal = SpringUtil.getBean(I18nOptions.class).getDefaultLocale();
        systemDictCache = SpringUtil.getBean(SystemDictCache.class);
    }

    /**
     * 将systemparam code值转为i18n翻译值
     *
     * @param type system_param表type字段
     * @param code system_param表code字段
     * @return
     */
    public static String getSystemParamAndTrans(String type, String code) {
        if (StrUtil.isBlank(type) || StrUtil.isBlank(code)) {
            return null;
        }
        // 获取字典值
        List<CodeValueVO> systemParamByType = systemDictCache.getSystemParamByType(type);
        // 翻译
        if (CollUtil.isNotEmpty(systemParamByType)) {
            //多状态处理
            if (code.contains(CommonConstant.COMMA)) {
                String[] split = code.split(CommonConstant.COMMA);
                List<String> list = systemParamByType.stream().filter(s -> Arrays.asList(split).contains(s.getCode()))
                        // 如果是 "LOOKUP_" 起始 则翻译，其他配置值 不翻译
                        .map(I18nMessageUtil::getValue)
                        .toList();
                return String.join(CommonConstant.COMMA, list);
            }
            return systemParamByType.stream().filter(s -> s.getCode().equals(code)).findFirst()
                    // 如果是 "LOOKUP_" 起始 则翻译，其他配置值 不翻译
                    .map(I18nMessageUtil::getValue)
                    .orElse(null);
        }
        return null;
    }

    @Nullable
    private static String getValue(CodeValueVO e) {
        return e.getValue() != null && e.getValue().startsWith(DICT_I18N_BASE) ? I18nMessageUtil.getI18NMessageInAdvice(e.getValue()) : e.getValue();
    }
    /**
     * 根据key获取对应文本,不查询配置文件
     *
     * @param messageKey key
     * @return string 文本
     */
    public static String getI18NMessageInAdvice(String messageKey) {
        if (StrUtil.isBlank(messageKey)) {
            return messageKey;
        }
        // 获取语言
        Locale currLocale = LocaleContextHolder.getLocale();
        Locale locale = ObjectUtil.isNull(currLocale) ? defaultLocal : currLocale;
        String result = Optional.ofNullable(i18nMessageCache.getDBI18nMessageByKey(messageKey))
                .map(s -> s.get(concatLanguageCode(locale)))
                .map(Object::toString).orElse(null);


        if(ObjectUtil.isEmpty(result)){
            return Optional.ofNullable(i18nMessageCache.getDBI18nMessageByKey(messageKey))
                    .map(s -> s.get(concatLanguageCode(defaultLocal)))
                    .map(Object::toString).orElse(null);
        }
        return result;
    }

    /**
     * 根据key和语言信息获取对应文本,不查询配置文件
     *
     * @param messageKey key
     * @param locale     语言信息
     * @return string 文本
     */
    public static String getI18NMessageInAdvice(String messageKey, Locale locale) {
        if (StrUtil.isBlank(messageKey)) {
            return messageKey;
        }
        return Optional.ofNullable(i18nMessageCache.getDBI18nMessageByKey(messageKey))
                .map(s -> s.get(concatLanguageCode(locale)))
                .map(Object::toString).orElse(null);
    }

    /**
     * 根据key获取对应文本
     *
     * @param messageKey key
     * @return string 文本
     */
    public static String getI18NMessage(String messageKey) {
        if (StrUtil.isBlank(messageKey)) {
            return messageKey;
        }
        // 获取语言
        Locale currLocale = LocaleContextHolder.getLocale();
        Locale locale = ObjectUtil.isNull(currLocale) ? defaultLocal : currLocale;
        String messageContent = getI18NMessage(messageKey, locale);
        return StrUtil.isBlank(messageContent) ? messageKey : messageContent;
    }

    /**
     * 根据key和语言获取对应文本
     *
     * @param messageKey key
     * @param locale     语言信息
     * @return string 文本
     */
    public static String getI18NMessage(String messageKey, Locale locale) {
        if (StrUtil.isBlank(messageKey)) {
            return messageKey;
        }
        String messageContent = null;

        try {
            // 提示类信息值优先搜索配置文件
            if (messageKey.startsWith(I18NMessageType.PROMPT.getCode())) {
                String[] split = messageKey.split(";");
                messageContent = Optional.of(messageSource.getMessage(split[0], null, locale))
                        .orElse(null);
            } else {
                messageContent = Optional.ofNullable(i18nMessageCache.getDBI18nMessageByKey(messageKey))
                        .map(s -> s.get(concatLanguageCode(locale)))
                        .map(Object::toString).orElse(null);
            }
        } catch (Exception e) {
            log.error("i18n翻译异常,messageKey:{}", messageKey,e);
        }
        return StrUtil.isBlank(messageContent) ? messageKey : messageContent;
    }

    private static @NotNull String concatLanguageCode(Locale locale) {
        return locale.getLanguage() + CENTER_LINE + locale.getCountry();
    }
}
