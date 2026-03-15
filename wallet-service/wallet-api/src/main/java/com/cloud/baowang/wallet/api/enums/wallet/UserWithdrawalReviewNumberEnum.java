package com.cloud.baowang.wallet.api.enums.wallet;

import java.util.Arrays;
import java.util.List;

/**
 * 使用人工确认会员充值
 */
public enum UserWithdrawalReviewNumberEnum {

    WAIT_WITHDRAWAL_MONEY(101, "一审通过"),
    ONE_REVIEW_REJECT(13, "一审拒绝"),
    ;

    private Integer code;
    private String name;

    UserWithdrawalReviewNumberEnum(Integer code, String name) {
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

    public static UserWithdrawalReviewNumberEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserWithdrawalReviewNumberEnum[] types = UserWithdrawalReviewNumberEnum.values();
        for (UserWithdrawalReviewNumberEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserWithdrawalReviewNumberEnum> getList() {
        return Arrays.asList(values());
    }
}
