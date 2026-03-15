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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.annotations.I18nIgnore;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18NMessageType;
import com.cloud.baowang.common.core.properties.CommonProperties;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.cache.I18nMessageCache;
import com.cloud.baowang.common.data.transfer.i18n.cache.LanguageInfoCache;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT_CODE_ARR;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT_CODE_TO_STR;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT_CURRENT_CODE_ARR;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT_LIST;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.FILE;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.FILE_LIST;
import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.SPEL;

/**
 * 利用 ResponseBodyAdvice 对返回结果进行国际化处理
 */
@Slf4j
@Order(2)
@RestControllerAdvice
@ConditionalOnProperty(value = "i18n.transfer.enable", havingValue = "true")
public class I18nResponseAdvice implements ResponseBodyAdvice<Object> {
    private static final ReflectionUtils.FieldFilter WRITEABLE_FIELDS = (field -> !(Modifier
            .isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())));
    /**
     * SpEL 解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    /**
     * 表达式缓存
     */
    private static final Map<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>();
    private final boolean useCodeAsDefaultMessage;
    private Locale fallbackLocale = null;
    @Autowired
    CommonProperties commonProperties;
    @Autowired
    private I18nMessageCache i18nMessageCache;
    @Autowired
    private LanguageInfoCache languageInfoCache;

    public I18nResponseAdvice(I18nOptions i18nOptions) {
        String fallbackLanguageTag = i18nOptions.getFallbackLanguageTag();
        if (fallbackLanguageTag != null) {
            String[] arr = fallbackLanguageTag.split("-");
            Assert.isTrue(arr.length == 2, "error fallbackLanguageTag!");
            fallbackLocale = new Locale(arr[0], arr[1]);
        }

        this.useCodeAsDefaultMessage = i18nOptions.isUseCodeAsDefaultMessage();
    }

    /**
     * 对于使用了 @I18nIgnore 之外的所有接口进行增强处理
     *
     * @param returnType    MethodParameter
     * @param converterType 消息转换器
     * @return boolean: true is support, false is ignored
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        AnnotatedElement annotatedElement = returnType.getAnnotatedElement();
        I18nIgnore i18nIgnore = AnnotationUtils.findAnnotation(annotatedElement, I18nIgnore.class);
        return i18nIgnore == null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        try {
            switchLanguage(body, null);
        } catch (Exception ex) {
            log.error("[国际化]响应体国际化处理异常:{},ex:", body, ex);
        }

        return body;
    }

    /**
     * <p>
     * 对提供了 {@link I18nClass} 注解的类进行国际化处理，递归检查所有属性。
     * </p>
     * ps: 仅处理 String 类型，且注解了 {@link I18nField} 的属性
     *
     * @param source 当前待处理的对象
     */
    public void switchLanguage(Object source, Locale locale) {
        if (source == null) {
            return;
        }
        if (ObjUtil.isNotNull(locale)) {
            LocaleContextHolder.setLocale(locale);
        }
        if (source instanceof ResponseVO<?> responseVO) {
            // message默认设置
            responseVO.setMessage(codeToMessage(responseVO.getMessage(), LocaleContextHolder.getLocale(), responseVO.getMessage(), fallbackLocale));
            switchLanguage(responseVO.getData(), null);
        } else if (source instanceof IPage<?> page) {
            switchLanguage(page.getRecords(), null);
        } else if (source instanceof List<?> list) {
            list.forEach(s -> switchLanguage(s, null));
        } else if (source instanceof Map<?, ?> map) {
            map.values().forEach(s -> switchLanguage(s, null));
        } else if (source instanceof Collection<?> collection) {
            collection.forEach(s -> switchLanguage(s, null));
        }
        Class<?> sourceClass = source.getClass();
        // 只对添加了 I18nClass 注解的类进行处理
        I18nClass i18nClass = sourceClass.getAnnotation(I18nClass.class);
        if (i18nClass == null) {
            return;
        }

        ReflectionUtils.doWithFields(sourceClass, (field) -> {
            ReflectionUtils.makeAccessible(field);
            Object fieldValue = ReflectionUtils.getField(field, source);
            if (fieldValue instanceof String || fieldValue instanceof Number) {
                // 若不存在国际化注解 直接跳过
                I18nField i18nField = field.getAnnotation(I18nField.class);
                if (i18nField == null) {
                    return;
                }
                String originVal = fieldValue.toString();
                // 获取类型
                int type = i18nField.type();
                switch (type) {
                    case DICT ->
                        // 字典值处理
                        dictProcess(source, field, i18nField, originVal, sourceClass);
                    case DICT_LIST ->
                        // 字典列表值
                        dictListProcess(source, field, originVal, sourceClass);
                    case SPEL ->
                        // spel
                        spelProcess(source, field, i18nField);
                    case DICT_CODE_ARR ->
                        //当前字段为codes的多语言数组（后台编辑使用）
                        dictCodeArrProcess(source, field, originVal, sourceClass);
                    case DICT_CURRENT_CODE_ARR ->
                        //当前字段为codes的当前语言数组（展示用）
                        dictCurrentCodeArrProcess(source, field, originVal, sourceClass);
                    case DICT_CODE_TO_STR ->
                        dictCodeToStrProcess(source, field, i18nField, originVal, sourceClass);
                    case FILE -> fileDomainProcess(source, field, originVal, sourceClass);
                    case FILE_LIST -> fileListDomainProcess(source, field, originVal, sourceClass);
                    default -> replaceI18nField(source, field, originVal);
                }
            } else {
                switchLanguage(fieldValue, null);
            }
        }, WRITEABLE_FIELDS);
    }

    private void fileListDomainProcess(Object source, Field field, String originVal, Class<?> sourceClass) {
        try {
            List<I18nMsgFrontVO> i18nList = getI18nListByKey(originVal);
            Field textField;
            try {
                textField = sourceClass.getDeclaredField(field.getName() + "List");
            } catch (NoSuchFieldException e) {
                //log.error("文件域名添加或翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
                Class<?> superclass = sourceClass.getSuperclass();
                textField = superclass.getDeclaredField(field.getName() + "List");
            }
            if (CollUtil.isNotEmpty(i18nList)) {
                i18nList.forEach(s -> s.setMessageFileUrl(concatImgDomain(s.getMessage())));
            }
            ReflectionUtils.makeAccessible(textField);
            ReflectionUtils.setField(textField, source, i18nList);
        } catch (Exception e) {
            log.error("父类 文件域名添加或翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
        }
    }

    private String concatImgDomain(String originVal) {
        String allImagUrl;
        String imgDomain = commonProperties.getFileDomain();
        if (originVal.startsWith(I18NMessageType.BUSINESS.getCode())) {
            Locale locale = LocaleContextHolder.getLocale();
            String message = codeToMessage(originVal, locale, originVal, fallbackLocale);
            allImagUrl = imgDomain + message;
        } else {
            allImagUrl = imgDomain + originVal;
        }
        return allImagUrl;
    }

    private void fileDomainProcess(Object source, Field field, String originVal, Class<?> sourceClass) {
        try {
            Field declaredField = null;
            try {
                declaredField = sourceClass.getDeclaredField(field.getName() + "FileUrl");
            } catch (NoSuchFieldException e) {
                Class<?> superclass = sourceClass.getSuperclass();
                declaredField = superclass.getDeclaredField(field.getName() + "FileUrl");
            }
            ReflectionUtils.makeAccessible(declaredField);
            ReflectionUtils.setField(declaredField, source, concatImgDomain(originVal));
        } catch (Exception e) {
            log.error("文件翻译添加域名失败，field:{},sourceClass:{}，error:", field, source, e);
        }
    }

    private void replaceI18nField(Object source, Field field, String originVal) {
        // 把当前 field 的值更新为国际化后的属性
        Locale locale = LocaleContextHolder.getLocale();
        String message = codeToMessage(originVal, locale, originVal, fallbackLocale);
        if (StrUtil.isNotBlank(message)) {
            ReflectionUtils.setField(field, source, message);
        }
    }

    private void spelProcess(Object source, Field field, I18nField i18nField) {
        try {
            String conditionExpression = i18nField.condition();
            if (StrUtil.isNotBlank(conditionExpression)) {
                Expression expression = EXPRESSION_CACHE.computeIfAbsent(conditionExpression,
                        PARSER::parseExpression);
                String messageCode = expression.getValue(source, String.class);
                // 把当前 field 的值更新为国际化后的属性
                replaceI18nField(source, field, messageCode);
            }
        } catch (Exception e) {
            log.error("spel格式 字典值翻译失败，field:{},spel:{},sourceClass:{}，error:", field, i18nField.condition(), source, e);
        }
    }

    private void dictListProcess(Object source, Field field, String originVal, Class<?> sourceClass) {
        try {
            List<I18nMsgFrontVO> i18nList = getI18nListByKey(originVal);
            Field textField;
            try {
                textField = sourceClass.getDeclaredField(field.getName() + "List");
            } catch (NoSuchFieldException e) {
                // log.warn("字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
                Class<?> superclass = sourceClass.getSuperclass();
                textField = superclass.getDeclaredField(field.getName() + "List");
            }

            ReflectionUtils.makeAccessible(textField);
            ReflectionUtils.setField(textField, source, i18nList);
        } catch (Exception e) {
            log.error("dictList 父类 字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
        }
    }

    /**
     * 处理某个字段使用的是codes的多语言字符串数组
     *
     * @param source
     * @param field       字段名
     * @param originVal   messageKeys
     * @param sourceClass
     */
    private void dictCodeArrProcess(Object source, Field field, String originVal, Class<?> sourceClass) {
        try {
            List<I18nMsgFrontVO> i18nList = getI18nListByArrKey(originVal);
            Field textField;
            try {
                textField = sourceClass.getDeclaredField(field.getName() + "FrontList");
            } catch (NoSuchFieldException e) {
                // log.warn("字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
                Class<?> superclass = sourceClass.getSuperclass();
                textField = superclass.getDeclaredField(field.getName() + "FrontList");
            }

            ReflectionUtils.makeAccessible(textField);
            ReflectionUtils.setField(textField, source, i18nList);
        } catch (Exception e) {
            log.error("dictCodeArr 父类 字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
        }
    }

    /**
     * 拼接某个
     *
     * @param source      obj
     * @param field       字段名
     * @param originVal   messageKeys
     * @param sourceClass
     */
    private void dictCodeToStrProcess(Object source, Field field, I18nField i18nField, String originVal, Class<?> sourceClass) {
        try {
            List<I18nMsgFrontVO> i18nList = getCurrentI18nListByArrKey(i18nField.value(), originVal);
            if (CollectionUtil.isNotEmpty(i18nList)) {
                //转成字符串
                String codesStr = i18nList.stream()
                        .map(I18nMsgFrontVO::getMessage)
                        .collect(Collectors.joining(CommonConstant.COMMA));
                Field textField ;
                try {
                    textField = sourceClass.getDeclaredField(field.getName() + "Text");
                }catch (NoSuchFieldException e) {
                    Class<?> superclass = sourceClass.getSuperclass();
                    textField = superclass.getDeclaredField(field.getName() + "Text");
                }
                ReflectionUtils.makeAccessible(textField);
                ReflectionUtils.setField(textField, source, codesStr);
            }

        } catch (Exception e) {
            log.error("dictCodeToStr 字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
        }
    }

    /**
     * 处理某个字段使用的是code字符串数组
     *
     * @param source      obj
     * @param field       字段名
     * @param originVal   messageKeys
     * @param sourceClass
     */
    private void dictCurrentCodeArrProcess(Object source, Field field, String originVal, Class<?> sourceClass) {
        try {
            List<I18nMsgFrontVO> i18nList = getCurrentI18nListByArrKey(originVal);
            Field textField;
            try {
                textField = sourceClass.getDeclaredField(field.getName() + "CurrentFrontList");
            } catch (NoSuchFieldException e) {
                // log.warn("字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
                Class<?> superclass = sourceClass.getSuperclass();
                textField = superclass.getDeclaredField(field.getName() + "CurrentFrontList");
            }

            ReflectionUtils.makeAccessible(textField);
            ReflectionUtils.setField(textField, source, i18nList);
        } catch (Exception e) {
            log.error("dictCurrentCodeArr 父类 字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
        }
    }

    /**
     * 获取所有当前codes对应的i18信息
     *
     * @param originVal
     * @return
     */
    public List<I18nMsgFrontVO> getI18nListByArrKey(String originVal) {
        List<I18nMsgFrontVO> result = new ArrayList<>();
        if (StrUtil.isNotBlank(originVal)) {
            for (String s : originVal.split(CommonConstant.COMMA)) {
                String i18NMessageInAdvice = I18nMessageUtil.getI18NMessageInAdvice(s);
                I18nMsgFrontVO i18nMsgFrontVO = new I18nMsgFrontVO().setMessageKey(s).setMessage(i18NMessageInAdvice);
                result.add(i18nMsgFrontVO);
            }
        }
        return result;
    }

    /**
     * 获取所有当前codes对应的i18信息
     *
     * @param value     注解value
     * @param originVal 字段codes,逗号拼接
     * @return i18列表
     */
    public List<I18nMsgFrontVO> getCurrentI18nListByArrKey(String value, String originVal) {
        List<I18nMsgFrontVO> result = new ArrayList<>();
        if (StrUtil.isNotBlank(value) && StrUtil.isNotBlank(originVal)) {
            for (String s : originVal.split(CommonConstant.COMMA)) {
                String systemParamAndTrans = I18nMessageUtil.getSystemParamAndTrans(value, s);
                I18nMsgFrontVO frontVO = new I18nMsgFrontVO();
                frontVO.setMessage(systemParamAndTrans);
                frontVO.setMessageKey(value);
                result.add(frontVO);
            }
            return result;
        }
        if (StrUtil.isNotBlank(originVal)) {
            for (String s : originVal.split(CommonConstant.COMMA)) {
                String i18NMessageInAdvice = I18nMessageUtil.getI18NMessageInAdvice(s);
                I18nMsgFrontVO i18nMsgFrontVO = new I18nMsgFrontVO().setMessageKey(s).setMessage(i18NMessageInAdvice);
                result.add(i18nMsgFrontVO);
            }
        }
        return result;
    }

    /**
     * 获取所有当前codes对应的i18信息
     *
     * @param originVal
     * @return
     */
    public List<I18nMsgFrontVO> getCurrentI18nListByArrKey(String originVal) {
        List<I18nMsgFrontVO> result = new ArrayList<>();
        if (StrUtil.isNotBlank(originVal)) {
            for (String s : originVal.split(CommonConstant.COMMA)) {
                String i18NMessageInAdvice = I18nMessageUtil.getI18NMessageInAdvice(s);
                I18nMsgFrontVO i18nMsgFrontVO = new I18nMsgFrontVO().setMessageKey(s).setMessage(i18NMessageInAdvice);
                result.add(i18nMsgFrontVO);
            }
        }
        return result;
    }

    public List<I18nMsgFrontVO> getI18nListByKey(String originVal) {
        Map<String, String> map = i18nMessageCache.getDBI18nMessageByKey(originVal);
        /*if (MapUtil.isEmpty(map)) {
            return List.of();
        }*/
        List<LanguageManagerListVO> languageManagerListVOS=languageInfoCache.getAllLanguageInfo();
        List<I18nMsgFrontVO>  i18nMsgFrontRespVOS= Lists.newArrayList();
        for(LanguageManagerListVO languageManagerListVO:languageManagerListVOS){
            I18nMsgFrontVO i18nMsgFrontVO= new I18nMsgFrontVO();
            i18nMsgFrontVO.setMessageKey(originVal);
            i18nMsgFrontVO.setIconUrl(languageManagerListVO.getIcon());
            i18nMsgFrontVO.setShowCode(languageManagerListVO.getShowCode());
            i18nMsgFrontVO.setLanguageName(languageManagerListVO.getName());
            i18nMsgFrontVO.setLanguage(languageManagerListVO.getCode());
            if(map.containsKey(languageManagerListVO.getCode())){
                i18nMsgFrontVO.setMessage(map.get(languageManagerListVO.getCode()));
            }else {
                i18nMsgFrontVO.setMessage("");
            }
            i18nMsgFrontRespVOS.add(i18nMsgFrontVO);
        }
        return i18nMsgFrontRespVOS;
    }

    private void dictProcess(Object source, Field field, I18nField i18nField, String originVal, Class<?> sourceClass) {
        try {
            String transVal = I18nMessageUtil.getSystemParamAndTrans(i18nField.value(), originVal);
            Field textField;
            try {
                textField = sourceClass.getDeclaredField(field.getName() + "Text");
            } catch (NoSuchFieldException e) {
                // log.warn("字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
                Class<?> superclass = sourceClass.getSuperclass();
                textField = superclass.getDeclaredField(field.getName() + "Text");
            }

            ReflectionUtils.makeAccessible(textField);
            ReflectionUtils.setField(textField, source, transVal);
        } catch (Exception e) {
            log.error("dict 父类 字典值翻译失败，field:{},value:{},sourceClass:{}", field, originVal, source, e);
        }
    }

    private void bindCollctionField(Object source, Field field, I18nField i18nField, String fieldValue) {
        // 国际化条件判断
        String conditionExpression = i18nField.condition();
        if (StringUtils.hasText(conditionExpression)) {
            Expression expression = EXPRESSION_CACHE.computeIfAbsent(conditionExpression, PARSER::parseExpression);
            Boolean needI18n = expression.getValue(source, Boolean.class);
            if (needI18n != null && !needI18n) {
                return;
            }
        }

        // 获取国际化标识
        String code = parseMessageCode(source, fieldValue, i18nField);
        if (!StringUtils.hasLength(code)) {
            return;
        }

        // 把当前 field 的值更新为国际化后的属性
        Locale locale = LocaleContextHolder.getLocale();
        String message = codeToMessage(code, locale, fieldValue, fallbackLocale);
        ReflectionUtils.setField(field, source, message);
    }

    private void bindField(Object source, Field field, I18nField i18nField, String fieldValue) {
        // 国际化条件判断
        String conditionExpression = i18nField.condition();
        if (StringUtils.hasText(conditionExpression)) {
            Expression expression = EXPRESSION_CACHE.computeIfAbsent(conditionExpression, PARSER::parseExpression);
            Boolean needI18n = expression.getValue(source, Boolean.class);
            if (needI18n != null && !needI18n) {
                return;
            }
        }

        // 获取国际化标识
        String code = parseMessageCode(source, fieldValue, i18nField);
        if (!StringUtils.hasLength(code)) {
            return;
        }

        // 把当前 field 的值更新为国际化后的属性
        Locale locale = LocaleContextHolder.getLocale();
        String message = codeToMessage(code, locale, fieldValue, fallbackLocale);
        ReflectionUtils.setField(field, source, message);
    }

    /**
     * 解析获取国际化code
     * <ul>
     * <li>如果 @I18nField 注解中未指定 code 的 SpEL 表达式， 则使用当前属性值作为 code。
     * <li>否则使用该表达式解析出来的 code 值。
     * </ul>
     *
     * @param source     源对象
     * @param fieldValue 属性值
     * @param i18nField  国际化注解
     * @return String 国际化 code
     */
    private String parseMessageCode(Object source, String fieldValue, I18nField i18nField) {
        // 如果没有指定 spel，则直接返回属性值
        String codeExpression = i18nField.code();
        if (!StringUtils.hasText(codeExpression)) {
            return fieldValue;
        }

        // 否则解析 spel
        Expression expression = EXPRESSION_CACHE.computeIfAbsent(codeExpression, PARSER::parseExpression);
        return expression.getValue(source, String.class);
    }

    /**
     * 转换 code 为对应的国家的语言文本
     *
     * @param code           国际化唯一标识
     * @param locale         当前地区
     * @param fallbackLocale 回退语言
     * @return 国际化 text 或者 code 本身
     */
    private String codeToMessage(String code, Locale locale, String defaultMessage, Locale fallbackLocale) {
        String message;

        try {
            message = I18nMessageUtil.getI18NMessage(code, locale);
            return message;
        } catch (NoSuchMessageException e) {
            log.warn("[codeToMessage]未找到对应的国际化配置，code: {}, local: {}", code, locale);
        }

        // 当配置了回退语言时，尝试回退
        if (fallbackLocale != null && locale != fallbackLocale) {
            try {
                message = I18nMessageUtil.getI18NMessage(code, fallbackLocale);
                return message;
            } catch (NoSuchMessageException e) {
                log.warn("[codeToMessage]期望语言和回退语言中皆未找到对应的国际化配置，code: {}, local: {}, fallbackLocale：{}", code, locale,
                        fallbackLocale);
            }
        }

        if (useCodeAsDefaultMessage) {
            return code;
        } else {
            return defaultMessage;
        }
    }

}
