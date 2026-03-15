package com.cloud.baowang.common.core.constants;

public class ConstantsCode {

    /*=====================系统异常定义   1xxxx=====================*/


    /**
     * 成功
     */
    public final static String SUCCESS = "PROMPT_10000";

    /**
     * 服务内部错误
     */
    public final static String SERVER_INTERNAL_ERROR = "PROMPT_10001";

    /**
     * 参数错误
     */
    public final static String PARAM_ERROR = "PROMPT_10002";

    /**
     * 系统异常
     */
    public final static String SYSTEM_ERROR = "PROMPT_10003";

    /**
     * 参数无效
     */
    public final static String PARAM_NOT_VALID = "PROMPT_10004";

    /**
     * 导出异常
     */
    public final static String EXPORT_ERROR = "PROMPT_10005";

    /**
     * 令牌为空
     */
    public final static String TOKEN_MISSION = "PROMPT_10006";

    /**
     * 令牌错误
     */
    public final static String TOKEN_INVALID = "PROMPT_10007";

    /**
     * 登陆过期
     */
    public final static String LOGIN_EXPIRE = "PROMPT_10008";

    /**
     * 签名为空
     */
    public final static String SIGN_EMPTY = "PROMPT_10009";

    /**
     * 签名异常
     */
    public final static String SIGN_ERROR = "PROMPT_10010";

    /**
     * 签名过期
     */
    public final static String SIGN_EXPIRED = "PROMPT_10011";

    /**
     * 无数据
     */
    public final static String NO_HAVE_DATA = "PROMPT_10012";

    /**
     * 数据不存在
     */
    public final static String DATA_NOT_EXIST = "PROMPT_10013";

    /**
     * 无API访问权限
     */
    public final static String NOT_API_PERMISSIONS = "PROMPT_10014";
    /**
     * 请求过于频繁已被限流
     */
    public final static String CURRENT_LIMIT = "PROMPT_10015";

    /**
     * 该请求已被限流
     */
    public final static String CURRENT_REQUEST_LIMIT = "PROMPT_10016";

    /**
     * 已触发熔断规则
     */
    public final static String HAS_BLOWN = "PROMPT_10017";

    /**
     * 已触发系统保护规则
     */
    public final static String SYSTEM_PROTECTION = "PROMPT_10018";

    /**
     * 未捕捉异常
     */
    public final static String UNCAUGHT_EXCEPTION = "PROMPT_10020";
    /**
     * 系统锁异常
     */
    public final static String SYSTEM_LOCK_ERROR = "PROMPT_10021";

    /**
     * 游戏平台不存在
     */
    public final static String NOT_FUND_PLATFORM = "PROMPT_10022";

    /**
     * 长度过长
     */
    public final static String MAX_LENGTH = "PROMPT_10023";

    /**
     * 缺少参数
     */
    public final static String MISSING_PARAMETERS = "PROMPT_10024";
    /*操作过于频繁，请稍后再试*/
    public static final String ONE_KEY_RECYCLING = "PROMPT_10025";

    //币种不支持
    public static final String VENUE_CURRENCY_NOT = "PROMPT_10026";

    public static final String UPDATE_ERROR = "PROMPT_10027";

    public static final String INSERT_ERROR = "PROMPT_10028";

    public static final String DATA_EXISTS_MORE = "PROMPT_10029";
    /*服务维护*/
    public static final String SERVER_MAINTENANCE = "PROMPT_10030";
    /*区域限制*/
    public static final String AREA_LIMIT = "PROMPT_10031";
    /*IP不存在*/
    public static final String IP_NO_EXIST = "PROMPT_10032";
    /*未找到路由*/
    public static final String ROUTE_NOT_FOUND = "PROMPT_10033";

    public static final String DATA_IS_EXIST = "PROMPT_10034";
    /*已启用不可编辑*/
    public static final String ENABLED_NOT_EDITABLE = "PROMPT_10035";
    /*Referer不能为空*/
    public static final String REFERER_EMPTY = "PROMPT_10036";

    public static final String OPEN_STATUS = "PROMPT_10037";

    public static final String LANG_NOT_FULL = "PROMPT_10038";

    public static final String XXL_JOB_API_ERROR = "PROMPT_10039";

    public static final String TIME_NOT_GOOD = "PROMPT_10040";

    public static final String SITECODE_IS_ERROR = "PROMPT_10041";

    public static final String RECEIVE_FAIL_DESCRIPTION = "PROMPT_10042";

    public static final String START_TIME_NOT_GOOD = "PROMPT_10043";
    /*未获取到分布式锁*/
    public static final String DISTRIBUTED_LOCK_NOT_OBTAINED = "PROMPT_10044";

    public static final String DELETE_GAME_ERROR_TWO = "PROMPT_10045";

    public static final String DELETE_OPEN_ERROR = "PROMPT_10046";

    public static final String NULL_PARAMETERS = "PROMPT_10047";

    public static final String AMOUNT_GREATER_ZERO = "PROMPT_10048";

    /*===================== user服务异常定义   2xxxx=====================*/
    /**
     * 标签名字不能重复
     */
    public final static String LABEL_ERROR = "PROMPT_20000";

    /**
     * 该标签在使用中，不能删除
     */
    public final static String USER_LABEL_USE = "PROMPT_20001";

    /**
     * 会员详情备注信息查询失败!!!
     */
    public final static String USER_REMARK_QUERY_ERROR = "PROMPT_20002";

    /**
     * 用户不存在
     */
    public final static String USER_NOT_EXIST = "PROMPT_20003";

    /**
     * 会员详情基本信息查询失败!!!
     */
    public final static String USER_BASIC_QUERY_ERROR = "PROMPT_20004";

    /**
     * 会员详情登录信息查询失败!!!
     */
    public final static String USER_INFO_ADD_ERROR = "PROMPT_20005";

    /**
     * 用户不存在，无法编辑信息!!!
     */
    public final static String USER_INFO_LOGIN_QUERY_ERROR = "PROMPT_20006";

    /**
     * 存在待审核数据,无法提交
     */
    public final static String USER_EXISTS_TO_REVIEWED = "PROMPT_20007";

    /**
     * 手机号已绑定
     */
    public final static String PHONE_BOUND = "PROMPT_20008";

    /**
     * 邮箱已被绑定
     */
    public final static String MAIL_BOUND = "PROMPT_20009";

    /**
     * 会员更新标签审核处理中，不能删除该标签
     */
    public final static String USER_LABEL_DEL_ERROR = "PROMPT_20010";

    /**
     * 请对手机号码进行检查
     */
    public final static String PHONE_ERROR = "PROMPT_20011";

    /**
     * 请对邮箱进行检查
     */
    public final static String EMAIL_ERROR = "PROMPT_20012";

    /**
     * 会员账号不能重复
     */
    public final static String USER_ACCOUNT_REPEAT_ERROR = "PROMPT_20013";

    /**
     * 请对登录密码进行检查
     */
    public final static String USER_PASSWORD_ERROR = "PROMPT_20014";

    /**
     * 测试账号类型不能有上级代理
     */
    public final static String SUPER_AGENT_ACCOUNT_NO_HAVE = "PROMPT_20015";

    /**
     * VIP等级配置保存错误!!!
     */
    public final static String VIP_RANK_SAVE_ERROR = "PROMPT_20016";

    /**
     * VIP等级配置查询错误!!!
     */
    public final static String VIP_RANK_QUERY_ERROR = "PROMPT_20017";

    /**
     * VIP等级配置恢复上次设置错误!!!
     */
    public final static String VIP_RANK_QUERY_LAST_ERROR = "PROMPT_20018";

    /**
     * 变更记录查询错误!!!
     */
    public final static String VIP_CHANGE_QUERY_ERROR = "PROMPT_20019";

    /**
     * VIP权益配置保存错误!!!
     */
    public final static String VIP_BENEFIT_SAVE_ERROR = "PROMPT_20020";

    /**
     * VIP权益配置查询错误!!!
     */
    public final static String VIP_BENEFIT_QUERY_ERROR = "PROMPT_20021";

    /**
     * VIP权益配置恢复上次设置错误!!!
     */
    public final static String VIP_BENEFIT_QUERY_LAST_ERROR = "PROMPT_20022";

    /**
     * 操作配置查询错误!!!
     */
    public final static String VIP_OPERATION_QUERY_ERROR = "PROMPT_20023";

    /**
     * 累计有效流水设置有误!!!
     */
    public final static String VIP_BET_AMOUNT_UPGRADE_ERROR = "PROMPT_20024";

    /**
     * 会员变更记录查询失败
     */
    public final static String USER_CHANGE = "PROMPT_20025";

    /**
     * 锁单异常
     */
    public final static String USER_REVIEW_LOCK_ERROR = "PROMPT_20026";

    /**
     * 会员登录日志查询失败!!!
     */
    public final static String USER_LOGIN_LOG_QUERY_ERROR = "PROMPT_20027";

    /**
     * 该订单已被其他操作
     */
    public final static String OPERATING_BY_OTHER = "PROMPT_20028";

    /**
     * 订单不存在
     */
    public final static String ORDER_NOT_EXIST = "PROMPT_20029";

    /**
     * 当前用户不能解锁
     */
    public final static String CURRENT_USER_CANT_UNLOCK = "PROMPT_20030";

    /**
     * 当前用户不能操作
     */
    public final static String CURRENT_USER_CANT_OPERATION = "PROMPT_20031";

    /**
     * 审核状态异常
     */
    public final static String REVIEW_STATUS_ERROR = "PROMPT_20032";

    /**
     * 更新失败
     */
    public final static String UPDATE_FAIL = "PROMPT_20033";

    /**
     * 注册失败,请输入有效的电子邮箱
     */
    public final static String REG_MAIL_ERROR = "PROMPT_20034";

    /**
     * 注册失败,请输入有效的手机号码
     */
    public final static String REG_PHONE_ERROR = "PROMPT_20035";

    /**
     * 验证码错误，请重新输入
     */
    public final static String CODE_ERROR = "PROMPT_20036";

    /**
     * 验证码不能为空
     */
    public final static String CODE_IS_EMPTY = "PROMPT_20037";

    /**
     * 电子邮箱已存在,请直接登录
     */
    public final static String MAIL_IS_EXIST = "PROMPT_20038";

    /**
     * 手机号码已存在,请直接登录
     */
    public final static String PHONE_IS_EXIST = "PROMPT_20039";

    /**
     * 登录失败,邮箱或者密码错误
     */
    public final static String MAIL_LOGIN_ERROR = "PROMPT_20040";

    /**
     * 登录失败,手机号码或者密码错误
     */
    public final static String PHONE_LOGIN_ERROR = "PROMPT_20041";

    /**
     * 账号已停用,请联系客服
     */
    public final static String USER_LOGIN_LOCK = "PROMPT_20042";

