package com.cloud.baowang.wallet.api.enums.usercoin;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 存款取款 订单类型
 *
 * @author qiqi
 */
@Getter
public enum DepositWithdrawalOrderTypeEnum {
    DEPOSIT(1, "存款"),
    WITHDRAWAL(2, "取款"),
    ;
    private final Integer code;
    private final String name;

    DepositWithdrawalOrderTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DepositWithdrawalOrderTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        DepositWithdrawalOrderTypeEnum[] types = DepositWithdrawalOrderTypeEnum.values();
        for (DepositWithdrawalOrderTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<DepositWithdrawalOrderTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
