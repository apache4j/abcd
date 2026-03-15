package com.cloud.baowang.wallet.api.enums.wallet;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 *
 * 多语言 关联到 system_param 中的 witdraw_type 类型
 */
public enum WithdrawTypeEnum {

    BANK_CARD("bank_card", "银行卡"),
    ELECTRONIC_WALLET("electronic_wallet", "电子钱包"),
    CRYPTO_CURRENCY("crypto_currency", "加密货币"),
    MANUAL_WITHDRAW("manual_withdraw", "人工提款"),
    ;

    private String code;
    private String name;

    WithdrawTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WithdrawTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        WithdrawTypeEnum[] types = WithdrawTypeEnum.values();
        for (WithdrawTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<WithdrawTypeEnum> getList() {
        return Arrays.asList(values());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
