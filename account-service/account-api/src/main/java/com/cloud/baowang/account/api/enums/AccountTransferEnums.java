package com.cloud.baowang.account.api.enums;

import com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum;
import lombok.Getter;

@Getter
public enum AccountTransferEnums {
    MEMBER_DEPOSIT("1", "会员存款",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_DEPOSIT),
    MEMBER_DEPOSIT_ADMIN("2", "会员存款(后台)",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_DEPOSIT_ADMIN),
    MEMBER_WITHDRAWAL("3", "会员提款冻结",SourceAccountTypeEnums.MEMBER.getType(),String.valueOf(AccountFreezeFlagEnum.EXPRESS.getCode()),AccountCoinTypeEnums.MEMBER_WITHDRAWAL),
    MEMBER_WITHDRAWAL_SUCCESS("3", "会员提款成功",SourceAccountTypeEnums.MEMBER.getType(),String.valueOf(AccountFreezeFlagEnum.UNFREEZE.getCode()),AccountCoinTypeEnums.MEMBER_WITHDRAWAL_SUCCESS),
    MEMBER_WITHDRAWAL_FAIL("4", "提款失败",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_WITHDRAWAL_FAIL),
    MEMBER_WITHDRAWAL_ADMIN("5", "会员提款(后台)",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_WITHDRAWAL_ADMIN),

