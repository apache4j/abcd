package com.cloud.baowang.play.api.enums.dbDj;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbDjIsLiveEnum {

    INITIAL(1, "初盘"),
    LIVE(2, "滚盘");


    private final Integer code;
    private final String desc;

    DbDjIsLiveEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 获取枚举
    public static DbDjIsLiveEnum fromCode(Integer code) {
        for (DbDjIsLiveEnum e : values()) {
            if (Objects.equals(e.getCode(), code)) {
                return e;
            }
        }
        return null;
    }


}