    /**
     * 电子邮箱不正确
     */
    public final static String MAIL_PASSWORD_ERROR = "PROMPT_20043";

    /**
     * 手机号码不正确
     */
    public final static String PHONE_PASSWORD_ERROR = "PROMPT_20044";

    /**
     * 该邮箱尚未注册,请前往注册
     */
    public final static String MAIL_NOT_REG = "PROMPT_20045";

    /**
     * 密码为6-16位数字+字母组合
     */
    public final static String PASSWORD_ERROR = "PROMPT_20046";

    /**
     * 两次密码输入不一致
     */
    public final static String PASSWORD_CONFIRM_ERROR = "PROMPT_20047";

    /**
     * 密码设置失败
     */
    public final static String PASSWORD_SET_ERROR = "PROMPT_20048";

    /**
     * 密码设置成功
     */
    public final static String PASSWORD_SET_SUCCESS = "PROMPT_20049";

    /**
     * 该手机号未注册,请前往注册
     */
    public final static String PHONE_NOT_REG = "PROMPT_20050";

    /**
     * 该标签不能修改与删除，属于定制化标签
     */
    public final static String LABEL_EDIT_ERROR = "PROMPT_20051";

    /**
     * 手机号是空
     */
    public final static String PHONE_IS_NULL = "PROMPT_20052";

    /**
     * 会员账号不能为空
     */
    public final static String ACCOUNT_NOT_NULL = "PROMPT_20053";
    public final static String SUPER_AGENT_ACCOUNT_NOT_EXIST = "PROMPT_20054";
    public final static String TYPE_NOT_SAME = "PROMPT_20055";

    public final static String QUERY_BUSY = "PROMPT_20056";

    public final static String REG_SUCCESS = "PROMPT_20057";

    public final static String ADD_SUCCESS = "PROMPT_20058";
    public final static String OLD_PASSWORD_ERROR = "PROMPT_20059";
    public final static String PASSWORD_SAME = "PROMPT_20060";

    public final static String REGISTER_IP_LIMIT = "PROMPT_20061";

    public final static String REGISTER_DEVICE_LIMIT = "PROMPT_20062";

    public final static String LOGIN_IP_LIMIT = "PROMPT_20063";

    public final static String LOGIN_DEVICE_LIMIT = "PROMPT_20064";
    public final static String SPECIFLE_MEMBER_DISPLAY = "PROMPT_20065";

    public final static String WITHDRAW = "PROMPT_20066";
    public final static String INSERT_NOTIFY = "PROMPT_20067";
    public final static String NOTIFY_LIST = "PROMPT_20068";
    public final static String USER_NOTICE_PARAM_ERROR = "PROMPT_20069";
    public final static String TERMINAL_NOT_NULL = "PROMPT_20070";

    public final static String USER_NOTICE_IS_NULL = "PROMPT_20071";

    public final static String INSERT_SYS_NOTICE_NOTIFY = "PROMPT_20072";

    public final static String USER_NOTICE_IS_REVOCATION = "PROMPT_20073";

    public final static String USER_NOTICE_IS_NOT_EXIST = "PROMPT_20074";

    public final static String USER_NOTICE_TYPE_IS_NULL = "PROMPT_20075";

    public final static String USER_NOTICE_DEVICE_TERMINAL_IS_NULL = "PROMPT_20076";

    public final static String USER_NOTICE_ADD_IS_FAIL = "PROMPT_20077";

    public final static String USER_NOTICE_IS_READED = "PROMPT_20078";

    public final static String USER_NOTICE_UPDATE_IS_FAIL = "PROMPT_20079";
    public final static String USER_ACCOUNT_NOT_EXIST_ = "PROMPT_20080";
    public final static String AGENT_USER_ACCOUNT_NOT_EXIST = "PROMPT_20081";
    public final static String AGENT_USER_NOT_SUBORDINATES = "PROMPT_20082";

    public final static String AGENT_ACCOUNT_ABNORMAL = "PROMPT_20083";
    public final static String ACTIVITY_BET_IS_ERROR_DESC = "PROMPT_20084";
    public final static String AGENT_PAY_PASSWORD_NOT_SET = "PROMPT_20085";
    public final static String AGENT_PAY_PASSWORD_ERROR = "PROMPT_20086";

    public final static String AGENT_IS_ARREARS = "PROMPT_20087";

    public final static String AGENT_COIN_AMOUNT_NOT_ENOUGH = "PROMPT_20088";


    public static final String SUBORDINATE_USER_LIST_2MONTH = "PROMPT_20089";

    public static final String USER_NOTICE_OF_TARGET_TYPE_IS_ERROR = "PROMPT_20090";

    public static final String USER_REPEAT_NICK_ERROR = "PROMPT_20091";

    public static final String ACTIVITY_BET_IS_NULL_DESC = "PROMPT_20092";

    public static final String ACCOUNT_IS_EXIST = "PROMPT_20093";

    public static final String ACCOUNT_ERROR = "PROMPT_20094";

    public static final String SELECT_RIGHT_EMAIL = "PROMPT_20095";

    public static final String SELECT_RIGHT_PHONE = "PROMPT_20096";

    public static final String EMAIL_NOT_EXIST = "PROMPT_20097";

    public static final String PHONE_NOT_EXIST = "PROMPT_20098";

    public static final String AREA_EMPTY = "PROMPT_20099";

    public static final String WITHDRAW_PASSWORD_ERROR = "PROMPT_20100";

    public static final String CATEGORY_NAME_EXIST = "PROMPT_20101";


    public static final String SITE_CODE_NOT_EXIST = "PROMPT_20102";

    public static final String SITE_STATUS_NOT_EXIST = "PROMPT_20103";

    public static final String PHONE_NOT_NULL = "PROMPT_20104";
    public static final String AREA_CODE_NOT_NULL = "PROMPT_20105";
    public static final String TRADE_PASSWORD_SAME = "PROMPT_20106";
    public static final String TWICE_PASSWORD_NOT_SAME = "PROMPT_20107";
    public static final String CHANGE_SUCCESS = "PROMPT_20108";
    public static final String MEDAL_REPEAT_ERROR = "PROMPT_20109";
    public static final String REWARD_NOT_NUll_ERROR = "PROMPT_20110";
    public static final String INPUT_INVALID_ERROR = "PROMPT_20111";
    public static final String MEDAL_AMOUNT_ZERO_ERROR = "PROMPT_20112";
    public static final String BIND_SUCCESS = "PROMPT_20113";
    public static final String NEW_EMAIL_SAME = "PROMPT_20114";
    public static final String NEW_PHONE_SAME = "PROMPT_20115";
    public static final String LOGIN_SUCCESS = "PROMPT_20116";
    public static final String MEDAL_HAS_EXISTS = "PROMPT_20117";
    public static final String MEDAL_UNLOCK_NUM_ORDER = "PROMPT_20118";
    public static final String AVATAR_AL_USED = "PROMPT_20119";

    public static final String VIP_GARDE_WRONG = "PROMPT_20120";
    public static final String USER_AL_HAV_AGENT = "PROMPT_20121";
    public static final String USER_TYPE_NOT_EQ_AGENT_TYPE = "PROMPT_20122";
    public static final String USER_NOT_HAV_AGENT = "PROMPT_20123";
    public static final String GAME_ONE_MODEL_ERROR = "PROMPT_20124";
    public static final String REBATE_RATE_ZERO = "PROMPT_20125";
    public static final String AVATAR_MAX_ENABLE_ERROR = "PROMPT_20126";
    public static final String CURRENT_AGENT_EQ_TRAGENT = "PROMPT_20127";
    public static final String AVATAR_NAME_NOT_EXIT = "PROMPT_20128";
    public static final String GAME_ONE_MODEL_DELETE_ERROR = "PROMPT_20129";
    public static final String PHONE_HAS_BINGED = "PROMPT_20130";
    public static final String MAIL_HAS_BINGED = "PROMPT_20131";
    //  QUERY_NOT_SUPPORT_60(20132, ConstantsCode.QUERY_NOT_SUPPORT_60, "仅支持60天数据查询"),
    public static final String QUERY_NOT_SUPPORT_60 = "PROMPT_20132";
    public static final String DOMAIN_NULL = "PROMPT_20133";
    public static final String MAIL_CHANNEL_CLOSE = "PROMPT_20134";
    public static final String CUSTOMER_CHANNEL_CLOSE = "PROMPT_20135";
    public static final String SMS_CHANNEL_CLOSE = "PROMPT_20136";
    public static final String ROBOT_IS_EXISTS="PROMPT_20137";
    public static final String LESS_THAN_30="PROMPT_20138";
    public static final String SET_SUCCESS = "PROMPT_20139";

    public static final String WITHDRAW_NOTIFICATION = "PROMPT_20140";
    public static final String EMAIL_NOT_BIND = "PROMPT_20141";
    public static final String PHONE_NOT_BIND = "PROMPT_20142";
    /*===================== wallet系统异常定义   3xxxx=====================*/
    /**
     * 查询会员银行卡信息失败！！！
     */
    public final static String USER_BANK_CARD_QUERY_ERROR = "PROMPT_30001";

    /**
     * 查询会员虚拟币账号信息失败！！！
     */
    public final static String USER_VIRTUAL_CURRENCY_QUERY_ERROR = "PROMPT_30002";
    /**
     * 调整金额为正数，支持两位小数
     */
    public final static String ADJUST_AMOUNT_INCORRECT = "PROMPT_30008";
    /**
     * 会员账号不存在
     */
    public final static String USER_ACCOUNT_NOT_EXIST = "PROMPT_30009";
    /**
     * 流水倍数最长不超过5个字符，支持两位小数
     */
    public final static String RUNNING_WATER_MULTIPLE_INCORRECT = "PROMPT_30010";
    /**
     * 申请人不能操作
     */
    public final static String APPLICANT_CANNOT_REVIEW = "PROMPT_30011";
    /**
     * 中心钱包加额异常
     */
    public final static String CHANGE_RECORD_ADD_FAIL = "PROMPT_30012";
    /**
     * 更新会员失败6.
     */
    public final static String UPDATE_USER_FAIL = "PROMPT_30013";
    /**
     * 会员信息不能为空
     */
    public final static String USER_INFO_NOT_NULL = "PROMPT_30014";

    /**
     * 审核人和锁单人不一致
     */
    public final static String LOCK_NOT_MATCH_REVIEW = "PROMPT_30015";


    /**
     * 订单被撤销
     */
    public final static String USER_REVIEW_CANCEL = "PROMPT_30020";

    /**
     * 订单状态异常
     */
    public final static String ORDER_STATUS_ERROR = "PROMPT_30021";

    /**
     * 实际到账金额为正整数
     */
    public final static String ARRIVE_AMOUNT_INVALID = "PROMPT_30022";

    /**
     * 实际到账金额最大8位
     */
    public final static String ARRIVE_AMOUNT_INVALID_1 = "PROMPT_30023";


