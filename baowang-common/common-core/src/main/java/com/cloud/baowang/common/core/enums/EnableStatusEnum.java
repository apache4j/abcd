package com.cloud.baowang.common.core.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 启用禁用状态
 *enable_disable_status
 *     多语言翻译采用:
 *     @I18nField(type = DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
 *     com.cloud.baowang.common.core.enums.SystemParamTypeEnum#ENABLE_DISABLE_STATUS
 */
public enum EnableStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),
    ;

    private Integer code;
    private String name;

    EnableStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static EnableStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        EnableStatusEnum[] types = EnableStatusEnum.values();
        for (EnableStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        EnableStatusEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<EnableStatusEnum> getList() {
        return Arrays.asList(values());
    }

}
