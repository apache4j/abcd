package com.cloud.baowang.system.api.enums.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置code-对应类型描述枚举
 */
@AllArgsConstructor
@Getter
public enum DictCodeConfigEnums {
    /**
     * 总台默认字典配置
     */
    USDT_MIN_THRESHOLD(0, "USDT加密归集最小USDT个数阈值", DictType.ADMIN_CENTER),
    CLIENT_ORDER_TIMEOUT(1, "客户端充提订单超时倒计时时间", DictType.ADMIN_CENTER),
    BACKEND_LOGOUT_TIMEOUT(2, "总控及站点后台当日登录后持续不操作的登出时间", DictType.ADMIN_CENTER),
    GOOGLE_VERIFY_MAX_TIMES(3, "总控及站点后台谷歌验证码连续错误五次的锁定时间", DictType.ADMIN_CENTER),
    CRYPTO_EXCHANGE_RATE_REFRESH_TIME(4, "虚拟币汇率刷新时间", DictType.ADMIN_CENTER),
    FIAT_EXCHANGE_RATE_REFRESH_TIME(5, "法币汇率刷新时间", DictType.ADMIN_CENTER),
    LOGIN_ERROR_LOCK_TIME(6, "总控及站点登录验证连续错误五次锁定时间", DictType.ADMIN_CENTER),


    /**
     * 站点初始化字典配置
     */
    /**
     * 7. 手机验证码每日获取上限
     */
    MOBILE_CODE_DAILY_LIMIT(7, "手机验证码每日获取上限", DictType.SITE_CENTER),

    /**
     * 8. 手机验证码每小时获取上限
     */
    MOBILE_CODE_HOURLY_LIMIT(8, "手机验证码每小时获取上限", DictType.SITE_CENTER),

    /**
     * 9. 邮箱验证码每日获取上限
     */
    EMAIL_CODE_DAILY_LIMIT(9, "邮箱验证码每日获取上限", DictType.SITE_CENTER),

    /**
     * 10. 邮箱验证码每小时获取上限
     */
    EMAIL_CODE_HOURLY_LIMIT(10, "邮箱验证码每小时获取上限", DictType.SITE_CENTER),

    /**
     * 11. 福利中心VIP福利发放后用户领取过期时间设定
     */
    VIP_BENEFIT_EXPIRATION_TIME(11, "福利中心VIP福利发放后用户领取过期时间设定", DictType.SITE_CENTER),

    /**
     * 12. 福利中心 活动福利 发放后用户领取过期时间设定
     */
    ACTIVITY_BENEFIT_EXPIRATION_TIME(12, "福利中心活动福利发放后用户领取过期时间设定", DictType.SITE_CENTER),

    /**
     * 13. 福利中心 任务福利 发放后用户领取过期时间设定
     */
    TASK_BENEFIT_EXPIRATION_TIME(13, "福利中心任务福利发放后用户领取过期时间设定", DictType.SITE_CENTER),

    /**
     * 14. 自定义头像配置单用户单日点击可自由替换次数
     */
    AVATAR_REPLACEMENT_DAILY_LIMIT(14, "自定义头像配置单用户单日点击可自由替换次数", DictType.SITE_CENTER),

    /**
     * 15. 单用户存提订单处理中的最大订单条数
     */
    MAX_ORDER_COUNT_IN_PROCESS(15, "单用户存提订单处理中的最大订单条数", DictType.SITE_CENTER),

    /**
     * 16. 意见反馈三分钟内单用户最大留言次数
     */
    FEEDBACK_MAX_MESSAGES_IN_3_MINUTES(16, "意见反馈三分钟内单用户最大留言次数", DictType.SITE_CENTER),

    /**
     * 17. 单用户三分钟内可登录最大次数
     */
    MAX_LOGIN_ATTEMPTS_IN_3_MINUTES(17, "单用户三分钟内可登录最大次数", DictType.SITE_CENTER),

    /**
     * 18. 单用户当日修改密码最大次数
     */
    MAX_PASSWORD_CHANGE_DAILY_LIMIT(18, "单用户当日修改密码最大次数", DictType.SITE_CENTER),

    /**
     * 19. 用户未操作退出登录时间
     */
    AUTO_LOGOUT_INACTIVITY_TIME(19, "用户未操作退出登录时间", DictType.SITE_CENTER),

