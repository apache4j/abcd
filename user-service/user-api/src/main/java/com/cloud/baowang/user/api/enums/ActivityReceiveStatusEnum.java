package com.cloud.baowang.user.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 领取状态
 * system_param 中的 activity_receive_status
 *
 */
public enum ActivityReceiveStatusEnum {

    UN_RECEIVE(0, "未领取"),
    RECEIVE(1, "已领取"),
    EXPIRED(2, "已过期"),
    ;

    private Integer code;
    private String name;

    ActivityReceiveStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ActivityReceiveStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ActivityReceiveStatusEnum[] types = ActivityReceiveStatusEnum.values();
        for (ActivityReceiveStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ActivityReceiveStatusEnum> getList() {
        return Arrays.asList(values());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
