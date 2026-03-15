package com.cloud.baowang.account.api.enums;

import com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum;
import lombok.Getter;

@Getter
public enum AccountCoinTypeEnums {
     //5位数字  第1位代表用户的类型 1，会员类，2 代理类 3平台币
     //5位数字  第2位0 代表会员主动  1代表管理后台
     //5位数字  第3位1 进账  2出账
     //5位数字  第4 第5 对应的业务类型
     //5位数字  第6 扩展 用于同一个账变类型 有多种账户操作
    //主货币帐变相关类型
    MEMBER_DEPOSIT("10101", "会员存款",AccountBusinessCoinTypeEnums.FINANCE),

    MEMBER_DEPOSIT_ADMIN("11101", "会员存款（后台）",AccountBusinessCoinTypeEnums.MANAGE),
    MEMBER_WITHDRAWAL("10201", "会员提款冻结",AccountBusinessCoinTypeEnums.FINANCE),
    MEMBER_WITHDRAWAL_ADMIN("11201", "会员提款（后台）",AccountBusinessCoinTypeEnums.MANAGE),
    MEMBER_WITHDRAWAL_SUCCESS("10202", "会员提款成功",AccountBusinessCoinTypeEnums.FINANCE),
    MEMBER_WITHDRAWAL_FAIL("10203", "会员提款失败",AccountBusinessCoinTypeEnums.FINANCE),