    public static final String MONEY_DEPOSITED = "PROMPT_30024";

    public static final String WITHDRAW_HANDED = "PROMPT_30025";


    /**
     * 提现需要的流水额度不足！！！
     */
    public final static String WITHDRAW_LIMIT = "PROMPT_30003";

    public final static String ADJUST_AMOUNT_NOT_LT_ZREO = "PROMPT_30004";

    public final static String ADJUST_AMOUNT_SCALE_GT_TWO = "PROMPT_30005";

    public final static String ADJUST_AMOUNT_MAX_LENGTH = "PROMPT_30006";

    public final static String WALLET_INSUFFICIENT_BALANCE = "PROMPT_30007";

    public static final String WITHDRAW_FAIL = "PROMPT_30026";

    /**
     * 锁单解锁失败
     */
    public final static String USER_UNLOCK_ERROR = "PROMPT_30027";
    /**
     * 订单未锁定
     */
    public static final String ORDER_NOT_LOCK = "PROMPT_30028";
    /**
     * SECOND_AUDIT_SAME_PEOPLE(30029, ConstantsCode.ORDER_NOT_LOCK,  "当前处理人与一审处理人是同一人"),
     */
    public static final String SECOND_AUDIT_SAME_PEOPLE = "PROMPT_30029";

    /**
     * 添加流水金额需大于0
     */
    public static final String ADD_TYPING_AMOUNT = "PROMPT_30030";
    public static final String ONE_REVIEWER_CANNOT_REVIEW = "PROMPT_30031";
    public static final String ONLY_LOCKER_CAN_REVIEW = "PROMPT_30032";
    public static final String DEPOSIT_RECORD_2MONTH = "PROMPT_30033";

    /**
     * 一个游戏不可以跨多个一级分类
     */
    public static final String GAME_JOIN_TWO_ERROR = "PROMPT_30034";

    /**
     * 交易密码错误
     */
    public static final String USER_WITHDRAW_PASSWORD_ERROR = "PROMPT_30035";

    /**
     * 提现申请失败
     */
    public static final String WITHDRAW_APPLY_FAIL = "PROMPT_30036";


    /**
     * 存在未处理订单
     */
    public static final String USER_FUND_ORDER_EXIST = "PROMPT_30037";

    /**
     * 取款金额需为整数
     */
    public static final String WITHDRAW_AMOUNT_NEED_WHOLE = "PROMPT_30038";

    /**
     * 充提锁定
     */
    public static final String USER_STATUS_PAY_LOCK = "PROMPT_30039";

    /**
     * 提款方式已禁用
     */
    public static final String WITHDRAW_WAY_DISABLE = "PROMPT_30040";


    /**
     * 暂无提款通道
     */
    public static final String NOT_FOUND_WITHDRAW_CHANNEL = "PROMPT_30041";

    /**
     * 提款金额超出范围
     */
    public static final String WITHDRAW_AMOUNT_GT_SCOPE = "PROMPT_30042";

    /**
     * 当前状态不能撤销订单
     */
    public static final String CURRENT_STATUS_NOT_CANCEL = "PROMPT_30043";


    /**
     * 查询热钱包地址失败
     */
    public static final String HOT_WALLET_ADDRESS_FAIL = "PROMPT_30044";

    /**
     * 活动参加手机与邮箱状态码
     */
    public static final String ACTIVITY_PHONE_NOT = "PROMPT_30045";

    /**
     * 活动参加邮箱状态码
     */
    public static final String ACTIVITY_EMAIL_NOT = "PROMPT_30053";

    /**
     * 活动IP校验状态码
     */
    public static final String ACTIVITY_IP_NOT = "PROMPT_30046";


    /**
     * 很抱歉。重复参与
     */
    public static final String ACTIVITY_REPEAT = "PROMPT_30047";

    /**
     * 很抱歉。您不符合参与活动条件
     */
    public static final String ACTIVITY_NOT = "PROMPT_30048";

    /**
     * 您还未存款
     */
    public static final String ACTIVITY_NOT_DEPOSIT = "PROMPT_30049";

    /**
     * 很抱歉。您不符合参与活动条件
     */
    public static final String ACTIVITY_FIRST_DEPOSIT_NOT_SATISFIED = "PROMPT_30050";

    /**
     * 全体会员
     */
    public static final String ACTIVITY_ALL_USER = "PROMPT_30051";

    /**
     * 新注册会员
     */
    public static final String ACTIVITY_NEW_USER = "PROMPT_30052";

    /**
     * 二级分类下存在游戏,不可以删除
     */
    public static final String GAME_JOIN_TWO_CANNOT_BE_DELETED = "PROMPT_30054";


    /**
     * 活动已经结束
     */
    public static final String ACTIVITY_HAS_END = "PROMPT_30055";
    /**
     * 会员账号币种不一致
     */
    public static final String USER_CURRENCY_MISMATCH = "PROMPT_30056";
    /**
     * 加款/扣款，类型为会员活动，没有找到对应活动
     */
    public static final String INVALID_ACTIVITY_ID = "PROMPT_30057";

    /**
     * 存在三笔充值订单
     */
    public static final String EXIST_DEPOSIT_THREE_ORDER = "PROMPT_30058";

    /**
     * 当前账号不支持存款
     */
    public static final String CURRENT_ACCOUNT_NOT_DEPOSIT = "PROMPT_30059";

    /**
     * 暂无可用通道
     */
    public static final String NO_CHANNEL_AVAILABLE = "PROMPT_30060";

    /**
     * 充值金额必须大于0
     */
    public static final String DEPOSIT_AMOUNT_NOT_LE_ZERO = "PROMPT_30061";


    /**
     * 提现类型不存在
     */
    public static final String WITHDRAW_TYPE_NOT_EXISTS = "PROMPT_30062";

    public static final String USER_MAIN_CURRENCY_NOT_NULL = "PROMPT_30063";

    public static final String THIRD_WITHDRAW_FAIL = "PROMPT_30064";
    public static final String DEPOSIT_AMOUNT_AND_BET_AMOUNT = "PROMPT_30065";

    public static final String SPIN_WHEEL_AMOUNT_NOT_AMOUNT = "PROMPT_30066";

    public static final String SPIN_WHEEL_MAX_TIME_TYPE_NOT_AMOUNT = "PROMPT_30067";

    public static final String SPIN_WHEEL_ALL_TIME_TYPE_NOT_NULL = "PROMPT_30068";

    public static final String SPIN_WHEEL_VIP_TIME_TYPE_NOT_NULL = "PROMPT_30069";

    public static final String SPIN_WHEEL_REWARD_NOT_NULL = "PROMPT_30070";
    public static final String SPIN_WHEEL_REWARD_NOT_THREE = "PROMPT_30071";
    public static final String SPIN_WHEEL_REWARD_BRONZE_WRONG = "PROMPT_30072";
    public static final String SPIN_WHEEL_REWARD_SILVER_WRONG = "PROMPT_30073";
    public static final String SPIN_WHEEL_REWARD_GOLD_WRONG = "PROMPT_30074";
    public static final String ACTIVITY_AND_EMAIL_NOT = "PROMPT_30075";

    /**
     * 充值暂停
     */
    public static final String RECHARGE_LIMIT = "PROMPT_30076";
    /*银行code重复*/
    public static final String BANK_CODE_REPEAT = "PROMPT_30077";

    public static final String VENUE_CODE_REPEAT = "PROMPT_30078";




    public static final String INSUFFICIENT_BALANCE = "PROMPT_30080";
    public static final String REPEAT_TRANSACTIONS = "PROMPT_30081";
    public static final String WALLET_NOT_EXIST = "PROMPT_30082";
    public static final String AMOUNT_LESS_ZERO = "PROMPT_30083";
    public static final String TRANSFER_ERROR = "PROMPT_30084";

    public static final String WITHDRAW_ADDRESS_ERROR = "PROMPT_30085";


    public static final String GREATER_MAX_AMOUNT = "PROMPT_30086";

    public static final String LESS_MIN_AMOUNT = "PROMPT_30087";

    public static final String TWO_SIGN_VENUE_ERROR = "PROMPT_30088";

    public static final String TWO_SIGN_VENUE_ONE_GAME_ERROR = "PROMPT_30089";


    public static final String WITHDRAW_TYPE_ERROR= "PROMPT_30090";

    public static final String BANK_NAME_IS_EMPTY = "PROMPT_30091";
    public static final String BANK_CARD_IS_EMPTY = "PROMPT_30092";
    public static final String BANK_CODE_IS_EMPTY = "PROMPT_30093";
    public static final String SURNAME_IS_EMPTY = "PROMPT_30094";
    public static final String USER_NAME_IS_EMPTY = "PROMPT_30095";
    public static final String USER_EMAIL_IS_EMPTY = "PROMPT_30096";
    public static final String USER_PHONE_IS_EMPTY = "PROMPT_30097";
    public static final String PROVINCE_NAME_IS_EMPTY = "PROMPT_30098";
    public static final String  CITY_NAME_IS_EMPTY = "PROMPT_30099";
    public static final String  DETAIL_ADDRESS_IS_EMPTY = "PROMPT_30100";
    public static final String  USER_ACCOUNT_IS_EMPTY = "PROMPT_30101";
    public static final String  NETWORK_TYPE_IS_EMPTY = "PROMPT_30102";
    public static final String  ADDRESS_NO_IS_EMPTY = "PROMPT_30103";

    public static final String  ACTIVITY_IS_NULL_END = "PROMPT_30104";
    public static final String  CHANNEL_CLOSED = "PROMPT_30105";

    public static final String  WITHDRAWAL_RESTRICTIONS = "PROMPT_30106";

    public static final String  WITHDRAW_AMOUNT_NULL = "PROMPT_30107";


    public static final String  CURRENCY_NOT_MATCH = "PROMPT_30108";

    public static final String  WITHDRAW_WAY_NOT_EXIST = "PROMPT_30109";

    public static final String  RECHARGE_WAY_NOT_EXIST = "PROMPT_30110";

    public static final String  AMOUNT_IS_NULL = "PROMPT_30111";

    public static final String  DEPOSIT_USER_NAME_IS_NULL = "PROMPT_30112";

    public static final String  RECHARGE_WAY_DISABLE = "PROMPT_30113";

    public static final String  AREA_CODE_IS_EMPTY = "PROMPT_30114";

    public static final String  AREA_CODE_IS_EXIST = "PROMPT_30115";

    public static final String  BANK_CODE_IS_EXIST = "PROMPT_30116";

    public static final String  SMS_CODE_IS_NULL = "PROMPT_30117";

    public static final String  SMS_CODE_NOT_MATCH = "PROMPT_30118";

    public static final String  WITHDRAW_ARRIVE_AMOUNT_NEED_WHOLE = "PROMPT_30119";

