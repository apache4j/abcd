package com.cloud.baowang.wallet.api.enums.wallet;


import java.util.Arrays;
import java.util.List;

public enum VipLevelEnum {

    LOW(0, "最低等级"),
    HIG(6, "最高等级")
    ;

    private Integer code;
    private String name;

    VipLevelEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static VipLevelEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        VipLevelEnum[] types = VipLevelEnum.values();
        for (VipLevelEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<VipLevelEnum> getList() {
        return Arrays.asList(values());
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
}
