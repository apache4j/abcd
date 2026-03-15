package com.cloud.baowang.common.core.constants;

import java.math.BigDecimal;

public class CommonConstant {

    public static final String LANGUAGE_HEAD = "Accept-Language";
    public static final String ADMIN_CENTER_TIMEZONE = "UTC-5";
    public static final String CONTENT_TYPE_HEAD = "Content-Type";
    public static final String USER_AGENT = "User-Agent";
    public static final String X_CUSTOM = "X-Custom";
    public static final String TRACE_ID="traceId";
    public static final String DEVICE_TYPE_VERSION = "device-type-version";

    public static final String BIZ_CUSTOM = "BIZ-Custom";
    public static final String CONTENT_TYPE_HEAD_VALUE = "application/json;charset=UTF-8";
    public static final String Deviceid = "Deviceid";
    public static final String Version = "Version";
    public static final String GAME = "GAME";
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final String AGENT_REGISTER_TEMPLATE = "AgentRegisterTemplate";

    public static final String TERMINAL_APP = "APP";
    public static final String TERMINAL_H5 = "H5";
    public static final String TERMINAL_PC = "PC";


    public static final String REQUEST_BASIC_INFO = "REQUEST_BASIC_INFO";

    // 查询功能 目前不限制时间
    public static final Long QUERY_LIMIT_TIME = 1L;
    // 导出功能 过期时间60 ，单位默认是秒
    public static final Long EXPORT_EXPIRE_TIME = 60L;
    //查询
    public static final Long QUERY_EXPIRE_TIME = 5L;

    public static final Long TEN_MINUTE_SECONDS = 10 * 60L;

    public static final Long THIRTY_MINUTE_SECONDS = 30 * 60L;
    // 2小时 毫秒数
    public static final Long TWO_HOUR_MILLISECONDS = 2 * 60 * 60 * 1000L;

    //两小时 秒数
    public static final Long TWO_HOUR_SECONDS = 2 * 60 * 60L;

    public static final Long HOUR25_SECONDS = 25 * 60 * 60L;

    public static final Long ONE_HOUR_MILLISECONDS = 60 * 60 * 1000L;
    public static final Long ONE_HOUR_MILLISECONDS_999 = 60 * 60 * 1000L - 1;
    public static final Long HOUR12_MILLISECONDS = 12 * 60 * 60 * 1000L;

    //30分钟毫秒数
    public static final Long THIRTY_MINUTE_MILLISECONDS = 30 * 60 * 1000L;

    //一天秒数
    public static final Long ONE_DAY_SECONDS = 60 * 60 * 24L;

    //三分钟
    public static final Long THREE_MINUTES_SECONDS = 3 * 60L;

    // bigdecimal 100
    public static final BigDecimal DECIMAL_100 = new BigDecimal("100");

    public static final String EMPTY_STRING = "";
    public static final String BLANK_STRING = " ";
    public static final String POINT = ".";

    public static final String COMMA2 = "、";
    public static final String COMMA = ",";
    public static final String PLUS_SIGN = "+";
    public static final String PERCENT_SIGN = "%";
    public static final String UNDERSCORE = "_";
    public static final String CENTER_LINE = "-";
    public static final String UNDERLINE = "_";
    public static final String COLON = ":";
    public static final String LEFT_BRACKET = "{";
    public static final String SEMICOLON = ";";
    public static final String ASTERISK = "\\*";
    public static final String ORDER_BY_ASC = "asc";
    public static final String ORDER_BY_DESC = "desc";

    public static final String business_zero_str = "0";
    public static final String business_one_str = "1";
    public static final String business_two_str = "2";
    public static final String business_three_str = "3";
    public static final String business_four_str = "4";
    public static final String business_five_str = "5";
    public static final String business_six_str = "6";
    public static final String business_seven_str = "7";
    public static final String business_eight_str = "8";
    public static final String business_nine_str = "9";
    public static final String business_ten_str = "10";

    public static final String ws_topic_prefix = "/";
    // 常量
    public static final Integer business_negative1 = -1;
    public static final Integer business_zero = 0;
    public static final Integer business_one = 1;
    public static final Integer business_two = 2;
    public static final Integer business_three = 3;
    public static final Integer business_four = 4;
    public static final Integer business_five = 5;
    public static final Integer business_six = 6;
    public static final Integer business_seven = 7;
    public static final Integer business_eight = 8;
    public static final Integer business_nine = 9;
    public static final Integer business_ten = 10;

    public static final Integer business_eleven = 11;
    public static final Integer business_twelve = 12;

    public static final Integer BUSINESS_20 = 20;

    public static final Integer business_thirty_one = 31;
    public static final Integer business_thirty_two = 32;

    public static final Integer business_fifty = 50;


    public static final String VIP0 = "VIP0";
    public static final String VIP1 = "VIP1";
    public static final String VIP2 = "VIP2";
    public static final String VIP3 = "VIP3";
    public static final String VIP4 = "VIP4";
    public static final String VIP5 = "VIP5";
    public static final String VIP6 = "VIP6";
    public static final String VIP7 = "VIP7";
    public static final String VIP8 = "VIP8";
    public static final String VIP9 = "VIP9";
    public static final String VIP10 = "VIP10";

    public static final String query_limit = " limit 1 ";

    // 账号类型 1测试 2正式
    public static final String USER_ACCOUNT_TYPE = "account_type";

    //虚拟币U
    public static final String USDT = "USDT";
    //平台币代码
    public static final String PLAT_CURRENCY_CODE = "WTC";
    //平台币名称 WinToCoin
    public static final String PLAT_CURRENCY_NAME = "WTC";
    //平台币符号
    public static final char PLAT_FORM_SYMBOL = 'ω';

    /**
     * 币种精度
     */
    public static final String CURRENCY_DECIMAL_TYPE = "CURRENCY_DECIMAL_TYPE";

    public static final String CURRENCY_NAME = "CURRENCY_NAME";

    /**
     * 启用禁用状态
     */
    public static final String ENABLE_DISABLE_TYPE = "ENABLE_DISABLE_TYPE";
    /**
     * 启用禁用状态
     */
    public static final String ENABLE_DISABLE_STATUS = "enable_disable_status";

