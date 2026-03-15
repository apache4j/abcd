package com.cloud.baowang.wallet.api.enums.wallet;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 */

public enum OwnerUserTypeEnum {


    USER("USER", "会员"),
    AGENT("AGENT", "代理"),
    ;

    private String code;
    private String name;

    OwnerUserTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OwnerUserTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        OwnerUserTypeEnum[] types = OwnerUserTypeEnum.values();
        for (OwnerUserTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<OwnerUserTypeEnum> getList() {
        return Arrays.asList(values());
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

}