    /**
     * 20. banner图自动切换倒计时时间配置
     */
    BANNER_AUTO_SWITCH_TIME(20, "banner图自动切换倒计时时间配置", DictType.SITE_CENTER),

    /**
     * 21. 系统自动清除打码量
     */
    SYSTEM_CLEANUP_BET_AMOUNT(21, "系统自动清除打码量", DictType.SITE_CENTER),


    /**
     * 23. 绑定邮箱&手机号的验证码的有效期时效
     */
    BIND_EMAIL_PHONE_CODE_EXPIRY_TIME(23, "绑定邮箱&手机号的验证码的有效期时效", DictType.SITE_CENTER),

    LOCKED_FOR_5_FAILED_ATTEMPTS(24, "单用户登录时密码验证连续错误五次锁定时间", DictType.SITE_CENTER),

    /**
     * 总台使用
     */
    THIRD_PARTY_RECHARGE_ORDER_TIMEOUT(25,"三方充值订单超时关闭时间",DictType.ADMIN_CENTER),
    /**
     * 总台使用
     */
    EZPAY_FUND_PASSWORD(26,"EZpay通道资金密码",DictType.ADMIN_CENTER),


    //WITHDRAW_SOUND_SWITCH(27, "提款提示音开关", DictType.SITE_CENTER),
    /**
     * 站点ip限制
     */
    SITE_IP_MAXCOUNT(28,"同IP可注册的最大用户数",DictType.SITE_CENTER),

    /**
     * 福利中心返水福利发放后用户领取过期时间设定
     */
    REBATE_BENEFIT_EXPIRATION_TIME(29,"福利中心返水福利发放后用户领取过期时间设定",DictType.SITE_CENTER),

    /**
     * 反水脚本执行时间
     */
    REBATE_SCRIPT_TIME(30,"反水脚本执行时间",DictType.SITE_CENTER),

    RECHARGE_REAL_NAME(31,"存款实名认证开关",DictType.SITE_CENTER),

    FIRST_WITHDRAW_ONLY_FIAT_CURRENCY(32,"首笔提款仅限法币",DictType.SITE_CENTER),

    WITHDRAW_REAL_NAME(33,"提款实名认证开关",DictType.SITE_CENTER),

    BANK_CARD_BINDING_NUMS(34,"银行卡绑定条数上限",DictType.SITE_CENTER),

    ELECTRONIC_WALLET_BINDING_NUMS(35,"电子钱包绑定条数上限",DictType.SITE_CENTER),

    CRYPTO_CURRENCY_BINDING_NUMS(36,"加密货币绑定条数上限",DictType.SITE_CENTER),

    VIP_BIRTHBONUE_EXPIRATION_TIME(37, "福利中心VIP福利-生日礼金发放后用户领取过期时间", DictType.SITE_CENTER),

    VIP_GRATEUPBONUE_EXPIRATION_TIME(38, "福利中心VIP福利-升级礼金发放后用户领取过期时间", DictType.SITE_CENTER),

    VIP_WEEKBONUE_EXPIRATION_TIME(39, "福利中心VIP福利-周红包发放后用户领取过期时间", DictType.SITE_CENTER),

    ACTIVIY_NEW_HAND_FIRST_WITHDRAW_DAYS(40,"新手活动-活动生效开始前6天和后6天注册的会员可以参加活动",DictType.ADMIN_CENTER),

    TASK_NEWBIE_EXPIRE_PERIOD(41,"新人任务有效期",DictType.SITE_CENTER),

    ACTIVITY_REDEMPTION_CODE_DEPOSIT_DAYS(42,"兑换码-兑换码生效前2天或2后,这段段时间内会员如有存款则可以参加兑换",DictType.ADMIN_CENTER)
    ;


    private final Integer code;
    private final String msg;
    private final DictType type;

    public static DictCodeConfigEnums getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        DictCodeConfigEnums[] types = DictCodeConfigEnums.values();
        for (DictCodeConfigEnums type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    @AllArgsConstructor
    @Getter
    public enum DictType {
        ADMIN_CENTER(0, "总台"),
        SITE_CENTER(1, "站点");
        private final Integer type;
        private final String msg;

    }
}