    public static final String DOMAIN_STATE = "domain_state";


    // 会员状态 1在线 0离线
    public static final String ONLINE_STATUS = "online_status";
    // 变更类型
    /**
     * 1	账号状态
     * 2	风控层级
     * 3	会员标签
     * 9	账号备注
     * 4	出生日期
     * 5	手机号码
     * 6	姓名
     * 7	性别
     * 8	邮箱
     * 10	VIP等级
     */
    public static final String USER_CHANGE_TYPE = "change_type";
    // 性别 1男 2女
    public static final String USER_GENDER = "gender";

    // 会员审核 - 锁单状态 0未锁 1已锁
    public static final String USER_REVIEW_LOCK_STATUS = "lock_status";
    // 会员审核 - 审核操作 1一审审核 2结单查看
    public static final String USER_REVIEW_REVIEW_OPERATION = "review_operation";
    // 审核状态 - 审核操作 1待审核 2审核中
    public static final String SITE_SECURITY_REVIEW_STATUS = "site_security_review_status";


    // 会员审核 - 审核申请类型 1会员标签 2出生日期 3手机号码 4姓名 5性别 6邮箱

    // 会员审核 - 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
    public static final String USER_REVIEW_REVIEW_STATUS = "review_status";
    //人工加减额-账变状态
    public static final String BALANCE_CHANGE_STATUS = "balance_change_status";

    // 会员账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定
    public static final String USER_ACCOUNT_STATUS = "account_status";

    //
    public static final String ACTIVITY_DAILY_RANKING = "activity_daily_ranking";

    // 会员注册终端 0后台 1PC 2IOS_H5 3IOS_APP 4Android_H5 5Android_APP
    public static final String USER_REGISTRY = "registry";

    // 银行卡管理/虚拟币账号管理-黑名单状态 0禁用中 1启用中
    public static final String BLACK_STATUS = "black_status";
    // 银行卡管理/虚拟币账号管理-绑定状态 0未绑定 1绑定中
    public static final String BINDING_STATUS = "binding_status";
    // 银行卡管理/虚拟币账号管理-操作类型 1下架 2绑定 3解绑 4启用 5禁用
    public static final String OPERATE_TYPE = "operate_type";
    /**
     * 活动时效
     * {@link com.cloud.baowang.common.core.enums.activity.ActivityDeadLineEnum}
     */
    public static final String ACTIVITY_DEADLINE = "activity_deadline";
    /**
     * 活动模版
     * {@link com.cloud.baowang.common.core.enums.activity.ActivityTemplateEnum}
     */
    public static final String ACTIVITY_TEMPLATE = "activity_template";

    public static final String ACTIVITY_TEMPLATE_V2 = "activity_template_v2";
    /**
     * 福利类型
     * {@link com.cloud.baowang.activity.api.enums.ActivityTemplateRewardEnum}
     */
    public static final String ACTIVITY_TEMPLATE_REWARD = "activity_template_reward";
    /**
     * 活动相关终端
     * {@link com.cloud.baowang.common.core.enums.activity.ActivityRelatedTerminalEnum}
     */
    public static final String ACTIVITY_DISCOUNT_TYPE = "activity_discount_type";

    /**
     * 派发方式
     * {@link com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum}
     */
    public static final String ACTIVITY_DISTRIBUTION_TYPE = "activity_distribution_type";

    //会员标签变更类型
    public static final String USER_LABEL_CHANGE_TYPE = "label_change_type";
    public static final String VIP_PROMOTION = "vip_promotion";

    //虚拟币协议
    public static final String AGREEMENT_TYPE = "agreement_type";

    public static final String VIP_OPERATION = "vip_operation";

    public static final String VIP_OPERATION_TYPE = "vip_operation_type";

    public static final String VIP_CHANGE_TYPE = "vip_change_type";
    /**
     * 游戏平台
     */
    public static final String VENUE_CODE = "venue_code";

    /**
     * 游戏平台
     */
    public static final String FREE_GAME_SEND_STATUS = "free_game_send_status";

    public static final String DEFAULT_REBATE = "default_rebate";
    public static final String HIGH_REBATE = "high_rebate";

    /**
     * 游戏类型
     */
    public static final String GAME_TYPE = "game_type";
    /**
     * vip段位
     */
    public static final String VIP_RANK = "vip_rank";
    /**
     * vip等级
     */
    public static final String VIP_GRADE = "vip_grade";
    /**
     * 登录状态
     */
    public static final String LOGIN_TYPE = "login_type";
    /**
     * 登录终端
     */
    public static final String DEVICE_TERMINAL = "device_terminal";
    /**
     * 注单状态
     */
    public static final String ORDER_STATUS = "order_status";
    /**
     * 注单分类
     */
    public static final String ORDER_CLASSIFY = "order_classify";
    /**
     * 变更状态
     */
    public static final String CHANGE_STATUS = "change_status";
    /**
     * vip0
     */
    public static final String VIP_ZERO = "VIP0";
    /**
     * 密码最大重试次数
     */
    public static final String PASSWORD_MAX_RETRY_COUNT = "password_max_retry_count";

    /**
     * 地址类型
     */
    public static final String ADD_TYPE = "add_type";


    public static final String VENUE_INFO_STATUS = "venue_info_status";
    /**
     * 活动页签状态
     */
    public static final String ACTIVE_TAB_STATE = "active_tab_state";
    /**
     * 游戏分类管理
     */
    public static final String GAME_CATEGORY_MANAGEMENT = "game_category_status";

    /**
     * 游戏信息状态
     */
    public static final String GAME_INFO_STATUS = "game_info_status";

    /**
     * 是否
     */
    public static final String YES_NO = "yes_no";

    /**
     * 游戏图标状态
     */
    public static final String GAME_ICON_STATUS = "game_icon_status";

    /**
     * 投注记录时间
     */
    public static final String BET_TIME = "bet_time";
    /**
     * 教程名称配置状态
     */
    public static final String TUTORIAL_NAME_CONFIGURATION = "tutorial_state";

    /**
     * 闪屏页显示终端
     */
    public static final String SCREEN_DEVICE = "screen_device";

    /**
     * 闪屏页显示终端
     */
    public static final String IS_DISABLE = "is_disable";

