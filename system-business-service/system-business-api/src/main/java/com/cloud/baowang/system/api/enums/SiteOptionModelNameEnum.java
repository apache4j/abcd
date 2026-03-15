package com.cloud.baowang.system.api.enums;

public enum SiteOptionModelNameEnum {
    site(0, "站点列表"),
    ;
    private final Integer code;
    private final String name;

    // 构造方法
    SiteOptionModelNameEnum(Integer code, String name) {
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
    public static SiteOptionModelNameEnum fromCode(Integer code) {
        for (SiteOptionModelNameEnum type : SiteOptionModelNameEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的操作模块类型: " + code);
    }


}