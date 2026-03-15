package com.cloud.baowang.common.core.constants;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author 小智
 * @Date 3/5/23 7:27 PM
 * @Version 1.0
 */
@Slf4j
public class RedisConstants {

    /**
     * 会员客户端 首页随机奖池 key
     */
    public static final String HOME_PRIZE_POOL = "userHome_prizePool::";

    /**
     * 会员绑定银行卡:存放会员账号集合(需要去重)
     */
    public static final String USER_BIND_BANK_CARD = "bankCard::bindUser::";
    /**
     * 会员绑定虚拟币:存放会员账号集合(需要去重)
     */
    public static final String USER_BIND_VIRTUAL_CURRENCY = "virtualCurrency::bindUser::";
    /**
     * 会员绑定EBpay:存放会员账号集合(需要去重)
     */
    public static final String USER_BIND_EBPAY = "EBpay::bindUser::";
    /**
     * 会员绑定易币付:存放会员账号集合(需要去重)
     */
    public static final String USER_BIND_YBF = "Ybf::bindUser::";
    /**
     * 会员绑定OKpay:存放会员账号集合(需要去重)
     */
    public static final String USER_BIND_OKPAY = "OKpay::bindUser::";

    /**
     * 新手活动-领取奖励 公平锁Key
     */
    public static final String ACTIVITY_NOVICE_KEY = "activity_novice_key_";
    /**
     * 邀请活动-领取奖励 公平锁Key
     */
    public static final String ACTIVITY_INVITE_KEY = "activity_invite_key_";
    /**
     * 会员每日盈亏-MQ队列 公平锁Key
     */
    public static final String USER_WIN_LOSE_KEY = "user_win_lose_key_";
    /**
     * 会员额外信息编辑 公平锁key
     */
    public static final String USER_EXTRA_UPDATE_KEY = "user_extra_update_key_";
    /**
     * 新增会员审核-锁单
     */
    public static final String USER_REVIEW_ORDER_NO = "user_review_order_no_";
    /**
     * 会员转代-锁单
     */
    public static final String USER_CHANGE_AGENT_ORDER_NO = "user_change_agent_order_no_";

    /**
     * VIP等级
     */
    public static final String VIP_RANK_CONFIG = "vip::rank::";

    /**
     * VIP权益
     */
    public static final String VIP_BENEFIT_CONFIG = "vip::benefit::";

    /**
     * VIP晋级优惠
     */
    public static final String VIP_PROMOTION_CONFIG = "vip::promotion::";

    /**
     * VIP返水比例
     */
    public static final String VIP_REBATE_CONFIG = "vip::rebate::";

    /**
     * 邮箱验证码
     */
    public static final String MAIL_VERIFY = "mailVerify::";

    public static final String VENUE_WIN_LOSS_LOCK_KEY = "report:venue:winloss:lock:";

    public static final String VENUE_FIXED_WIN_LOSS_LOCK_KEY = "report:venue:fixed:winloss:lock:";

    /**
     * 会员充值审核-充值上分确认 公平锁Key
     */
    public static final String USER_RECHARGE_REVIEW_TWO_SUCCESS = "user_recharge_review_two_success_";

    /**
     * 会员人工加额审核-二审通过 公平锁Key
     */
    public static final String UP_REVIEW_TWO_REVIEW_SUCCESS = "up_review_two_review_success_";

    /**
     * 代理人工加额审核-二审通过 公平锁Key
     */
    public static final String AGENT_UP_REVIEW_TWO_REVIEW_SUCCESS = "agent_up_review_two_review_success_";

    public static final String VIP_UPGRADE_DOWNGRADE_LOCK_KEY = "vip::upgrade_downgrade::lock";

