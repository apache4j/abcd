package com.cloud.baowang.system.api.enums;

public enum CustomerType {
    AIONLINE("AIONLINE", "爱挚能在线客服"),
    SaleSmartly("SaleSmartly", "SaleSmartly"),
    MEIQIA("MEIQIA", "美洽"),
   ;

    private final String code;
    private final String name;

    // 构造方法
    CustomerType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}