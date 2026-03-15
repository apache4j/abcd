package com.cloud.baowang.common.excel.aspect;

import java.lang.annotation.*;

/**
 * 字典方法method
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelI18nMethod {
}
