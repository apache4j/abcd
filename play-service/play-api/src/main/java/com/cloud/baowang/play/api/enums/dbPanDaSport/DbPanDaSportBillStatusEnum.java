package com.cloud.baowang.play.api.enums.dbPanDaSport;

/**
 * 结算状态枚举
 */
public enum DbPanDaSportBillStatusEnum {

    /** 未结算 */
    UNSETTLED(0, "未结算"),

    /** 已结算 */
    SETTLED(1, "已结算"),

    /** 结算异常 */
    EXCEPTION(2, "结算异常");

    /** 枚举值 */
    private final int code;

    /** 描述 */
    private final String description;

    DbPanDaSportBillStatusEnum(int code, String description) {
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
    public static DbPanDaSportBillStatusEnum fromCode(int code) {
        for (DbPanDaSportBillStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
