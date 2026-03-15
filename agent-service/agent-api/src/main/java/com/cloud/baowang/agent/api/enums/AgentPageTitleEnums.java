package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AgentPageTitleEnums {
    /**
     * 银行卡名称
     */
    BANK_CARD_NAME("LOOKUP_11760"),
    /**
     * 银行名称
     */
    BANK_NAME("LOOKUP_11761"),
    /**
     * 持卡人姓名
     */
    CARD_HOLDER_NAME("LOOKUP_11762"),
    /**
     * 虚拟币协议
     */
    BLOCKCHAIN_PROTOCOL("LOOKUP_11763"),
    /**
     * 虚拟币地址
     */
    BLOCKCHAIN_ADDRESS("LOOKUP_11764"),
    /**
     * 电子钱包账号
     */
    DIGITAL_WALLET_ACCOUNT("LOOKUP_11765"),
    /**
     * 电子钱包名称
     */
    DIGITAL_WALLET_NAME("LOOKUP_11766"),
    /**
     * 银行编号
     */
    BANK_CARD_CODE("LOOKUP_11767"),
    /**
     * 姓
     */
    DEPOSIT_LAST_NAME("LOOKUP_11768"),
    /**
     * 名
     */
    /*DEPOSIT_FIRST_NAME("LOOKUP_11769"),*/
    /**
     *省
     */
    DEPOSIT_PROVINCE("LOOKUP_11770"),
    /**
     * 市
     */
    DEPOSIT_CITY("LOOKUP_11771"),
    /**
     * 详细地址
     */
    DEPOSIT_DETAILED_ADDRESS("LOOKUP_11772"),
    /**
     *邮箱地址
     */
    DEPOSIT_EMAIL_ADDRESS("LOOKUP_11773"),
    /**
     * 手机号
     */
    DEPOSIT_PHONE_NUMBER("LOOKUP_11774"),
    /**
     * 账号
     */
    DEPOSIT_ACCOUNT("LOOKUP_11775"),

    /**
     * ifsc码(印度)
     */
    IFSC_CODE("LOOKUP_11776"),

    /**
     * 钱包名称
     */
    ELECTRONIC_WALLET_NAME("LOOKUP_11777")
    ;
    private final String i18Value;
}