    /**
     * 活动期间申请计数 %s 活动id:
     */
    public static final String ACTIVITY_ALL_APPLY_COUNT = "activity:all:apply:count:%s";
    /**
     * 活动期间每个账号申请计数 1 %s  活动id 2 %s userAccount
     */
    public static final String ACTIVITY_ACCOUNT_APPLY_COUNT = "activity:account:apply:count:%s:%s";
    /**
     * 活动期间每天每个账号申请计数  %s 1  活动id %s 2  会员账号
     */
    public static final String ACTIVITY_EVERYDAY_APPLY_COUNT = "activity:everyday:apply:count:%s:%s";
    /**
     * 活动申请锁 %s 活动id
     */
    public static final String ACTIVITY_APPLY_LOCK = "activity:apply:lock:%s";

    /**
     * 会员参与首存活动 redis记录
     */
    public static final String USER_JOIN_ACTIVITY_REDIS = "activity::joinUser::";

    /**
     * 会员参与充值送 redis记录
     */
    public static final String USER_JOIN_RECHARGE_SEND_ACTIVITY_REDIS = "activity::rechargeSend::";


    public static final String AGENT_TRANSFER_MEMBER_LOCK_KEY = "agent::transfer::member::lock";
    public static final String AGENT_OVERFLOW_MEMBER_LOCK_KEY = "agent::overflow::member::lock";

    /**
     * 新增会员审核-锁单
     */
    public static final String AGENT_INFO_CHANGE_REVIEW_ORDER_NO = "agent:info:change:review:orderNo:";

    public static final String CONTRACT_KEY = "agent::contract::lastMonth::%s::%s";

    /**
     * 代理充值审核通过
     **/
    public static final String AGENT_DEPOSIT_REVIEW_TWO_SUCCESS = "agent:deposit:review:two:success";

    /**
     * 代理充值失败次数
     */
    public static final String AGENT_DEPOSIT_FAIL_COUNT = "agent:deposit:fail:count:%s";

    /**
     * 图形验证码登录失败次数
     */
    public static final String CAPTCHA_LOGIN_FAIL_COUNT = "captcha:login::fail:count:%s";

    /**
     * 图形验证码注册失败次数
     */
    public static final String CAPTCHA_REG_FAIL_COUNT = "captcha:reg::fail:count:%s";

    /**
     * 代理返点记录当前月 记录 %s agentAccount
     */
    public static final String AGENT_REBATE_CURRENT_CACHE = "agent:rebate:current:%s";


    /**
     * 代理安全设置编辑  %s agentInfoId
     */
    public static final String AGENT_SECURITY_SET_EDIT = "agent:security:set:edit:%s";


    /**
     * 场馆一键回收
     */
    public static final String VENUE_ONE_KEY_TRANSFER = "venue:onekey:transfer:%s";

    /**
     * 线下收款锁 1 %s 表
     */
    public static final String OFF_LINE_COLLECTION_LOCK_KEY = "offline:collection:table:lock:%s";

    /**
     * 代理登录密码错误失败次数
     */
    public static final String AGENT_PASSWORD_LOGIN_FAIL_COUNT = "agent::password::login::fail:count:%s";

    /**
     * 会员登录密码错误失败次数
     */
    public static final String USER_PASSWORD_LOGIN_FAIL_COUNT = "user::password::login::fail:count:%s::%s";
    /**
     * 会员登录锁定状态
     */
    public static final String USER_PASSWORD_LOGIN_STATUS = "user::password::login::status:%s";

    /**
     * minio domain
     */
    public static final String MINIO_DOMAIN_URL = "address:minio:domain:url";

    /**
     * 后台状态
     */
    public static final String ADD_MEMBER_REVIEW_ORDER = "add:member:review:order:%s";

    /**
     * 游戏最大ID
     */
    public static final String GAME_INFO_MAX_ID = "game:info:max_id";

    /**
     * 沙巴体育Token
     */
    public static final String SABA_GAME_TOKEN = "saba:game_token:%s";

    /**
     * 沙巴体育，余额变动锁
     */
    public static final String SABA_COIN_LOCK = "saba:coin_lock:%s";


    /**
     * 沙巴体育，接口重复请求验证
     */
    public static final String SABA_API_REPEAT_REQUEST = "saba:repeat:%s:%s";


