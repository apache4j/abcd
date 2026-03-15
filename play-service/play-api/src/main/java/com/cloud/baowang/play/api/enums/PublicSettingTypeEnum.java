package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum PublicSettingTypeEnum {
    ODDS("sport_odds", "体育赔率设置")
    ;
    private String code;
    private String name;

    PublicSettingTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PublicSettingTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        PublicSettingTypeEnum[] types = PublicSettingTypeEnum.values();
        for (PublicSettingTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<PublicSettingTypeEnum> getList() {
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
