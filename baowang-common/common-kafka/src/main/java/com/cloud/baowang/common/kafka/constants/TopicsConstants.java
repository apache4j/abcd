package com.cloud.baowang.common.kafka.constants;

/**
 * baowang kafka topicConstants
 */
public class TopicsConstants {

    /**
     * 注单消息
     */
    public static final String PUSH_BET_ORDER_TOPIC = "#{environment['common.config.env']}_push_bet_order_topic";

    /**
     * 取消下注
     */
    public static final String CANCEL_BET_ORDER_TOPIC = "#{environment['common.config.env']}_cancel_bet_order_topic";

    /**
     * 打码量
     */
    public static final String PUSH_TYPING_AMOUNT_TOPIC = "#{environment['common.config.env']}_push_typing_amount_topic";

    public static final String VIP_FLOW_LIST_TOPIC = "#{environment['common.config.env']}_vip_flow_list_topic";

    /**
     * 每个站点的场馆当天的打码量信息,统计
     */
    public static final String VENUE_SITE_DAY_TOTAL_BET_AMOUNT_TOPIC = "#{environment['common.config.env']}_venue_site_day_total_bet_amount_topic";

    /**
     * 勋章_会员投注盈利信息
     */
//    public static final String MEDAL_ORDER_RECORD_WIN_LOSE_TOPIC = "#{environment['common.config.env']}_medal_order_order_win_lose_topic";
    // 勋章_投注累计
//    public static final String MEDAL_VALID_ORDER_TOPIC = "#{environment['common.config.env']}_medal_valid_order_topic";

    /**
     * 会员每日盈亏_队列
     */
    public static final String USER_WIN_LOSE_CHANNEL = "#{environment['common.config.env']}_user_win_lose_channel";

    /**
     * 会员场馆每日盈亏
     */
    public static final String USER_VENUE_WIN_LOSE_BATCH_QUEUE = "#{environment['common.config.env']}_user_venue_win_loss_batch_topic";
    /**
     * 三方游戏注单
     */
    public static final String THIRD_GAME_ORDER_RECORD = "#{environment['common.config.env']}_third_game_order_record";

    /**
     * 三方游戏派奖
     */
    public static final String THIRD_GAME_PAYOUT_TOPIC = "#{environment['common.config.env']}_third_game_payout_topic";

    /**
     * 发放礼包_多个
     */
    public static final String SEND_USER_ACTIVITY_ORDER_LIST = "#{environment['common.config.env']}_send_user_activity_order_list";

    /**
     * 会员充值
     */
    public static final String MEMBER_RECHARGE = "#{environment['common.config.env']}_member_recharge";

    /**
     * 会员提现
     */
    public static final String MEMBER_WITHDRAW = "#{environment['common.config.env']}_member_withdraw";
    /**
     * 会员存款取款累计
     */
    public static final String  USER_RECHARGE_WITHDRAW = "#{environment['common.config.env']}_user_recharge_withdraw";

    /**
     * 代理存款取款累计
     */
    public static final String  AGENT_RECHARGE_WITHDRAW = "#{environment['common.config.env']}_agent_recharge_withdraw";


    /**
     * 游戏免费旋转
     */
    public static final String FREE_GAME = "#{environment['common.config.env']}_free_game";
    public static final String FREE_GAME_RECORD = "#{environment['common.config.env']}_free_game_record";

    public static final String FREE_GAME_RECORD_CONSUME = "#{environment['common.config.env']}_free_game_consume_record";
    /**
     * 新人活动
     */
    public static final String TASK_NOVICE_ORDER_RECORD_TOPIC = "#{environment['common.config.env']}_task_novice_order_record";

    /**
     * 会员场馆每日盈亏
     */
    public static final String TASK_DAILY_WEEK_ORDER_RECORD_TOPIC = "#{environment['common.config.env']}_task_daily_week_order_record";


    /**
     * 勋章获取队列
     */
    public static final String  MEDAL_ACQUIRE_QUEUE = "#{environment['common.config.env']}_MEDAL_ACQUIRE_QUEUE";

    /**
     * 邀请好友 - 后台存款和代理代存
     */
    public static final String CALL_FRIEND_MEMBER_RECHARGE = "#{environment['common.config.env']}_call_friend_member_recharge";

    /**
     * 用户最新投注
     */
    public static final String USER_LATEST_BET_QUEUE = "#{environment['common.config.env']}_user_latest_bet_topic";


    /**
     * 有效邀请统计
     */
    public static final String  VALID_INVITE_USER_RECHARGE = "#{environment['common.config.env']}_valid_invite_user_recharge";


    /**
     * 返水信息发送福利中心
     */
    public static final String  USER_REBATE_REWARD_TOPIC = "#{environment['common.config.env']}_user_rebate_reward_topic";

    /**
     * 充值、归集 交易消息
     */
    public static final String CHAIN_TRADE_NOTIFY_TOPIC = "#{environment['spring.profiles.active']}_CHAIN_TRADE_NOTIFY_TOPIC";

    /**
     * 地址余额 消息
     */
    public static final String ADDRESS_BALANCE_NOTIFY_TOPIC = "#{environment['spring.profiles.active']}_ADDRESS_BALANCE_NOTIFY_TOPIC";

    /**
     * 免费旋转-个数
     */
   // public static final String TASK_DAILY_WEEK_ORDER_RECORD_TOPIC = "#{environment['common.config.env']}_task_daily_week_order_record";
    /**
     * 站点保证金
     */
    public static final String SITE_SECURITY_BALANCE = "#{environment['spring.profiles.active']}_SITE_SECURITY_BALANCE_TOPIC";

    /**
     * 清理游戏所有下注金额
     */
    public static final String CLEAN_ACCOUNT_VENUE_BET_AMOUNT_TOPIC = "#{environment['common.config.env']}_clean_account_venue_bet_amount_topic";



    /**
     * 游戏消息
     */
    public static final String ACCOUNT_GAME_TOPIC = "#{environment['common.config.env']}_account_game_topic";

    /**
     * 会员主货币消息
     */
    public static final String ACCOUNT_USER_COIN_TOPIC = "#{environment['common.config.env']}_account_user_coin_topic";

    /**
     * 会员主货币消息
     */
    public static final String ACCOUNT_USER_PLATFROM_COIN_TOPIC = "#{environment['common.config.env']}_account_user_platfrom_coin_topic";

    /**
     * 代理相关的消息
     */
    public static final String ACCOUNT_AGENT_COIN_TOPIC = "#{environment['common.config.env']}_account_agent_coin_topic";





}