    /**
     * 客户端配置记录--操作页面
     */
    public static final String CLIENT_OPERATOR_WEB = "operator_web";

    // 轮播图区域
    public static final String BANNER_REGION = "banner_region";
    // 轮播图是否跳转 1是 0否
    public static final String IS_JUMP = "is_jump";
    // 轮播图跳转目标 1游戏 2内部 3外部地址
    public static final String JUMP_TARGET = "jump_target";
    // 轮播图终端 1APP端 2H5端 3PC端
    public static final String BANNER_TERMINAL = "banner_terminal";
    // 轮播图终端 1APP端 2H5端 3PC端
    public static final String FEED_BACK_TYPE = "feedback_type";
    // 默认轮播图
    public static final String DEFAULT_BANNER = "default_banner";

    /**
     * 游戏信息
     */
    public static final String GAME_INFO = "game_info";

    /**
     * 账变业务类型
     */
    public static final String BUSINESS_COIN_TYPE = "business_coin_type";

    /**
     * 账变类型
     */
    public static final String COIN_TYPE = "coin_type";


    /**
     * 账变收支类型
     */
    public static final String COIN_BALANCE_TYPE = "coin_balance_type";

    /**
     * 风控级别
     */
    public static final String RISK_CONTROL_LEVEL = "risk_control_level";

    public static final String ONE_REVIEW = "一审：";
    public static final String TWO_REVIEW = "二审：";

    // 会员人工添加额度-调整类型
    public static final String USER_MANUAL_UP_ADJUST_TYPE = "user_manual_up_adjust_type";
    // 会员人工添加额度-活动类型
    public static final String USER_MANUAL_UP_ACTIVITY_TYPE = "user_manual_up_activity_type";
    // 流水倍数-系统默认值
    public static final String DEFAULT_RUNNING_WATER_MULTIPLE = "default_running_water_multiple";

    /**
     * 会员人工扣除额度-调整类型
     */
    public static final String USER_MANUAL_DOWN_ADJUST_TYPE = "user_manual_down_adjustment_type";

    /**
     * 奖励类型
     */
    public static final String VIP_AWARD_TYPE = "award_type";

    public static final String VIP_AWARD_TYPE_CN = "vip_award_type_cn";
    /**
     * 平台类型
     */
    public static final String VENUE_TYPE = "venue_type";

    /**
     * 三方消息状态
     */
    public static final String THREE_PARTY_MESSAGE_STATUS = "three_party_message_status";

    // 风险类型
    public static final String RISK_CONTROL_TYPE = "risk_control_type";
    //  终端类型(包含后台)
    public static final String DEVICE_TYPE = "device_terminal";
    //  终端类型(不包含后台)
    public static final String GAME_SUPPORT_DEVICE = "device_terminal";

    /***
     *通知配置
     */
    //通知类型
    public static final String NOTIFICATION_TYPE = "notification_type";

    public static final String SEND_OBJECT_TYPE = "target_type";
    //通知对象
    public static final String SEND_OBJECT = "send_object";

    public static final String MEMBERSHIP_TYPE = "membership_type";

    public static final String SEND_STATUS = "send_status";

    public static final String NOTICE_AGENT_TYPE = "notice_agent_type";

    public static final String NOTICE_BUSINESS_TYPE = "notice_business_type";

    public static final String SPECIFY_MEMBERSHIP_TYPE = "specify_membership_type";

    /**
     * 取款密码校验失效时间
     */
    public static final Long CHECK_PASSWORD_EXPIRE_TIME = 20 * 60L;

    /**
     * 重算状态
     */
    public static final String RESETTLE_STATUS = "resettle_status";


    /**
     * 资金明细记录时间
     */
    public static final String COIN_RECORD_TIME = "coin_record_time";

    /**
     * 资金明细状态
     */
    public static final String COIN_RECORD_STATUS = "coin_record_status";

    /**
     * 资金明细类型
     */
    public static final String CUSTOMER_COIN_TYPE = "customer_coin_type";

    /**
     * 福利中心类型
     */
    public static final String WELFARE_TYPE = "welfare_type";

    /**
     * 福利中心时间范围
     */
    public static final String WELFARE_TIME = "welfare_time";

    // 代理信息变更类型
    public static final String AGENT_CHANGE_TYPE = "agent_change_type";

    /**
     * 代理类型
     */
    public static final String AGENT_TYPE = "agent_type";
    /**
     * 代理归属
     */
    public static final String AGENT_ATTRIBUTION = "agent_attribution";
    /**
     * 代理类别
     */
    public static final String AGENT_CATEGORY = "agent_category";
    /**
     * 代理推广素材-图片类型
     */
    public static final String AGENT_IMAGE_TYPE = "agent_image_type";
    /**
     * 代理推广素材-图片变更类型
     */
    public static final String AGENT_IMAGE_CHANGE_TYPE = "agent_image_change_type";
    /**
     * 代理推广素材-图片尺寸
     */
    public static final String AGENT_IMAGE_SIZE = "agent_image_size";
    /**
     * 代理推广素材-域名类型
     */
    public static final String AGENT_DOMAIN_TYPE = "domain_type";

    /**
     * 虚拟币种类型
     */
    public static final String VIRTUAL_CURRENCY_TYPE = "virtual_currency_type";

    /**
     * 虚拟币种协议
     */
    public static final String VIRTUAL_CURRENCY_PROTOCOL = "virtual_currency_protocol";

    /**
     * 代理登录终端
     */
    public static final String AGENT_LOGIN_DEVICE = "agent_login_device";

    /**
     * 创建修改删除
     */
    public static final String AGENT_CONTRACT_TEMPLATE_RATE_BASE_RECORD_TYPE = "agent_record_type";

    /**
     * 代理钱包类型
     */
    public static final String AGENT_WALLET_TYPE = "agent_wallet_type";

    /**
     * 代理业务类型
     */
    public static final String AGENT_BUSINESS_COIN_TYPE = "agent_business_coin_type";

    /**
     * 代理账变类型
     */
    public static final String AGENT_COIN_TYPE = "agent_coin_type";
    /**
     * 会员提款账单状态
     */
    public static final String DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS = "deposit_withdrawal_order_customer_status";
    /**
     * 会员充值账单状态
     */
    public static final String DEPOSIT_ORDER_CUSTOMER_STATUS = "deposit_order_customer_status";