    /**
     * 参数字典
     */
    public static final String SYSTEM_PARAM_CONFIG_KEY = "system:param:config:code:%s";

    /**
     * 代理忘记密码验证
     */
    public static final String AGENT_FORGET_PASSWORD_VERIFY = "agent:forget:password:verify:%s";

    /**
     * minio domain
     */
    public static final String USER_ID_LIST = "user:id:list:pro";

    /**
     *
     */
    public static final String USER_VERIFY_CODE_RESULT = "userAccount:verify:result:%s %s";

    /**
     * 站点信息
     */
    public static final String SITE_INFO = "siteinfo:";


   /**
     * 服务维护信息
     */
/*    public static final String KEY_SERVER_MAINTAIN_INFO_KEY = "server:maintain:info";*/
    /**
     * 站点维护信息 json 格式
     */
    public static final String KEY_SERVER_MAINTAIN_SITE_KEY = "server:maintain:site:";


    /**
     * 区域限制 国家code ipinfo code redis key
     */
    public static final String KEY_AREA_LIMIT_COUNTRY_KEY = "area:limit:country";

    /**
     * 区域限制 ip redis key
     */
    public static final String KEY_AREA_LIMIT_IP_KEY = "area:limit:ip";

    /**
     * i18n 数据表多语言redis缓存key %s 消息类型
     */
    public static final String KEY_I18N_MESSAGE_CACHE_KEY = "i18n:message:%s";

    /**
     * VIP段位
     */
    public static final String KEY_VIP_RANK_CONFIG = "vip:rank:";

    /**
     * VIP等级
     */
    public static final String KEY_VIP_GRADE_CONFIG = "vip:grade:";

    /**
     * VIP权益
     */
    public static final String KEY_VIP_BENEFIT_CONFIG = "vip:benefit:";

    /**
     * 代理语言 设置到redis
     */
    public static final String KEY_AGENT_LANGUAGE = "agent:language:";

    /*=============================redisson local cache map start================================*/



    /*=============================redisson local cache map end================================*/
    /**
     * 会员id 生成
     */
    public static final String KEY_USER_ID = "user:info:id";


    public static final String KEY_SITE_CODE = "%s:%s";



    public static final String KEY_LOBBY_GAME = "lobby_game:";


    public static final String KEY_GAME_MEMBER = "gameMember:";

    /**
     * 轮播图
     */
    public static final String KEY_LOBBY_BANNER = KEY_GAME_MEMBER + "banner";

    /**
     * 轮播图-未登陆
     */
    public static final String KEY_LOBBY_UN_BANNER = KEY_GAME_MEMBER + "unBanner";

    /**
     * 一级分类
     */
    public static final String KEY_LOBBY_GAME_ONE = KEY_GAME_MEMBER + "gameOneClass";

    /**
     * 二级分类
     */
    public static final String KEY_LOBBY_GAME_TWO = KEY_GAME_MEMBER + "gameTwoClass";

    /**
     * 场馆
     */
    public static final String KEY_LOBBY_VENUE = KEY_GAME_MEMBER + "venueList";


    /**
     * 游戏二级分类与游戏管理数据
     */
    public static final String KEY_GAME_JOIN_CLASS = KEY_GAME_MEMBER + "gameJoinClass";


    /**
     * 站点与游戏管理集合
     */
    public static final String KEY_SITE_GAME_LIST = KEY_GAME_MEMBER + "siteGameList";

    /**
     * 站点与场馆管理集合
     */
    public static final String KEY_SITE_VENUE_CONFIG_LIST = KEY_GAME_MEMBER + "siteVenueConfigList";

    /**
     * 游戏集合
     */
    public static final String KEY_GAME_INFO_LIST = KEY_GAME_MEMBER + "gameInfoList";

    /**
     * 游戏账变通知客户端延时队列
     */
    public static final String KEY_QUEUE_DELAYED_NOTICE_BALANCE_CHANGE =  "queue:delayed:noticeBalanceChange";


