package com.cloud.baowang.account.api.enums;

import lombok.Getter;

@Getter
public enum AccountBusinessCoinTypeEnums {
//   财务业务类型 游戏,财务,红利,佣金，转账,管理
    GAME("0", "游戏"),
    FINANCE("1", "财务"),
    BONUS("2", "红利"),
    COMMISSION("3", "佣金"),
    TRANSFER("4", "转账"),
    MANAGE("5", "管理"),
    ;
    private final String code;
    private final String value;

    // 构造函数
    AccountBusinessCoinTypeEnums(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
