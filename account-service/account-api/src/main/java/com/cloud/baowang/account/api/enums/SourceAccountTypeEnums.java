package com.cloud.baowang.account.api.enums;

import lombok.Getter;

@Getter
public enum SourceAccountTypeEnums {
//    0会员、1代理、2平台、3三方支付、4三方游戏
    // 定义枚举常量
    MEMBER("0", "会员"),
    AGENT("1", "代理"),
    PLATFORM("2", "平台"),
    THIRDPAY("3", "三方通道"),
    THIRDVENUE("4", "三方游戏"),

    ;

    private final String type;
    private final String value;


    // 构造函数
    SourceAccountTypeEnums(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public static SourceAccountTypeEnums of(String type) {
        for (SourceAccountTypeEnums sourceAccountTypeEnums : SourceAccountTypeEnums.values()) {
            if (sourceAccountTypeEnums.type.equals(type) ) {
                return sourceAccountTypeEnums;
            }
        }
        return null; // 异常
    }


}
