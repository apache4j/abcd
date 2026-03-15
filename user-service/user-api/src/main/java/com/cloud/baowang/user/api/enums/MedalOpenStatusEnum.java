package com.cloud.baowang.user.api.enums;

import lombok.Getter;

/**
 * 宝箱打开状态
 *
 * 0:可打开 1:已打开 2:未获得
 */
@Getter
public enum MedalOpenStatusEnum {

    CAN_UNLOCK(0,"可打开", 1),
    HAS_UNLOCK(1,"已打开", 0),
    NOT_UNLOCK(2,"未获得", 2)
    ;

    private final int code;
    private final String name;
    //排序顺序
    private final int sortNum;

    MedalOpenStatusEnum(int code, String name, int sortNum) {
        this.code = code;
        this.name = name;
        this.sortNum = sortNum;
    }

    public static MedalOpenStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        MedalOpenStatusEnum[] types = MedalOpenStatusEnum.values();
        for (MedalOpenStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String parseName(Integer code) {
        if (null == code) {
            return null;
        }
        MedalOpenStatusEnum[] types = MedalOpenStatusEnum.values();
        for (MedalOpenStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static Integer parseSortNum(Integer code) {
        if (null == code) {
            return null;
        }
        MedalOpenStatusEnum[] types = MedalOpenStatusEnum.values();
        for (MedalOpenStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getSortNum();
            }
        }
        return null;
    }
}
