package com.cloud.baowang.activity.api.enums;


/**
 * 参与方式
 * 0 手动参与 1 自动参与
 *  system_param 中的  activity_participation_type
 */
public enum ActivityParticipationModeEnum {
    MANUAL(0, "手动参与"),

    AUTO(1, "自动参与");



    private final Integer code;
    private final String name;

    ActivityParticipationModeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ActivityParticipationModeEnum fromCode(int code) {
        for (ActivityParticipationModeEnum type : ActivityParticipationModeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static ActivityParticipationModeEnum fromName(String name) {
        for (ActivityParticipationModeEnum type : ActivityParticipationModeEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
