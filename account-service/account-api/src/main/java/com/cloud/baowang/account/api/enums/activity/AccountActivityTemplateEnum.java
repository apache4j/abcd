package com.cloud.baowang.account.api.enums.activity;


import lombok.Getter;

/**
 * 解冻标记
 *
 * @author qiqi
 */
@Getter
public enum AccountActivityTemplateEnum {
    //原先vip福利的
    PROMOTION_BONUS("0", "晋级奖金"),
    WEEKLY_WATER("1","周流水礼金"),
    MONTHLY_WATER("2","月流水礼金"),
    WEEKLY_SPORTS_WATER("3","周体育流水礼金"),
    BIRTHDAY("4","生日礼金"),
    WEEKLY_RED_PACKET("5","周红包"),


    //优惠活动相关的
    FIRST_DEPOSIT("6","首存活动"),
    SECOND_DEPOSIT("7","次存活动"),
    SPECIFIED_DATE_DEPOSIT("8","指定日期存款活动"),
    GAME_LOSS("9","游戏负盈利"),
    DAILY_COMPETITION("10","每日竞赛"),
    RED_PACKET_RAIN("11","红包雨"),
    SIGN_IN("12","签到"),
    TURNTABLE_BONUS("13","转盘奖金"),
    INVITE_FRIENDS_HEAD_COUNT("14","邀请好友人头费"),
    INVITE_FRIENDS_COMMISSION("15","邀请好友佣金返利"),
    WELCOME_NEWBIE("16","欢迎新人礼金"),
    CURRENCY_CONFIRMATION("17","币种确认礼金"),
    PHONE_CONFIRMATION("18","手机号确认"),
    EMAIL_CONFIRMATION("19","邮箱确认"),
    DAILY_DEPOSIT("20","每日存款"),
    DAILY_BET("21","每日投注"),
    DAILY_PROFIT("22","每日盈利"),
    DAILY_LOSS("23","每日负盈利"),
    WEEKLY_BET("24","每周投注"),
    WEEKLY_PROFIT("25","每周盈利"),
    WEEKLY_LOSS("26","每周负盈利"),
    WEEKLY_INVITE_FRIENDS("27","周邀请好友"),
    MEDAL_BONUS("28","勋章礼金"),
    TREASURE_BOX_BONUS("29","宝箱礼金"),
    REBATE("30","返水");
    ;
    private final String code;
    private final String name;

    AccountActivityTemplateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


}
