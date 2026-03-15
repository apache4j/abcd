package com.cloud.baowang.account.api.enums.account;

import lombok.Getter;

@Getter
public enum SourceAccountTypeEnum {
//    来源账号类型 会员、代理、平台、三方支付、三方游戏"
    member("会员", "0"),
    agent("代理", "1"),
    platform("平台", "2"),
    thirdPay("三方支付", "3"),
    thirdVenue("三方游戏场馆", "4");
    
    private final String name;
    private final String code;

    SourceAccountTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

}
