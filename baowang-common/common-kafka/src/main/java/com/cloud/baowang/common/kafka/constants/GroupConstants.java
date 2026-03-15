package com.cloud.baowang.common.kafka.constants;

public class GroupConstants {

    /**
     * 注单消息
     */
    public static final String PUSH_BET_ORDER_GROUP = "#{environment['common.config.env']}_push_bet_order_group";

    /**
     * 取消下注消息
     */
    public static final String CANCEL_BET_ORDER_GROUP = "#{environment['common.config.env']}_cancel_bet_order_group";


    public static final String PUSH_TYPING_AMOUNT_GROUP = "#{environment['common.config.env']}_push_typing_amount_group";

    /**
     * 每个站点的场馆当天的打码量信息,统计
     */
    public static final String VENUE_SITE_DAY_TOTAL_BET_AMOUNT_GROUP = "#{environment['common.config.env']}_venue_site_day_total_bet_amount_group";

    public static final String VIP_FLOW_LIST_GROUP = "#{environment['common.config.env']}_vip_flow_list_group";

    /**
     * 会员每日盈亏_MQ队列
     */
    public static final String USER_WIN_LOSE_CHANNEL_GROUP = "#{environment['common.config.env']}_user_win_lose_channel_group";

    /**
     * 会员场馆每日盈亏_MQ队列
     */
    public static final String USER_VENUE_WIN_LOSE_CHANNEL_GROUP = "#{environment['common.config.env']}_user_venue_win_lose_channel_group";

    /**
     * 会员场馆首次结算时间盈亏_group
     */
    public static final String USER_VENUE_FIXED_WIN_LOSE_CHANNEL_GROUP = "#{environment['common.config.env']}_user_venue_fixed_win_lose_channel_group";


    /**
     * 三方游戏注单记录
     */
    public static final String THIRD_GAME_ORDER_RECORD_GROUP = "#{environment['common.config.env']}_third_game_order_record_group";

    /**
     * 三方游戏派彩
     */
    public static final String THIRD_GAME_PAYOUT_GROUP = "#{environment['common.config.env']}_third_game_payout_group";

    /**
     * 用户_关注游戏
     */
    public static final String USER_GAME_COLLECTION_GROUP = "#{environment['common.config.env']}_user_game_collection_group";

    /**
     * 发放礼包_多个
     */
    public static final String SEND_USER_ACTIVITY_GROUP_LIST = "#{environment['common.config.env']}_send_user_activity_group_list";

    /**
     * 会员充值
     */
    public static final String MEMBER_RECHARGE_GROUP = "#{environment['common.config.env']}_member_recharge_group";

    /**
     * 会员提现消息组
     */
    public static final String MEMBER_WITHDRAW_GROUP = "#{environment['common.config.env']}_member_withdraw_group";

    /**
     * 转盘活动监控
     */
    public static final String MEMBER_RECHARGE_GROUP_SPIN_WHEEL = "#{environment['common.config.env']}_member_recharge_group_spin_wheel";

    /**
     * 每日任务监控,存款消息
     */
    public static final String MEMBER_RECHARGE_GROUP_TASK_DAILY = "#{environment['common.config.env']}_member_recharge_group_task_daily";

    /**
     * 签到活动,存款消息
     */
    public static final String MEMBER_RECHARGE_GROUP_CHECK_IN_ACTIVITY = "#{environment['common.config.env']}_member_recharge_group_check_in_activity";


    /**
     * 会员存款累计
     */
    public static final String USER_RECHARGE_WITHDRAW_GROUP = "#{environment['common.config.env']}_user_recharge_withdraw_group";

    /**
     * 会员存款累计
     */
    public static final String Agent_RECHARGE_WITHDRAW_GROUP = "#{environment['common.config.env']}_agent_recharge_withdraw_group";

    /**
     * 免费游戏
     */
    public static final String FREE_GAME_GROUP = "#{environment['common.config.env']}_free_game_group";

