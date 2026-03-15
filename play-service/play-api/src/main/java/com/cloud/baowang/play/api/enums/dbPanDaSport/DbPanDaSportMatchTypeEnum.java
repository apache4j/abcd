package com.cloud.baowang.play.api.enums.dbPanDaSport;

/**
 * 赛事类型枚举
 */
public enum DbPanDaSportMatchTypeEnum {

    /** 早盘赛事 */
    EARLY(1, "早盘赛事"),

    /** 滚球盘赛事 */
    LIVE(2, "滚球盘赛事"),

    /** 冠军盘赛事 */
    CHAMPION(3, "冠军盘赛事"),

    /** 虚拟赛事 */
    VIRTUAL(4, "虚拟赛事"),

    /** 电竞赛事 */
    ESPORTS(5, "电竞赛事");

    /** 枚举值 */
    private final int code;

    /** 描述 */
    private final String description;

    DbPanDaSportMatchTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
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
    public static DbPanDaSportMatchTypeEnum fromCode(int code) {
        for (DbPanDaSportMatchTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