    /**
     * 游戏大厅-热门游戏
     */
    public static final String KEY_QUERY_LOBBY_TOP_GAME = KEY_LOBBY_GAME + "new_topGame:%s";


    /**
     * 游戏大厅 首页热门游戏列表
     */
    public static final String KEY_QUERY_LOBBY_HOME_HOT_SORT = KEY_LOBBY_GAME + "home_hot_sort:%s";


    /**
     * 游戏大厅-获取二级分类下的游戏列表
     */
    public static final String KEY_QUERY_LOBBY_BY_TWO_GAME = KEY_LOBBY_GAME + "by_two_game:%s:%s";


    /**
     * 前50个体育联赛
     */
    public static final String KEY_LOBBY_EVENTS_TOP = KEY_LOBBY_GAME + "topEvents_%s_%s";


    /**
     * 游戏大厅-获取一级分类下的游戏列表
     */
    public static final String KEY_QUERY_LOBBY_BY_ONE_GAME = KEY_LOBBY_GAME + "by_one_game:%s:%s";

    public static final String WILDCARDS = "*";

    public static final String KEY_WILDCARDS = WILDCARDS + ":%s";

    /**
     * 通配符
     */
    public static String getWildcardsKey(String key) {
        return String.format(KEY_WILDCARDS, key);
    }


    /**
     * 游戏大厅-获取站点赞助商列表
     */
    public static final String KEY_QUERY_LOBBY_TOP_PARTNER = KEY_LOBBY_GAME + "partners";


    /**
     * 游戏大厅-获取支付商列表
     */
    public static final String KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR = KEY_LOBBY_GAME + "paymentVendors";


    /**
     * 游戏大厅-体育推荐
     */
    public static final String KEY_QUERY_SPORT_EVENTS_RECOMMEND_ALL = KEY_LOBBY_GAME + "sportEventsAll";

    public static String getSiteCodeKeyConstant(String key) {
        return String.format(KEY_SITE_CODE, CurrReqUtils.getSiteCode(), key);
    }
    public static String getSiteCodeKeyConstant(String key,String siteCode){
        return String.format(KEY_SITE_CODE, siteCode, key);
    }
    public static String getToSetSiteCodeKeyConstant(String siteCode, String key) {
        return String.format(KEY_SITE_CODE, siteCode, key);
    }

    /**
     * 会员id 生成
     */
    public static final String USER_ID = "user:info:id";

    /**
     * 视讯，余额变动锁
     */
    public static final String SH_COIN_LOCK = "sh:coin_lock:%s";

    /**
     *
     */
    public static final String USER_VERIFY_CODE_LOGIN = "userAccount:verify:login:%s %s";


    /**
     * 开启的活动配置
     */
    public static final String ACTIVITY_CONFIG = "activityConfig:%s";

    public static final String ACTIVITY_CONFIG_V2 = "activityConfigV2:%s";

    public static final String SITE_CODE = "%s:";
    public static final String ACTIVITY_FLOAT_ICON_SHOW_NUMBER = SITE_CODE + "floatIconShowNumber";

    /**
     * 查询出活动模板的基础信息
     */
    public static final String ACTIVITY_TEMPLATE = "activity:template:%s";


    /**
     * 发放活动礼包
     */
    public static final String ACTIVITY_SEND_ORDER_NO = "activity:sendOrder:%s";


    /**
     * 领取礼金行为锁
     */
    public static final String ACTIVITY_GET_REWARD_LOCK = "activity_get_reward_lock:%s_%s";

    /**
     * 领取-批量领取锁
     */
    public static final String ACTIVITY_GET_BATCH_REWARD_LOCK = "activity_get_batch_reward_lock:";


    /**
     * 领取礼金锁
     */
    public static final String ACTIVITY_GET_REWARD_ID_LOCK = "activity_get_reward_id_lock:";

