package com.cloud.baowang.activity.api.enums;


/**
 * 会员类型
 * activity_user_type
 */
public enum ActivityUserTypeEnum {
    /**
     * 全体会员
     */
    ALL_USER(0, "全体会员"),

    /**
     * 新注册会员
     */
    NEW_REG_USER(1, "新注册会员");

    private final Integer code;
    private final String description;

    ActivityUserTypeEnum(Integer code, String description) {
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
     * 根据代码获取枚举实例
     *
     * @param code 枚举代码
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果代码无效
     */
    public static ActivityUserTypeEnum fromCode(int code) {
        for (ActivityUserTypeEnum type : ActivityUserTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据描述获取枚举实例
     *
     * @param description 枚举描述
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果描述无效
     */
    public static ActivityUserTypeEnum fromDescription(String description) {
        for (ActivityUserTypeEnum type : ActivityUserTypeEnum.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid activity user type description: " + description);
    }
}
