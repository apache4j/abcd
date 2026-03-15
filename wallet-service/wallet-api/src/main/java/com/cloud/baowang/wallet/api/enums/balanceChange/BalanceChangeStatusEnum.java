package com.cloud.baowang.wallet.api.enums.balanceChange;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param balance_change_status code
 * 会员/代理 人工加减额审核记录-账变状态枚举
 */
@AllArgsConstructor
@Getter
public enum BalanceChangeStatusEnum {

    FAILED(0, "账变失败"),
    SUCCESS(1, "账变成功"),
    ;

    private final Integer status;
    private final String name;

    public static BalanceChangeStatusEnum nameOfStatus(Integer status) {
        if (null == status) {
            return null;
        }
        BalanceChangeStatusEnum[] types = BalanceChangeStatusEnum.values();
        for (BalanceChangeStatusEnum type : types) {
            if (status.equals(type.status)) {
                return type;
            }
        }
        return null;
    }

    public static List<BalanceChangeStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
