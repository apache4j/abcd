package com.cloud.baowang.wallet.api.enums.wallet;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.List;

public enum TypingAmountAdjustTypeEnum {

    DEPOSIT("1","存款"),

    BET("2","投注"),

    MEDAL("3","勋章"),

    VIP_BENEFITS("4","VIP福利"),

    TASK("5","任务"),

    ACTIVITY("6","活动"),

    MANUAL("7","人工"),

    SYSTEM("8","系统"),

    REBATE("9","返水"),

    RISK_CONTROL_ADJUSTMENT("10","风控调整")


    ;

    private String code;
    private String name;


    TypingAmountAdjustTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TypingAmountAdjustTypeEnum nameOfCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        TypingAmountAdjustTypeEnum[] types = TypingAmountAdjustTypeEnum.values();
        for (TypingAmountAdjustTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<TypingAmountAdjustTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
