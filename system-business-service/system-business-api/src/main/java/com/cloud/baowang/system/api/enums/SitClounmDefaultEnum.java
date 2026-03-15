package com.cloud.baowang.system.api.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum SitClounmDefaultEnum {
    baseClounm("baseClounm", "baseClounm"),
    handlingFee("handlingFee", "场馆负盈利手续费"),
    validProportion("validProportion", "场馆有效流水费率"),
    percentageFee("percentagefee", "百分比手续费"),
    ProportionalHandlingFee("wayFeeFixedAmount", "单笔固定金额手续费"),
    DomainSetting("DomainSetting", "域名设置"),
    resetPassword("resetPassword", "重置密码"),
    state("resetPassword", "状态"),

            ;
    private final String code;
    private final String name;

    // 构造方法
    SitClounmDefaultEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // 获取code
    public String getCode() {
        return code;
    }

    // 获取描述
    public String getname() {
        return name;
    }

    // 根据code获取枚举
    public static SitClounmDefaultEnum fromCode(String code) {
        for (SitClounmDefaultEnum type : SitClounmDefaultEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的分类: " + code);
    }
}