    /**
     * 代理账变客户端类型
     */
    public static final String AGENT_CUSTOMER_COIN_TYPE = "agent_customer_coin_type";

    /**
     * 代理转账类型
     */
    public static final String AGENT_TRANSFER_TYPE = "agent_transfer_type";


    /**
     * 代理账变额度钱包客户端账变类型
     */
    public static final String AGENT_QUOTA_CUSTOMER = "agent_quota_customer";

    /**
     * 代理账变佣金钱包客户端账变类型
     */
    public static final String AGENT_COMMISSION_CUSTOMER = "agent_commission_customer";


    /**
     * 代存记录时间
     */
    public static final String AGENT_DEPOSIT_SUBORDINATES_TIME = "agent_deposit_subordinates_time";

    /**
     * 代存类型
     */
    public static final String AGENT_DEPOSIT_SUBORDINATES_TYPE = "agent_deposit_subordinates_type";

    /**
     * 代理带自定义时间范围
     */
    public static final String AGENT_DATE = "agent_date";


    /**
     * 代理取款支付类型
     */
    public static final String AGENT_WITHDRAWAL_PAYMENT_METHOD = "agent_withdrawal_payment_method";

    /**
     * 代理取款客户端状态
     */
    public static final String AGENT_WITHDRAWAL_CUSTOMER_STATUS = "agent_withdrawal_customer_status";

    /**
     * 代理存款客户端状态
     */
    public static final String AGENT_DEPOSIT_CUSTOMER_STATUS = "agent_deposit_customer_status";
    /**
     * 代理存款类型
     */
    public static final String AGENT_DEPOSIT_TYPE = "agent_deposit_type";

    /**
     * 代理排序字段
     */
    public static final String AGENT_SORT = "agent_sort";

    /**
     * 代理返点期数
     */
    public static final String AGENT_REBATE_PERIODS = "agent_rebate_periods";

    /**
     * 代理活跃，有效活跃配置
     */
    public static final String /**/AGENT_ACTIVE_CONFIG = "agent_active_config";

    /**
     * 充值活跃
     */
    public static final String ACTIVE_DEPOSIT = "active_deposit";

    /**
     * 充值投注
     */
    public static final String ACTIVE_BET = "active_bet";

    /**
     * 充值有效活跃
     */
    public static final String VALID_ACTIVE_DEPOSIT = "valid_active_deposit";

    /**
     * 有效活跃投注
     */
    public static final String VALID_ACTIVE_BET = "valid_active_bet";

    /**
     * 代理返点状态
     */
    public static final String AGENT_REBATE_STATUS = "agent_rebate_status";

    /**
     * 代理密保问题配置状态
     */
    public static final String AGENT_SECURITY_QUESTION_CONFIG_STATUS = "agent_security_question_config_status";

    public static final String DEPOSIT_PAYMENT_METHOD = "deposit_payment_method";


    /**
     * 代理充提数字币协议
     */
    public static final String AGENT_DEPOSIT_WITHDRAWAL_PROTOCOL = "agent_deposit_withdrawal_protocol";

    /**
     * 取款记录时间
     */
    public static final String AGENT_WITHDRAWAL_RECORD_TIME = "agent_withdrawal_record_time";

    /**
     * 异常类型
     */
    public static final String ABNORMAL_TYPE = "abnormal_type";

    public static final String MEDAL_NAME = "medal_name";

    public static final String MEDAL_OPERATION = "medal_operation";

    public static final String REWARD_CONFIG = "rewardConfig";

    public static final String MEDAL_DESC = "medal_desc";

    public static final String COIN_CODE = "COIN_CODE";

    /**
     * 订单状态 system_param deposit_withdraw_status
     */
    public static final String DEPOSIT_WITHDRAW_STATUS = "deposit_withdraw_status";
    /**
     * 代理提款审核记录-条件查询-时间类型
     */
    public static final String AUDIT_TIME_TYPE = "audit_time_type";
    /**
     * 某个审核节点用,审核状态
     */
    public static final String AUDIT_STATUS = "audit_status";
    /**
     * todo 存款取款都有时间类型筛选,这俩时间不一样,分两个类型做配置
     * 代理存款审核记录-条件查询-时间类型
     */
    public static final String APPLY_TIME_TYPE = "apply_time_type";
    /**
     * 会员提款审核操作
     */
    public static final String USER_WITHDRAW_REVIEW_OPERATION = "user_withdraw_review_operation";

    /**
     * 存取款订单状态
     */
    public static final String DEPOSIT_WITHDRAW_CHANNEL = "deposit_withdraw_channel";

    /**
     * 客户端状态
     */
    public static final String DEPOSIT_WITHDRAW_CUSTOMER_STATUS = "deposit_withdraw_customer_status";

    /**
     * 三方消息状态
     */
    public static final String DEPOSIT_WITHDRAW_PAY_PROCESS_STATUS = "deposit_withdraw_pay_process_status";


    public static final String AGENT_STATUS = "agent_status";

    public static final String REGISTER_WAY = "register_way";

    //会员福利
    public static final String AGENT_USER_BENEFIT = "agent_user_benefit";

    /*区域限制类型 ip/ 国家*/
    public static final String AREA_LIMIT_TYPE = "area_limit_type";
    /*开关状态*/
    public static final String SWITCH_STATUS = "switch_status";
    /*维护终端*/
    public static final String MAINTENANCE_TERMINAL = "maintenance_terminal";

    /**
     * 代理PC，H5  账变明细显示账变类型
     */
    public static final String AGENT_CUSTOMER_SHOW_TYPE = "agent_customer_show_type";
    /**
     * 代理PC，H5  账变明细显示账变状态
     */
    public static final String AGENT_COIN_TYPE_STATUS = "agent_coin_type_status";

    /**
     * 处理状态
     */
    public static final String PROCESS_STATUS = "process_status";

    public static final String USER_LIST = "会员列表";
    public static final String USER_FREE_GAME = "免费旋转配置";

    public static final String USER_REGISTRATION_LIST = "会员注册信息";

    public static final String SPIN_WHEEL_LIST = "转盘抽奖次数获取记录";

