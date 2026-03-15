package com.cloud.baowang.wallet.api.enums;

import java.util.Arrays;
import java.util.List;

public enum UserManualUpReviewNumberEnum {
    WAIT_ONE_REVIEW(1, "待一审"),
    WAIT_TWO_REVIEW(2, "待二审"),
    ;

    private Integer code;
    private String name;

    UserManualUpReviewNumberEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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

    public static UserManualUpReviewNumberEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserManualUpReviewNumberEnum[] types = UserManualUpReviewNumberEnum.values();
        for (UserManualUpReviewNumberEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserManualUpReviewNumberEnum> getList() {
        return Arrays.asList(values());
    }
}
