package com.cloud.baowang.play.api.enums.dbDj;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbDjOrderTypeEnum {

    NORMAL(1, "普通注单"),
    NORMAL_PARLAY(2, "普通串关注单"),
    IN_GAME_PARLAY(3, "局内串关注单"),
    COMPLEX_PLAY(4, "复合玩法注单");

    private final Integer code;
    private final String desc;

    DbDjOrderTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /** 根据 code 获取枚举 */
    public static DbDjOrderTypeEnum fromCode(Integer code) {
        for (DbDjOrderTypeEnum status : DbDjOrderTypeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        return null; // 或者抛异常，根据业务需求
    }
}
