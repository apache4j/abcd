package com.cloud.baowang.agent.api.enums.commission;

import lombok.Getter;

@Getter
public enum CommissionTypeEnum {

    NEGATIVE("1", "负盈利佣金"),
    REBATE("2", "有效流水返点"),
    ADDING("3", "人头费")
    ;

    private final String code;
    private final String name;

    CommissionTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CommissionTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        CommissionTypeEnum[] types = CommissionTypeEnum.values();
        for (CommissionTypeEnum type : types) {
            if (code.toString().equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
