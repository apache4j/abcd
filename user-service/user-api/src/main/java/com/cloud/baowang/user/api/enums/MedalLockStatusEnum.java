package com.cloud.baowang.user.api.enums;

import lombok.Getter;

/**
 * 解锁状态
 *
 * 0:可点亮 1:已点亮 2:未获得
 */
@Getter
public enum MedalLockStatusEnum {

    CAN_UNLOCK(0,"可点亮", 1),
    HAS_UNLOCK(1,"已点亮", 0),
    NOT_UNLOCK(2,"未获得", 2)
    ;

    private final int code;
    private final String name;
    //排序顺序
    private final int sortNum;

    MedalLockStatusEnum(int code, String name, int sortNum) {
        this.code = code;
        this.name = name;
        this.sortNum = sortNum;
    }

    public static MedalLockStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        MedalLockStatusEnum[] types = MedalLockStatusEnum.values();
        for (MedalLockStatusEnum type : types) {
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
        MedalLockStatusEnum[] types = MedalLockStatusEnum.values();
        for (MedalLockStatusEnum type : types) {
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
        MedalLockStatusEnum[] types = MedalLockStatusEnum.values();
        for (MedalLockStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getSortNum();
            }
        }
        return null;
    }
}
