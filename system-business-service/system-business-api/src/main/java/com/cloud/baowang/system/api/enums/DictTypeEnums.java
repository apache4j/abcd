package com.cloud.baowang.system.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 参数字典值类型,后台配置无需system_param
 *
 * @author aomiao
 */
@Getter
@AllArgsConstructor
public enum DictTypeEnums {
    NUMBER_TYPE(1, "数字类型"),
    STRING_TYPE(2, "字符串类型");
    private final Integer type;
    private final String desc;
}