    /**
     * 转盘领取次数加锁，前端也加锁
     */
    public static final String ACTIVITY_SPIN_WHEEL_GET_REWARD_LOCK = "activity_spin_wheel_get_reward:%s";


    /**
     * 活动已经领取的总金额
     */
    public static final String ACTIVITY_TOTAL_AMOUNT = "activity:total_amount:%s";

    /**
     * 会员每日盈亏-MQ队列 公平锁Key
     */
    public static final String USER_RECHARGE_KEY = "user_recharge_key_";

    /**
     * 代理token缓存key
     */
    public static final String AGENT_TOKEN_CACHE_KEY = "agent::token::jwt::%s::%s";


    /**
     * 红包雨奖池金额 param1:siteCode param2:sessionId
     */
    public static final String ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT = "activity:redbag:session:%s:%s";

    /**
     * 红包雨奖池金额 param1:siteCode param2:sessionId param3:userAccount
     */
    public static final String ACTIVITY_REDBAG_SESSION_USER_COUNT = "activity:redbag:count:%s:%s:%s";

    /**
     * 红包雨奖池锁 param1:siteCode param2:sessionId
     */
    public static final String ACTIVITY_REDBAG_SESSION_PRIZE_POOL_REDUCE_LOCK = "activity:redbag:prize:pool:";

    /**
     * 红包雨 参与校验通过redis 记录  param1:siteCode param2:sessionId param3:userAccount
     */
    public static final String ACTIVITY_REDBAG_SESSION_PARTICIPATE_TAG = "activity:redbag:participate:pass:%s:%s:%s";

    /**
     * 红包雨 参与标记redis 记录  param1:siteCode param2:sessionId param3:userAccount
     */
    public static final String ACTIVITY_REDBAG_SESSION_SEND_TAG = "activity:redbag:send:pool:%s:%s:%s";
    /**
     * 红包雨 会员结算redis lock  param1:siteCode param2:sessionId param3:userid
     */
    public static final String ACTIVITY_REDBAG_SESSION_SETTLEMENT_USER = "activity:redbag:settlement:";
    /**
     * 红包雨 会员结算redis tag  param1:siteCode param2:sessionId param3:userid
     */
    public static final String ACTIVITY_REDBAG_SESSION_SETTLEMENT_USER_TAG = "activity:redbag:settlement:tag:%s:%s:%s";
    /**
     * 红包雨 场次重算锁 记录  param1:siteCode
     */
    public static final String ACTIVITY_REDBAG_RESESSION_SITE = "activity:redbag:resession:";
    /**
     * 红包雨 推送锁 记录  param1:siteCode
     */
    public static final String ACTIVITY_REDBAG_SESSION_OPERATE_SITE = "activity:redbag:session:operate:";

    /**
     * 每个站点的场馆当天的打码量信息,统计
     */
    public static final String VENUE_SITE_DAY_TOTAL_BET_AMOUNT = "venue:%s:site:%s:day:%s:total_bet_amount";

    public static final String ACTIVITY_SPIN_WHEEL_LOCK_KEY = "activity:spin:wheel:lock:user:%s";

    public static final String TASK_RECEIVE_LOCK_KEY = "task:receive:lock:key:user:%s";

    public static final String TASK_SEND_LOCK_KEY = "task:send:lock:key:user:";

    /**
     * 去参与任务锁
     */
    public static final String TO_ACTIVITY_LOCK = "to_activity:";

    /**
     * 去参与游戏
     */
    public static final String TO_ACTIVITY_GAME_LOCK = "to_activity_game:";

    /**
     * 去参与任务锁-转盘
     */
    public static final String TO_ACTIVITY_SPIN_WHEEL_LOCK = "to_activity_spin_wheel:";

    /**
     * 活动基础信息缓存
     */
    public static final String ACTIVITY_BASE_LIST = "activity_base";

    /**
     * 活动基础信息缓存V2
     */
    public static final String ACTIVITY_BASE_V2_LIST = "activity_base_V2";

    /**
     * 场馆信息
     */
    public static final String VENUE_INFO_LIST = "venue:code:%s";