    public static final String USER_LOG_HIS_LIST = "会员登录信息";

    public static final String USER_REVIEW_HIS_LIST = "会员审核信息";

    public static final String USER_TRANSFER_AGENT_LIST = "会员转代信息";

    public static final String AGENT_USER_OVER_FLOW_LIST = "会员溢出信息";

    public static final String USER_COIN_RECORD_LIST = "会员账变记录";

    public static final String USER_DEPOSIT_WITHDRAW_REPORT = "会员存取报表";

    public static final String USER_PLAT_RECORD_LIST = "会员平台币账变记录";

    public static final String USER_HOT_WALLET_ADDRESS = "会员链上资金管理";

    public static final String AGENT_HOT_WALLET_ADDRESS = "代理链上资金管理";

    public static final String USER_ACTIVITY_FINANCE_LIST = "会员活动记录";

    public static final String USER_PLAT_TRANSFER_RECORD_LIST = "会员平台币兑换记录";

    public static final String USER_MANUAL_DOWN_RECORD = "会员人工扣除记录";

    public static final String USER_WITHDRAW_RECORD = "会员提款记录";

    public static final String USER_WITHDRAW_SUCCESS_RECORD_RICK = "会员提款重复项查询";

    public static final String USER_WITHDRAW_MANUAL_RECORD = "会员人工提款记录";

    public static final String AGENT_WITHDRAW_MANUAL_RECORD = "代理人工提款记录";

    public static final String USER_WITHDRAW_REVIEW_RECORD = "会员提款审核记录";
    public static final String AGENT_WITHDRAW_REVIEW_RECORD = "代理提款审核记录";

    public static final String USER_MANUAL_UP_RECORD = "会员人工加额记录";
    public static final String USER_MANUAL_UP_REVIEW_RECORD = "会员人工加额审核记录";

    public static final String AGENT_MANUAL_UP_RECORD = "代理人工加额记录";
    public static final String AGENT_MANUAL_UP_REVIEW_RECORD = "代理人工加额审核记录";

    public static final String AGENT_COIN_RECORD = "代理账变记录";


    public static final String USER_DEPOSIT_RECORD = "会员存款记录";

    public static final String AGENT_LIST = "代理列表";
    public static final String AGENT_LOGIN_RECORD = "代理登录信息";

    public static final String AGENT_DEPOSIT_RECORD = "代理存款记录";

    public static final String AEGENT_DEPOSIT_SUBORDINATES_RECORD = "代理代存记录";

    public static final String AGENT_WITHDRAWAL_RECORD = "代理提款记录";

    public static final String AGENT_MANUAL_DOWN_RECORD = "代理人工扣除记录";
    public static final String AGENT_TRANSFER_RECORD = "代理转账记录";
    public static final String AGENT_SHORT_RECORD = "短链接管理";
    public static final String MEMBER_WITHDRAWAL_MANUAL_CONFIRM_RECORD = "会员提款人工确认记录";

    public static final String ACTIVITY_ORDER_RECORD = "活动奖金记录";

    public static final String ACTIVITY_DATA_REPORT = "活动数据报表";

    public static final String AGENT_STATIC_REPORT = "代理报表";

    public static final String TOP_AGENT_STATIC_REPORT = "总代报表";

    public static final String AGENT_MERCHANT_STATIC_REPORT_SITE = "站点商务报表";

    public static final String AGENT_MERCHANT_STATIC_REPORT_BUSINESS = "商务后台商务报表";

    public static final String AGENT_DEPOSIT_WITHDRAW_REPORT = "代理充提报表";

    public static final String RECHARGE_STATIC_DATA_REPORT = "充值渠道报表";

    public static final String WITHDRAW_STATIC_DATA_REPORT = "提现渠道报表";

    public static final String SITE_MEDAL_OPERATOR_RECORD = "勋章变更记录";

    public static final String MEDAL_ACQUIRE_RECORD = "会员获勋记录";

    public static final String MEDAL_REWARD_RECORD = "宝箱奖励记录";

    public static final String VIP_CHANGE_RECORD = "会员等级变更记录";

    public static final String USER_INFORMATION_CHANGE_RECORD = "会员信息变更记录";

    public static final String ORDER_RECORD = "游戏注单记录";
    public static final String ORDER_ABNORMAL_RECORD = "异常注单记录";
    public static final String USER_LABEL_RECORD = "会员标签变更记录";

    public static final String LABEL_CONFIG_CHANGE_RECORD = "标签配置变更记录";
    public static final String LABEL_CONFIG_RECORD = "标签管理";

    public static final String USER_TASK_RECORD = "任务领取记录";

    public static final String AGENT_REVIEW_RECORD = "代理佣金审核记录";
    public static final String SITE_REPORT = "平台报表";
    public static final String GAME_REPORT_SITE = "游戏报表-站点";
    public static final String GAME_REPORT_VENUE_TYPE = "游戏报表-场馆分类";
    public static final String GAME_REPORT_VENUE = "游戏报表-场馆";
    public static final String GAME_REPORT_GAME = "游戏报表-游戏";
    public static final String GET_USER_WINLOSE_PAGE = "会员输赢";
    public static final String GET_VENUE_WINLOSE_PAGE = "场馆输赢";
    public static final String DAILY_WIN_LOSE = "每日输赢";

    public static final String REPORT_TASK_ORDER_RECORD = "任务报表";
    public static final String REPORT_VIP_DATA = "VIP数据报表";

    public static final String USER_INFO_STATEMENT = "会员报表";

    public static final String AGENT_COMMISSION_GRANT_RECORD = "代理佣金发放记录";

    public static final String AGENT_MERCHANT_COMMISSION_GRANT_RECORD = "商务代理佣金发放记录";

    public static final String ADMIN_INTEGRATE_RECORD = "总台综合报表";
    public static final String SITE_INTEGRATE_RECORD = "站点综合报表";
    public static final String SITE_INVITE_RECORD = "邀请好友记录";
    public static final String AGENT_COMMISSION_RECORD_DETAIL = "SUB_AGENT_COMM_DETAIL";
    public static final String DOWNLOAD_LIMIT = "DOWNLOAD_LIMIT";
    public static final String PLATFORM_REPLY = "LOOKUP_PLATFORM_REPLY";