    public static final String  BASE_ERROR_ACTIVITY = "PROMPT_30120";

    public static final String  BASE_ERROR_ACTIVITY_TEMPLATE = "PROMPT_30121";

    public static final String  SYSTEM_CURRENCY_IS_DISABLE = "PROMPT_30122";

    public static final String  RATE_NOT_CONFIG = "PROMPT_30123";

    public static final String  INCORRECT_RANKING_SETTINGS = "PROMPT_30124";

    public static final String  PERCENT_PARAMETER_ABNORMALITY = "PROMPT_30125";
    public static final String  MANUAL_ACTIVITY_TEMPLATE_NOT_EXIT = "PROMPT_30126";
    public static final String  MANUAL_ACTIVITY_ID_NOT_EXIT = "PROMPT_30127";

    public static final String  WITHDRAW_WAY_NONE = "PROMPT_30128";

    public static final String  NO_RECHARGE_CHANNEL_AVAILABLE = "PROMPT_30129";

    public static final String  BANK_CARD_IS_ERROR = "PROMPT_30130";

    public static final String  USER_PHONE_IS_ERROR = "PROMPT_30131";

    public static final String  EXIST_WITHDRAW_HANDING_ORDER = "PROMPT_30132";


    public static final String  LIMIT_TIME_RANGE = "PROMPT_30133";


    public static final String  CURRENCY_FORBID = "PROMPT_30134";
    public static final String  ORDER_NOT_THIRD = "PROMPT_30135";
    public static final String  LIMIT_WITHDRAW = "PROMPT_30136";

    public static final String  ADMIN_CENTER_DISABLE_WAY = "PROMPT_30137";

    public static final String  ADMIN_CENTER_DISABLE_CHANNEL = "PROMPT_30138";


    public static final String  ADMIN_CENTER_ACTIVITY_PARTICIPATION_LIMIT = "PROMPT_30139";

    public static final String  ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH = "PROMPT_30140";
    //
    public static final String GAME_TWO_UP_ERROR = "PROMPT_10049";

    public static final String PLAN_CONFIG_COUNT_ERROR = "PROMPT_10050";
    public static final String FILE_MAX_SIZE_ERROR = "PROMPT_10051";
    public static final String EXPORT_FIELD_NOT_NULL = "PROMPT_10052";
    public static final String OUT_TIME_RANGE = "PROMPT_10053";
    public static final String VENUE_TYPE_REPEAT = "PROMPT_10054";
    public static final String ABNORMAL_BONUS_RATIO = "PROMPT_10055";

    public static final String  AVATOR_NAME_REPEAT = "PROMPT_10058";
    public static final String SERVER_DISABLE="PROMPT_10059";

    public static final String CURRENCY_HAVE_NO_LIMIT="PROMPT_10060";


    public static final String CURRENCY_HAVE_ALREADY_CONFIG="PROMPT_10061";

    public static final String USER_ACCOUNT_HAVE_NO="PROMPT_10062";

    public static final String FILE_ACCOUNT_WRONG_NO="PROMPT_10063";
    public static final String FILE_ACCOUNT_WRONG_LIMIT="PROMPT_10064";
    public static final String FILE_ACCOUNT_WRONG_LIMIT_AMOUNT="PROMPT_10065";

    public static final String FILE_ACCOUNT_WRONG_NO_EXCEPT="PROMPT_10066";

    public static final String CONTACT_CUSTOMER_SERVICE="PROMPT_10067";

    public static final String SITE_IS_CLOSE="PROMPT_10068";

    public static final String ACTIVITY_REDEMPTION_CODE_COMMON="PROMPT_10068";

    public static final String APPLY_SUCCESS="PROMPT_130001";
    public static final String RECEIVE_SUCCESS="PROMPT_130002";

    public static final String OLD_PASSWORD_NULL = "PROMPT_14000";
    public static final String NEW_PASSWORD_NULL = "PROMPT_14001";
    public static final String CONFIRM_PASSWORD_NULL = "PROMPT_14002";
    public static final String USER_PASSWORD_NULL = "PROMPT_14003";
    public static final String PARAM_MISSING = "PROMPT_14004";
    public static final String Google_MISSING = "PROMPT_14005";
    public static final String AGENT_MISSING = "PROMPT_14006";
    public static final String USER_MISSING = "PROMPT_14007";
    public static final String VERIFY_CODE_LIMIT_DAY = "PROMPT_14008";
    public static final String VERIFY_CODE_LIMIT_HOUR = "PROMPT_14009";
    public static final String AREA_CODE_NOT_USE = "PROMPT_14010";
    public static final String LOGIN_ERROR_OTHER_AREA = "PROMPT_14011";
    /**
     * 请缩小搜索范围至92天
     */
    public final static String DATE_MAX_SPAN_92 = "PROMPT_98001";

    public final static String DATE_MAX_SPAN_31 = "PROMPT_98002";

    /**
     * 注册时间未到
     */
    //public static final String ACTIVITY_REGISTER_DAY_NOT_YES = "PROMPT_30050";


    /*===================== play系统异常定义   4xxxx=====================*/
    /**
     * 游戏场馆不存在
     */
    public final static String QUERY_GAME_VENUE_NOT_EXIST = "PROMPT_40001";

    /*===================== system系统异常定义 5xxxx=====================*/
    /**
     * 谷歌身份验证码错误!!!
     */
    public final static String GOOGLE_AUTH_NO_PASS = "PROMPT_50001";

    /**
     * 用户名或密码错误!!!
     */
    public final static String ADMIN_NAME_NOT_EXIST = "PROMPT_50002";

    /**
     * 账号已禁用!!!
     */
    public final static String ACCOUNT_DISABLED = "PROMPT_50003";

    /**
     * 账号已锁定!!!
     */
    public final static String ACCOUNT_LOCK = "PROMPT_50004";

    /**
     * 用户名已存在!!!
     */
    public final static String ADMIN_NAME_IS_EXIST = "PROMPT_50005";

    /**
     * 两次输入密码不一致!!!
     */
    public final static String PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT = "PROMPT_50006";

    /**
     * 先禁用职员?再进行编辑!!!
     */
    public final static String BUSINESS_ADMIN_EDIT_ERROR = "PROMPT_50007";

    /**
     * 超管密码不能重置,请登录超管账号在个人中心进行修改!!!
     */
    public final static String SUPER_ADMIN_PASSWORD_NOT_RESET = "PROMPT_50008";

    /**
     * 两次输入的新密码不一致!!!
     */
    public final static String TWO_PASSWORDS_ENTERED_NOT_MATCH = "PROMPT_50009";

    /**
     * 旧密码不匹配!!!
     */
    public final static String OLD_PASSWORD_NOT_MATCH = "PROMPT_50010";

    /**
     * 角色名称已存在!!!
     */
    public final static String ROLE_NAME_IS_EXIST = "PROMPT_50011";

    /**
     * 菜单KEY已存在!!!
     */
    public final static String MENU_KEY_IS_EXIST = "PROMPT_50012";

    /**
     * 先禁用职员?再进行删除!!!
     */
    public final static String BUSINESS_ADMIN_DELETE_ERROR = "PROMPT_50013";

    /**
     * 角色存在关联用户?不允许删除!!!
     */
    public final static String ROLE_EXIST_USER = "PROMPT_50014";

    /**
     * 该黑名单已存在
     */
    public final static String RISK_ACCOUNT_EXIST = "PROMPT_50015";

    /**
     * 风控层级是空
     */
    public final static String RISK_LEVEL_IS_NULL = "PROMPT_50016";

    /**
     * 风控类型必填!!!
     */
    public final static String RISK_CONTROL_TYPE = "PROMPT_50017";

    /**
     * 风控层级字符长度最少2个,最多10个
     */
    public final static String RISK_CONTROL_LEVEL = "PROMPT_50018";

    /**
     * 风控层级描述必填!!!
     */
    public final static String RISK_CONTROL_LEVEL_DESCRIBE = "PROMPT_50019";

    /**
     * 风控层级描述最多字符上限50!!!
     */
    public final static String RISK_CONTROL_LEVEL_DESCRIBE_LEN = "PROMPT_50020";

    /**
     * 风控层级已存在!!!
     */
    public final static String RISK_CONTROL_LEVEL_DONE = "PROMPT_50021";

    /**
     * 风控类型为空
     */
    public final static String RISK_CTRL_TYPE_NULL = "PROMPT_50022";

    /**
     * 风控账户为空
     */
    public final static String RISK_CTRL_ACCOUNT_NULL = "PROMPT_50023";

    /**
     * 风控账号的字数最大11位
     */
    public final static String RISK_MEMBER_MAX_LENGHT = "PROMPT_50024";

    /**
     * 风险代理的字数最大11位
     */
    public final static String RISK_AGENT_MAX_LENGHT = "PROMPT_50025";

    /**
     * 风险银行卡的字数最大25位
     */
    public final static String RISK_BANK_MAX_LENGHT = "PROMPT_50026";

    /**
     * 风险虚拟币的字数最大11位
     */
    public final static String RISK_VIRTUAL_MAX_LENGHT = "PROMPT_50027";

    /**
     * 风险IP的字数最大15位
     */
    public final static String RISK_IP_MAX_LENGHT = "PROMPT_50028";

    /**
     * 风险终端设备号的字数最大50位
     */
    public final static String RISK_DEVICE_MAX_LENGHT = "PROMPT_50029";

    /**
     * 传递的风控层级有错误
     */
    public final static String RISK_CONTROLLER_TYPE_IS_ERROR = "PROMPT_50030";

    /**
     * 会员不存在
     */
    public final static String RISK_USER_IS_NOT_EXIST = "PROMPT_50031";

    /**
     * 还在开发中
     */
    public final static String DEVELOPING = "PROMPT_50032";

    /**
     * 银行卡号不存在
     */
    public final static String RISK_RANK_NO_IS_NOT_EXIST = "PROMPT_50033";

    /**
     * 虚拟币地址不存在
     */
    public final static String RISK_VIRTUAL_CURRENCY_NO_IS_NOT_EXIST = "PROMPT_50034";

    /**
     * 该IP没有被风控
     */
    public final static String RISK_IP_NO_IS_NOT_EXIST = "PROMPT_50035";

    /**
     * 该设备没有被风控
     */
    public final static String RISK_DEVICE_NO_IS_NOT_EXIST = "PROMPT_50036";

    /**
     * 该设备没有被风控
     */
    public final static String RISK_LEVEL_ID_IS_NULL = "PROMPT_50037";

    /**
     * 风控账号是空
     */
    public final static String RISK_ACCOUNT_IS_NULL = "PROMPT_50038";

    /**
     * 风控原因是空
     */
    public final static String RISK_DESC_IS_NULL = "PROMPT_50039";

    /**
     * 风控原因的字数最大50位
     */
    public final static String RISK_DESC_MAX_LENGHT_LIMIT = "PROMPT_50040";