    /**
     * 场馆信息
     */
    public static final String NEW_VENUE_INFO_LIST = "venue_code:%s";



    /**
     * 平台_商户
     */
    public static final String VENUE_INFO_PLAT_MERCHANT = "venue:merchants:%s_%s";



    /**
     * 平台_商户密钥
     */
    public static final String VENUE_INFO_PLAT_MERCHANT_KEY = "venue:merchantsKey:%s_%s";


    /**
     * 三方游戏token
     */
    public static final String THREE_GAME_TOKEN = "three_token:%s:user_id:%s";


    /**
     * 币种名称
     */
    public static final String CURRENCY_NAME = "currency:name:%s";
    /**
     * 币种符号
     */
    public static final String CURRENCY_SYMBOL = "currency:symbol:%s";

    /**
     * 用户连接ws锁
     */
    public static final String WS_ONLINE_USER_LOCK = "ws:user:online:lock:";

    /**
     * 任务锁
     */
    public static final String TASK_EXPIRE_LOCK = "task:expire_lock:";

    /**
     * 任务锁
     */
    public static final String REWARD_SPIN_WHEEL_LOCK = "activity:reward_spin_wheel_LOCK:";

    /**
     * 会员提款审核锁
     */
    public static final String USER_WITHDRAW_REVIEW_ORDER_ID = "lockOrUnLock.userWithdraw.review.key.%s";

    /**
     * 代理提款审核锁
     */
    public static final String AGENT_WITHDRAW_REVIEW_ORDER_ID = "lockOrUnLock:agentWithdraw:review:key:%s";

    /**
     * 沙巴体育-拉单出现异常后
     */
    public static final String SBA_PULL_ERROR_BET_RECORD = "sba_pull_error_bet_record";


    /**
     * 活动超时逻辑锁
     */
    public static final String ACTIVITY_AWARD_EXPIRE = "activity_%s_award_lock";

    /**
     * 沙巴下注锁
     */
    public static final String SBA_PLAT_BET_LOCK = "sba_plat_bet_lock:%s";

    /**
     * 会员打码量添加
     */
    public static final String ADD_TYPING_AMOUNT_LOCK_KEY = "add.typingAmount.lock.key.%s";
    /**
     * 代理人工加额审核锁
     */
    public static final String AGENT_WITHDRAW_LOCK_ORDER_ID = "lockOrUnLock:agentWithdraw:review:key:%s";

    public static final String AGENT_VERIFY_CODE_RESULT = "agentAccount:verify:result:%s %s";


    public static final String VERIFY_CODE_CACHE = "verify:%s:%s";


    /**
     * 彩票，余额变动锁
     */
    public static final String ACELT_COIN_LOCK = "acelt:coin_lock";

    /**
     * 帮助中心信息配置集合
     */
    public static final String KEY_HELP_CENTER_OPTION = "help:center:option:list";

    /**
     * 跑马灯<公告>
     */
    public static final String KEY_MARQUEE_CONTENT_CACHE = "marquee:content:cache";

    /**
     * 强制弹窗<公告>
     */
    public static final String KEY_FORCED_POPUP_CACHE = "forced:pop-up::cache";

    /**
     * 彩票最大的赢金额
     */
    public static final String ACE_MAX_AMOUNT = "ace_max_win_amt_%s:%s:%s_%s";

    /**
     * 彩票游戏大厅汇率
     */
    public static final String ACELT_RATE = "acelt_rate%s";


    /**
     * 每日竞赛的-指定站点-指定-日下的 指定竞赛的前100条数据
     */
    public static final String ACTIVITY_DAILY_TOP_100 = "daily:activity_top_100_%s_%s_%s_%s";


    /**
     * 登陆场馆
     */
    public static final String VENUE_LOGIN = "venue_login:%s";


    /**
     * app 闪屏图
     */
    public static final String KEY_SPLASH_SCREEN = KEY_GAME_MEMBER + "splash_screen";

