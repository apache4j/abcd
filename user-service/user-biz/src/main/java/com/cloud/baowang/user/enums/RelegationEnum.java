package com.cloud.baowang.user.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 30/6/23 3:33 PM
 * @Version : 1.0
 */
public enum RelegationEnum {

    UPGRADE("0", "升级"),
    DOWNGRADE("1", "降级"),
    RELEGATION("2", "保级"),
    ;

    private String code;
    private String name;

    RelegationEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RelegationEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        RelegationEnum[] types = RelegationEnum.values();
        for (RelegationEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<RelegationEnum> getList() {
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
