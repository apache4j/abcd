package com.cloud.baowang.wallet.api.enums.wallet;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 *
 * 多语言 关联到 system_param 中的 recharge_type类型
 */
public enum RechargeTypeEnum {

    BANK_CARD("bank_card", "银行卡"),
    ELECTRONIC_WALLET("electronic_wallet", "电子钱包"),
    CRYPTO_CURRENCY("crypto_currency", "加密货币"),
    MANUAL_RECHARGE("manual_recharge", "人工存款"),
    ;

    private String code;
    private String name;

    RechargeTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RechargeTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        RechargeTypeEnum[] types = RechargeTypeEnum.values();
        for (RechargeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String parseName(String code) {
        if (null == code) {
            return null;
        }
        RechargeTypeEnum[] types = RechargeTypeEnum.values();
        for (RechargeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static List<RechargeTypeEnum> getList() {
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
