package com.cloud.baowang.common.core.constants;

/**
 * 缓存常量信息
 *
 * @author qiqi
 */
public class CacheConstants {

    /*==================二级缓存====================*/
    private static final String L2CACHE = "l2cache:";
    /*默认key 针对全局性key*/
    public static final String LIST = "'list'";
    public static final String INFO = "'info'";
    /*语言列表*/
    public static final String LANGUAGE_CACHE = L2CACHE + "lang:cache";
    /**币种列表**/
    public static final String CURRENCY_CACHE = L2CACHE + "currency:cache";
    /**充值类型**/
    public static final String RECHARGE_TYPE_CACHE = L2CACHE + "rechargeType:cache";
    /**充值方式**/
    public static final String RECHARGE_WAY_CACHE = L2CACHE + "rechargeWay:cache";
    /*系统参数缓存 下拉框列表缓存*/
    public static final String SYSTEM_BUSINESS_PARAM_CACHE = L2CACHE + "systemParam";
    /*=================活动==============*/
    /** 红包雨场次信息缓存 **/
    public static final String ACTIVITY_REDBAG_RAIN_SESSION_CACHE = L2CACHE + "activity:redbag:session";
    /** 红包雨场次id缓存 **/
    public static final String ACTIVITY_REDBAG_RAIN_SESSION_ID_CACHE = L2CACHE + "activity:redbag:session:id";

    /*==========================redisson缓存========================*/
    private static final String REDISSON = "redisson:";
    /*系统字典缓存 i18n配置使用 可以删除*/
    public static final String SYSTEM_BUSINESS_DICT_CACHE = REDISSON + "systemDict";
    /**
     * 域名信息 不可直接手动删除，需在页面操作
     */
    public static final String KEY_DOMAIN_INFO = REDISSON + "domain:info:cache";
    /**
     * 站点-平台币
     */
    public static final String KEY_SITE_PLAT_CURRENCY = REDISSON + "site:platCurrency:cache";

    /**币种列表**/
    public static final String KEY_CURRENCY_CACHE = REDISSON + "currency:cache";

    public static final String KEY_SITE_PLAT_CURRENCY_SYMBOL = REDISSON + "site:platCurrencySymbol:cache";

    /**
     * 平台币图标
     */
    public static final String KEY_SITE_PLAT_CURRENCY_ICON = REDISSON + "site:platCurrencyIcon:cache";

    /**
     * 语种信息 可以删除
     */
    public static final String LANGUAGE_INFO = REDISSON + "language:info:cache";
    /**
     * i18n message redis key i18n信息 可以删除
     */
    public static final String KEY_I18N_MESSAGE = REDISSON + "i18n:message";
    /**
     * 总控权限信息 redis key 不可以直接删除
     */
    public static final String KEY_ADMIN_AUTH_INFO_KEY = REDISSON + "admin:auth:role";
    /**
     * system param redis key 下拉框查询缓存 可以删除
     */
    public static final String KEY_SYSTEM_PARAM = REDISSON + "system:param";

    public static final String USER_LOGIN_DAY = REDISSON + "user:login:day:%s";


    public static final String VENUE_MAINTAIN_CLOSE_STATUS = REDISSON + "venue:maintain_close:status:%s";

    public static final String IP_ADDRESS_CURRENCY = REDISSON + "venue:ip:address:currency";
    public static final String DEFALUT_IP_ADDRESS_CURRENCY = REDISSON + "defalut:venue:ip:address:currency";

    public static final String ERROR_ORDER_NO = REDISSON + "venue:error:orderNo:";

    public static final String VENUE_CMD_LANG = REDISSON + "venue:lang:";

    public static final String VENUE_CMD_COLLUSION = REDISSON + "venue:collusion:";
    public static final String SYSTEM_IPAPI_INFO_IP = REDISSON + "system:ipapi:info:ip:";

    public static final String SITE_HANDICAPMODE = REDISSON + "site:handicapMode:cache:";

    public static final String SITE_CN_VIP_CONFIG = REDISSON + "site:cn:vip:config:";


}
