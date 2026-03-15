package com.cloud.baowang.system.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 皮肤模板枚举类型
 */
@AllArgsConstructor
@Getter
public enum SkinTemplateCodeEnums {
    SKIN_ONE("skinTemplate1", "常规皮肤"),
    SKIN_TWO("skinTemplate2", "时尚版");
    private final String skinCode;
    private final String skinName;
}