    //vip福利
    MEMBER_VIP_BENEFITS("6", "VIP福利",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_VIP_BENEFITS),
    PROMOTION_BONUS("6", "VIP福利-晋级奖金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.PROMOTION_BONUS.getCode(),AccountCoinTypeEnums.MEMBER_PROMOTION_BONUS),
    WEEKLY_WATER("6", "VIP福利-周流水礼金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WEEKLY_WATER.getCode(),AccountCoinTypeEnums.MEMBER_WEEKLY_WATER),
    MONTHLY_WATER("6", "VIP福利-月流水礼金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.MONTHLY_WATER.getCode(),AccountCoinTypeEnums.MEMBER_MONTHLY_WATER),
    WEEKLY_SPORTS_WATER("6", "VIP福利-周体育流水礼金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WEEKLY_SPORTS_WATER.getCode(),AccountCoinTypeEnums.MEMBER_WEEKLY_SPORTS_WATER),
    BIRTHDAY("6", "VIP福利-生日礼金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.BIRTHDAY.getCode(),AccountCoinTypeEnums.MEMBER_BIRTHDAY),
    WEEKLY_RED_PACKET("6", "VIP福利-周红包",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WEEKLY_RED_PACKET.getCode(),AccountCoinTypeEnums.MEMBER_WEEKLY_RED_PACKET),

    MEMBER_VIP_BENEFITS_ADD("7", "VIP福利增加调整",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_VIP_BENEFITS_ADD),
    MEMBER_VIP_BENEFITS_SUBTRACT("8", "VIP福利扣除调整",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_VIP_BENEFITS_SUBTRACT),

    PROMOTIONS("9", "活动优惠",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.PROMOTIONS),
    MEMBER_PROMOTIONS_FIRST_DEPOSIT("9", "活动优惠-首存活动",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.FIRST_DEPOSIT.getCode(),AccountCoinTypeEnums.MEMBER_FIRST_DEPOSIT),
    MEMBER_PROMOTIONS_SECOND_DEPOSIT("9","活动优惠-次存活动",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.SECOND_DEPOSIT.getCode(),AccountCoinTypeEnums.MEMBER_SECOND_DEPOSIT),
    MEMBER_PROMOTIONS_SPECIFIED_DATE_DEPOSIT("9","活动优惠-指定日期存款活动",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.SPECIFIED_DATE_DEPOSIT.getCode(),AccountCoinTypeEnums.MEMBER_SPECIFIED_DATE_DEPOSIT),
    MEMBER_PROMOTIONS_GAME_LOSS("9","活动优惠-游戏负盈利",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.GAME_LOSS.getCode(),AccountCoinTypeEnums.MEMBER_GAME_LOSS),
    MEMBER_PROMOTIONS_DAILY_COMPETITION("9","活动优惠-每日竞赛",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.DAILY_COMPETITION.getCode(),AccountCoinTypeEnums.MEMBER_DAILY_COMPETITION),
    MEMBER_PROMOTIONS_RED_PACKET_RAIN("9","活动优惠-红包雨",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.RED_PACKET_RAIN.getCode(),AccountCoinTypeEnums.MEMBER_RED_PACKET_RAIN),
    MEMBER_PROMOTIONS_SIGN_IN("9","活动优惠-签到",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.SIGN_IN.getCode(),AccountCoinTypeEnums.MEMBER_SIGN_IN),
    MEMBER_PROMOTIONS_TURNTABLE_BONUS("9","活动优惠-转盘奖金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.TURNTABLE_BONUS.getCode(),AccountCoinTypeEnums.MEMBER_TURNTABLE_BONUS),
    MEMBER_PROMOTIONS_INVITE_FRIENDS_HEAD_COUNT("9","活动优惠-邀请好友人头费",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.INVITE_FRIENDS_HEAD_COUNT.getCode(),AccountCoinTypeEnums.MEMBER_INVITE_FRIENDS_HEAD_COUNT),
    MEMBER_PROMOTIONS_INVITE_FRIENDS_COMMISSION("9","活动优惠-邀请好友佣金返利",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.INVITE_FRIENDS_COMMISSION.getCode(),AccountCoinTypeEnums.MEMBER_INVITE_FRIENDS_COMMISSION),
    MEMBER_PROMOTIONS_WELCOME_NEWBIE("9","活动优惠-欢迎新人礼金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WELCOME_NEWBIE.getCode(),AccountCoinTypeEnums.MEMBER_WELCOME_NEWBIE),
    MEMBER_PROMOTIONS_CURRENCY_CONFIRMATION("9","活动优惠-币种确认礼金",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.CURRENCY_CONFIRMATION.getCode(),AccountCoinTypeEnums.MEMBER_CURRENCY_CONFIRMATION),
    MEMBER_PROMOTIONS_PHONE_CONFIRMATION("9","活动优惠-手机号确认",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.PHONE_CONFIRMATION.getCode(),AccountCoinTypeEnums.MEMBER_PHONE_CONFIRMATION),
    MEMBER_PROMOTIONS_EMAIL_CONFIRMATION("9","活动优惠-邮箱确认",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.EMAIL_CONFIRMATION.getCode(),AccountCoinTypeEnums.MEMBER_EMAIL_CONFIRMATION),
    MEMBER_PROMOTIONS_DAILY_DEPOSIT("9","活动优惠-每日存款",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.DAILY_DEPOSIT.getCode(),AccountCoinTypeEnums.MEMBER_DAILY_DEPOSIT),
    MEMBER_PROMOTIONS_DAILY_BET("9","活动优惠-每日投注",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.DAILY_BET.getCode(),AccountCoinTypeEnums.MEMBER_DAILY_BET),
    MEMBER_PROMOTIONS_DAILY_PROFIT("9","活动优惠-每日盈利",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.DAILY_PROFIT.getCode(),AccountCoinTypeEnums.MEMBER_DAILY_PROFIT),
    MEMBER_PROMOTIONS_DAILY_LOSS("9","活动优惠-每日负盈利",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.DAILY_LOSS.getCode(),AccountCoinTypeEnums.MEMBER_DAILY_LOSS),
    MEMBER_PROMOTIONS_WEEKLY_BET("9","活动优惠-每周投注",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WEEKLY_BET.getCode(),AccountCoinTypeEnums.MEMBER_WEEKLY_BET),
    MEMBER_PROMOTIONS_WEEKLY_PROFIT("9","活动优惠-每周盈利",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WEEKLY_PROFIT.getCode(),AccountCoinTypeEnums.MEMBER_WEEKLY_PROFIT),
    MEMBER_PROMOTIONS_WEEKLY_LOSS("9","活动优惠-每周负盈利",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WEEKLY_LOSS.getCode(),AccountCoinTypeEnums.MEMBER_WEEKLY_LOSS),
    MEMBER_PROMOTIONS_WEEKLY_INVITE_FRIENDS("9","活动优惠-周邀请好友",SourceAccountTypeEnums.MEMBER.getType(), AccountActivityTemplateEnum.WEEKLY_INVITE_FRIENDS.getCode(),AccountCoinTypeEnums.MEMBER_WEEKLY_INVITE_FRIENDS),

    PROMOTIONS_ADD("10", "活动优惠增加金额",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.PROMOTIONS_ADD),
    PROMOTIONS_SUBTRACT("11", "活动优惠扣除金额",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.PROMOTIONS_SUBTRACT),

    MEMBER_PLATFORM_CONVERSION("17", "平台币兑换",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.MEMBER_PLATFORM_CONVERSION),
    TRANSFER_FROM_SUPERIOR("18", "代理代存",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.TRANSFER_FROM_SUPERIOR),
    OTHER_ADD("19", "其他增加调整",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.OTHER_ADD),
    OTHER_SUBTRACT("20", "其他扣除调整",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.OTHER_SUBTRACT),

    REBATE("22", "返水",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.REBATE),
    REBATE_ADD("23", "返水增加金额",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.REBATE_ADD),
    REBATE_SUBTRACT("24","返水扣除金额",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.REBATE_SUBTRACT),
    RISK_CONTROL_ADJUSTMENT_ADD("25", "风控调整增加金额",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.RISK_CONTROL_ADJUSTMENT_ADD),
    RISK_CONTROL_ADJUSTMENT_SUBTRACT("26", "风控调整扣除金额",SourceAccountTypeEnums.MEMBER.getType(),AccountCoinTypeEnums.RISK_CONTROL_ADJUSTMENT_SUBTRACT),




    //代理相关
    //其他调整额度
    AGENT_OTHERS_ADD_ADJUSTMENTS_QUOTA("1","其他增加调整",SourceAccountTypeEnums.AGENT.getType(),AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode(),AccountCoinTypeEnums.AGENT_OTHERS_ADD_ADJUSTMENTS_QUOTA),
    AGENT_OTHERS_SUBTRACT_ADJUSTMENTS_QUOTA("2","其他扣除调整",SourceAccountTypeEnums.AGENT.getType(),AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode(),AccountCoinTypeEnums.AGENT_OTHERS_SUBTRACT_ADJUSTMENTS_QUOTA),
    AGENT_OTHERS_ADD_ADJUSTMENTS_COMMISSION("1","其他增加调整",SourceAccountTypeEnums.AGENT.getType(),AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode(),AccountCoinTypeEnums.AGENT_OTHERS_ADD_ADJUSTMENTS_COMMISSION),
    AGENT_OTHERS_SUBTRACT_ADJUSTMENTS_COMMISSION("2","其他扣除调整",SourceAccountTypeEnums.AGENT.getType(),AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode(),AccountCoinTypeEnums.AGENT_OTHERS_SUBTRACT_ADJUSTMENTS_COMMISSION),

    //代理存款
    AGENT_DEPOSIT("3", "代理存款",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_DEPOSIT),
    AGENT_AGENT_ADMIN_DEPOSIT("4", "代理存款(后台)",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_DEPOSIT_ADMIN),

//    //代理转账 额度
    AGENT_TRANSFER_SUBORDINATES_QUOTA("5", "转给下级代理",SourceAccountTypeEnums.AGENT.getType(),AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode(),AccountCoinTypeEnums.AGENT_TRANSFER_SUBORDINATES_QUOTA),
    AGENT_SUPERIOR_TRANSFER_QUOTA("6", "上级转入",SourceAccountTypeEnums.AGENT.getType(),AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode(),AccountCoinTypeEnums.AGENT_SUPERIOR_TRANSFER_QUOTA),

    //代理转账 佣金
    AGENT_TRANSFER_SUBORDINATES_COMMISSION("5", "转给下级代理",SourceAccountTypeEnums.AGENT.getType(), AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode(),AccountCoinTypeEnums.AGENT_TRANSFER_SUBORDINATES_COMMISSION),
    AGENT_SUPERIOR_TRANSFER_COMMISSION("6", "上级转入",SourceAccountTypeEnums.AGENT.getType(),AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode(),AccountCoinTypeEnums.AGENT_SUPERIOR_TRANSFER_COMMISSION),
//
//    //额度转账
    AGENT_QUOTA_TRANSFER("7","转入额度钱包",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_QUOTA_TRANSFER),
    AGENT_TO_QUOTA_TRANSFER("8","转给额度钱包",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_TO_QUOTA_TRANSFER),
//
//    //代理额度
    AGENT_QUOTA_ADD("9", "额度增加调整",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_QUOTA_ADD),
    AGENT_QUOTA_SUBTRACT("10", "额度扣除调整",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_QUOTA_SUBTRACT),
//
//    //代理活动
    AGENT_PROMOTIONS_ADD("11", "活动增加调整",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_PROMOTIONS_ADD),
    AGENT_PROMOTIONS_SUBTRACT("12", "活动扣除调整",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_PROMOTIONS_SUBTRACT),

    //代理取款
    AGENT_WITHDRAWAL("13","代理取款冻结",SourceAccountTypeEnums.AGENT.getType(),String.valueOf(AccountFreezeFlagEnum.EXPRESS.getCode()),AccountCoinTypeEnums.AGENT_WITHDRAWAL),
    AGENT_WITHDRAWAL_SUCCESS("13","代理取款成功",SourceAccountTypeEnums.AGENT.getType(),String.valueOf(AccountFreezeFlagEnum.UNFREEZE.getCode()),AccountCoinTypeEnums.AGENT_WITHDRAWAL_SUCCESS),
    AGENT_WITHDRAWAL_FAIL("14","取款失败",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_WITHDRAWAL_FAIL),
    AGENT_WITHDRAWAL_ADMIN("15", "代理取款(后台)",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_WITHDRAWAL_ADMIN),

    //代理佣金
//        PLATFORM_ADD("16", "平台转入"),
    AGENT_COMMISSION_ADD("17", "佣金增加调整",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_COMMISSION_ADD),
    AGENT_COMMISSION_SUBTRACT("18", "佣金扣除调整",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_COMMISSION_SUBTRACT),

    TRANSFER_SUBORDINATES_MEMBER_QUOTA("19", "转给下级会员",SourceAccountTypeEnums.AGENT.getType(),
            AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode(),AccountCoinTypeEnums.TRANSFER_SUBORDINATES_MEMBER_QUOTA),
    TRANSFER_SUBORDINATES_MEMBER_COMMISSION("19", "转给下级会员",SourceAccountTypeEnums.AGENT.getType(),
            AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode(),AccountCoinTypeEnums.TRANSFER_SUBORDINATES_MEMBER_COMMISSION),

    AGENT_NEGATIVE_PROFIT_COMMISSION("20", "负盈利佣金",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_NEGATIVE_PROFIT_COMMISSION),

    AGENT_EFFECTIVE_TURNOVER_REBATE("21", "有效流水返点",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_EFFECTIVE_TURNOVER_REBATE),

    AGENT_CAPITATION_FEE("22", "人头费",SourceAccountTypeEnums.AGENT.getType(),AccountCoinTypeEnums.AGENT_CAPITATION_FEE),





    //平台币相关的业务场景
    PLATFORM_MEMBER_VIP_BENEFITS("1", "VIP福利",SourceAccountTypeEnums.PLATFORM.getType(), AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS),
    PLATFORM_WEEKLY_WATER("1", "VIP福利-周流水礼金",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WEEKLY_WATER.getCode(),AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS),
    PLATFORM_MONTHLY_WATER("1", "VIP福利-月流水礼金",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.MONTHLY_WATER.getCode(),AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS),
    PLATFORM_WEEKLY_SPORTS_WATER("1", "VIP福利-周体育流水礼金",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WEEKLY_SPORTS_WATER.getCode(),AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS),
    PLATFORM_BIRTHDAY("1", "VIP福利-生日礼金",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.BIRTHDAY.getCode(),AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS),
    PLATFORM_WEEKLY_RED_PACKET("1", "VIP福利-周红包",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WEEKLY_RED_PACKET.getCode(),AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS),


    PLATFORM_PROMOTIONS("2", "活动优惠",SourceAccountTypeEnums.PLATFORM.getType(), AccountCoinTypeEnums.PLATFORM_PROMOTIONS),
    PLATFORM_FIRST_DEPOSIT("2", "活动优惠-首存活动",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.FIRST_DEPOSIT.getCode(),AccountCoinTypeEnums.PLATFORM_FIRST_DEPOSIT),
    PLATFORM_SECOND_DEPOSIT("2","活动优惠-次存活动",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.SECOND_DEPOSIT.getCode(),AccountCoinTypeEnums.PLATFORM_SECOND_DEPOSIT),
    PLATFORM_SPECIFIED_DATE_DEPOSIT("2","活动优惠-指定日期存款活动",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.SPECIFIED_DATE_DEPOSIT.getCode(),AccountCoinTypeEnums.PLATFORM_SPECIFIED_DATE_DEPOSIT),
    PLATFORM_GAME_LOSS("2","活动优惠-游戏负盈利",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.GAME_LOSS.getCode(),AccountCoinTypeEnums.PLATFORM_GAME_LOSS),
    PLATFORM_DAILY_COMPETITION("2","活动优惠-每日竞赛",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.DAILY_COMPETITION.getCode(),AccountCoinTypeEnums.PLATFORM_DAILY_COMPETITION),
    PLATFORM_RED_PACKET_RAIN("2","活动优惠-红包雨",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.RED_PACKET_RAIN.getCode(),AccountCoinTypeEnums.PLATFORM_RED_PACKET_RAIN),
    PLATFORM_SIGN_IN("2","活动优惠-签到",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.SIGN_IN.getCode(),AccountCoinTypeEnums.PLATFORM_SIGN_IN),
    PLATFORM_TURNTABLE_BONUS("2","活动优惠-转盘奖金",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.TURNTABLE_BONUS.getCode(),AccountCoinTypeEnums.PLATFORM_TURNTABLE_BONUS),
    PLATFORM_INVITE_FRIENDS_HEAD_COUNT("2","活动优惠-邀请好友人头费",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.INVITE_FRIENDS_HEAD_COUNT.getCode(),AccountCoinTypeEnums.PLATFORM_INVITE_FRIENDS_HEAD_COUNT),
    PLATFORM_INVITE_FRIENDS_COMMISSION("2","活动优惠-邀请好友佣金返利",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.INVITE_FRIENDS_COMMISSION.getCode(),AccountCoinTypeEnums.PLATFORM_INVITE_FRIENDS_COMMISSION),
    PLATFORM_WELCOME_NEWBIE("2","活动优惠-欢迎新人礼金",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WELCOME_NEWBIE.getCode(),AccountCoinTypeEnums.PLATFORM_WELCOME_NEWBIE),
    PLATFORM_CURRENCY_CONFIRMATION("2","活动优惠-币种确认礼金",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.CURRENCY_CONFIRMATION.getCode(),AccountCoinTypeEnums.PLATFORM_CURRENCY_CONFIRMATION),
    PLATFORM_PHONE_CONFIRMATION("2","活动优惠-手机号确认",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.PHONE_CONFIRMATION.getCode(),AccountCoinTypeEnums.PLATFORM_PHONE_CONFIRMATION),
    PLATFORM_EMAIL_CONFIRMATION("2","活动优惠-邮箱确认",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.EMAIL_CONFIRMATION.getCode(),AccountCoinTypeEnums.PLATFORM_EMAIL_CONFIRMATION),
    PLATFORM_DAILY_DEPOSIT("2","活动优惠-每日存款",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.DAILY_DEPOSIT.getCode(),AccountCoinTypeEnums.PLATFORM_DAILY_DEPOSIT),
    PLATFORM_DAILY_BET("2","活动优惠-每日投注",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.DAILY_BET.getCode(),AccountCoinTypeEnums.PLATFORM_DAILY_BET),
    PLATFORM_DAILY_PROFIT("2","活动优惠-每日盈利",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.DAILY_PROFIT.getCode(),AccountCoinTypeEnums.PLATFORM_DAILY_PROFIT),
    PLATFORM_DAILY_LOSS("2","活动优惠-每日负盈利",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.DAILY_LOSS.getCode(),AccountCoinTypeEnums.PLATFORM_DAILY_LOSS),
    PLATFORM_WEEKLY_BET("2","活动优惠-每周投注",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WEEKLY_BET.getCode(),AccountCoinTypeEnums.PLATFORM_WEEKLY_BET),
    PLATFORM_WEEKLY_PROFIT("2","活动优惠-每周盈利",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WEEKLY_PROFIT.getCode(),AccountCoinTypeEnums.PLATFORM_WEEKLY_PROFIT),
    PLATFORM_WEEKLY_LOSS("2","活动优惠-每周负盈利",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WEEKLY_LOSS.getCode(),AccountCoinTypeEnums.PLATFORM_WEEKLY_LOSS),
    PLATFORM_WEEKLY_INVITE_FRIENDS("2","活动优惠-周邀请好友",SourceAccountTypeEnums.PLATFORM.getType(), AccountActivityTemplateEnum.WEEKLY_INVITE_FRIENDS.getCode(),AccountCoinTypeEnums.PLATFORM_WEEKLY_INVITE_FRIENDS),

    PLATFORM_REWARD("3", "勋章奖励",SourceAccountTypeEnums.PLATFORM.getType(),AccountCoinTypeEnums.PLATFORM_MEDAL_REWARD),
    PLATFORM_CONVERSION("4", "平台币兑换",SourceAccountTypeEnums.PLATFORM.getType(), AccountCoinTypeEnums.PLATFORM_CONVERSION),
//    REBATE("5", "返水", AccountCoinTypeEnums.REBATE),
    PLATFORM_MEMBER_VIP_BENEFITS_ADD("6", "VIP福利(平台币上分)",SourceAccountTypeEnums.PLATFORM.getType(),AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS_ADD),
    PLATFORM_MEMBER_VIP_BENEFITS_SUBTRACT("7", "VIP福利(平台币下分)",SourceAccountTypeEnums.PLATFORM.getType(),AccountCoinTypeEnums.PLATFORM_VIP_BENEFITS_SUBTRACT),
    PLATFORM_ACTIVITIES_ADD("8", "活动优惠(平台币上分)",SourceAccountTypeEnums.PLATFORM.getType(), AccountCoinTypeEnums.PLATFORM_ACTIVITIES_ADD),
    PLATFORM_ACTIVITIES_SUBTRACT("9", "活动优惠(平台币下分)",SourceAccountTypeEnums.PLATFORM.getType(), AccountCoinTypeEnums.PLATFORM_ACTIVITIES_SUBTRACT),
    PLATFORM_OTHER_ADJUSTMENTS_ADD("10", "其他(平台币上分)",SourceAccountTypeEnums.PLATFORM.getType(), AccountCoinTypeEnums.PLATFORM_OTHER_ADJUSTMENTS_ADD),
    PLATFORM_OTHER_ADJUSTMENTS_SUBTRACT("11", "其他(平台币下分)",SourceAccountTypeEnums.PLATFORM.getType(),AccountCoinTypeEnums.PLATFORM_OTHER_ADJUSTMENTS_SUBTRACT),
    ;
    private String code;
    private String name;
    private String flag;
    private String accountType;

    private final AccountCoinTypeEnums accountCoinTypeEnums;

    AccountTransferEnums(String code, String name,String accountType,String flag, AccountCoinTypeEnums accountCoinTypeEnums) {
        this.code = code;
        this.name = name;
        this.accountType= accountType;
        this.flag= flag;
        this.accountCoinTypeEnums = accountCoinTypeEnums;
    }
    AccountTransferEnums(String code, String name,String accountType, AccountCoinTypeEnums accountCoinTypeEnums) {
        this.code = code;
        this.name = name;
        this.accountType= accountType;
        this.accountCoinTypeEnums = accountCoinTypeEnums;
    }
    public static AccountTransferEnums of(String code,String accountType) {
        for (AccountTransferEnums accountTransferEnums : AccountTransferEnums.values()) {
            if (accountTransferEnums.code.equals(code) && accountTransferEnums.getAccountType().equals(accountType)  ) {
                return accountTransferEnums;
            }
        }
        return null; // 异常
    }

    public static AccountTransferEnums of(String code,String accountType,String flag) {
        for (AccountTransferEnums accountTransferEnums : AccountTransferEnums.values()) {
            if (accountTransferEnums.code.equals(code) && accountTransferEnums.getAccountType().equals(accountType)
                    && accountTransferEnums.getFlag().equals(flag)) {
                return accountTransferEnums;
            }
        }
        return null; // 异常
    }
}
