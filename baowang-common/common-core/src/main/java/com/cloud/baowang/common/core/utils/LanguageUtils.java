package com.cloud.baowang.common.core.utils;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.constants.CommonConstant.LANGUAGE_HEAD;

/**
 * @Author: sheldon
 * @Date: 4/2/24 4:03 下午
 */
public class LanguageUtils {
    private static final List<String> languageList = Arrays.stream(LanguageEnum.values())
            .filter(LanguageEnum::isLocalTransLateFlag)
            .map(LanguageEnum::getLang).toList();

    /**
     * 获取出当前语言
     */
    public static String getLanguage() {
        if (ObjectUtil.isNull(LocaleContextHolder.getLocale())) {
            return "";
        }
        Locale locale = LocaleContextHolder.getLocale();

        return locale.getLanguage() + "-" + locale.getCountry();
//        return "7";
    }

    public static String getLanguage(String language) {

        if (!StringUtils.hasLength(language) ) {
            return LanguageEnum.getDefaultLang();
        } else {
            List<String> langList = Arrays.stream(language.split("[,;]")).toList();
            boolean hasLang = languageList.stream().anyMatch(langList::contains);
            if (!hasLang) {
                return LanguageEnum.getDefaultLang();
            }
            List<String> matchingElements = languageList.stream()
                    .filter(langList::contains).toList();

            return matchingElements.get(0);

        }
    }

    /**
     * 从HttpServletRequest中获取语言信息
     *
     * @param request HttpServletRequest
     * @return 语言信息
     */
    public static String getLanguageFromRequest(HttpServletRequest request) {
        String language = request.getHeader(LANGUAGE_HEAD);
        if (!StringUtils.hasLength(language)) {
            return LanguageEnum.getDefaultLang();
        }
        return language;
    }
}
