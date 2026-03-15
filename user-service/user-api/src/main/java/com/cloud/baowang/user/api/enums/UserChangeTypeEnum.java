package com.cloud.baowang.user.api.enums;

import lombok.Getter;

@Getter
public enum UserChangeTypeEnum {

    ZHANG_HAO_STATUS(1,"账号状态"),
    FENG_KONG_STATUS(2,"风控层级"),
    HUI_YUAN_STATUS(3,"会员标签"),
    CHU_SHENG_STATUS(4,"出生日期"),
    PHONE_STATUS(5,"手机号码"),
    NAME_STATUS(6,"姓名"),
    SEX_STATUS(7,"性别"),
    EMAIL_STATUS(8,"邮箱"),
    ACCOUNT_NUMBER_STATUS(9,"账号备注"),
    VIP_RANK_STATUS(10, "VIP等级"),

    ADD_TYPING(11,"增加流水"),

    BANK_CARD_UN_BIND(12,"解绑银行卡"),

    ELECTRONIC_WALLET_UN_BIND(13,"解绑电子钱包"),

    CRYPTO_CURRENCY_UN_BIND(14,"解绑加密货币"),
    ;


    private final int code;
    private final String name;

    UserChangeTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserChangeTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserChangeTypeEnum[] types = UserChangeTypeEnum.values();
        for (UserChangeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