    /**
     * 会员下注时间缓存
     */
    public static final String USER_BET_TIME_FLUSH = "user:bet:time:";
    /**
     * 字典配置缓存
     */
    public static final String SYSTEM_DICT_CONFIG = "system:dict:config:%s:";
    /**
     * 总台/站点后台,用户登录锁定key值
     */
    public static final String LOGIN_ERROR_USER = "system:login:error:user:%s:%s";


    public static String getCommonConfigurationKey(String key,String value) {
        return String.format(KEY_SITE_CODE,key, value);
    }
    /**
     * 控及站点后台当日单次谷歌验证码输入错误次数
     */
    public static final String KEY_GOOGLE_AUTH_VERIFY_LIMIT="key_google_auth_verify_limit:%s:%s";

    /**
     *单用户三分钟内可登录最大次数
     */
    public static final String KEY_LOGIN_TIMES_THREE_MINUTES_LIMIT ="KEY_LOGIN_TIMES_LIMIT:%s:%s";

    /**
     * 总控及站点及用户登录验证连续错误五次锁定时间
     */
    public static final String KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT="KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT:%s:%s";



    /**
     * 总控及站点谷歌验证码连续错误五次锁定时间
     */
    public static final String KEY_GOOGLE_AUTH_FIVE_TIMES_LIMIT="KEY_GOOGLE_AUTH_FIVE_TIMES_LIMIT:%s:%s";


    /**
     * 意见反馈三分钟限制次数
     */
    public static final String KEY_FEEDBACK_TIMES_LIMIT = "KEY_FEEDBACK_TIMES_LIMIT:%s:%s";

    /**
     * 邮箱发送上限
     */
    public static final String KEY_SEND_VERIFY_COUNT_MAIL = "KEY_SEND_VERIFY_COUNT_MAIL:%s:%s:%s";
    /**
     * 短信发送上限
     */
    public static final String KEY_SEND_VERIFY_COUNT_SMS = "KEY_SEND_VERIFY_COUNT_SMS:%s:%s:%s";

    /**
     * 用户头像配置单日点击可自由替换次数
     */
    public static final String KEY_AVATAR_CHANGE_TIMES_LIMIT = "KEY_AVATAR_CHANGE_TIMES_LIMIT:%s:%s";

    /**
     * 单用户当日修改密码最大次数
     */
    public static final String KEY_PW_CHANGE_TIMES_LIMIT = "KEY_PW_CHANGE_TIMES_LIMIT:%s:%s";

    /**
     * 会员登录密码错误连续失败次数
     */
    public static final String USER_PASSWORD_CONTINUE_LOGIN_FAIL_COUNT = "user::password::continue::login::fail:count:%s::%s";

    /**
     * 用户是否被锁定key
     */
    public static final String KEY_LOCKED_FOR_5_FAILED_ATTEMPTS="KEY_LOCKED_FOR_5_FAILED_ATTEMPTS:%s:%s";

    public static final String LOCK_KEY_NOTICE_BALANCE_CHANGE = "lock:key:notice:balance:change:user:";

    /**
     * 会员人工提款 出款锁
     */
    public static final String USER_WITHDRAW_MANUAL_ORDER_ID = "pay.userWithdraw.manual.key.%s";


    /**
     * FTG，余额变动锁
     */
    public static final String FTG_COIN_LOCK = "ftg:coin_lock";


    /**
     * 多宝电竞，余额变动锁
     */
    public static final String DB_DJ_COIN_LOCK = "db_dj:coin_lock";


    /**
     * 多宝熊猫体育，余额变动锁
     */
    public static final String DB_PANDA_SPORT_COIN_LOCK = "db_panda_sport:coin_lock";


    /**
     * 游戏TOKEN
     */
    public static final String VENUE_TOKEN = "venue_wallet_token:%s:%s";
    public static final String FTG_TOKEN = "ftg:token:%s";

    /**
     * CQ9，余额变动锁
     */
    public static final String CQ9_COIN_LOCK = "cq9:coin_lock";


