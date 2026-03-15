package com.cloud.baowang.play.api.enums.dbPanDaSport;

/**
 * 盘口类型枚举
 */
public enum DbPanDaSportMarketTypeEnum {

    /** 欧盘 */
    EU("EU", "欧盘"),

    /** 香港盘 */
    HK("HK", "香港盘"),

    /** 美式盘 */
    US("US", "美式盘"),

    /** 印尼盘 */
    ID("ID", "印尼盘"),

    /** 马来盘 */
    MY("MY", "马来盘"),

    /** 英式盘 */
    GB("GB", "英式盘");

    /** 枚举值 */
    private final String code;

    /** 描述 */
    private final String description;

    DbPanDaSportMarketTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
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
    public static DbPanDaSportMarketTypeEnum fromCode(String code) {
        for (DbPanDaSportMarketTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
