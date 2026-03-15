package com.cloud.baowang.play.api.enums.dbPanDaSport;

/**
 * 预约状态枚举
 */
public enum DbPanDaSportPreOrderStatusEnum {

    /** 预约中 */
    PENDING(0, "预约中"),

    /** 预约成功 */
    SUCCESS(1, "预约成功"),

    /** 风控预约失败 */
    RISK_FAIL(2, "风控预约失败"),

    /** 风控取消预约注单 */
    RISK_CANCEL(3, "风控取消预约注单"),

    /** 用户手动取消预约投注 */
    USER_CANCEL(4, "用户手动取消预约投注");

    /** 枚举值 */
    private final int code;

    /** 描述 */
    private final String description;

    DbPanDaSportPreOrderStatusEnum(int code, String description) {
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
    public static DbPanDaSportPreOrderStatusEnum fromCode(int code) {
        for (DbPanDaSportPreOrderStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
