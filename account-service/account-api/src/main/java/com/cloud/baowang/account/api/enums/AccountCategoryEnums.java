package com.cloud.baowang.account.api.enums;

import lombok.Getter;

@Getter
public enum AccountCategoryEnums {
//   0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户
    CASH("0", "现金账户",AccountTypeEnums.DEBIT),
    FREEZE("1", "冻结账户",AccountTypeEnums.DEBIT),
    PLATFROM("2", "平台币账户",AccountTypeEnums.DEBIT),
    BONUS("3", "红利账户",AccountTypeEnums.DEBIT),
    VENUE("4", "场馆账户",AccountTypeEnums.DEBIT),
    QUOTA("5", "额度账户",AccountTypeEnums.DEBIT),
    COMMISSION("6", "佣金账户",AccountTypeEnums.DEBIT),
    WINLOSS("7", "盈亏账户",AccountTypeEnums.CREDIT),
    CREDIT("8", "贷记账户",AccountTypeEnums.CREDIT),
    ;
    private final String code;
    private final String value;
    private final AccountTypeEnums accountTypeEnums;
    // 构造函数
    AccountCategoryEnums(String code, String value,AccountTypeEnums accountTypeEnums) {
        this.code = code;
        this.value = value;
        this.accountTypeEnums=accountTypeEnums;
    }

    public static AccountCategoryEnums of(String code) {
        for (AccountCategoryEnums accountTransferEnums : AccountCategoryEnums.values()) {
            if (accountTransferEnums.getCode().equals(code)) {
                return accountTransferEnums;
            }
        }
        return null; // 异常
    }

}
