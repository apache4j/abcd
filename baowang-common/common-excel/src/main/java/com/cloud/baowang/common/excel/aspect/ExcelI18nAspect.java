package com.cloud.baowang.common.excel.aspect;

import com.cloud.baowang.common.data.transfer.i18n.I18nResponseAdvice;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 文件导出，拦截导出的文件内容，对文件内容做国际化。对文件内容值以excel开头的进行替换
 */
@Slf4j
//@Aspect
public class ExcelI18nAspect {


    //@Autowired
    private I18nResponseAdvice i18nResponseAdvice;

    /**
     * 环绕通知.
     *
     * @param pjp pjp
     * @return obj
     * @throws Throwable exception
     */
    @Around("@annotation(com.cloud.baowang.common.excel.aspect.ExcelI18nMethod) ")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        i18nResponseAdvice.switchLanguage(result, LocaleContextHolder.getLocale());
        return result;
    }


}
