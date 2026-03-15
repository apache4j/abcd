package com.cloud.baowang.wallet.api.enums.wallet;

import java.util.Arrays;
import java.util.List;

public enum WithdrawalStatusEnum {

    PENDINGFIRSTINSTANCE(1, "待一审"),
    FIRSTINSTANCEREVIEW(2, "一审审核"),
    REJECTEDATFIRSTINSTANCE(3, "一审拒绝"),
    PENDINGSECONDINSTANCE(4, "待二审"),
    SECONDREVIEW(5, "二审审核"),
    REJECTEDINTHESECONDINSTANCE(6, "二审拒绝"),
    PENDINGTHIRDTRIAL(7, "待三审"),
    THIRDREVIEW(8, "三审审核"),
    THIRDTRIALREFUSED(9, "三审拒绝"),
    PENDINGPAYMENT(10, "待出款"),
    PAYMENTSUCCESSFUL(11, "出款成功"),
    PAYMENTFAILED(12, "出款失败"),
    WITHDRAWALCANCELLATION(13, "出款取消"),
    ;

    private Integer code;
    private String name;

    WithdrawalStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WithdrawalStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        WithdrawalStatusEnum[] types = WithdrawalStatusEnum.values();
        for (WithdrawalStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<WithdrawalStatusEnum> getList() {
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
