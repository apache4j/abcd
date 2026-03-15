package com.cloud.baowang.wallet.api.enums.wallet;

import java.util.Arrays;
import java.util.List;

public enum TradeRecordTypeEnum {

    BANK_CARD_RECHARGE("bank_card_recharge", "银行卡存款"),
    ELECTRONIC_WALLET_RECHARGE("electronic_wallet_recharge", "电子钱包存款"),
    CRYPTO_CURRENCY_RECHARGE("crypto_currency_recharge", "加密货币存款"),
    MANUAL_UP("manual_up", "人工加额"),
    MANUAL_DOWN("manual_down", "人工减额"),
    SUPERIOR_TRANSFER("superior_transfer", "上级转入"),
    PLATFORM_TRANSFER("platform_transfer","平台币兑换"),

    BANK_CARD_WITHDRAW("bank_card_withdraw", "银行卡取款"),
    ELECTRONIC_WALLET_WITHDRAW("electronic_wallet_withdraw", "银行卡取款"),
    CRYPTO_CURRENCY_WITHDRAW("crypto_currency_withdraw", "加密货币取款"),
    MANUAL_WITHDRAW("manual_withdraw", "人工提款"),
            ;

    private String code;
    private String name;

    TradeRecordTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TradeRecordTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        TradeRecordTypeEnum[] types = TradeRecordTypeEnum.values();
        for (TradeRecordTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<TradeRecordTypeEnum> getList() {
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