    /**
     * 风控层级记录不存在
     */
    public final static String RISK_LEVEL_IS_NOT_EXIST = "PROMPT_50041";

    /**
     * 风控层级记录已经删除
     */
    public final static String RISK_LEVEL_IS_ALREAD_DELETE = "PROMPT_50042";

    /**
     * 保存风控层级记录失败
     */
    public final static String RISK_RECORD_ADD_IS_FAIL = "PROMPT_50043";

    /**
     * 保存风险账号表的记录失败
     */
    public final static String RISK_RECORD_SAVE_IS_FAIL = "PROMPT_50044";

    /**
     * 更新风险账号表的记录失败
     */
    public final static String RISK_RECORD_UPDATE_IS_FAIL = "PROMPT_50045";

    /**
     * 更新用户风险等级失败
     */
    public final static String RISK_LEVEL_UPDATE_IS_FAIL = "PROMPT_50046";

    /**
     * 更新银行卡风险等级失败
     */
    public final static String RISK_RANK_UPDATE_IS_FAIL = "PROMPT_50047";

    /**
     * 更新虚拟币风险等级失败
     */
    public final static String RISK_VIRTUAL_CURRENCY_UPDATE_IS_FAIL = "PROMPT_50048";

    /**
     * 风控黑名单不存在
     */
    public final static String RISK_CONTROL_TYPE_NOT_EXIST = "PROMPT_50049";


    /**
     * 百分比调整: 整数-50到50之间
     */
    public final static String PERCENT_ADJUST_HINT = "PROMPT_50050";

    /**
     * 固定值调整: -1到1之间，可以带2位小数
     */
    public final static String FIXED_VALUE_ADJUST_HINT = "PROMPT_50051";

    public static final String QUERY_SITE_INFO_ERROR = "PROMPT_50052";

    public static final String ADD_SITE_BASIC_ERROR = "PROMPT_50053";
    public static final String UPDATE_SITE_CONFIG_ERROR = "PROMPT_50054";
    public static final String UPDATE_SITE_VENUE_ERROR = "PROMPT_50055";
    public static final String UPDATE_SITE_DEPOSIT_ERROR = "PROMPT_50056";
    public static final String UPDATE_SITE_WITHDRAW_ERROR = "PROMPT_50057";
    public static final String UPDATE_SITE_MESSAGE_ERROR = "PROMPT_50058";
    public static final String UPDATE_SITE_EMAIL_ERROR = "PROMPT_50059";
    public static final String UPDATE_SITE_CUSTOMER_ERROR = "PROMPT_50060";

    public static final String SET_HANDING_FEE_ERROR = "PROMPT_50061";

    public static final String BIG_MONEY_GT_SINGLE_TOTAL_AMOUNT = "PROMPT_50062";

    public static final String BANK_CARD_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT = "PROMPT_50063";

    public static final String BANK_CARD_MIN_AMOUNT_GT_BANK_MAX_AMOUNT = "PROMPT_50064";
    public static final String CRYPTO_CURRENCY_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT = "PROMPT_50065";
    public static final String CRYPTO_CURRENCY_MIN_AMOUNT_GT_VIRTUAL_MAX_AMOUNT = "PROMPT_50066";
    public static final String ELECTRONIC_WALLET_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT = "PROMPT_50067";
    public static final String ELECTRONIC_WALLET_MIN_AMOUNT_GT_ELECTRONIC_WALLET_MAX_AMOUNT = "PROMPT_50068";
    public static final String BK_NAME_IS_EXIST = "PROMPT_50069";
    public static final String BK_NAME_LENGTH_MORE = "PROMPT_50070";
    public static final String SITE_NAME_IS_EXIST = "PROMPT_50071";
    public static final String SITE_PREFIX_IS_EXIST = "PROMPT_50072";

    public static final String SITE_INCLUDES_RISK_CONTROL = "PROMPT_50073";
    /**
     * 皮肤code已重复
     */
    public static final String SKIN_CODE_EXIST = "PROMPT_50074";
    /**
     * 皮肤名称已重复
     */
    public static final String SKIN_NAME_EXIST = "PROMPT_50075";

    public static final String CANNOT_DISABLE_YOURSELF = "PROMPT_50076";
    public static final String SITE_CURRENCY_ERROR = "PROMPT_50077";
    public static final String SITE_VIP_INIT_ERROR = "PROMPT_50078";

    public static final String BUSINESS_ROLE_EDIT_ERROR = "PROMPT_50079";

    public static final String BUSINESS_ROLE_DELETE_ERROR = "PROMPT_50080";

    public static final String CHANNEL_CODE_IS_EXIST = "PROMPT_50081";

    public static final String CENTER_LANGUAGE_DISABLE = "PROMPT_50082";

    public static final String CURRENCY_NAME_REPEAT = "PROMPT_50083";

    public static final String GAME_ONE_NAME_REPEAT = "PROMPT_50084";

    public static final String GAME_TWO_NAME_REPEAT = "PROMPT_50085";

    public static final String RECHARGE_WAY_IS_NOT_EXIST = "PROMPT_50086";

    public static final String WITHDRAW_WAY_IS_NOT_EXIST = "PROMPT_50087";
    public static final String VENUE_CHOOSE_ERROR = "PROMPT_50088";
    public static final String DEPOSIT_CHOOSE_ERROR = "PROMPT_50089";
    public static final String WITHDRAW_CHOOSE_ERROR = "PROMPT_50090";
    public static final String NOT_DELETABLE = "PROMPT_50091";
    public static final String ONLY_ONE_ENABLED = "PROMPT_50092";
    public static final String CURRENCY_CODE_NOT_EXIT = "PROMPT_50093";
    public static final String VERSION_NUMBER_ERROR = "PROMPT_50094";
    public static final String SITE_CUSTOMER_CHANNEL_MUST_ONE_ENABLE = "PROMPT_50095";
    public static final String CUSTOMER_CHANNEL_AL_USED = "PROMPT_50096";

    public static final String NAME_ALREADY_EXIST = "PROMPT_50097";
    public static final String DELETED_AFTER_DISABLED = "PROMPT_50098";
    public static final String CATEGORY_IS_DISABLED = "PROMPT_50099";
    public static final String CLASS_IS_DISABLED = "PROMPT_50100";
    public static final String TABS_IS_DISABLED = "PROMPT_50101";

    public static final String ADDRESS_ALREADY_EXIST = "PROMPT_50102";

    public static final String VIRTUAL_ADDRESS_ILLEGAL = "PROMPT_50103";

    public static final String MERCHANT_NO_EXIST = "PROMPT_50104";
    public static final String CANT_ENABlE_BANNER = "PROMPT_50105";

    public static final String MNEMONIC_PHRASE_IS_NULL = "PROMPT_50106";

    public static final String CLASS_NOT_BELONG_CATEGORY = "PROMPT_50107";

    public static final String  TBAS_NOT_BELONG_CLASS = "PROMPT_50108";
    public static final String  SITE_ONLY_ONE_DOMAIN = "PROMPT_50109";
    public static final String  SITE_NAME_NOT_CHINESE = "PROMPT_50110";
    public static final String  AL_BIND_DOMAIN_CANT_OPER = "PROMPT_50111";
    public static final String  NOT_BIND_DOMAIN_CANT_OPER = "PROMPT_50112";
    public static final String  PARTNER_ENABLE_ERROR = "PROMPT_50113";
    public static final String  PARTNER_ENABLE_MAX_ERROR = "PROMPT_50114";
    public static final String  PARTNER_CENTER_DISABLE_ERROR = "PROMPT_50115";
    public static final String  PAYMENT_CENTER_DISABLE_ERROR = "PROMPT_50116";
    public static final String  PAYMENT_VENDOR_ENABLE_ERROR = "PROMPT_50117";

    public static final String  SKIN_DISABLED = "PROMPT_50118";

    /*===================== agent系统异常定义  6xxxx=====================*/
    public final static String AGENT_CHANGE_TYPE_ERROR = "PROMPT_60000";

    public final static String AGENT_EXISTS_TO_REVIEWED = "PROMPT_60001";

    public final static String AGENT_NOT_EXISTS = "PROMPT_60002";
    public final static String QUERY_AGENT_REGISTER_RECORD_ERROR = "PROMPT_60003";



    /**
     * 记录代理注册记录信息错误
     */
    public final static String INSERT_AGENT_REGISTER_RECORD_ERROR = "PROMPT_60004";


    public static final String AGENT_PASSWORD_SAME = "PROMPT_60007";


    public static final String AGENT_CATEGORY_ERROR = "PROMPT_60008";

    public static final String AGENT_CATEGORY_WHITE_LIST_ERROR = "PROMPT_60009";

    public static final String AGENT_CATEGORY_WHITE_LIST_STYLE_ERROR = "PROMPT_60010";


    public static final String AGENT_ACCOUNT_REPEAT_ERROR = "PROMPT_60011";
    public static final String LINK_CANNOT_BE_EMPTY = "PROMPT_14016";
    public static final String LINK_LENGTH_ERR = "PROMPT_14012";
    public static final String REASON_TOO_LONG = "PROMPT_14013";
    public static final String MEMBER_CANNOT_APPLY = "PROMPT_14014";
    public static final String MEMBER_REGISTER_OVER_3_DAYS = "PROMPT_14015";
    public static final String MIN_LENGTH_BIGGER = "PROMPT_14017";
    public static final String QUERY_SIXTY_RANGE ="PROMPT_60172";
    public static final String ALREADY_REJECT = "PROMPT_60173";
    public static final String DEALING = "PROMPT_60174";
    public static final String REGISTER_TIME_EARLY_AGENT_TIME = "PROMPT_60175";
    public static final String ONLY_MAIN_AGENT_MODIFY_PLAN = "PROMPT_60176";
    public static final String AGENT_LABEL_AL_USED = "PROMPT_60177";
    public static final String IP_NOT_ALLOW = "PROMPT_60178";

    public static final String AGENT_MERCHANT_NOT_EXISTS="PROMPT_60179";
    public static final String AGENT_MERCHANT_NOT_MATCH="PROMPT_60180";


    public static final String MAIL_CODE_ERROR = "PROMPT_14019";
    public static final String PLEASE_RE_ENTER = "PROMPT_14020";

    public static final String PLEASE_RE_ENTER_USER_ACCOUNT = "PROMPT_14021";

    public static final String MEDAL_HAS_LIGHT = "PROMPT_14022";

    public static final String MEDAL_REWARD_HAS_OPEN = "PROMPT_14023";
    public static final String AUTH_WRONG_OPEN = "PROMPT_20147";

    public static String USER_ACCOUNT_ERROR = "PROMPT_60012";