    //vip福利相关
    MEMBER_VIP_BENEFITS("10106", "VIP福利", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_PROMOTION_BONUS("10110", "晋级奖金", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WEEKLY_WATER("10111", "周流水礼金",AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_MONTHLY_WATER("10112", "月流水礼金",AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WEEKLY_SPORTS_WATER("10113", "周体育流水礼金",AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_BIRTHDAY("10114", "生日礼金",AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WEEKLY_RED_PACKET("10115", "周红包",AccountBusinessCoinTypeEnums.BONUS),


    MEMBER_VIP_BENEFITS_ADD("11107", "VIP福利增加调整", AccountBusinessCoinTypeEnums.MANAGE),

    MEMBER_VIP_BENEFITS_SUBTRACT("11208", "VIP福利扣除调整",AccountBusinessCoinTypeEnums.MANAGE),

    PROMOTIONS("10109", "活动优惠", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_FIRST_DEPOSIT("10116","首存活动", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_SECOND_DEPOSIT("10117","次存活动", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_SPECIFIED_DATE_DEPOSIT("10118","指定日期存款活动", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_GAME_LOSS("10119","游戏负盈利", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_DAILY_COMPETITION("10120","每日竞赛", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_RED_PACKET_RAIN("10121","红包雨", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_SIGN_IN("10122","签到", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_TURNTABLE_BONUS("10123","转盘奖金", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_INVITE_FRIENDS_HEAD_COUNT("10124","邀请好友人头费", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_INVITE_FRIENDS_COMMISSION("101215","邀请好友佣金返利", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WELCOME_NEWBIE("101216","欢迎新人礼金", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_CURRENCY_CONFIRMATION("10127","币种确认礼金", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_PHONE_CONFIRMATION("10128","手机号确认", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_EMAIL_CONFIRMATION("10129","邮箱确认", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_DAILY_DEPOSIT("10130","每日存款", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_DAILY_BET("10131","每日投注", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_DAILY_PROFIT("10132","每日盈利", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_DAILY_LOSS("10133","每日负盈利", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WEEKLY_BET("10134","每周投注", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WEEKLY_PROFIT("10135","每周盈利", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WEEKLY_LOSS("10136","每周负盈利", AccountBusinessCoinTypeEnums.BONUS),
    MEMBER_WEEKLY_INVITE_FRIENDS("10137","周邀请好友", AccountBusinessCoinTypeEnums.BONUS),


    PROMOTIONS_ADD("11110", "活动优惠增加金额", AccountBusinessCoinTypeEnums.MANAGE),

    PROMOTIONS_SUBTRACT("11211", "活动优惠扣除金额", AccountBusinessCoinTypeEnums.MANAGE),

    TRANSFER_FROM_SUPERIOR("10118", "代理代存", AccountBusinessCoinTypeEnums.TRANSFER),

    MEMBER_PLATFORM_CONVERSION("10217", "平台币兑换", AccountBusinessCoinTypeEnums.TRANSFER),

    OTHER_ADD("11119", "其他增加调整", AccountBusinessCoinTypeEnums.MANAGE),

    OTHER_SUBTRACT("11220", "其他扣除调整", AccountBusinessCoinTypeEnums.MANAGE),

    REBATE("10122", "返水", AccountBusinessCoinTypeEnums.BONUS),
    REBATE_ADD("11123", "返水增加金额", AccountBusinessCoinTypeEnums.MANAGE),
    REBATE_SUBTRACT("11224","返水扣除金额", AccountBusinessCoinTypeEnums.MANAGE),

    RISK_CONTROL_ADJUSTMENT_ADD("11125", "风控调整增加金额", AccountBusinessCoinTypeEnums.MANAGE),

    RISK_CONTROL_ADJUSTMENT_SUBTRACT("11226", "风控调整扣除金额", AccountBusinessCoinTypeEnums.MANAGE),


    //游戏相关 by mufan
    GAME_TRANSFER_IN("00201", "转入", AccountBusinessCoinTypeEnums.GAME),
    GAME_TRANSFER_OUT("00101", "转出", AccountBusinessCoinTypeEnums.GAME),
    GAME_BET_FREEZD("00202", "投注冻结", AccountBusinessCoinTypeEnums.GAME),
    GAME_BET_CONFIRM("00205", "投注冻结确认", AccountBusinessCoinTypeEnums.GAME),
    GAME_BET("00203", "投注", AccountBusinessCoinTypeEnums.GAME),
    GAME_CANCEL_BET("00102", "投注取消", AccountBusinessCoinTypeEnums.GAME),
    GAME_PAYOUT("00103", "派彩", AccountBusinessCoinTypeEnums.GAME),
    GAME_RECALCULATE_PAYOUT("00104", "重算派彩", AccountBusinessCoinTypeEnums.GAME),
    GAME_CANCEL_PAYOUT("00105", "派彩取消", AccountBusinessCoinTypeEnums.GAME),
    GAME_RETURN_BET("00204", "投注返还", AccountBusinessCoinTypeEnums.GAME),
    GAME_TIPS("00106", "打赏", AccountBusinessCoinTypeEnums.GAME),

    //游戏不用关心着部分 内部划转
    GAME_CLEAN_BET_AMOUNT_TRANSFER("00106", "内部划转", AccountBusinessCoinTypeEnums.GAME),
    //by mufan end



    //代理相关

    AGENT_OTHERS_ADD_ADJUSTMENTS_QUOTA("211012","其他增加调整",AccountBusinessCoinTypeEnums.MANAGE),
    AGENT_OTHERS_SUBTRACT_ADJUSTMENTS_QUOTA("212022","其他扣除调整",AccountBusinessCoinTypeEnums.MANAGE),
    AGENT_OTHERS_ADD_ADJUSTMENTS_COMMISSION("211011","其他增加调整",AccountBusinessCoinTypeEnums.MANAGE),
    AGENT_OTHERS_SUBTRACT_ADJUSTMENTS_COMMISSION("212021","其他扣除调整",AccountBusinessCoinTypeEnums.MANAGE),
    AGENT_DEPOSIT("20103", "代理存款",AccountBusinessCoinTypeEnums.FINANCE),
    AGENT_DEPOSIT_ADMIN("20104", "代理存款(后台)",AccountBusinessCoinTypeEnums.MANAGE),

    //代理转账
    AGENT_TRANSFER_SUBORDINATES_QUOTA("202052", "额度转给下级代理",AccountBusinessCoinTypeEnums.TRANSFER),
    AGENT_SUPERIOR_TRANSFER_QUOTA("201062", "额度上级转入",AccountBusinessCoinTypeEnums.TRANSFER),

    AGENT_TRANSFER_SUBORDINATES_COMMISSION("202051", "佣金转给下级代理",AccountBusinessCoinTypeEnums.TRANSFER),
    AGENT_SUPERIOR_TRANSFER_COMMISSION("201061", "佣金上级转入",AccountBusinessCoinTypeEnums.TRANSFER),

    AGENT_QUOTA_TRANSFER("20107", "转入额度钱包",AccountBusinessCoinTypeEnums.TRANSFER),
    AGENT_TO_QUOTA_TRANSFER("20208","转给额度钱包",AccountBusinessCoinTypeEnums.TRANSFER),

    AGENT_QUOTA_ADD("21109", "额度增加调整",AccountBusinessCoinTypeEnums.MANAGE),
    AGENT_QUOTA_SUBTRACT("21210", "额度扣除调整",AccountBusinessCoinTypeEnums.MANAGE),

    AGENT_PROMOTIONS_ADD("21111", "活动增加调整",AccountBusinessCoinTypeEnums.MANAGE),
    AGENT_PROMOTIONS_SUBTRACT("21212", "活动扣除调整",AccountBusinessCoinTypeEnums.MANAGE),

    AGENT_WITHDRAWAL("20213", "代理取款冻结",AccountBusinessCoinTypeEnums.FINANCE),

    AGENT_WITHDRAWAL_SUCCESS("21213", "代理提款成功",AccountBusinessCoinTypeEnums.FINANCE),
    AGENT_WITHDRAWAL_FAIL("20214", "代理提款失败",AccountBusinessCoinTypeEnums.FINANCE),
    AGENT_WITHDRAWAL_ADMIN("21215", "代理提款(后台)",AccountBusinessCoinTypeEnums.MANAGE),

    CHARGE("4", "手续费扣除",AccountBusinessCoinTypeEnums.FINANCE),

    AGENT_COMMISSION_ADD("21117", "佣金增加调整",AccountBusinessCoinTypeEnums.MANAGE),

    AGENT_COMMISSION_SUBTRACT("21218", "佣金扣除调整",AccountBusinessCoinTypeEnums.MANAGE),
    TRANSFER_SUBORDINATES_MEMBER_QUOTA("202192", "额度转给下级会员",AccountBusinessCoinTypeEnums.TRANSFER),
    TRANSFER_SUBORDINATES_MEMBER_COMMISSION("202191", "佣金转给下级会员",AccountBusinessCoinTypeEnums.TRANSFER),

    AGENT_NEGATIVE_PROFIT_COMMISSION("20120", "负盈利佣金",AccountBusinessCoinTypeEnums.COMMISSION),
    AGENT_EFFECTIVE_TURNOVER_REBATE("20121", "有效流水返点",AccountBusinessCoinTypeEnums.COMMISSION),
    AGENT_CAPITATION_FEE("20122", "人头费",AccountBusinessCoinTypeEnums.COMMISSION),


    //平台币相关的业务场景
    PLATFORM_VIP_BENEFITS("30101", "VIP福利", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_PROMOTION_BONUS("30105", "晋级奖金", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WEEKLY_WATER("30106", "周流水礼金",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_MONTHLY_WATER("30107", "月流水礼金",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WEEKLY_SPORTS_WATER("30108", "周体育流水礼金",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_BIRTHDAY("30109", "生日礼金",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WEEKLY_RED_PACKET("30110", "周红包",AccountBusinessCoinTypeEnums.BONUS),

    PLATFORM_PROMOTIONS("30102", "活动优惠", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_FIRST_DEPOSIT("30111","首存活动", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_SECOND_DEPOSIT("30112","次存活动", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_SPECIFIED_DATE_DEPOSIT("30113","指定日期存款活动", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_GAME_LOSS("30114","游戏负盈利", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_DAILY_COMPETITION("30115","每日竞赛", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_RED_PACKET_RAIN("30116","红包雨", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_SIGN_IN("30117","签到", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_TURNTABLE_BONUS("30118","转盘奖金", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_INVITE_FRIENDS_HEAD_COUNT("30119","邀请好友人头费", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_INVITE_FRIENDS_COMMISSION("30120","邀请好友佣金返利", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WELCOME_NEWBIE("30121","欢迎新人礼金", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_CURRENCY_CONFIRMATION("30122","币种确认礼金", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_PHONE_CONFIRMATION("30123","手机号确认", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_EMAIL_CONFIRMATION("30124","邮箱确认", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_DAILY_DEPOSIT("30125","每日存款", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_DAILY_BET("30126","每日投注", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_DAILY_PROFIT("30127","每日盈利", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_DAILY_LOSS("30128","每日负盈利", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WEEKLY_BET("30129","每周投注", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WEEKLY_PROFIT("30130","每周盈利", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WEEKLY_LOSS("30131","每周负盈利", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_WEEKLY_INVITE_FRIENDS("30132","周邀请好友", AccountBusinessCoinTypeEnums.BONUS),

    PLATFORM_MEDAL_REWARD("30103", "勋章奖励",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_CONVERSION("30104", "平台币兑换", AccountBusinessCoinTypeEnums.TRANSFER),
    PLATFORM_VIP_BENEFITS_ADD("31101", "VIP福利(平台币上分)",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_VIP_BENEFITS_SUBTRACT("31201", "VIP福利(平台币下分)",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_ACTIVITIES_ADD("31102", "活动优惠(平台币上分)", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_ACTIVITIES_SUBTRACT("31202", "活动优惠(平台币下分)", AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_OTHER_ADJUSTMENTS_ADD("31103", "其他(平台币上分)",AccountBusinessCoinTypeEnums.BONUS),
    PLATFORM_OTHER_ADJUSTMENTS_SUBTRACT("31203", "其他(平台币下分)",AccountBusinessCoinTypeEnums.BONUS),
    ;
    private final String code;
    private final String value;
    private final AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums;

    // 构造函数
    AccountCoinTypeEnums(String code, String value,AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums ) {
        this.code = code;
        this.value = value;
        this.accountBusinessCoinTypeEnums = accountBusinessCoinTypeEnums;
    }

    public static AccountCoinTypeEnums of(String code) {
        for (AccountCoinTypeEnums accountCoinTypeEnums : AccountCoinTypeEnums.values()) {
            if (accountCoinTypeEnums.getCode().equals(code)) {
                return accountCoinTypeEnums;
            }
        }
        return null; // 异常
    }
}
