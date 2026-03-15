package com.cloud.baowang.system.api.enums;

public enum SiteChangeTypeEnum {
    Baseinfo("Baseinfo", "基础信息"),
    BaseConfig("BaseConfig", "站点配置"),
    VenueAuthor("VenueAuthor", "场馆授权"),
    RechargeAuthor("RechargeAuthor", "存款授权"),
    withdrawAuthor("withdrawAuthor", "提款授权"),
    smsAuthor("smsAuthor", "短信通道授权"),
    mailAuthor("mailAuthor", "邮箱授权"),
    customerAuthor("customerAuthor", "客服授权"),
    option("option", "操作"),
    ;
    private final String code;
    private final String name;

    // 构造方法
    SiteChangeTypeEnum(String code, String name) {
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
    public static SiteChangeTypeEnum fromCode(String code) {
        for (SiteChangeTypeEnum type : SiteChangeTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的分类: " + code);
    }
}