    public static final String ADMIN_NAME = "SuperAdmin";

    public static final String IP_TOP10_RECORD = "来路分析报表";

    public static final String NON_REBATE_CONFIG = "不返水游戏配置";

    public static final String USER_REBATE_RECORD = "返水记录";

    public static final String SITE_REBATE_RECORD = "返水报表";

    public static final String SITE_SECURITY_ADJUST_REVIEW_LOG_EXPORT_RECORD = "站点保证金调整审核记录报表";


    /**
     * 推荐标识
     */
    public static final String RECOMMEND = "recommend";

    /**
     * 站点类型
     */
    public static final String SITE_TYPE = "site_type";

    /**
     * 站点模式
     */
    public static final String SITE_MODEL = "site_model";

    /**
     * 抽成方案
     */
    public static final String COMMISSION_PLAN = "commission_plan";

    /**
     * 语言
     */
    public static final String LANGUAGE_TYPE = "language_type";

    public static final String WITHDRAW_TYPE = "withdraw_type";

    public static final String RECHARGE_TYPE = "recharge_type";
    /**
     * vip变更类型
     */
    public static final String VIP_LEVEL_CHANGE_TYPE = "vip_level_change_type";

    /**
     * 客服类型
     */
    public static final String CUSTOMER_TYPE = "customer_type";

    /**
     * 站点域名类型
     */
    public static final String SITE_DOMAIN_TYPE = "site_domain_type";
    /**
     * 活动有效期类型
     */
    public static final String ACTIVITY_PRESCRIPTION_TYPE = "activity_prescription_type";
    /**
     * 用户范围类型
     */
    public static final String ACTIVITY_USER_RANGE = "activity_user_range";
    /**
     * 代理标签-操作类型
     */
    public static final String AGENT_LABEL_OPERATION_TYPE = "agent_label_operation_type";


    public static final String DEFAULT_GOOGLE_AUTH_KEY = "CB7EMCSISOTQNX7E";

    public static final String CURRENCY_TYPE = "currency_type";
    public static final String TIME_ZONE = "time_zone";

    public static final String PLATFORM_CLASS_STATUS_TYPE = "platform_class_status_type";

    public static final String VENUE_JOIN_TYPE = "venue_join_type";

    public static final String SPORT_RECOMMEND_STATUS = "sport_recommend_status";
    /**
     * 前端展示状态 1显示 0 隐藏
     */
    public static final String FRONT_END_SHOW_STATUS = "front_end_show_status";

    /**
     * 总站siteCode 默认是0
     */
    public static final String ADMIN_CENTER_SITE_CODE = "0";

    /**
     * 通道名称
     */
    public static final String PAY_CHANNEL_NAME = "pay_channel_name";
    /**
     * 站点存款通道类型
     */
    public static final String CHANNEL_TYPE = "CHANNEL_TYPE";
    /**
     * 活动发放过期时间
     */
    public static final String ACTIVITY_EXPIRY_TIME = "activity_expiry_time";
    /**
     * 转盘活动奖励次数来源-转盘活动配置
     */
    public static final String ACTIVITY_PRIZE_SOURCE = "activity_prize_source";
    /**
     * 转盘活动奖励次数来源-来源展示
     */
    public static final String ACTIVITY_PRIZE_SOURCE_SHOW = "activity_prize_source_show";
    /**
     * 转盘奖励实物
     */
    public static final String ACTIVITY_REWARD_TYPE = "activity_reward_type";

    /**
     * 任务类型
     */
    public static final String TASK_TYPE = "task_type";

    /**
     * 任务类型
     */
    public static final String SUB_TASK_TYPE = "sub_task_type";

    /**
     * 转盘奖品等级国际化
     */
    public static final String VIP_RANK_SPIN_WHEEL_PRIZE = "activity_reward_rank";

    /**
     * 任务领取状态
     */
    public static final String TASK_RECEIVE_STATUS = "task_receive_status";

    /**
     * 汇率调整方式
     */
    public static final String EXCHANGE_RATE_ADJUST_WAY = "exchange_rate_adjust_way";

    public static final String EXCHANGE_RATE_SHOW_WAY = "exchange_rate_show_way";

    /**
     * 游戏标签
     */
    public static final String GAME_LABEL = "game_label";


    /**
     * 角标
     */
    public static final String CORNER_LABELS = "corner_labels";


    /**
     * 领取状态
     */
    public static final String ACTIVITY_RECEIVE_STATUS = "activity_receive_status";

    /**
     * 领取状态 wade 默认是24 小时
     */
    public static final String NOVICE_TASK_EXPIRE = "novice_task_expire";
    /**
     * 会员资金调整类型-加额枚举
     */
    public static final String MANUAL_ADJUST_TYPE = "manual_adjust_type";
    /**
     * 会员资金调整类型-减额枚举
     */
    public static final String MANUAL_ADJUST_DOWN_TYPE = "manual_adjust_down_type";
    /**
     * 会员/代理资金变更种类
     */
    public static final String MANUAL_ADJUST_WAY = "manual_adjust_way";

    /**
     * 代理资金调整类型-加额枚举
     */
    public static final String AGENT_MANUAL_ADJUST_TYPE = "agent_manual_adjust_type";
    /**
     * 代理资金调整类型-减额枚举
     */
    public static final String AGENT_MANUAL_ADJUST_DOWN_TYPE = "agent_manual_adjust_down_type";
    /**
     * 转盘活动每位会员可领取次数上限, 0是全部，1 按VIP等级限制会员领取次数
     */
    public static final String ACTIVITY_LIMIT_TYPE = "activity_limit_type";

    /**
     * 会员类型
     */
    public static final String ACTIVITY_USER_TYPE = "activity_user_type";

    /**
     * 会员类型
     */
    public static final String WITHDRAW_COLLECT = "withdraw_collect";


    /**
     * 转账记录-账号类型
     */
    public static final String OWNER_USER_TYPE = "owner_user_type";

    /**
     * 转账记录-转账方向
     */
    public static final String DIRECTION = "direction";

    /**
     * 转账记录-钱包类型
     */
    public static final String TRANSFER_WALLET_TYPE = "transfer_wallet_type";