    //
    public static String AGENT_REVIEW_STATUS_ERROR = "PROMPT_60013";
    public static final String AMOUNT_LT_ZERO = "PROMPT_60014";
    /**
     * 只有流量代理才可以查看密钥
     */
    public final static String FLOW_AGENT_SECRET_KEY_ERROR = "PROMPT_60015";
    /**
     * 只有流量代理才可以更新白名单
     */
    public final static String FLOW_AGENT_WHITE_LIST_ERROR = "PROMPT_60016";


    public final static String AGENT_DOMAIN_TYPE_IS_ERROR = "PROMPT_60017";
    public final static String AGENT_DOMAIN_STATE_IS_ERROR = "PROMPT_60018";
    public final static String AGENT_DOMAIN_IS_NULL = "PROMPT_60019";

    public static final String AGENT_SUPER_AGENT_EMPTY_ERROR = "PROMPT_60020";
    public static final String AGENT_ACCOUNT_STATUS_EMPTY = "PROMPT_60021";
    public static final String AGENT_RISK_LEVEL_EMPTY = "PROMPT_60022";
    public static final String AGENT_LABEL_EMPTY = "PROMPT_60023";
    public static final String AGENT_ACCOUNT_REMARK_EMPTY = "PROMPT_60024";
    public static final String AGENT_AGENT_ATTRIBUTION_EMPTY = "PROMPT_60025";
    public static final String AGENT_PLAN_CODE_EMPTY = "PROMPT_60026";
    public static final String AGENT_USER_BENEFIT_EMPTY = "PROMPT_60027";
    public static final String AGENT_FORCE_CONTRACT_EFFECT_EMPTY = "PROMPT_60028";
    public static final String AGENT_ENTRANCE_PERM_EMPTY = "PROMPT_60029";
    public static final String AGENT_SUPER_AGENT_NOT_ERROR = "PROMPT_60030";
    public static final String AGENT_SUPER_AGENT_NOT_VALID_ERROR = "PROMPT_60031";
    public static final String AGENT_LOWEST_LEVEL_AGENT_ERROR = "PROMPT_60032";
    public static final String AGENT_REMOVE_RECHARGE_RESTRICTIONS_EMPTY = "PROMPT_60033";
    public static final String AGENT_PAYMENT_PASSWORD_RESET_EMPTY = "PROMPT_60034";
    public static final String AGENT_ATTRIBUTION_EMPTY = "PROMPT_60035";
    public static final String ONLY_MAIN_AGENT_CAN_MODIFY = "PROMPT_60036";
    public static final String EMAIL_TYPE_ERROR = "PROMPT_60037";
    public static final String EMAIL_ALREADY_USED = "PROMPT_60038";
    public static final String QUERY_AGENT_DETAIL_ERROR = "PROMPT_60039";
    public static final String QUERY_AGENT_REMARK_ERROR = "PROMPT_60040";
    public static final String QUERY_AGENT_LOGIN_ERROR = "PROMPT_60041";
    /*代理标签名称已经存在*/
    public static final String AGENT_LABEL_EXISTED = "PROMPT_60042";
    /*标签记录不存在*/
    public static final String RECORD_IS_NOT_EXIST = "PROMPT_60043";

    public static final String AGENT_LEVEL_CONFIG_NAME_EXIST_ERROR = "PROMPT_60044";

    public static final String AGENT_LEVEL_CONFIG_LEVEL_EXIST_ERROR = "PROMPT_60045";

    public static final String AGENT_LEVEL_CONFIG_LIMIT_ERROR = "PROMPT_60046";

    public static final String UP_AGENT_ACCOUNT_ERROR = "PROMPT_60047";

    public static final String AGENT_ACCOUNT_NOT = "PROMPT_60048";
    /*查询代理下用户标签记录失败*/
    public static final String RECORD_IS_NOT_FAIL = "PROMPT_60049";
    public static final String AGENT_WITHDRAW_CONFIG_COMMON_ERROR = "PROMPT_60050";
    public static final String AGENT_WITHDRAW_CONFIG_ONLY_ERROR = "PROMPT_60051";
    public static final String AGENT_WITHDRAW_CONFIG_COMMON_DEL_ERROR = "PROMPT_60052";
    public static final String AGENT_WITHDRAW_CONFIG_CLOSE_DEL_ERROR = "PROMPT_60053";
    public static final String AGENT_WITHDRAW_CONFIG_EMPTY_ERROR = "PROMPT_60054";
    public static final String AGENT_BANK_MIN_GT_BANK_MAX_ERROR = "PROMPT_60055";
    public static final String AGENT_VIRTUAL_MIN_GT_VIRTUAL_MAX_ERROR = "PROMPT_60056";

    public static final String DEPOSIT_FAIL = "PROMPT_60057";
    public static final String MEMBER_DEPOSIT_LIST = "PROMPT_60058";


    public static final String USER_MANUAL_UP_REVIEW_ONE_REVIEWING = "PROMPT_60059";

    public static final String USER_MANUAL_UP_REVIEW_TWO_REVIEWING = "PROMPT_60060";

    //代理账号不存在
    public static final String AGENT_ACCOUNT_NOT_EXIS = "PROMPT_60061";

    public static final String ADJUST_TYPE_IS_ERROR = "PROMPT_60062";

    public static final String WALLET_TYPE_IS_ERROR = "PROMPT_60063";
    public static final String ALREADY_PENDING_DATA = "PROMPT_60064";


    public static final String AGENT_ARREARS_NOT_TRANSFER = "PROMPT_60071";

    public static final String QUERY_AGENT_TRANSFER_WALLET_ERROR = "PROMPT_60072";


    public static final String PAYPASSWORD_ERROR = "PROMPT_60073";

    public static final String AGENT_ACCOUNT_NOT_EXISTS = "PROMPT_60074";

    public static final String TRANS_MORE_DAY = "PROMPT_60075";

    public static final String AGENT_TRANSFER_ERROR = "PROMPT_60076";

    public static final String AGENT_PARENT_ERROR = "PROMPT_60077";

    public static final String SAVE_AGENT_TRANSFER_ERROR = "PROMPT_60078";

    public static final String AGENT_TEMPLATE_NAME_MAX = "PROMPT_60079";

    public static final String AGENT_TRANSFER_RECORD_ERROR = "PROMPT_60080";


    public static final String AGENT_TRANSFER_COIN_NOT_ENOUGH = "PROMPT_60102";

    public static final String AGENT_MANUAL_DEP_ERROR = "PROMPT_60104";

    public static final String COMMISSION_CHANGE_RECORD_ADD_FAIL = "PROMPT_60105";

    public static final String QUOTA_CHANGE_RECORD_ADD_FAIL = "PROMPT_60106";

    public static final String UPDATE_AGENT_FAIL = "PROMPT_60107";
    public static final String AGENT_MANUAL_DOWN_COIN_AMOUNT_NOT_ENOUGH = "PROMPT_60108";
    public static final String AGENT_ACCOUNT_NOT_EXIST = "PROMPT_60109";
    public static final String USER_LOGIN_ERROR = "PROMPT_60110";
    public static final String INSERT_AGENT_LOGIN_ERROR = "PROMPT_60111";
    public static final String AGENT_LOGIN_CODE_ERROR = "PROMPT_60112";
    public static final String AGENT_LOGIN_LOCK = "PROMPT_60113";
    public static final String AGENT_BINDING_NUMBER_LIMIT = "PROMPT_60114";

    public static final String AGENT_LOGIN_PASSWROD_ERROR = "PROMPT_60115";

    public static final String VIRTUAL_CURRENCY_BLACK_STATUS_NOT = "PROMPT_60116";

    public static final String VIRTUAL_CURRENCY_ALREADY_BIND = "PROMPT_60117";

    public static final String VIRTUAL_CURRENCY_ALREADY_BIND_NOT = "PROMPT_60118";

    public static final String ALREADY_UNBIND_CAN_NOT_BIND = "PROMPT_60119";

    public static final String BLACK_STATUS_INCORRECT = "PROMPT_60120";

    public static final String BINDING_STATUS_INCORRECT = "PROMPT_60121";

    public static final String CONFIRM_VIRTUAL_CURRENCY_ADDRESS_ERROR = "PROMPT_60122";

    public static final String AGENT_QA_NOT_SET = "PROMPT_60123";
    public static final String AGENT_QA_VERIFY_ERROR = "PROMPT_60124";

    public static final String AGENT_SECURITY_QA_VERIFY_ERROR = "PROMPT_60125";
    public static final String AGENT_SECURITY_QA_INCORRECT_ERROR = "PROMPT_60126";
    public static final String AGENT_CURRENT_ALREADY_BIND_EMAIL = "PROMPT_60127";
    public static final String GOOGLE_AUTH_KEY_EXSIT = "PROMPT_60128";
    public static final String DYNAMIC_VERIFICATION_CODE_ERR = "PROMPT_60129";
    public static final String AGENT_PAYPASSWORD_EDIT_ERROR = "PROMPT_60130";
    public static final String AGENT_QUOTA_INCREASE_ERROR = "PROMPT_60131";
    public static final String AGENT_COMISSION_DECREASE_ERROR = "PROMPT_60132";
    public static final String AGENT_PAY_PASSWORD_NOT_SET_ERROR = "PROMPT_60133";
    public static final String AGENT_COMMISSION_COIN_INSUFFICIENT_BALANCE = "PROMPT_60134";

    public static final String AGENT_ACCOUNT_NOT_A = "PROMPT_60135";

    public static final String AGENT_ACCOUNT_ERROR = "PROMPT_60136";

    public static final String AGENT_ACCOUNT_EXIST_ERROR = "PROMPT_60137";

    public static final String AGENT_PASSWORD_ERROR = "PROMPT_60138";
    public static final String QUERY_THIRTY_RANGE = "PROMPT_60139";
    public static final String QUERY_CLIENT_ORDER_ERROR = "PROMPT_60140";


    public static final String AGENT_WITHDRAW_APPLY_FAIL = "PROMPT_60141";

    public static final String AGENT_WITHDRAW_AMOUNT_NEED_WHOLE = "PROMPT_60142";

    public static final String AGENT_CURRENT_ACCOUNT_NOT_WITHDRAW = "PROMPT_60143";

    public static final String AGENT_STATUS_PAY_LOCK = "PROMPT_60144";

    public static final String AGENT_VIRTUAL_CURRENCY_ADDRESS_BLACK = "PROMPT_60145";

    public static final String AGENT_ADDRESS_NOT_BUILDING = "PROMPT_60146";

    public static final String AGENT_WITHDRAWAL_PAY_PASSWORD_ERROR = "PROMPT_60147";

    public static final String AGENT_GOOGLE_AUTH_KEY_NOT_SET = "PROMPT_60148";

    public static final String AGENT_GOOGLE_AUTH_CODE_ERROR = "PROMPT_60149";

    public static final String AGENT_GOOGLE_AUTH_KEY_NOT_BLANK = "PROMPT_60150";

