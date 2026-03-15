package com.cloud.baowang.system.api.enums;

public enum SiteOptionStatusEnum {
    fail(0, "失败"),
    success(1, "成功"),

    ;
    private final Integer code;
    private final String name;

    // 构造方法
    SiteOptionStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    // 获取code
    public Integer getCode() {
        return code;
    }

    // 获取描述
    public String getname() {
        return name;
    }

    // 根据code获取枚举
    public static SiteOptionStatusEnum fromCode(Integer code) {
        for (SiteOptionStatusEnum type : SiteOptionStatusEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的操作状态类型: " + code);
    }


}