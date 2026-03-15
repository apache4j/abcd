package com.cloud.baowang.wallet.api.enums.usercoin;

import java.util.Arrays;
import java.util.List;

/**
 * 使用人工确认会员充值
 */
public enum UserRechargeReviewNumberEnum {
    WAIT_ONE_REVIEW(1, "待一审"),
    WAIT_BRING_MONEY(2, "待入款"),
    ;

    private Integer code;
    private String name;

    UserRechargeReviewNumberEnum(Integer code, String name) {
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

    public static UserRechargeReviewNumberEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserRechargeReviewNumberEnum[] types = UserRechargeReviewNumberEnum.values();
        for (UserRechargeReviewNumberEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserRechargeReviewNumberEnum> getList() {
        return Arrays.asList(values());
    }
}