    public static final String FREE_GAME_RECORD_GROUP = "#{environment['common.config.env']}_free_game_record_group";

    /**
     * 新人任务
     */
    public static final String SITE_TASK_ORDER_RECORD_GROUP = "#{environment['common.config.env']}_site_task_order_record_group";
    /**
     * 每日任务+每周任务
     */
    public static final String SITE_TASK_DAILY_WEEK_RECORD_GROUP = "#{environment['common.config.env']}_site_task_daily_week_record_group";

    /**
     * 签到活动
     */
    public static final String SITE_ACTIVITY_CHECK_IN_RECORD_GROUP = "#{environment['common.config.env']}_site_activity_check_in_record_group";

    /**
     * 勋章获取组
     */
    public static final String MEDAL_ACQUIRE_GROUP = "#{environment['common.config.env']}_MEDAL_ACQUIRE_GROUP";

    /**
     * 勋章_无敌幸运星
     */
    public static final String MEDAL_ORDER_RECORD_WIN_LOSE_GROUP = "#{environment['common.config.env']}_medal_order_record_win_lose_group";
    /**
     * 勋章_叫我有钱人，小有成就, 招财猫
     */
    public static final String MEDAL_VALID_ORDER_GROUP = "#{environment['common.config.env']}_medal_valid_order_group";

    /**
     * 邀请好好友
     */
    public static final String MEDAL_CALL_FRIEND_GROUP = "#{environment['common.config.env']}_medal_call_friend_group";
    /**
     * 会员更新最近下注时间group
     */
    public static final String USER_BET_TIME_FLUSH = "#{environment['common.config.env']}_user_bet_time_flush";


    /**
     * 会员有效邀请
     */
    public static final String VALID_INVITE_USER_RECHARGE_GROUP = "#{environment['common.config.env']}_valid_invite_user_recharge_group";

    /**
     * 会员有效邀请
     */
    public static final String USER_REBATE_REWARD_GROUP = "#{environment['common.config.env']}_user_rebate_reward_group";



    /**
     * 充值、归集 交易消息
     */
    public static final String CHAIN_TRADE_NOTIFY_GROUP = "#{environment['spring.profiles.env']}_CHAIN_TRADE_NOTIFY_GROUP";

    /**
     * 地址余额 消息
     */
    public static final String ADDRESS_BALANCE_NOTIFY_GROUP = "#{environment['spring.profiles.env']}_ADDRESS_BALANCE_NOTIFY_GROUP";

    /**
     * 免费旋转消费
     */
    public static final String FREE_GAME_CONSUME_RECORD_GROUP = "#{environment['common.config.env']}_site_activity_free_game_consume_record_group";

    /**
     *站点保证金
     */
    public static final String SITE_SECURITY_BALANCE_GROUP = "#{environment['spring.profiles.env']}_SITE_SECURITY_BALANCE_GROUP";


    public static final String CLEAN_ACCOUNT_VENUE_BET_AMOUNT_TOPIC_GROUP = "#{environment['common.config.env']}_clean_account_venue_bet_amount_topic_group";


    /**
     * 游戏消息
     */
    public static final String ACCOUNT_GAME_TOPIC_GROUP = "#{environment['common.config.env']}_account_game_topic_group";

    /**
     * 会员主货币消息
     */
    public static final String ACCOUNT_USER_COIN_TOPIC_GROUP = "#{environment['common.config.env']}_account_user_coin_topic_group";

    /**
     * 会员主货币消息
     */
    public static final String ACCOUNT_USER_PLATFROM_COIN_TOPIC_GROUP = "#{environment['common.config.env']}_account_user_platfrom_coin_topic_group";

    /**
     * 代理相关的消息
     */
    public static final String ACCOUNT_AGENT_COIN_TOPIC_GROUP = "#{environment['common.config.env']}_account_agent_coin_topic_group";
}
