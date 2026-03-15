package com.cloud.baowang.common.core.enums;

/**
 * 正则表达式
 */
public enum RegexEnum {
    NAME("^[\\u0391-\\uFFE5a-zA-Z·&\\\\s]{1,20}+$", "姓名", "长度限制1~20位,并且只能输入中英文"),
    PASSWORD("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$", "密码", "密码为6~16位的字母和数字组合"),

    PASSWORD8("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$", "密码", "密码为8~16位的字母和数字组合"),

    PHONE("^[0-9]{11,11}$", "手机号", "长度限制11位,并且仅允许输入数字"),
    PHONE_OVER_SEA("^(\\+\\d{1,3}[- ]?)?\\d{10,14}$", "手机号", "匹配国际电话号码，包括国家代码和区号"),
    PHONE_OVER_SEA_ONLY( "^\\d+$", "手机号", "匹配国际电话号码，包括国家代码和区号"),


    ACCOUNT("^(?![0-9]+$)[A-Za-z0-9]{4,11}$", "会员账号", "会员账号不能是纯数字"),
    PROXYACCOUNT("^[0-9a-zA-Z]{2,15}$", "用户名", "必须是2-15位的字母或数字"),
    BANK_ACCOUNT("^\\d{8,20}$", "银行卡号", "长度限制8~20位,并且仅允许输入数字"),
    WITHDRAW_PWD("^\\d{6,6}$", "取款密码", "取款密码为6位数字"),
    EMAIL("\\w+@\\w+(\\.\\w{2,3})*\\.\\w{2,3}", "邮箱", "邮箱格式填写错误"),
    CHARATOR32("^[a-zA-Z0-9._%+-@]{1,32}", "邮箱", "字符不能超过32位"),
    WEBCHAT("^[a-zA-Z]([-_a-zA-Z0-9]{5,19})+$", "微信号", "仅支持6-20个字母、数字、下划线或减号，以字母开头"),
    NUMBER_OR_LETTER("^[0-9a-zA-Z]{1,20}$", "手机号", "长度限制1~20位,并且必须是数字或字母"),
    QQ("[1-9][0-9]{4,14}", "QQ号", "长度限制5~15位,仅允许输入数字且不能以0开头"),
    IP("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$", "ip", "ip驗證"),

    ONLY_ZIMU("^[A-Za-z]+$", "荷官名称", "荷官名称只能输入英文"),
    ;

    private final String regex;

    private final String name;

    private final String desc;

    RegexEnum(String regex, String name, String desc) {
        this.regex = regex;
        this.name = name;
        this.desc = desc;
    }

    public String getRegex() {
        return regex;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

}