    public static final String AGENT_FUND_ORDER_EXIST = "PROMPT_60151";

    public static final String AGENT_GREATER_MAX_AMOUNT = "PROMPT_60152";

    public static final String AGENT_LESS_MIN_AMOUNT = "PROMPT_60153";

    public static final String AGENT_CONTRACT_EXISTED = "PROMPT_60154";


    public static final String INPUT_ACCOUNT_NOT_DOWN = "PROMPT_60155";

    public static final String AGENT_PLAN_DELETE_FAIL = "PROMPT_60156";
    public static final String AGENT_LABEL_NAME_DUPLICATE = "PROMPT_60157";
    public static final String AGENT_EMAIL_ERROR = "PROMPT_60158";
    //当前代理基础信息和上级不一致
    public static final String AGENT_NOT_MATCH_SUPER = "PROMPT_60159";
    public static final String AGENT_PLAN_REPEAT = "PROMPT_60160";
    public static final String AGENT_FLOW_SUB_ERROR = "PROMPT_60161";
    public static final String USER_ACCOUNT_TYPE_NOT_EQ_AGENT = "PROMPT_60162";
    public static final String AGENT_MSG_NOT_CHANGE = "PROMPT_60163";
    /**
     * 支付密码不能为空
     */
    public static final String AGENT_H5_PASSWORD_NOT_EMPTY = "PROMPT_60164";
    public static final String AGENT_H5_PASSWORD_AMOUNT_NOT_EMPTY = "PROMPT_60165";
    public static final String AGENT_H5_WALLET_NOT_EMPTY = "PROMPT_60166";
    public static final String USER_ACCOUNT_NOT_EMPTY = "PROMPT_60167";
    public static final String REMARK_NOT_BLANK = "PROMPT_60168";
    public static final String AMOUNT_CAN_ONLY_BE_INTEGER = "PROMPT_60169";
    public static final String AMOUNT_CANNOT_BE_ZERO = "PROMPT_60170";
    public static final String AGENT_NOT_EXIT = "PROMPT_60171";

    public static final String NOT_MATCH_PASSWORD = "PROMPT_60190";

    public static final String NOT_EMPTY_PASSWORD = "PROMPT_60191";

    public static final String NOT_EMPTY_NEW_PASSWORD = "PROMPT_60192";

    public static final String NOT_EMPTY_CONFIRM_PASSWORD = "PROMPT_60193";

    public static final String ICCORRECT_PASSWORD_VERIFY = "PROMPT_60194";

    public static final String ERROR_EMAIL = "PROMPT_60195";

    public static final String ERR_OLD_PASSWORD = "PROMPT_60196";

    public static final String NEW_PASSWORD_SAME_OLD_PASSWORD = "PROMPT_60197";

    public static final String BIND_FAILED_ERROR_PASSWORD = "PROMPT_60187";

    public static final String OTHER_BIND = "PROMPT_60199";

    public static final String BIND_OTHER_BIND = "PROMPT_60200";

    public static final String FIEXD_FEE_AMOUNT_INVALID = "PROMPT_60201";


    /*===================== pay系统异常定义  7xxxx=====================*/
    public static final String CHANNEL_NOT_EXISTS = "PROMPT_70000";
    public static final String ADDRESS_NOT_MATCH_CHAIN = "PROMPT_70001";
    public static final String ADDRESS_OWNER_INNER = "PROMPT_70002";
    public static final String OUT_ADDRESS_NOT_EXISTS = "PROMPT_70003";
    public static final String OUT_GAS_NOT_ENOUGH = "PROMPT_70004";
    public static final String OUT_BALANCE_NOT_ENOUGH = "PROMPT_70005";
    /*===================== 活动服务 异常定义  8xxxx=====================*/
    /**
     * 前一场次结束时间不得早于下一场次开始时间
     */
    public final static String RED_BAG_SESSION_TIME_AFTER_ERROR = "PROMPT_80000";
    /**
     * 场次开始时间不得早于结束时间
     */
    public final static String RED_BAG_SESSION_TIME_ERROR = "PROMPT_80001";
    /*红包雨提前提示时间不得大于600秒*/
    public final static String RED_BAG_ADVANCE_TIME_ERROR = "PROMPT_80002";
    /*红包雨掉落持续时间不得大于30秒*/
    public final static String RED_BAG_DROP_TIME_ERROR = "PROMPT_80003";
    /*存款奖励金额需要大于0*/
    public final static String RED_BAG_DEPOSIT_LIMIT_ERROR = "PROMPT_80004";
    /*流水奖励金额需要大于0*/
    public final static String RED_BAG_BET_DEPOSIT_LIMIT_ERROR = "PROMPT_80005";
    /*红包总金额必须大于0*/
    public final static String RED_BAG_TOTAL_AMOUNT_ERROR = "PROMPT_80006";
    /*红包中奖设置不能为空*/
    public final static String RED_BAG_HIT_CONFIG_ERROR = "PROMPT_80007";
    /*有效红包数量上限必须大于0*/
    public static final String RED_BAG_MAXIMUM_ERROR = "PROMPT_80008";
    /*请输入有效红包金额类型*/
    public static final String RED_BAG_AMOUNT_TYPE_ERROR = "PROMPT_80009";
    /*段位红包配置不能为空*/
    public static final String RED_BAG_RANK_CONFIG_ERROR = "PROMPT_80010";
    /*段位红包配置概率总和需要等于100*/
    public static final String RED_BAG_RANK_HATE_ERROR = "PROMPT_80011";
    /*段位红包配置随机金额起始金额不可大于前一个结束金额*/
    public static final String RED_BAG_RANDOM_AMOUNT_ERROR = "PROMPT_80012";
    /*红包雨活动配置重复*/
    public static final String RED_BAG_SITE_REPEAT_ERROR = "PROMPT_80013";
    /*本场次已结束*/
    public static final String RED_BAG_SESSION_END_ERROR = "PROMPT_80014";
    /*红包已被抢完了*/
    public static final String RED_BAG_OVER_ERROR = "PROMPT_80015";
    /*当前vip段位不符合参与条件*/
    public static final String RED_BAG_VIP_RANK_LIMIT_ERROR = "PROMPT_80016";
    /**
     * 未到领取时间
     */
    public static final String ACTIVITY_NOT_YET_CLAIM_TIME = "PROMPT_80017";

    /**
     * 活动领取时间已经过期
     */
    public static final String ACTIVITY_NOT_YET_CLAIM_EXPIRED = "PROMPT_80028";

    /**
     * 领取失败
     */
    public static final String ACTIVITY_NOT_YET_CLAIM_FAIL = "PROMPT_80029";

    public static final String TASK_PARAM_MIN_BET_NULL = "PROMPT_80030";

    public static final String TASK_PARAM_VENUE_TYPE_NULL = "PROMPT_80031";

    public static final String TASK_PARAM_VENUE_CODE_NULL = "PROMPT_80032";
    /*场次已结算,不可再次结算*/
    public static final String REDBAG_SREPEAT_ETTLED_ERROR = "PROMPT_80033";
    /*本场次未开始*/
    public static final String RED_BAG_SESSION_NOT_START_ERROR = "PROMPT_80034";
    //
    public static final String SPIN_WHEEL_NOT_PRIZE_CONFIG = "PROMPT_80035";

    public static final String ACTIVITY_BASE_SHOW_TIME_ERROR = "PROMPT_80036";


    public static final String INSUFFICIENT_VIP_LEVEL = "PROMPT_80037";

    public static final String ACTIVITY_CAN_NOT_JOIN = "PROMPT_80038";
    public static final String RED_BAG_AMOUNT_OVER_ZERO_ERROR = "PROMPT_80039";

    public static final String WASH_RATIO_AMOUNT_OVER_ZERO_ERROR = "PROMPT_80040";
    /*红包雨活动未开启*/
    public static final String RED_BAG_NO_OPEN_ERROR = "PROMPT_80018";
    //ACTIVITY_NOT_HAVE_PRIZE_TIME
    public static final String ACTIVITY_NOT_HAVE_PRIZE_TIME = "PROMPT_80019";
    /*活动状态已变更*/
    public static final String ACTIVITY_OPEN_ALREADY_TIME = "PROMPT_80020";
    /*该活动不可禁用,还存在进行中场次*/
    public static final String REDBAG_SESSION_PROCESS_ERROR = "PROMPT_80021";
    /*已参与过该场次*/
    public static final String REDBAG_SESSION_PARTICIPATED_ERROR = "PROMPT_80022";
    /*您没有参与资格*/
    public static final String REDBAG_SESSION_NOT_ALLOWED_PARTICIPATED_ERROR = "PROMPT_80023";
    /*时间参数异常*/
    public static final String ACTIVITY_TIME_ERROR = "PROMPT_80024";
    /*未达到参与红包雨存款金额要求*/
    public static final String REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR = "PROMPT_80025";
    /*未达到参与红包雨流水金额要求*/
    public static final String REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR = "PROMPT_80026";
    /*活动未开启*/
    public static final String ACTIVITY_NOT_OPEN = "PROMPT_80027";

    public static final String TASK_ALREADY_ENABLE = "PROMPT_80041";

    public static final String TASK_ALREADY_NO_ENABLE = "PROMPT_80042";


    public static final String MERCHAT_MISSING = "PROMPT_80043";


    public static final String CHECKIN_OUT_OFF_TIME = "PROMPT_80044";
    public static final String CHECKIN_OUT_ALREADY = "PROMPT_80045";
    public static final String DEPOSIT_NOT_MEET = "PROMPT_80046";
    public static final String BET_NOT_MEET = "PROMPT_80047";
    public static final String ACTIVITY_VENUE_TYPE_REPEAT = "PROMPT_80048";

    public static final String CHECKIN_NOT_MAKEUP_LIMIT = "PROMPT_80049";


    public static final String CHECKIN_NOT_MAKEUP_LIMIT_REQ = "PROMPT_80050";



    public static final String CHECKIN_NOT_MEET_CURRENT_TODAY = "PROMPT_80051";

    //
    public static final String TASK_REWARD_AMOUNT_OVER_ZERO_ERROR = "PROMPT_80052";


    /*===================== 其他 异常定义  9xxxx=====================*/

    /**
     * 自己申请的数据不能自己审核
     */
    public final static String WRONG_OPERATION = "PROMPT_90000";

    /**
     * 已被锁单
     */
    public final static String LOCKED = "PROMPT_90001";

    /**
     * 已被审核，请勿反复操作
     */
    public final static String AUDITED = "PROMPT_90002";

    /**
     * 该单号已经被锁住
     */
    public final static String USER_REVIEW_ALREADY_LOCK_ERROR = "PROMPT_90003";

    /**
     * 查询客户端商务合作配置错误！！
     */
    public final static String QUERY_BUSINESS_CONFIG_ERR = "PROMPT_90004";