    /**
     * 游戏TOKEN
     */
    public static final String CQ9_TOKEN = "cq9:token:%s";

    /**
     * 总控场馆修改状态10秒一次
     */
    public static final String UP_VENUE_LOCK = "up_venue_lock";


    public static final String SITE_CURRENCY ="currency:site:%s";


    /**
     * 游戏大厅侧边栏
     */
    public static final String SITE_LOBBY_LABEL = KEY_LOBBY_GAME + "label_news:%s";


    public static final String SITE_USER_DUPLICATE ="site:%s:user:%s:duplicate:";

    /**
     * 福利中心领取锁
     */
    public static final String REBATE_RECEIVE_LOCK_KEY = "rebate:receive:lock:key:user:%s";

    /**
     * 代理-下级管理,新增代理锁
     */
    public static final String AGENT_ADD_LOCK_KEY = "agent:add:lock:key:%s";

    /**
     * 签到活动
     */
    public static final String ACTIVITY_CHECK_IN_ADD_LOCK_KEY = "site:activity:check:makeup:lock:key:%s";

    /**
     * 签到活动
     */
    public static final String ACTIVITY_CHECK_IN_APP_LOCK_KEY = "site:activity:check:checkin:lock:key:%s";


    /**
     * 批量修改游戏状态锁-总台
     */
    public static final String UP_ADMIN_GAME_STATUS_BATCH = "admin_batch_status_lock";


    /**
     * 批量修改游戏状态锁-站点
     */
    public static final String UP_SITE_GAME_STATUS_BATCH = "site_batch_status_lock_%s";


    /**
     * 批量修改场馆状态锁
     */
    public static final String UP_ADMIN_VENUE_STATUS_BATCH = "admin_venue_batch_status_lock";

    /**
     * 免费旋转
     */

    public static final String FREE_GAME_UPDATE_CON = "free_game::update::lock";

    /**
     * 领取礼金锁
     */
    public static final String OVER_DRAW_LOG_SITE_CODE_LOCK = "over_draw_log_site_code_lock:";


    /**
     * 场馆-类型
     */
    public static final String VENUE_TYPE = "venue_type";


    /**
     * 沙巴 获取联赛
     */
    public static final String SBA_EVENT_INFO_LAST_VERSION_KEY = "sba_event_info_last_version_key";


    /**
     * 沙巴 获取体育联赛
     */
    public static final String SYS_EVENTS_INFO = "sys_events_info";

    /**
     * 每日竞赛执行
     */
    public static final String SITE_DAILY_ROBOT_LOCK = "site_daily_robot:%s";


    /**
     * 充值创建热钱包地址
     */
    public static final String RECHARGE_GEN_HOT_WALLET_ADDRESS = "user:recharge:genHotWalletAddress:%s";


    public static final String VENUE_LANGUAGE = "venue_language_token:%s:%s";

    public static final String SEXY_CANCEL_BET_KEY = "sexy_cancel_bet_key:%s:%s";

    /**
     * 取消投注的key
     */
    public static final String EVO_CANCEL_BET_KEY = "evo_cancel_bet_key:%s:%s";

    /**
     * 绑定/解绑账户密码校验
     */
    public static final String ACCOUNT_CHECK_PASSWORD = "account::bind::checkPassword::%s";

    /**
     * vip 领取
     */
    public static final String REWARD_VIP_REWARD_LOCK = "vip:reward_user_vip_lock:%s";

    /**
     * 账务系统 调用标识
     *  空或0 默认走走老的代码逻辑,新的账务接口不会被调用;打码量清除走老的接口
     *  1 默认只是走流量过来(kafka形式,账务模块提供消费者,只作为线上数据观察,不做业务逻辑处理);打码量清除走老的接口
     *  2 全部走新的账务接口,老的接口不会被调用;打码量清除走新的接口
     ***/
    public static final String ACCOUNT_OPEN_FLAG = "account:open:flag";

}