    /**
     * 视讯玩法类型
     */
    public static final String SH_PLAY_TYPE = "sh_play_type";
    /**
     * 视讯玩法类型
     */
    public static final String EVO_PLAY_TYPE = "evo_play_type";

    /**
     * 视讯游戏大类
     */
    public static final String SH_GAME_TYPE = "sh_game_type";

    /**
     * EVO游戏大类
     */
    public static final String EVO_GAME_TYPE = "evo_game_type";


    /**
     * SA-视讯玩法类型
     */
    public static final String SA_GAME_TYPE = "sa_game_type";


    /**
     * SA-视讯桌子名称
     */
    public static final String SA_GAME_CODE = "sa_game_table_number_name";

    /**
     * SA-视讯下注 类型 %s=游戏大类
     */
    public static final String SA_BET_TYPE = "sa_%s_bet_type";



    /**
     * SA-视讯结果集 游戏类型 + sa_game_detail 获取到游戏类型说明集合
     */
    public static final String SA_GAME_DETAIL = "sa_game_detail";


    /**
     * sexy真人游戏大类
     */
    public static final String SEXY_GAME_TYPE = "sexy_game_type";


    /**
     * 彩票游戏名称
     */
    public static final String LT_GAME_TYPE = "acelt_game_info";

    /**
     * 电竞游戏大类
     */
    public static final String ES_GAME_TYPE = "es_game_type";

    /**
     * 斗鸡报表游戏名称 i18ncode
     */
    public static final String CF_GAME_TYPE = "LOOKUP_CF_GAME_TYPE_1";

    /**
     * 视讯开局结果
     */
    public static final String SH_BET_RESULT = "sh_bet_result";

    /**
     * FTG开局结果
     */
    public static final String FTG_ORDER_STATUS = "ftg_order_status";

    /**
     * FTG 游戏类型
     */
    public static final String FTG_GAME_TYPE = "ftg_game_type";


    /**
     * 彩票-下注类型
     */
    public static final String ACELT_BET_TYPE = "acelt_bet_type";


    /**
     * 彩票-玩法类型
     */
    public static final String ACELT_PLAY_TYPE = "acelt_play_type";

    /**
     * 沙巴下注类型
     */
    public static final String SBA_BET_TYPE = "sba_bet_type";
    /**
     * 站点banner-轮播图区域
     */
    public static final String BANNER_AREA = "banner_area";
    /**
     * 站点banner-时效
     */
    public static final String BANNER_DURATION = "banner_duration";
    /**
     * 站点banner-跳转目标
     */
    public static final String BANNER_LINK_TARGET = "banner_link_target";
    /**
     * 站点banner-首页顶部枚举
     */
    public static final String HOME_PAGE_TOP = "home_page_top";

    /**
     * 沙巴体育大类
     */
    public static final String SBA_SPORT_TYPE = "sba_sport_type";

    /**
     * 版本管理-更新状态
     */
    public static final String VERSION_UPDATE_STATUS = "version_update_status";
    /**
     * 版本管理-平台类型
     */
    public static final String VERSION_MOBILE_PLATFORM = "version_mobile_platform";
    /**
     * app-福利中心-奖励类型
     */
    public static final String WELFARE_CENTER_REWARD_TYPE = "welfare_center_reward_type";
    /**
     * 条件范围-近几天-时间类型
     */
    public static final String DATE_NUM_LABEL = "date_num_label";

    /**
     * 交易记录-交易类型
     */
    public static final String TRADE_TYPE = "trade_type";


    /**
     * 交易记录-交易类型
     */
    public static final String TRADE_WAY_TYPE = "trade_way_type";

    /**
     * 交易记录-日期
     */
    public static final String TRADE_DATE_NUM = "trade_date_num";

    /**
     * 反馈问题类型
     */
    public static final String FEEDBACK_QUESTION_TYPE = "feedback_question_type";


    /**
     * 一级分类类型
     */
    public static final String GAME_ONE_TYPE = "game_one_type";

    /**
     * 站点初始化,勋章日,周,月job描述
     */
    public static final String MEDAL_DAY_JOB_DESC = "勋章每日-满足条件job";
    public static final String MEDAL_WEAK_JOB_DESC = "勋章每周-满足条件job";
    public static final String MEDAL_MONTH_JOB_DESC = "勋章每月-满足条件job";

    /**
     * 流水类型
     */
    public static final String TYPING_ADJUST_TYPE = "typing_adjust_type";
    /**
     * 流水增减类型
     */
    public static final String TYPING_BALANCE_TYPE = "typing_balance_type";

    /**
     * 站点代办
     */
    public static final String SITE_DODO = "site_dodo";

    public static final String COMMISSION_TYPE = "commission_type";

    public static final String SETTLE_CYCLE = "settle_cycle";

    public static final String COMMISSION_REVIEW_STATUS = "commission_review_status";

    public static final String COMMISSION_OPERATION = "commission_operation";

    public static final String USER_VIP_AWARD_LIST = "VIP奖励记录";
    public static final String VIP_RECEIVE_TYPE = "vip_receive_type";

    /**
     * 账变业务类型
     */
    public static final String PLATFORM_BUSINESS_COIN_TYPE = "paltform_business_coin_type";

    /**
     * 账变类型
     */
    public static final String PLATFORM_COIN_TYPE = "paltform_coin_type";


    /**
     * 账变收支类型
     */
    public static final String PLATFORM_COIN_BALANCE_TYPE = "paltform_coin_balance_type";

    /**
     * VIP段位变更记录
     */
    public static final String VIP_RANK_OPERATION = "VIP段位变更记录";


    /**
     * 代理-会员红利-红利类型
     */
    public static final String DIVIDEND_TYPE = "user_dividend_type";

    /**
     * 代理-会员红利-红利状态
     */
    public static final String DIVIDEND_STATUS = "dividend_status";
    /**
     * 帮助中心-多语言
     */
    public static final String HELP_CENTER_OPTION = "help_center_option";

    /**
     * 投诉邮箱
     */
    public static final String COMPLAINT_EMAIL = "complaint_email";
    /**
     * 客服邮箱
     */
    public static final String CUSTOMER_SERVICE_EMAIL = "customer_service_email";
    /**
     * iso下载地址
     */
    public static final String IOS_DOWNLOAD_URL = "ios_download_url";
    /**
     * android下载地址
     */
    public static final String ANDROID_DOWNLOAD_URL = "android_download_url";

