package com.cloud.baowang.play.api.enums.dbPanDaSport;

/**
 * 注单类型枚举
 */
public enum DbPanDaSportOrderTypeEnum {

    /** 常规 */
    NORMAL(1, "常规"),

    /** 预约 */
    PRE_ORDER(2, "预约"),

    /** 合买 */
    GROUP_BUY(3, "合买");

    /** 枚举值 */
    private final int code;

    /** 描述 */
    private final String description;

    DbPanDaSportOrderTypeEnum(int code, String description) {
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
    public static DbPanDaSportOrderTypeEnum fromCode(int code) {
        for (DbPanDaSportOrderTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