    /**
     * 风控层级下拉框查询失败!!!
     */
    public final static String RISK_LEVEL_GET = "PROMPT_90005";

    /**
     * ip格式错误
     */
    public final static String IP_FORMAT_ERROR = "PROMPT_90006";

    /**
     * 该平台已存在此游戏名
     */
    public final static String ADMIN_ALREADY_EXISTS = "PROMPT_90007";

    /**
     * 平台未配置
     */
    public final static String PLATFORM_NOT_CONFIGURED = "PROMPT_90008";

    /**
     * 该平台接入参数重复
     */
    public final static String PLATFORM_PARAM_REPEAT = "PROMPT_90009";

    /**
     * 导出数据条数不可超过10w条
     */
    public final static String EXPORTED_NUM_LIMITED = "PROMPT_90010";
    /**
     * 必须选择一个时间范围
     */
    public static final String TIME_MUST_CHOOSE = "PROMPT_40002";
    /**
     * 查询时间范围不能超过40天!!!
     */
    public static final String FORTY_DAY_OVER = "PROMPT_40003";
    /**
     * 进入游戏失败
     */
    public static final String CREATE_MEMBER_FAIL = "PROMPT_40004";
    /**
     * 游戏已关闭
     */
    public static final String CASINO_IS_CLOSED = "PROMPT_40005";
    /**
     * 维护中
     */
    public static final String CASINO_IS_MAINTAIN = "PROMPT_40006";
    /**
     * 场馆不可用
     */
    public static final String VENUE_IS_DISABLE = "PROMPT_40007";
    /**
     * 进入游戏操作频繁,请稍后再试
     */
    public static final String OPEN_GAME_FREQUENTLY = "PROMPT_40008";
    /**
     * 游戏房间维护中
     */
    public static final String GAME_ROOM_MAINTAIN = "PROMPT_40009";
    /**
     * 游戏锁定
     */
    public static final String USER_GAME_LOCKED = "PROMPT_40010";

    /**
     * 查询最新的5条结算注单失败
     */
    public static final String SELECT_LATEST_5ORDER_FAIL = "PROMPT_40011";

    /**
     * 场馆币种重复
     */
    public static final String VENUE_REPEAT_CURRENCY = "PROMPT_40012";

    /**
     * 场馆未开启,游戏不允许开启
     */
    public static final String VENUE_NOT_OPEN = "PROMPT_40013";

    /**
     * 总控场馆未开启,游戏不允许开启
     */
    public static final String ADMIN_VENUE_CLOSE = "PROMPT_40016";

    /**
     * 总控游戏维护中
     */
    public static final String ADMIN_GAME_MAINTAIN_SYN = "PROMPT_40017";

    /**
     * 总控游戏未开启
     */
    public static final String ADMIN_GAME_NOT_OPEN = "PROMPT_40018";

    /**
     * 总控游戏禁用中
     */
    public static final String ADMIN_GAME_CLOSE = "PROMPT_40019";

    /**
     * 站点场馆禁用中
     */
    public static final String SITE_VENUE_CLOSE = "PROMPT_40020";

    /**
     * 此场馆总控维护中,确认后将同步总控维护时间和状态
     */
    public static final String ADMIN_VENUE_NOT_MAINTAIN = "PROMPT_40021";

    /**
     * 站点场馆未开启
     */
    public static final String SITE_VENUE_NOT_MAINTAIN = "PROMPT_40022";

    /**
     * 站点游戏未开启
     */
    public static final String SITE_GAME_NOT_MAINTAIN = "PROMPT_40023";

    /**
     * 场馆不可重复修改,等待10秒
     */
    public static final String PLEASE_TRY_AGAIN_LATER = "PROMPT_40024";


    /**
     * 总控已维护此场馆。不允许修改
     */
    public static final String ADMIN_VENUE_MAINTAIN = "PROMPT_40025";

    /**
     * 不可以跨场馆
     */
    public static final String CROSS_VENUE = "PROMPT_40026";


    public static final String VENUE_FEE_ZERO = "PROMPT_40014";

    public static final String VENUE_VALID_FEE_ZERO = "PROMPT_40015";

    /**
     * 暂不支持
     */
    public final static String NOT_SUPPORTED_YET = "PROMPT_90011";
    /**
     * 该场馆还有拉单任务正在处理中，请稍后再试
     */
    public final static String EXECUTING = "PROMPT_90012";

    /**
     * 该场馆还有拉单任务正在处理中，请稍后再试
     */
    public final static String PULL_TIME_ERR = "PROMPT_90013";

    /**
     * 状态异常
     */
    public final static String STATUS_EXCEPT = "PROMPT_90014";
    /**
     * websocket连接已存在
     */
    public final static String WEBSOCKET_CONNECT_EXIST = "PROMPT_90015";

    /**
     * 查询会员累计金额失败
     */
    public final static String QUERY_REPORT_USER_CHARGE_FAIL = "PROMPT_90016";
    /**
     * 未锁单不能审核
     */
    public final static String APPLY_UNLOCK = "PROMPT_90017";
    /**
     * 锁单人不是审核人
     */
    public final static String NOT_CURRENT_LOCK = "PROMPT_90018";
    /**
     * 审核完成不能再次审核
     */
    public final static String APPLY_IS_COMPLATE = "PROMPT_90019";

    /**
     * VIP奖励查询错误!!!
     */
    public final static String VIP_AWARD_QUERY_ERROR = "PROMPT_90083";

    /**
     * 头像名称格式错误!!!
     */
    public final static String AVATAR_NAME_ERROR = "PROMPT_90084";
    /**
     * 不能被多人锁单
     */
    public final static String AL_IS_LOCK = "PROMPT_90020";

    /**
     * app屏闪页使用中，不能删除
     */
    public final static String SYS_TERMINAL_SPLASH_USED = "PROMPT_98003";
    /**
     * 数据已经过了有效性，不能启用
     */
    public final static String SYS_TERMINAL_SPLASH_EXPIRED = "PROMPT_98004";

    public final static String TERMINAL_ONE_ENABLE = "PROMPT_98005";
    public final static String ACTIVITY_LIMIT_ONE_ON = "PROMPT_98007";

    public final static String LOGIN_GAME_NOT_WAGERING = "PROMPT_50119";

    public final static String DOWNLOAD_EXPORT_NOTICE = "PROMPT_50120";

    public static final String FREE_NUM_GT_DAY_NUM = "PROMPT_50121";
    public static final String FREE_AMOUNT_GT_DAY_AMOUNT = "PROMPT_50122" ;

    public static final String EXCEED_DAY_MAX_NUM = "PROMPT_30141";
    public static final String EXCEED_DAY_MAX_AMOUNT = "PROMPT_30142" ;

    public static final String TOTAL_SHOW_CODE = "PROMPT_30143" ;

    public static final String NEED_TO_RESET_THE_TIME = "PROMPT_30144" ;

    public static final String EXISTS_VENUE_NON_REBATE_CONFIG = "PROMPT_50123" ;

    public static final String CHOOSE_GAME_WITHOUT_CASHBACK = "PROMPT_50124" ;

    public final static String AGENT_CORRECTION_ERROR = "PROMPT_60202";

    public final static String RE_ENTER_AMOUNT = "PROMPT_60203";

    public final static String CANNOT_BE_CREATED_REPEATEDLY = "PROMPT_60204";




    public static final String EXECUTE_NOTICE = "PROMPT_50125" ;

    public static final String NEW_PASSWORD_MATCH_OLD = "PROMPT_50126" ;

    public static final String REGISTER_INVITE_CODE_ERROR = "PROMPT_98008" ;

    public static final String ADJUST_AMOUNT_IS_NULL = "PROMPT_30145" ;

    public static final String USER_AMOUNT_INSUFFICIENT_BALANCE = "PROMPT_30146" ;

    public static final String SECURITY_CANNOT_CLOSE = "PROMPT_30147" ;

    public static final String NAME_ALREADY_RECORD = "PROMPT_30148" ;


    public static final String NAME_ALREADY_RECORD_OVER_10 = "PROMPT_30149" ;

    public static final String ELECTRONIC_WALLET_COLLECT_SELECT_ONE = "PROMPT_30150" ;

    //
    public static final String PLEASE_REMOVE_ACTIVITY = "PROMPT_30151" ;

    public static final String OVER_BALANCE = "PROMPT_30152" ;

    public static final String SECURITY_ADJUST_AMOUNT = "PROMPT_30153" ;


    public static final String ORDER_NOT_MANUAL_WITHDRAW = "PROMPT_30154" ;

    public static final String HASH_REPEAT_ERROR = "PROMPT_30155" ;

    public static final String REVIEW_FAILED_CODE_ERROR = "PROMPT_30156" ;
    public static final String SEO_LANG_REPEAT_ERROR = "PROMPT_30157" ;
    public static final String OVERDRAW_ERROR = "PROMPT_30158" ;

    public static final String IFSC_CODE_IS_EMPTY = "PROMPT_30159" ;
    public static final String ACTIVITY_IS_NOT_OPEN_ALLOW = "PROMPT_30160" ;

    public static final String VENUE_JOIN_TYPE_INVITE_CODE_ERROR = "PROMPT_98009" ;

    public static final String ACTIVITY_RECOMMENDED_EXIST = "PROMPT_98010" ;
    public static final String IP_LIST_EXIST = "PROMPT_98011" ;
    public static final String IP_WHITE_NOT_EXIST = "PROMPT_98012" ;

    public static final String ELECTRONIC_WALLET_NAME_IS_EMPTY = "PROMPT_30161" ;

    public static final String ACCOUNT_HAS_BEEN_BOUND = "PROMPT_30162" ;

    public static final String ACCOUNT_HAS_BEEN_BOUND_CANNOT_BE_ADDED = "PROMPT_30163" ;


    public static final String ACCOUNT_BIND_NUMS_GT = "PROMPT_30164" ;

    public static final String NEED_AUTH_INFO = "PROMPT_30165" ;

    public static final String USER_NAME_NOT_MATCH_BANK_CARD = "PROMPT_30166" ;

    public static final String ACCOUNT_IS_BLACK = "PROMPT_30167" ;

    public static final String NEW_OLD_PASSWORD_SAME = "PROMPT_60205" ;

    public static final String CPF_IS_EMPTY = "PROMPT_30168" ;

    public static final String AGENT_EXCEED_THE_MAX_LEVEL = "PROMPT_60206" ;

    public static final String AGENT_PLAN_TURNOVER_NOT_EXIST = "PROMPT_60207" ;

    public static final String AGENT_PLAN_TURNOVER_NO_CONFIG_ITEMS = "PROMPT_60208"
            ;
    public static final String AGENT_PLAN_TURNOVER_CONFIG_MAX_LIMIT = "PROMPT_60209" ;

    public static final String AGENT_PLAN_TURNOVER_CONFIG_REPEAT = "PROMPT_60210" ;

}