    /**
     * pc未登录图
     */
    public static final String UN_LOGIN_PC_URL = "un_login_pc_url";
    /**
     * h5未登录图
     */
    public static final String UN_LOGIN_H5_URL = "un_login_h5_url";

    /**
     * android下载地址
     */
    public static final String USER_INFO_QUERY_TYPE = "user_info_query_type";
    /**
     * 代理提款 银行卡 表头 多语言
     */
    public static final String BANK_CARD_NAME = "bank_card_name";
    /**
     * 代理提款 银行名称 表头 多语言
     */
    public static final String BANK_NAME = "bank_name";
    /**
     * 代理提款 持卡人姓名 表头 多语言
     */
    public static final String CARD_HOLDER_NAME = "card_holder_name";
    /**
     * 代理提款 虚拟币币种
     */
    public static final String CRYPTOCURRENCY_TYPE = "cryptocurrency_type";
    /**
     * 链协议
     */
    public static final String BLOCKCHAIN_PROTOCOL = "blockchain_protocol";
    /**
     * 电子钱包账户
     */
    public static final String DIGITAL_WALLET_ACCOUNT = "digital_wallet_account";
    /**
     * 电子钱包名称
     */
    public static final String DIGITAL_WALLET_NAME = "digital_wallet_name";

    /**
     * app闪屏时效
     */
    public static final String VALIDITY_PERIOD = "validity_period";

    public static final String RISK_TYPE = "risk_type";

    /**
     * 字典分类
     */
    public static final String DICT_CONFIG_CATEGORY = "dict_config_category";

    public static final String HOT_REM_TYPE = "hot_rem_type";

    /**
     * 活跃非活跃状态
     */
    public static final String ACTIVE_TYPE = "active_type";


    /**
     * 转账状态
     */
    public static final String TRANSFER_STATUS = "transfer_status";
    /**
     * 推广管理-域名管理-域名变更操作类型
     */
    public static final String RECORD_DOMAIN = "record_domain";


    public static final String SUPER_ADMIN = "superadmin";
    /**
     * 商务信息变更审核-审核类型
     */
    public static final String MERCHANT_MODIFY_TYPE = "merchant_modify_type";
    /**
     * 手续费类型
     */
    public static final String FEE_TYPE = "fee_type";

    public static final String USER_TYPING_RECORD_LIST = "会员流水变动记录";

    public static final String USER_ACTIVITY_TYPING_RECORD = "存款活动流水变动记录";
    public static final String SITE_STATUS = "site_status";

    public static final String SMS_STATISTIC_RECORD = "短信统计报表";

    public static final String EMAIL_STATISTIC_RECORD = "邮箱统计报表";

    public static final String AGENT_LEVEL_LOOKUP = "agent_level";

    /**
     * 平台币上下分调整方式
     */
    public static final String PLATFORM_COIN_MANUAL_ADJUST_WAY = "platform_coin_manual_adjust_way";

    /**
     * 平台币上分调整类型
     */
    public static final String PLATFORM_COIN_MANUAL_ADJUST_UP_TYPE = "platform_coin_manual_adjust_up_type";
    /**
     * 平台币下分调整类型
     */
    public static final String PLATFORM_COIN_MANUAL_ADJUST_DOWN_TYPE = "platform_coin_manual_adjust_down_type";

    /**
     *平台币上下分审核状态
     */
    public static final String PLATFORM_COIN_REVIEW_STATUS = "platform_coin_review_status";

    public static final String USER_PLATFORM_COIN_MANUAL_UP_REVIEW_RECORD = "会员平台币上分审核记录";

    public static final String USER_PLATFORM_COIN_MANUAL_UP_RECORD = "会员平台币上分记录";
    public static final String USER_PLATFORM_COIN_MANUAL_DOWN_RECORD = "会员平台币下分记录";


    public static final String SITE_SECURITY_REVIEW = "site_security_review";

    public static final String COMMISSION_OPERATION_STATUS = "commission_operation";

    /**
     * 保证金账户状态
     */
    public static final String SECURITY_ACCOUNT_STATUS = "security_account_status";

    /**
     * 保证金管理状态
     */
    public static final String SECURITY_STATUS = "security_status";



    /**
     * 保证金业务类型
     */
    public static final String SECURITY_SOURCE_COIN_TYPE = "security_source_coin_type";

    /**
     * 保证金帐变类型
     */
    public static final String SECURITY_COIN_TYPE = "security_coin_type";


    /**
     * 保证金 会员类型
     */
    public static final String SECURITY_USER_TYPE = "security_user_type";

    /**
     * 保证金 收入类型
     */
    public static final String SECURITY_AMOUNT_DIRECT = "security_amount_direct";


    /**
     * 保证金类型
     */
    public static final String SITE_SECURITY_BALANCE_ACCOUNT = "site_security_balance_account";


    /**
     * 保证金透支业务类型
     */
    public static final String SECURITY_OVERDRAW_SOURCE_COIN_TYPE = "security_overdraw_source_coin_type";

    /**
     * 保证金透支帐变类型
     */
    public static final String SECURITY_OVERDRAW_COIN_TYPE = "security_overdraw_coin_type";
    /**
     * 保证金报表
     */
    public static final String SITE_SECURITY_BALANCE_REPORT = "保证金报表";


    /**
     * 保证金报表
     */
    public static final String SITE_SECURITY_CHANGE_LOG_REPORT = "保证金帐变记录报表";

    public static final String SITE_SECURITY_OVERDRAW_LOG_REPORT = "保证金透支额度帐变记录报表";


    public static final String USER_MANUAL_DEPOSIT_REVIEW_RECORD = "会员人工存款审核记录";

    public static final String AGENT_MANUAL_DEPOSIT_REVIEW_RECORD = "代理人工存款审核记录";
    /**
     * 盘口模式 0海外派 1大陆盘
     */
    public static final String COMMISSION_HANDICAP_MODE = "commission_handicap_mode";


    /**
     * 一级分类模板
     */
    public static final String GAME_MODEL = "game_model";
}
