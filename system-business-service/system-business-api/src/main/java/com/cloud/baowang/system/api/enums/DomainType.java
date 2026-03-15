package com.cloud.baowang.system.api.enums;

public enum DomainType {
    AGENT(1, "代理端"),
    H5(2, "H5端"),
    APP(3, "APP端"),
    BACKEND(4, "后端"),
    DOWNLOAD(5, "下载页");

    private final int code;
    private final String name;

    // 构造方法
    DomainType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    // 获取code
    public int getCode() {
        return code;
    }

    // 获取描述
    public String getname() {
        return name;
    }

    // 根据code获取枚举
    public static DomainType fromCode(int code) {
        for (DomainType type : DomainType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的域名类型: " + code);
    }


}