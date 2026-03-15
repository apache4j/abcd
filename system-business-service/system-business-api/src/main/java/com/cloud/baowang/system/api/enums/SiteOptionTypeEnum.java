package com.cloud.baowang.system.api.enums;

public enum SiteOptionTypeEnum {
    DataInsert(0, "新增"),
    DataUpdate(1, "修改"),
    DataDelete(2, "删除"),
    ;
    private final Integer code;
    private final String name;

    // 构造方法
    SiteOptionTypeEnum(Integer code, String name) {
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
    public static SiteOptionTypeEnum fromCode(Integer code) {
        for (SiteOptionTypeEnum type : SiteOptionTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的操作类型类型: " + code);
    }


}