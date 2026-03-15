package com.cloud.baowang.play.api.enums.dbPanDaSport;

/**
 * 比赛类型枚举
 */
public enum DbPanDaSportMatchCodeEnum {

    /** 常规赛事 */
    REGULAR(1, "常规赛事"),

    /** 冠军赛事 */
    CHAMPION(2, "冠军赛事"),

    /** VR赛事 */
    VR(3, "VR赛事"),

    /** 电子赛事 */
    ELECTRONIC(4, "电子赛事");

    /** 枚举值 */
    private final int code;

    /** 描述 */
    private final String description;

    DbPanDaSportMatchCodeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     * @param code 枚举值
     * @return 枚举对象，如果找不到返回null
     */
    public static DbPanDaSportMatchCodeEnum fromCode(int code) {
        for (DbPanDaSportMatchCodeEnum matchCode : values()) {
            if (matchCode.code == code) {
                return matchCode;
            }
        }
        return null;
    }
}
