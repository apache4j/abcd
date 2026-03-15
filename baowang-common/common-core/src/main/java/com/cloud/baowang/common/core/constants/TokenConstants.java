package com.cloud.baowang.common.core.constants;

/**
 * Token的Key常量
 */
public class TokenConstants {
    /**
     * 令牌自定义标识
     */
    public static final String SIGN = "Sign";

    /**
     * 客户端标识
     */
    public static final String CLIENT = "client";

    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥
     */
    public final static String SECRET = "mayaabcde121231231fg123456";


    /**
     * 1分鐘, 单位秒
     */
    public final static Integer ONE_MINUTES_IN_SECONDS = 60;

    /**
     * 30 分鐘, 单位秒
     */
    public final static Integer HALF_HOUR_IN_SECONDS = ONE_MINUTES_IN_SECONDS * 30;

    /**
     * 10 秒鐘, 单位毫秒
     */
    public final static Integer TEN_SECONDS = 10000;

    /**
     * 7 天，单位 小時
     */
    public final static Integer SEVEN_DAYS = 7 * 24;

    /**
     * 主键id
     */
    public static final String ID = "id";

    /**
     * 用户ID字段 不是主键id
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 代理ID字段 不是主键id
     */
    public static final String DETAILS_AGENT_ID = "agent_id";

    /**
     * 用户账号字段
     */
    public static final String DETAILS_USER_ACCOUNT = "user_account";

    /**
     * 账号类型字段
     */
    public static final String DETAILS_ACCOUNT_TYPE = "account_type";

    /**
     * admin::id
     */
    public static final String DETAILS_ADMIN_ID = "admin_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "userName";

    public static final String SUPER_ADMIN = "superAdmin";


    /**
     * 用户标识
     */
    public static final String USER_KEY = "user_key";

    public static final String ADMIN_KEY = "admin_key";

    /**
     * 请求来源
     */
    public static final String FROM_SOURCE = "from-source";

    /**
     * 内部请求
     */
    public static final String INNER = "inner";

    public final static String JWT_CACHE_KEY_HEAD= "jwt::siteCode::%s";
    /**
     * Jwt Key规则 全局唯一
     * 站点
     * 登录端:DomainInfoTypeEnum
     * 用户id
     */
    public final static String JWT_CACHE_KEY = JWT_CACHE_KEY_HEAD+"::domainType::%s::userId::%s";



    /**
     * 缓存刷新时间
     */
    public final static long FLUSH_TIME = 30 * 60 * 60;

    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;

    /**
     * 密码最大错误次数
     */
    public final static int PASSWORD_MAX_RETRY_COUNT = 3;

    /**
     * 密码锁定时间，默认10（分钟）
     */
    public final static long PASSWORD_LOCK_TIME = 10;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY_HEAD = "login_tokens::siteCode::%s";

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = LOGIN_TOKEN_KEY_HEAD+"::domainType::%s::tokenVal::%s";


    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt::";

    /**
     * 会员令牌有效期 默认24小时 单位毫秒
     */
    public final static Long TOKEN_EXPIRE_TIME = 3600000 * 24L;


    /**
     * FEIGN调用传递的上下文对象
     */
    public static final String FEIGN_USER = "FEIGN_USER";

    /**
     * 站点code
     */
    public static final String SITE_CODE = "SITE_CODE";

}
