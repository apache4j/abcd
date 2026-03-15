package com.cloud.baowang.common.core.enums;

import com.cloud.baowang.common.core.constants.ConstantsCode;

public enum ResultCode {
    /*=====================系统异常定义   1xxxx=====================*/
    SUCCESS(10000, ConstantsCode.SUCCESS, "成功"),
    SERVER_INTERNAL_ERROR(10001, ConstantsCode.SERVER_INTERNAL_ERROR, "服务内部错误"),
    PARAM_ERROR(10002, ConstantsCode.PARAM_ERROR, "参数错误"),
    SYSTEM_ERROR(10003, ConstantsCode.SYSTEM_ERROR, "系统异常"),
    PARAM_NOT_VALID(10004, ConstantsCode.PARAM_NOT_VALID, "参数无效"),
    EXPORT_ERROR(10005, ConstantsCode.EXPORT_ERROR, "导出异常"),
    TOKEN_MISSION(10006, ConstantsCode.TOKEN_MISSION, "令牌为空"),
    TOKEN_INVALID(10007, ConstantsCode.TOKEN_INVALID, "令牌错误"),
    LOGIN_EXPIRE(10008, ConstantsCode.LOGIN_EXPIRE, "登录过期"),
    SIGN_EMPTY(10009, ConstantsCode.SIGN_EMPTY, "签名为空"),
    SIGN_ERROR(10010, ConstantsCode.SIGN_ERROR, "签名异常"),
    SIGN_EXPIRED(10011, ConstantsCode.SIGN_EXPIRED, "签名过期"),
    NO_HAVE_DATA(10012, ConstantsCode.NO_HAVE_DATA, "无数据"),
    DATA_NOT_EXIST(10013, ConstantsCode.DATA_NOT_EXIST, "数据不存在"),
    NOT_API_PERMISSIONS(10014, ConstantsCode.NOT_API_PERMISSIONS, "无API访问权限!"),


    CURRENT_LIMIT(10015, ConstantsCode.CURRENT_LIMIT, "请求过于频繁已被限流"),
    CURRENT_REQUEST_LIMIT(10016, ConstantsCode.CURRENT_REQUEST_LIMIT, "该请求已被限流"),
    HAS_BLOWN(10017, ConstantsCode.HAS_BLOWN, "已触发熔断规则"),
    SYSTEM_PROTECTION(10018, ConstantsCode.SYSTEM_PROTECTION, "已触发系统保护规则"),
    UNCAUGHT_EXCEPTION(10020, ConstantsCode.UNCAUGHT_EXCEPTION, "未捕捉异常"),

    SYSTEM_LOCK_ERROR(10021, ConstantsCode.SYSTEM_LOCK_ERROR, "系统锁异常"),
    NOT_FUND_PLATFORM(10022, ConstantsCode.NOT_FUND_PLATFORM, "游戏平台不存在"),

    MAX_LENGTH(10023, ConstantsCode.MAX_LENGTH, "长度过长"),
    MISSING_PARAMETERS(10024, ConstantsCode.MISSING_PARAMETERS, "缺少参数"),
    ONE_KEY_RECYCLING(10025, ConstantsCode.ONE_KEY_RECYCLING, "操作过于频繁,请稍后再试"),
    VENUE_CURRENCY_NOT(10026, ConstantsCode.VENUE_CURRENCY_NOT, "该游戏,暂不支持您的币种"),
    UPDATE_ERROR(10027, ConstantsCode.UPDATE_ERROR, "修改数据失败"),
    INSERT_ERROR(10028, ConstantsCode.INSERT_ERROR, "新增数据失败"),

    DATA_EXISTS_MORE(10029, ConstantsCode.DATA_EXISTS_MORE, "数据存在多条"),

    SERVER_MAINTENANCE(10030, ConstantsCode.SERVER_MAINTENANCE, "服务维护"),
    AREA_LIMIT(10031, ConstantsCode.AREA_LIMIT, "区域限制"),
    IP_NO_EXIST(10032, ConstantsCode.IP_NO_EXIST, "IP不存在"),
    ROUTE_NOT_FOUND(10033, ConstantsCode.ROUTE_NOT_FOUND, "未找到路由"),
    DATA_IS_EXIST(10034, ConstantsCode.DATA_IS_EXIST, "数据已存在"),
    ENABLED_NOT_EDITABLE(10035, ConstantsCode.ENABLED_NOT_EDITABLE, "已启用不可编辑"),
    REFERER_EMPTY(10036, ConstantsCode.REFERER_EMPTY, "Referer不能为空"),
    OPEN_STATUS(10037, ConstantsCode.OPEN_STATUS, "活动启用中"),
    LANG_NOT_FULL(10038, ConstantsCode.LANG_NOT_FULL, "语言缺失"),
    XXL_JOB_API_ERROR(10039, ConstantsCode.XXL_JOB_API_ERROR, "定时任务API异常"),
    TIME_NOT_GOOD(10040, ConstantsCode.TIME_NOT_GOOD, "开始时间不能大于结束时间"),
    SITECODE_IS_ERROR(10041, ConstantsCode.SITECODE_IS_ERROR, "域名错误"),

    RECEIVE_FAIL_DESCRIPTION(10042, ConstantsCode.RECEIVE_FAIL_DESCRIPTION, "领取失败,请稍后再试"),

    START_TIME_NOT_GOOD(10043, ConstantsCode.START_TIME_NOT_GOOD, "活动开始时间不能早于当前时间"),
    DISTRIBUTED_LOCK_NOT_OBTAINED(10044, ConstantsCode.DISTRIBUTED_LOCK_NOT_OBTAINED, "未获取到分布式锁"),

    DELETE_GAME_ERROR_TWO(10045, ConstantsCode.DELETE_GAME_ERROR_TWO, "该一级分类下存在二级分类"),

    DELETE_OPEN_ERROR(10046, ConstantsCode.DELETE_OPEN_ERROR, "未禁用,不允许操作"),
    NULL_PARAMETERS(10047, ConstantsCode.NULL_PARAMETERS, "参数不能为空"),
    AMOUNT_GREATER_ZERO(10048, ConstantsCode.AMOUNT_GREATER_ZERO, "金额不能为负数"),
    GAME_TWO_UP_ERROR(10049, ConstantsCode.GAME_TWO_UP_ERROR, "一级分类不允许修改"),
    PLAN_CONFIG_COUNT_ERROR(10050, ConstantsCode.PLAN_CONFIG_COUNT_ERROR, "阶梯分成配置最多20条"),
    FILE_MAX_SIZE_ERROR(10051, ConstantsCode.FILE_MAX_SIZE_ERROR, "超过允许上传文件最大限制"),
    EXPORT_FIELD_NOT_NULL(10052, ConstantsCode.EXPORT_FIELD_NOT_NULL, "导出字段不能为空"),
    OUT_TIME_RANGE(10053, ConstantsCode.OUT_TIME_RANGE, "请缩小搜索范围至31天,粒度精确到小时"),
    VENUE_TYPE_REPEAT(10054, ConstantsCode.VENUE_TYPE_REPEAT, "已存在同类型的赛事"),
    ABNORMAL_BONUS_RATIO(10055, ConstantsCode.ABNORMAL_BONUS_RATIO, "奖励配置异常"),
    LOGIN_ERROR_OTHER_AREA(10056, ConstantsCode.LOGIN_ERROR_OTHER_AREA, "您已在其他客户端登录，若非您本人操作，请修改密码"),

    AVATOR_NAME_REPEAT(10058, ConstantsCode.AVATOR_NAME_REPEAT, "头像名称不可重复"),
    SERVER_DISABLE(10059, ConstantsCode.SERVER_DISABLE, "服务停止运营"),

    CURRENCY_HAVE_NO_LIMIT(10060, ConstantsCode.CURRENCY_HAVE_NO_LIMIT, "【%s】账号与币种不一致"),
    CURRENCY_HAVE_ALREADY_CONFIG(10061, ConstantsCode.CURRENCY_HAVE_ALREADY_CONFIG, "【%s】账号已经配置了免费旋转"),
    USER_ACCOUNT_HAVE_NO(10062, ConstantsCode.USER_ACCOUNT_HAVE_NO, "请检查账号"),

    FILE_ACCOUNT_WRONG_NO(10063, ConstantsCode.FILE_ACCOUNT_WRONG_NO, "文件解析错误"),
    FILE_ACCOUNT_WRONG_LIMIT(10064, ConstantsCode.FILE_ACCOUNT_WRONG_LIMIT, "导入失败"),
    FILE_ACCOUNT_WRONG_LIMIT_AMOUNT(10065, ConstantsCode.FILE_ACCOUNT_WRONG_LIMIT_AMOUNT, "请检查限注金额"),

    FILE_ACCOUNT_WRONG_NO_EXCEPT(10066, ConstantsCode.FILE_ACCOUNT_WRONG_NO_EXCEPT, "导入数据异常"),

    CONTACT_CUSTOMER_SERVICE(10067, ConstantsCode.CONTACT_CUSTOMER_SERVICE, "暂时无法操作,请联系客服"),

    SITE_IS_CLOSE(10068, ConstantsCode.SITE_IS_CLOSE, "站点已关闭"),
    ACTIVITY_REDEMPTION_CODE_COMMON(10069,ConstantsCode.ACTIVITY_REDEMPTION_CODE_COMMON,"兑换失败"),
    /**
     * 所有自定义成功描述，以13000开头
     */
    APPLY_SUCCESS(130001, ConstantsCode.APPLY_SUCCESS, "申请成功"),
    RECEIVE_SUCCESS(130002, ConstantsCode.RECEIVE_SUCCESS, "领取成功"),


    /**
     * 通用校验提示
     */
    OLD_PASSWORD_NULL(14000, ConstantsCode.OLD_PASSWORD_NULL, "旧密码不能为空"),
    NEW_PASSWORD_NULL(14001, ConstantsCode.NEW_PASSWORD_NULL, "新密码不能为空"),
    CONFIRM_PASSWORD_NULL(14002, ConstantsCode.CONFIRM_PASSWORD_NULL, "确认密码不能为空"),
    USER_PASSWORD_NULL(14003, ConstantsCode.USER_PASSWORD_NULL, "用户密码不能为空"),
    PARAM_MISSING(14004, ConstantsCode.PARAM_MISSING, "参数缺失"),
    Google_MISSING(14005, ConstantsCode.Google_MISSING, "Google验证码不能为空"),
    AGENT_MISSING(14006, ConstantsCode.AGENT_MISSING, "代理账号不能为空"),
    USER_MISSING(14007, ConstantsCode.USER_MISSING, "用户账号不能为空"),
    VERIFY_CODE_LIMIT_DAY(14008, ConstantsCode.VERIFY_CODE_LIMIT_DAY, "请明天再试"),
    VERIFY_CODE_LIMIT_HOUR(14009, ConstantsCode.VERIFY_CODE_LIMIT_HOUR, "请稍后再试"),
    AREA_CODE_NOT_USE(14010, ConstantsCode.AREA_CODE_NOT_USE, "不支持此区号"),
    LINK_CANNOT_BE_EMPTY(14016, ConstantsCode.LINK_CANNOT_BE_EMPTY, "溢出链接不能为空"),
    LINK_LENGTH_ERR(14012, ConstantsCode.LINK_LENGTH_ERR, "溢出链接不能超过100个字符"),
    REASON_TOO_LONG(14013, ConstantsCode.REASON_TOO_LONG, "申请理由不能超过50个字符"),
    MEMBER_CANNOT_APPLY(14014, ConstantsCode.MEMBER_CANNOT_APPLY, "该会员已有上级，无法申请"),
    MEMBER_REGISTER_OVER_3_DAYS(14015, ConstantsCode.MEMBER_REGISTER_OVER_3_DAYS, "该会员注册时间已超3天"),
    MIN_LENGTH_BIGGER(14017, ConstantsCode.MIN_LENGTH_BIGGER, "最小长度不能大于最大长度"),
    IP_NOT_ALLOW(14018, ConstantsCode.IP_NOT_ALLOW, "IP不在白名单中，请联系管理员"),
    MAIL_CODE_ERROR(14019, ConstantsCode.MAIL_CODE_ERROR, "邮箱或验证码错误"),



    /*===================== user服务异常定义   2xxxx=====================*/

    LABEL_ERROR(20000, ConstantsCode.LABEL_ERROR, "标签名字不能重复"),
    USER_LABEL_USE(20001, ConstantsCode.USER_LABEL_USE, "该标签在使用中,不能删除"),
    USER_REMARK_QUERY_ERROR(20002, ConstantsCode.USER_REMARK_QUERY_ERROR, "会员详情备注信息查询失败!!!"),
    USER_NOT_EXIST(20003, ConstantsCode.USER_NOT_EXIST, "用户不存在"),
    USER_BASIC_QUERY_ERROR(20004, ConstantsCode.USER_BASIC_QUERY_ERROR, "会员详情基本信息查询失败!!!"),
    /**/USER_INFO_ADD_ERROR(20005, ConstantsCode.USER_INFO_ADD_ERROR, "会员详情登录信息查询失败!!!"),
    USER_INFO_LOGIN_QUERY_ERROR(20006, ConstantsCode.USER_INFO_LOGIN_QUERY_ERROR, "用户不存在,无法编辑信息!!!"),
    USER_EXISTS_TO_REVIEWED(20007, ConstantsCode.USER_EXISTS_TO_REVIEWED, "存在待审核数据,无法提交"),
    PHONE_BOUND(20008, ConstantsCode.PHONE_BOUND, "手机号已绑定"),
    MAIL_BOUND(20009, ConstantsCode.MAIL_BOUND, "邮箱已被绑定"),
    USER_LABEL_DEL_ERROR(20010, ConstantsCode.USER_LABEL_DEL_ERROR, "会员更新标签审核处理中,不能删除该标签"),
    PHONE_ERROR(20011, ConstantsCode.PHONE_ERROR, "请对手机号码进行检查"),
    EMAIL_ERROR(20012, ConstantsCode.EMAIL_ERROR, "请对邮箱进行检查"),
    USER_ACCOUNT_REPEAT_ERROR(20013, ConstantsCode.USER_ACCOUNT_REPEAT_ERROR, "账号已存在"),
    USER_PASSWORD_ERROR(20014, ConstantsCode.USER_PASSWORD_ERROR, "请对登录密码进行检查"),
    SUPER_AGENT_ACCOUNT_NO_HAVE(20015, ConstantsCode.SUPER_AGENT_ACCOUNT_NO_HAVE, "测试账号类型不能有上级代理"),
    VIP_RANK_SAVE_ERROR(20016, ConstantsCode.VIP_RANK_SAVE_ERROR, "VIP等级配置保存错误!!!"),
    VIP_RANK_QUERY_ERROR(20017, ConstantsCode.VIP_RANK_QUERY_ERROR, "VIP等级配置查询错误!!!"),
    VIP_RANK_QUERY_LAST_ERROR(20018, ConstantsCode.VIP_RANK_QUERY_LAST_ERROR, "VIP等级配置恢复上次设置错误!!!"),
    VIP_CHANGE_QUERY_ERROR(20019, ConstantsCode.VIP_CHANGE_QUERY_ERROR, "变更记录查询错误!!!"),
    VIP_BENEFIT_SAVE_ERROR(20020, ConstantsCode.VIP_BENEFIT_SAVE_ERROR, "VIP权益配置保存错误!!!"),
    VIP_BENEFIT_QUERY_ERROR(20021, ConstantsCode.VIP_BENEFIT_QUERY_ERROR, "VIP权益配置查询错误!!!"),
    VIP_BENEFIT_QUERY_LAST_ERROR(20022, ConstantsCode.VIP_BENEFIT_QUERY_LAST_ERROR, "VIP权益配置恢复上次设置错误!!!"),
    VIP_OPERATION_QUERY_ERROR(20023, ConstantsCode.VIP_OPERATION_QUERY_ERROR, "操作配置查询错误!!!"),
    VIP_BET_AMOUNT_UPGRADE_ERROR(20024, ConstantsCode.VIP_BET_AMOUNT_UPGRADE_ERROR, "累计有效流水设置有误!!!"),
    USER_CHANGE(20025, ConstantsCode.USER_CHANGE, "会员变更记录查询失败"),
    USER_REVIEW_LOCK_ERROR(20026, ConstantsCode.USER_REVIEW_LOCK_ERROR, "不能被多人锁单"),
    USER_LOGIN_LOG_QUERY_ERROR(20027, ConstantsCode.USER_LOGIN_LOG_QUERY_ERROR, "会员登录日志查询失败!!!"),
    OPERATING_BY_OTHER(20028, ConstantsCode.OPERATING_BY_OTHER, "该订单已被其他操作"),
    ORDER_NOT_EXIST(20029, ConstantsCode.ORDER_NOT_EXIST, "订单不存在"),
    CURRENT_USER_CANT_UNLOCK(20030, ConstantsCode.CURRENT_USER_CANT_UNLOCK, "当前用户不能解锁"),
    CURRENT_USER_CANT_OPERATION(20031, ConstantsCode.CURRENT_USER_CANT_OPERATION, "当前用户不能操作"),
    REVIEW_STATUS_ERROR(20032, ConstantsCode.REVIEW_STATUS_ERROR, "审核状态异常,未处于处理中申请不能审核"),
    UPDATE_FAIL(20033, ConstantsCode.UPDATE_FAIL, "更新失败"),
    REG_MAIL_ERROR(20034, ConstantsCode.REG_MAIL_ERROR, "注册失败,请输入有效的电子邮箱"),
    REG_PHONE_ERROR(20035, ConstantsCode.REG_PHONE_ERROR, "注册失败,请输入有效的手机号码"),
    CODE_ERROR(20036, ConstantsCode.CODE_ERROR, "验证码错误"),
    CODE_IS_EMPTY(20037, ConstantsCode.CODE_IS_EMPTY, "验证码不能为空"),
    MAIL_IS_EXIST(20038, ConstantsCode.MAIL_IS_EXIST, "电子邮箱已存在,请直接登录"),
    PHONE_IS_EXIST(20039, ConstantsCode.PHONE_IS_EXIST, "手机号码已存在,请直接登录"),
    MAIL_LOGIN_ERROR(20040, ConstantsCode.MAIL_LOGIN_ERROR, "登录失败,邮箱或者密码错误"),
    PHONE_LOGIN_ERROR(20041, ConstantsCode.PHONE_LOGIN_ERROR, "登录失败,手机号码或者密码错误"),
    USER_LOGIN_LOCK(20042, ConstantsCode.USER_LOGIN_LOCK, "账号异常,请联系在线客服"),
    MAIL_PASSWORD_ERROR(20043, ConstantsCode.MAIL_PASSWORD_ERROR, "电子邮箱不正确"),
    PHONE_PASSWORD_ERROR(20044, ConstantsCode.PHONE_PASSWORD_ERROR, "手机号码不正确"),
    MAIL_NOT_REG(20045, ConstantsCode.MAIL_NOT_REG, "该邮箱尚未注册,请前往注册"),
    PASSWORD_ERROR(20046, ConstantsCode.PASSWORD_ERROR, "密码为6-16位数字+字母组合"),
    PASSWORD_CONFIRM_ERROR(20047, ConstantsCode.PASSWORD_CONFIRM_ERROR, "两次密码输入不一致"),
    PASSWORD_SET_ERROR(20048, ConstantsCode.PASSWORD_SET_ERROR, "密码设置失败"),
    PASSWORD_SET_SUCCESS(20049, ConstantsCode.PASSWORD_SET_SUCCESS, "密码设置成功"),
    PHONE_NOT_REG(20050, ConstantsCode.PHONE_NOT_REG, "该手机号未注册,请前往注册"),

    LABEL_EDIT_ERROR(20051, ConstantsCode.LABEL_EDIT_ERROR, "该标签不能修改与删除,属于定制化标签"),
    PHONE_IS_NULL(20052, ConstantsCode.PHONE_IS_NULL, "手机号是空"),
    ACCOUNT_NOT_NULL(20053, ConstantsCode.ACCOUNT_NOT_NULL, "账号不能为空"),
    SUPER_AGENT_ACCOUNT_NOT_EXIST(20054, ConstantsCode.SUPER_AGENT_ACCOUNT_NOT_EXIST, "上级代理不存在"),
    TYPE_NOT_SAME(20055, ConstantsCode.TYPE_NOT_SAME, "新增会员类型和上级代理类型不匹配"),

    QUERY_BUSY(20056, ConstantsCode.QUERY_BUSY, "操作过于频繁,请5秒后再试"),
    REG_SUCCESS(20057, ConstantsCode.REG_SUCCESS, "注册成功"),
    ADD_SUCCESS(20058, ConstantsCode.ADD_SUCCESS, "添加成功"),
    OLD_PASSWORD_ERROR(20059, ConstantsCode.OLD_PASSWORD_ERROR, "旧密码错误"),
    PASSWORD_SAME(20060, ConstantsCode.PASSWORD_SAME, "新登录密码不能与旧密码相同"),

    REGISTER_IP_LIMIT(20061, ConstantsCode.REGISTER_IP_LIMIT, "IP异常,已被限制注册"),

    REGISTER_DEVICE_LIMIT(20062, ConstantsCode.REGISTER_DEVICE_LIMIT, "当前设备存在风险,禁止注册"),

    LOGIN_IP_LIMIT(20063, ConstantsCode.LOGIN_IP_LIMIT, "当前IP存在风险,禁止登录"),

    LOGIN_DEVICE_LIMIT(20064, ConstantsCode.LOGIN_DEVICE_LIMIT, "当前设备存在风险,禁止登录"),

    SPECIFLE_MEMBER_DISPLAY(20065, ConstantsCode.SPECIFLE_MEMBER_DISPLAY, "特定会员展示失败"),

    WITHDRAW(20066, ConstantsCode.WITHDRAW, "撤回通知异常"),

    INSERT_NOTIFY(20067, ConstantsCode.INSERT_NOTIFY, "新增通知异常"),

    NOTIFY_LIST(20068, ConstantsCode.NOTIFY_LIST, "通知配置列表发生异常"),

    USER_NOTICE_PARAM_ERROR(20069, ConstantsCode.USER_NOTICE_PARAM_ERROR, "弹窗类型参数或者轮播参数为空"),
    TERMINAL_NOT_NULL(20070, ConstantsCode.TERMINAL_NOT_NULL, "请选择终端"),

    USER_NOTICE_IS_NULL(20071, ConstantsCode.USER_NOTICE_IS_NULL, "该消息类型没有配置"),

    INSERT_SYS_NOTICE_NOTIFY(20072, ConstantsCode.INSERT_SYS_NOTICE_NOTIFY, "新增系统消息异常"),

    USER_NOTICE_IS_REVOCATION(20073, ConstantsCode.USER_NOTICE_IS_REVOCATION, "该通知已撤销,不能阅读"),

    USER_NOTICE_IS_NOT_EXIST(20074, ConstantsCode.USER_NOTICE_IS_NOT_EXIST, "通知不存在"),

    USER_NOTICE_TYPE_IS_NULL(20075, ConstantsCode.USER_NOTICE_TYPE_IS_NULL, "通知类型是空"),

    USER_NOTICE_DEVICE_TERMINAL_IS_NULL(20076, ConstantsCode.USER_NOTICE_DEVICE_TERMINAL_IS_NULL, "终端设备是空"),

    USER_NOTICE_ADD_IS_FAIL(20077, ConstantsCode.USER_NOTICE_ADD_IS_FAIL, "添加通知数据失败"),

    USER_NOTICE_IS_READED(20078, ConstantsCode.USER_NOTICE_IS_READED, "通知已读了"),

    USER_NOTICE_UPDATE_IS_FAIL(20079, ConstantsCode.USER_NOTICE_UPDATE_IS_FAIL, "修改通知数据失败"),
    USER_ACCOUNT_NOT_EXIST_(20080, ConstantsCode.USER_ACCOUNT_NOT_EXIST_, "无该用户信息"),


    AGENT_USER_ACCOUNT_NOT_EXIST(20081, ConstantsCode.AGENT_USER_ACCOUNT_NOT_EXIST, "账号不存在"),
    AGENT_USER_NOT_SUBORDINATES(20082, ConstantsCode.AGENT_USER_NOT_SUBORDINATES, "输入账号不是您的直属下级"),
    AGENT_ACCOUNT_ABNORMAL(20083, ConstantsCode.AGENT_ACCOUNT_ABNORMAL, "您的账号存在异常,请联系客服咨询"),

    ACTIVITY_BET_IS_ERROR_DESC(20084, ConstantsCode.ACTIVITY_BET_IS_ERROR_DESC, "流水倍数必须是1到99的整数"),

    AGENT_PAY_PASSWORD_NOT_SET(20085, ConstantsCode.AGENT_PAY_PASSWORD_NOT_SET, "支付密码未设置"),


    AGENT_PAY_PASSWORD_ERROR(20086, ConstantsCode.AGENT_PAY_PASSWORD_ERROR, "支付密码校验错误,代存失败"),

    AGENT_IS_ARREARS(20087, ConstantsCode.AGENT_IS_ARREARS, "您有未付清的佣金欠款,还清后才代存"),

    AGENT_COIN_AMOUNT_NOT_ENOUGH(20088, ConstantsCode.AGENT_COIN_AMOUNT_NOT_ENOUGH, "钱包余额不足,不能进行代存"),


    SUBORDINATE_USER_LIST_2MONTH(20089, ConstantsCode.SUBORDINATE_USER_LIST_2MONTH, "仅支持最近两个月的统计"),

    USER_NOTICE_OF_TARGET_TYPE_IS_ERROR(20090, ConstantsCode.USER_NOTICE_OF_TARGET_TYPE_IS_ERROR, "参数有误"),

    USER_REPEAT_NICK_ERROR(20091, ConstantsCode.USER_REPEAT_NICK_ERROR, "昵称重复"),

    ACTIVITY_BET_IS_NULL_DESC(20092, ConstantsCode.ACTIVITY_BET_IS_NULL_DESC, "流水倍数不能为空"),

    ACCOUNT_IS_EXIST(20093, ConstantsCode.ACCOUNT_IS_EXIST, "注册失败，账号已存在"),
    ACCOUNT_ERROR(20094, ConstantsCode.ACCOUNT_ERROR, "账号错误"),
    SELECT_RIGHT_EMAIL(20095, ConstantsCode.SELECT_RIGHT_EMAIL, "请选择您绑定的电子邮箱"),
    SELECT_RIGHT_PHONE(20096, ConstantsCode.SELECT_RIGHT_PHONE, "请输入您绑定的手机号"),
    EMAIL_NOT_EXIST(20097, ConstantsCode.EMAIL_NOT_EXIST, "电子邮箱不存在,请选择其他方式或联系客服"),
    PHONE_NOT_EXIST(20098, ConstantsCode.PHONE_NOT_EXIST, "手机号不存在,请选择其他方式或联系客服"),
    AREA_EMPTY(20099, ConstantsCode.AREA_EMPTY, "区号不能为空"),
    WITHDRAW_PASSWORD_ERROR(20100, ConstantsCode.WITHDRAW_PASSWORD_ERROR, "原交易密码错误"),


    //这里先随便给个code,编写demo用的
    CATEGORY_NAME_EXIST(20101, ConstantsCode.CATEGORY_NAME_EXIST, "当前站点已存在相同名称分类"),

    SITE_CODE_NOT_EXIST(20102, ConstantsCode.SITE_CODE_NOT_EXIST, "站点不能为空"),

    SITE_STATUS_NOT_EXIST(20103, ConstantsCode.SITE_STATUS_NOT_EXIST, "站点不存在"),

    PHONE_NOT_NULL(20104, ConstantsCode.PHONE_NOT_NULL, "手机号码不能为空"),

    AREA_CODE_NOT_NULL(20105, ConstantsCode.AREA_CODE_NOT_NULL, "区号不能为空"),

    TRADE_PASSWORD_SAME(20106, ConstantsCode.TRADE_PASSWORD_SAME, "新交易密码不能与旧交易密码相同"),
    TWICE_PASSWORD_NOT_SAME(20107, ConstantsCode.TWICE_PASSWORD_NOT_SAME, "两次交易密码不一致"),
    CHANGE_SUCCESS(20108, ConstantsCode.CHANGE_SUCCESS, "修改成功"),
    MEDAL_REPEAT_ERROR(20109, ConstantsCode.MEDAL_REPEAT_ERROR, "解锁勋章数重复"),
    REWARD_NOT_NUll_ERROR(20110, ConstantsCode.REWARD_NOT_NUll_ERROR, "奖金数不能为空"),
    INPUT_INVALID_ERROR(20111, ConstantsCode.INPUT_INVALID_ERROR, "输入参数不符合条件"),
    MEDAL_AMOUNT_ZERO_ERROR(20112, ConstantsCode.MEDAL_AMOUNT_ZERO_ERROR, "解锁勋章数量不能为空"),
    BIND_SUCCESS(20113, ConstantsCode.BIND_SUCCESS, "绑定成功"),
    NEW_EMAIL_SAME(20114, ConstantsCode.NEW_EMAIL_SAME, "新电子邮箱不能与原电子邮箱相同"),
    NEW_PHONE_SAME(20115, ConstantsCode.NEW_PHONE_SAME, "新手机号不能与原手机号相同"),
    LOGIN_SUCCESS(20116, ConstantsCode.LOGIN_SUCCESS, "登录成功"),


    MEDAL_HAS_EXISTS(20117, ConstantsCode.MEDAL_HAS_EXISTS, "勋章名称已存在"),
    MEDAL_UNLOCK_NUM_ORDER(20118, ConstantsCode.MEDAL_UNLOCK_NUM_ORDER, "请按照顺序填写解锁数量"),

    AVATAR_AL_USED(20119, ConstantsCode.AVATAR_AL_USED, "当前头像已被会员使用,不允许删除/禁用"),

    VIP_GARDE_WRONG(20120, ConstantsCode.VIP_GARDE_WRONG, "vip等级必须高于当前会员等级"),

    USER_AL_HAV_AGENT(20121, ConstantsCode.USER_AL_HAV_AGENT, "当前会员已存在代理,不能发起溢出"),
    USER_TYPE_NOT_EQ_AGENT_TYPE(20122, ConstantsCode.USER_TYPE_NOT_EQ_AGENT_TYPE, "当前会员账号类型与代理类型不一致,不能发起溢出"),
    USER_NOT_HAV_AGENT(20123, ConstantsCode.USER_NOT_HAV_AGENT, "当前会员没有上级代理,不能发起转代"),
    GAME_ONE_MODEL_ERROR(20124, ConstantsCode.GAME_ONE_MODEL_ERROR, "一级分类原声游戏不允许修改模板"),
    REBATE_RATE_ZERO(20125, ConstantsCode.REBATE_RATE_ZERO, "返点比例不能小于0"),
    AVATAR_MAX_ENABLE_ERROR(20126, ConstantsCode.AVATAR_MAX_ENABLE_ERROR, "启用头像数量不能超过11个,需要禁用后再启用"),
    CURRENT_AGENT_EQ_TRAGENT(20127, ConstantsCode.CURRENT_AGENT_EQ_TRAGENT, "转入代理不能与当前代理一致"),
    AVATAR_NAME_NOT_EXIT(20128, ConstantsCode.AVATAR_NAME_NOT_EXIT, "头像名称不能为空"),
    GAME_ONE_MODEL_DELETE_ERROR(20129, ConstantsCode.GAME_ONE_MODEL_DELETE_ERROR, "一级分类原声游戏不允许删除模板"),
    PHONE_HAS_BINGED(20130, ConstantsCode.PHONE_HAS_BINGED, "此手机号已经被绑定"),
    MAIL_HAS_BINGED(20131, ConstantsCode.MAIL_HAS_BINGED, "此邮箱已经被绑定"),

    QUERY_NOT_SUPPORT_60(20132, ConstantsCode.QUERY_NOT_SUPPORT_60, "仅支持60天数据查询"),
    DOMAIN_NULL(20133, ConstantsCode.DOMAIN_NULL, "域名未配置"),
    MAIL_CHANNEL_CLOSE(20134, ConstantsCode.MAIL_CHANNEL_CLOSE, "邮箱验证出了一点故障，请稍后验证"),
    CUSTOMER_CHANNEL_CLOSE(20135, ConstantsCode.CUSTOMER_CHANNEL_CLOSE, "等待客服中"),
    SMS_CHANNEL_CLOSE(20136, ConstantsCode.SMS_CHANNEL_CLOSE, "短信验证出了一点故障，请稍后验证"),
    ROBOT_IS_EXISTS(20137, ConstantsCode.ROBOT_IS_EXISTS, "机器人名称已存在"),
    LESS_THAN_30(20138, ConstantsCode.LESS_THAN_30, "最多添加30条"),
    SET_SUCCESS(20139, ConstantsCode.SET_SUCCESS, "设置成功"),

    WITHDRAW_NOTIFICATION(20140, ConstantsCode.WITHDRAW_NOTIFICATION, "极光推送的通知不可撤回"),
    EMAIL_NOT_BIND(20141, ConstantsCode.EMAIL_NOT_BIND, "未绑定电子邮箱地址，请先绑定电子邮箱或更换验证方式"),
    PHONE_NOT_BIND(20142, ConstantsCode.PHONE_NOT_BIND, "未绑定手机号，请先绑定手机号或更换验证方式"),

    PLEASE_RE_ENTER(20143, ConstantsCode.PLEASE_RE_ENTER, "请重新输入"),

    PLEASE_RE_ENTER_USER_ACCOUNT(20144, ConstantsCode.PLEASE_RE_ENTER_USER_ACCOUNT, "请输入账号查询"),

    MEDAL_HAS_LIGHT(20145, ConstantsCode.MEDAL_HAS_LIGHT, "勋章已经被点亮"),

    MEDAL_REWARD_HAS_OPEN(20146, ConstantsCode.MEDAL_REWARD_HAS_OPEN, "宝箱已经被打开"),

    AUTH_WRONG_OPEN(20147, ConstantsCode.AUTH_WRONG_OPEN, "认证失败"),
    /*===================== wallet系统异常定义   3xxxx=====================*/
    //

    USER_BANK_CARD_QUERY_ERROR(30001, ConstantsCode.USER_BANK_CARD_QUERY_ERROR, "查询会员银行卡信息失败!!!"),
    USER_VIRTUAL_CURRENCY_QUERY_ERROR(30002, ConstantsCode.USER_VIRTUAL_CURRENCY_QUERY_ERROR, "查询会员虚拟币账号信息失败!!!"),
    WITHDRAW_LIMIT(30003, ConstantsCode.WITHDRAW_LIMIT, "提现需要的流水额度不足!!!"),

    ADJUST_AMOUNT_NOT_LT_ZREO(30004, ConstantsCode.ADJUST_AMOUNT_NOT_LT_ZREO, "调整金额需大于0!!!"),

    ADJUST_AMOUNT_SCALE_GT_TWO(30005, ConstantsCode.ADJUST_AMOUNT_SCALE_GT_TWO, "调整金额小数位数为2位!!!"),
    ADJUST_AMOUNT_MAX_LENGTH(30006, ConstantsCode.ADJUST_AMOUNT_MAX_LENGTH, "调整金额最大长度为11位!!!"),
    WALLET_INSUFFICIENT_BALANCE(30007, ConstantsCode.WALLET_INSUFFICIENT_BALANCE, "钱包余额不足!!!"),
    ADJUST_AMOUNT_INCORRECT(30008, ConstantsCode.ADJUST_AMOUNT_INCORRECT, "调整金额为正数,支持两位小数"),
    USER_ACCOUNT_NOT_EXIST(30009, ConstantsCode.USER_ACCOUNT_NOT_EXIST, "会员信息错误"),
    RUNNING_WATER_MULTIPLE_INCORRECT(30010, ConstantsCode.RUNNING_WATER_MULTIPLE_INCORRECT, "流水倍数最长不超过5个字符,支持两位小数"),
    APPLICANT_CANNOT_REVIEW(30011, ConstantsCode.APPLICANT_CANNOT_REVIEW, "申请人不能操作"),
    CHANGE_RECORD_ADD_FAIL(30012, ConstantsCode.CHANGE_RECORD_ADD_FAIL, "中心钱包加额异常"),
    UPDATE_USER_FAIL(30013, ConstantsCode.UPDATE_USER_FAIL, "更新会员失败"),
    USER_INFO_NOT_NULL(30014, ConstantsCode.USER_INFO_NOT_NULL, "会员信息不能为空"),

    LOCK_NOT_MATCH_REVIEW(30015, ConstantsCode.LOCK_NOT_MATCH_REVIEW, "审核人和锁单人不匹配"),
    USER_REVIEW_CANCEL(30020, ConstantsCode.USER_REVIEW_CANCEL, "订单被撤销"),


    ORDER_STATUS_ERROR(30021, ConstantsCode.ORDER_STATUS_ERROR, "订单被撤销"),

    ARRIVE_AMOUNT_INVALID(30022, ConstantsCode.ARRIVE_AMOUNT_INVALID, "实际到账金额为正整数"),

    ARRIVE_AMOUNT_INVALID_1(30023, ConstantsCode.ARRIVE_AMOUNT_INVALID_1, "实际到账金额最大8位"),

    MONEY_DEPOSITED(30024, ConstantsCode.MONEY_DEPOSITED, "已入款"),

    WITHDRAW_HANDED(30025, ConstantsCode.WITHDRAW_HANDED, "订单已处理"),

    WITHDRAW_FAIL(30026, ConstantsCode.WITHDRAW_FAIL, "提款审核失败"),
    USER_UNLOCK_ERROR(30027, ConstantsCode.USER_UNLOCK_ERROR, "锁单解锁失败"),
    ORDER_NOT_LOCK(30028, ConstantsCode.ORDER_NOT_LOCK, "订单未锁定"),

    SECOND_AUDIT_SAME_PEOPLE(30029, ConstantsCode.SECOND_AUDIT_SAME_PEOPLE, "当前处理人与一审处理人是同一人"),


    ADD_TYPING_AMOUNT(30030, ConstantsCode.ADD_TYPING_AMOUNT, "添加流水金额必须大于0！"),
    ONE_REVIEWER_CANNOT_REVIEW(30031, ConstantsCode.ONE_REVIEWER_CANNOT_REVIEW, "一审人不能操作"),
    ONLY_LOCKER_CAN_REVIEW(30032, ConstantsCode.ONLY_LOCKER_CAN_REVIEW, "只有锁单人才能审核"),
    DEPOSIT_RECORD_2MONTH(30033, ConstantsCode.DEPOSIT_RECORD_2MONTH, "仅支持最近两个月的存款记录查询"),
    GAME_JOIN_TWO_ERROR(30034, ConstantsCode.GAME_JOIN_TWO_ERROR, "一个游戏不可以跨多个一级分类"),

    USER_WITHDRAW_PASSWORD_ERROR(30035, ConstantsCode.USER_WITHDRAW_PASSWORD_ERROR, "交易密码错误"),

    WITHDRAW_APPLY_FAIL(30036, ConstantsCode.WITHDRAW_APPLY_FAIL, "提现申请失败"),
    USER_FUND_ORDER_EXIST(30037, ConstantsCode.USER_FUND_ORDER_EXIST, "存在未处理订单"),
    WITHDRAW_AMOUNT_NEED_WHOLE(30038, ConstantsCode.WITHDRAW_AMOUNT_NEED_WHOLE, "取款金额必须为正整数"),
    USER_STATUS_PAY_LOCK(30039, ConstantsCode.USER_STATUS_PAY_LOCK, "账号异常,请联系在线客服"),

    WITHDRAW_WAY_DISABLE(30040, ConstantsCode.WITHDRAW_WAY_DISABLE, "取款方式已禁用"),

    NOT_FOUND_WITHDRAW_CHANNEL(30041, ConstantsCode.NOT_FOUND_WITHDRAW_CHANNEL, "暂无对应支付通道信息"),

    WITHDRAW_AMOUNT_GT_SCOPE(30042, ConstantsCode.WITHDRAW_AMOUNT_GT_SCOPE, "提款金额超出范围"),

    CURRENT_STATUS_NOT_CANCEL(30043, ConstantsCode.CURRENT_STATUS_NOT_CANCEL, "当前状态不能撤销订单"),

    HOT_WALLET_ADDRESS_FAIL(30044, ConstantsCode.HOT_WALLET_ADDRESS_FAIL, "查询热钱包地址失败"),


    ACTIVITY_PHONE_NOT(30045, ConstantsCode.ACTIVITY_PHONE_NOT, "很抱歉，您不符合参与活动条件。" +
            "参与活动前需要验证绑定您的手机号,请尽快完善资料"),

    ACTIVITY_EMAIL_NOT(30053, ConstantsCode.ACTIVITY_EMAIL_NOT, "很抱歉，您不符合参与活动条件。" +
            "参与活动前需要验证绑定您的邮箱，请尽快完善资料"),

    ACTIVITY_IP_NOT(30046, ConstantsCode.ACTIVITY_IP_NOT, "很抱歉，您不符合参与活动条件。" +
            "您所在IP已有账号参与该活动"),

    ACTIVITY_NOT(30048, ConstantsCode.ACTIVITY_NOT, "很抱歉，您不符合参与活动条件。"),

    ACTIVITY_REPEAT(30047, ConstantsCode.ACTIVITY_REPEAT, "很抱歉，不可以重复参与"),

    ACTIVITY_NOT_DEPOSIT(30049, ConstantsCode.ACTIVITY_NOT_DEPOSIT, "您还未存款"),

    ACTIVITY_DEPOSIT_NOT_SATISFIED(30050, ConstantsCode.ACTIVITY_FIRST_DEPOSIT_NOT_SATISFIED, "金额不满足条件"),

    ACTIVITY_ALL_USER(30051, ConstantsCode.ACTIVITY_ALL_USER, "全体会员"),

    ACTIVITY_NEW_USER(30052, ConstantsCode.ACTIVITY_NEW_USER, "新注册会员"),

    GAME_JOIN_TWO_CANNOT_BE_DELETED(30054, ConstantsCode.GAME_JOIN_TWO_CANNOT_BE_DELETED, "二级分类下存在游戏,不可以删除"),


    ACTIVITY_HAS_END(30055, ConstantsCode.ACTIVITY_HAS_END, "活动已经结束"),

    USER_CURRENCY_MISMATCH(30056, ConstantsCode.USER_CURRENCY_MISMATCH, "会员币种不一致"),
    INVALID_ACTIVITY_ID(30057, ConstantsCode.INVALID_ACTIVITY_ID, "加款/扣款，类型为会员活动，没有找到对应活动"),


    EXIST_DEPOSIT_THREE_ORDER(30058, ConstantsCode.EXIST_DEPOSIT_THREE_ORDER, "目前有正在进行中的订单，请处理完成后存款"),

    CURRENT_ACCOUNT_NOT_DEPOSIT(30059, ConstantsCode.CURRENT_ACCOUNT_NOT_DEPOSIT, "您的账户类型不支持存款和取款，请联系在线客服"),

    NO_CHANNEL_AVAILABLE(30060, ConstantsCode.NO_CHANNEL_AVAILABLE, " 暂无可用通道"),
    DEPOSIT_AMOUNT_NOT_LE_ZERO(30061, ConstantsCode.DEPOSIT_AMOUNT_NOT_LE_ZERO, "充值金额必须大于0"),

    WITHDRAW_TYPE_NOT_EXISTS(30062, ConstantsCode.WITHDRAW_TYPE_NOT_EXISTS, "提现类型不存在"),

    USER_MAIN_CURRENCY_NOT_NULL(30063, ConstantsCode.USER_MAIN_CURRENCY_NOT_NULL, "会员主货币不能为空"),

    THIRD_WITHDRAW_FAIL(30064, ConstantsCode.THIRD_WITHDRAW_FAIL, "三方提款失败"),

    DEPOSIT_AMOUNT_AND_BET_AMOUNT(30065, ConstantsCode.DEPOSIT_AMOUNT_AND_BET_AMOUNT, "存款金额和投注金额不能同时为空"),

    SPIN_WHEEL_AMOUNT_NOT_AMOUNT(30066, ConstantsCode.SPIN_WHEEL_AMOUNT_NOT_AMOUNT, "转盘初始获得金额不能为空"),

    SPIN_WHEEL_MAX_TIME_TYPE_NOT_AMOUNT(30067, ConstantsCode.SPIN_WHEEL_MAX_TIME_TYPE_NOT_AMOUNT, "会员可领取次数不能为空"),

    SPIN_WHEEL_ALL_TIME_TYPE_NOT_NULL(30068, ConstantsCode.SPIN_WHEEL_ALL_TIME_TYPE_NOT_NULL, "全部会员领取次数不能为空"),

    SPIN_WHEEL_VIP_TIME_TYPE_NOT_NULL(30069, ConstantsCode.SPIN_WHEEL_VIP_TIME_TYPE_NOT_NULL, "按VIP等级限制会员领取次数不能为空"),

    SPIN_WHEEL_REWARD_NOT_NULL(30070, ConstantsCode.SPIN_WHEEL_REWARD_NOT_NULL, "奖品列表不能为空"),

    SPIN_WHEEL_REWARD_NOT_THREE(30071, ConstantsCode.SPIN_WHEEL_REWARD_NOT_THREE, "奖品列表配置错误"),

    SPIN_WHEEL_REWARD_BRONZE_WRONG(30072, ConstantsCode.SPIN_WHEEL_REWARD_BRONZE_WRONG, "青铜奖品概率之和不是100"),
    SPIN_WHEEL_REWARD_SILVER_WRONG(30073, ConstantsCode.SPIN_WHEEL_REWARD_SILVER_WRONG, "白银奖品概率之和不是100"),

    SPIN_WHEEL_REWARD_GOLD_WRONG(30074, ConstantsCode.SPIN_WHEEL_REWARD_GOLD_WRONG, "黄金以及上奖品概率之和不是100"),

    ACTIVITY_AND_EMAIL_NOT(30075, ConstantsCode.ACTIVITY_AND_EMAIL_NOT, "很抱歉，您不符合参与活动条件。" +
            "参与活动前需要验证绑定您的手机号和邮箱地址，请尽快完善资料"),

    RECHARGE_LIMIT(30076, ConstantsCode.RECHARGE_LIMIT, "您已被暂停存款功能，请1小时后再试"),
    BANK_CODE_REPEAT(30077, ConstantsCode.BANK_CODE_REPEAT, "银行code重复"),

    VENUE_CODE_REPEAT(30078, ConstantsCode.VENUE_CODE_REPEAT, "场馆币种重复"),

    INSUFFICIENT_BALANCE(30080, ConstantsCode.INSUFFICIENT_BALANCE, "余额不足"),
    REPEAT_TRANSACTIONS(30081, ConstantsCode.REPEAT_TRANSACTIONS, "交易重复"),
    WALLET_NOT_EXIST(30082, ConstantsCode.WALLET_NOT_EXIST, "支出无钱包信息"),
    AMOUNT_LESS_ZERO(30083, ConstantsCode.AMOUNT_LESS_ZERO, "订单账变金额小于0"),
    TRANSFER_ERROR(30084, ConstantsCode.TRANSFER_ERROR, "转账失败"),

    WITHDRAW_ADDRESS_ERROR(30085, ConstantsCode.WITHDRAW_ADDRESS_ERROR, "取款地址不合法"),

    GREATER_MAX_AMOUNT(30086, ConstantsCode.GREATER_MAX_AMOUNT, "高于单次最高限额"),

    LESS_MIN_AMOUNT(30087, ConstantsCode.LESS_MIN_AMOUNT, "低于单次最低限额"),

    TWO_SIGN_VENUE_ERROR(30088, ConstantsCode.TWO_SIGN_VENUE_ERROR, "单场馆游戏只能新建一个二级分类"),

    TWO_SIGN_VENUE_ONE_GAME_ERROR(30089, ConstantsCode.TWO_SIGN_VENUE_ONE_GAME_ERROR, "单场馆游戏只能新建一个游戏"),

    WITHDRAW_TYPE_ERROR(30090, ConstantsCode.WITHDRAW_TYPE_ERROR, "提款类型错误"),
    BANK_NAME_IS_EMPTY(30091, ConstantsCode.BANK_NAME_IS_EMPTY, "银行名称不能为空"),
    BANK_CARD_IS_EMPTY(30092, ConstantsCode.BANK_CARD_IS_EMPTY, "银行卡号不能为空"),
    BANK_CODE_IS_EMPTY(30093, ConstantsCode.BANK_CODE_IS_EMPTY, "银行代码不能为空"),
    SURNAME_IS_EMPTY(30094, ConstantsCode.SURNAME_IS_EMPTY, "姓不能为空"),
    USER_NAME_IS_EMPTY(30095, ConstantsCode.USER_NAME_IS_EMPTY, "名不能为空"),
    USER_EMAIL_IS_EMPTY(30096, ConstantsCode.USER_EMAIL_IS_EMPTY, "邮箱不能为空"),
    USER_PHONE_IS_EMPTY(30097, ConstantsCode.USER_PHONE_IS_EMPTY, "联系电话不能为空"),
    PROVINCE_NAME_IS_EMPTY(30098, ConstantsCode.PROVINCE_NAME_IS_EMPTY, "省份不能为空"),
    CITY_NAME_IS_EMPTY(30099, ConstantsCode.CITY_NAME_IS_EMPTY, "城市不能为空"),
    DETAIL_ADDRESS_IS_EMPTY(30100, ConstantsCode.DETAIL_ADDRESS_IS_EMPTY, "详细地址称不能为空"),
    USER_ACCOUNT_IS_EMPTY(30101, ConstantsCode.USER_ACCOUNT_IS_EMPTY, "账户不能为空"),
    NETWORK_TYPE_IS_EMPTY(30102, ConstantsCode.NETWORK_TYPE_IS_EMPTY, "协议不存在"),
    ADDRESS_NO_IS_EMPTY(30103, ConstantsCode.ADDRESS_NO_IS_EMPTY, "收款地址不能为空"),

    ACTIVITY_IS_NULL_END(30104, ConstantsCode.ACTIVITY_IS_NULL_END, "活动不存在"),
    CHANNEL_CLOSED(30105, ConstantsCode.CHANNEL_CLOSED, "当前通道已关闭"),

    WITHDRAWAL_RESTRICTIONS(30106, ConstantsCode.WITHDRAWAL_RESTRICTIONS, "出款限制！"),

    WITHDRAW_AMOUNT_NULL(30107, ConstantsCode.WITHDRAW_AMOUNT_NULL, "金额不能为空！"),

    CURRENCY_NOT_MATCH(30108, ConstantsCode.CURRENCY_NOT_MATCH, "币种不一致"),

    WITHDRAW_WAY_NOT_EXIST(30109, ConstantsCode.WITHDRAW_WAY_NOT_EXIST, "取款方式不存在"),

    RECHARGE_WAY_NOT_EXIST(30110, ConstantsCode.RECHARGE_WAY_NOT_EXIST, "充值方式不存在"),

    AMOUNT_IS_NULL(30111, ConstantsCode.AMOUNT_IS_NULL, "金额不能为空"),

    DEPOSIT_USER_NAME_IS_NULL(30112, ConstantsCode.DEPOSIT_USER_NAME_IS_NULL, "姓名不能为空"),

    RECHARGE_WAY_DISABLE(30113, ConstantsCode.RECHARGE_WAY_DISABLE, "充值方式已禁用"),

    AREA_CODE_IS_EMPTY(30114, ConstantsCode.AREA_CODE_IS_EMPTY, "区号不能为空"),

    AREA_CODE_IS_EXIST(30115, ConstantsCode.AREA_CODE_IS_EXIST, "区号不存在"),

    BANK_CODE_IS_EXIST(30116, ConstantsCode.BANK_CODE_IS_EXIST, "银行不存在"),

    SMS_CODE_IS_NULL(30117, ConstantsCode.SMS_CODE_IS_NULL, "验证码不能为空"),

    SMS_CODE_NOT_MATCH(30118, ConstantsCode.SMS_CODE_NOT_MATCH, "验证码不匹配"),

    WITHDRAW_ARRIVE_AMOUNT_NEED_WHOLE(30119, ConstantsCode.WITHDRAW_ARRIVE_AMOUNT_NEED_WHOLE, "到账金额不能为0,提款申请失败"),

    BASE_ERROR_ACTIVITY(30120, ConstantsCode.BASE_ERROR_ACTIVITY, "活动开始时间小于现在时间，不允许创建"),

    BASE_ERROR_ACTIVITY_TEMPLATE(30121, ConstantsCode.BASE_ERROR_ACTIVITY_TEMPLATE, "活动开始时间小于现在时间，不允许创建"),


    SYSTEM_CURRENCY_IS_DISABLE(30122, ConstantsCode.SYSTEM_CURRENCY_IS_DISABLE, "总站币种已禁用,不允许操作"),

    RATE_NOT_CONFIG(30123, ConstantsCode.RATE_NOT_CONFIG, "暂时无法操作,请联系客服"),

    INCORRECT_RANKING_SETTINGS(30124, ConstantsCode.INCORRECT_RANKING_SETTINGS, "排名设置不正确"),


    PERCENT_PARAMETER_ABNORMALITY(30125, ConstantsCode.PERCENT_PARAMETER_ABNORMALITY, "百分比配置总数超出100"),

    MANUAL_ACTIVITY_TEMPLATE_NOT_EXIT(30126, ConstantsCode.MANUAL_ACTIVITY_TEMPLATE_NOT_EXIT, "活动模板不存在,请重新输入"),
    MANUAL_ACTIVITY_ID_NOT_EXIT(30127, ConstantsCode.MANUAL_ACTIVITY_ID_NOT_EXIT, "活动ID不存在,请重新输入"),

    WITHDRAW_WAY_NONE(30128, ConstantsCode.WITHDRAW_WAY_NONE, "暂无取款方式"),

    NO_RECHARGE_CHANNEL_AVAILABLE(30129, ConstantsCode.NO_RECHARGE_CHANNEL_AVAILABLE, " 暂无充值通道"),

    BANK_CARD_IS_ERROR(30130, ConstantsCode.BANK_CARD_IS_ERROR, "银行卡号输入错误"),

    USER_PHONE_IS_ERROR(30131, ConstantsCode.USER_PHONE_IS_ERROR, "手机号输入错误"),

    EXIST_WITHDRAW_HANDING_ORDER(30132, ConstantsCode.EXIST_WITHDRAW_HANDING_ORDER, "您已有一笔提款订单正在处理中"),

    CURRENCY_FORBID(30134, ConstantsCode.CURRENCY_FORBID, "会员币种无效"),

    ORDER_NOT_THIRD(30135, ConstantsCode.ORDER_NOT_THIRD, "三方取款不能进行人工出款"),

    LIMIT_WITHDRAW(30136, ConstantsCode.LIMIT_WITHDRAW, "存在被限制出款的会员，无法进行减额操作"),

    ADMIN_CENTER_DISABLE_WAY(30137, ConstantsCode.ADMIN_CENTER_DISABLE_WAY, "总站已禁用此方式"),
    ADMIN_CENTER_DISABLE_CHANNEL(30138, ConstantsCode.ADMIN_CENTER_DISABLE_CHANNEL, "总站已禁用此通道"),

    ADMIN_CENTER_ACTIVITY_PARTICIPATION_LIMIT(30139, ConstantsCode.ADMIN_CENTER_ACTIVITY_PARTICIPATION_LIMIT, "你已参与活动，无法参与其他其他类型的存款活动"),

    ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH(30140, ConstantsCode.ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH, "该活动配置游戏类型与启用的游戏类型不一致"),

    EXCEED_DAY_MAX_NUM(30141, ConstantsCode.EXCEED_DAY_MAX_NUM, "超过当日最大提款次数，请明天重试"),

    EXCEED_DAY_MAX_AMOUNT(30142, ConstantsCode.EXCEED_DAY_MAX_AMOUNT, "超过当日最大提款限额，请明天重试"),
    TOTAL_SHOW_CODE(30143, ConstantsCode.TOTAL_SHOW_CODE, "总计"),


    NEED_TO_RESET_THE_TIME(30144, ConstantsCode.NEED_TO_RESET_THE_TIME, "启用该活动需要重新设置时间"),

    ADJUST_AMOUNT_IS_NULL(30145, ConstantsCode.ADJUST_AMOUNT_IS_NULL, "会员对应的资金数据为空"),

    USER_AMOUNT_INSUFFICIENT_BALANCE(30146, ConstantsCode.USER_AMOUNT_INSUFFICIENT_BALANCE, "会员【%s】余额不足"),

    SECURITY_CANNOT_CLOSE(30147, ConstantsCode.SECURITY_CANNOT_CLOSE, "当前站点保证金未平账,无法关闭"),

    SECURITY_ADJUST_AMOUNT(30153, ConstantsCode.SECURITY_ADJUST_AMOUNT, "调整金额必须大于0"),


    NAME_ALREADY_RECORD(30148, ConstantsCode.NAME_ALREADY_RECORD, "活动页签名称存在"),

    NAME_ALREADY_RECORD_OVER_10(30149, ConstantsCode.NAME_ALREADY_RECORD_OVER_10, "活动页签最多创建10个"),

    ELECTRONIC_WALLET_COLLECT_SELECT_ONE(30150, ConstantsCode.ELECTRONIC_WALLET_COLLECT_SELECT_ONE, "电子钱包收集信息账户和收款地址只能勾选一个"),
    PLEASE_REMOVE_ACTIVITY(30151, ConstantsCode.PLEASE_REMOVE_ACTIVITY, "请移除页签内活动再操作"),

    OVER_BALANCE(30152, ConstantsCode.OVER_BALANCE, "可减少金额不能超过剩余金额"),

    ORDER_NOT_MANUAL_WITHDRAW(30154, ConstantsCode.ORDER_NOT_MANUAL_WITHDRAW, "人工出款类型不能进行三方取款"),
    HASH_REPEAT_ERROR(30155, ConstantsCode.HASH_REPEAT_ERROR, "交易哈希重复，无法审核成功"),
    REVIEW_FAILED_CODE_ERROR(30156, ConstantsCode.REVIEW_FAILED_CODE_ERROR, "审核失败"),
    SEO_LANG_REPEAT_ERROR(30157, ConstantsCode.SEO_LANG_REPEAT_ERROR, "请勿重复配置已有语种"),

    OVERDRAW_ERROR(30158, ConstantsCode.OVERDRAW_ERROR, "剩余透支额度+冻结透支额度>透支额度异常,需要排查原因"),

    IFSC_CODE_IS_EMPTY(30159, ConstantsCode.IFSC_CODE_IS_EMPTY, "IFSC码不能为空"),
    ACTIVITY_IS_NOT_OPEN_ALLOW(30160, ConstantsCode.ACTIVITY_IS_NOT_OPEN_ALLOW, "活动模板未授权"),

    ELECTRONIC_WALLET_NAME_IS_EMPTY(30161, ConstantsCode.ELECTRONIC_WALLET_NAME_IS_EMPTY, "电子钱包名称不能为空"),

    ACCOUNT_HAS_BEEN_BOUND(30162, ConstantsCode.ACCOUNT_HAS_BEEN_BOUND, "该账户已绑定"),

    ACCOUNT_HAS_BEEN_BOUND_CANNOT_BE_ADDED(30163, ConstantsCode.ACCOUNT_HAS_BEEN_BOUND_CANNOT_BE_ADDED, "账户已绑定过，无法添加"),

    ACCOUNT_BIND_NUMS_GT(30164, ConstantsCode.ACCOUNT_BIND_NUMS_GT, "账户绑定数超过上限"),

    NEED_AUTH_INFO(30165, ConstantsCode.NEED_AUTH_INFO, "需先认证个人资料"),
    USER_NAME_NOT_MATCH_BANK_CARD(30165, ConstantsCode.USER_NAME_NOT_MATCH_BANK_CARD, "抱歉，您填写的姓名和银行卡信息不匹配，请核对后再试"),

    ACCOUNT_IS_BLACK(30167, ConstantsCode.ACCOUNT_IS_BLACK, "该账户已拉黑，请选择其他账户"),

    CPF_IS_EMPTY(30168, ConstantsCode.CPF_IS_EMPTY, "CPF不能为空"),

    /*===================== play系统异常定义   4xxxx=====================*/
    QUERY_GAME_VENUE_NOT_EXIST(40001, ConstantsCode.QUERY_GAME_VENUE_NOT_EXIST, "游戏场馆不存在"),
    TIME_MUST_CHOOSE(40002, ConstantsCode.TIME_MUST_CHOOSE, "必须选择一个时间范围"),
    FORTY_DAY_OVER(40003, ConstantsCode.FORTY_DAY_OVER, "查询时间范围不能超过90天!!!"),

    CREATE_MEMBER_FAIL(40004, ConstantsCode.CREATE_MEMBER_FAIL, "进入游戏失败"),
    CASINO_IS_CLOSED(40005, ConstantsCode.CASINO_IS_CLOSED, "游戏已关闭"),

    CASINO_IS_MAINTAIN(40006, ConstantsCode.CASINO_IS_MAINTAIN, "维护中"),
    VENUE_IS_DISABLE(40007, ConstantsCode.VENUE_IS_DISABLE, "场馆不可用"),
    OPEN_GAME_FREQUENTLY(40008, ConstantsCode.OPEN_GAME_FREQUENTLY, "进入游戏操作频繁,请稍后再试"),
    GAME_ROOM_MAINTAIN(40009, ConstantsCode.GAME_ROOM_MAINTAIN, "游戏房间维护中"),
    USER_GAME_LOCKED(40010, ConstantsCode.USER_GAME_LOCKED, "游戏锁定"),
    SELECT_LATEST_5ORDER_FAIL(40011, ConstantsCode.SELECT_LATEST_5ORDER_FAIL, "查询最新的5条结算注单失败"),

    VENUE_REPEAT_CURRENCY(40012, ConstantsCode.VENUE_REPEAT_CURRENCY, "场馆币种重复"),
    VENUE_NOT_OPEN(40013, ConstantsCode.VENUE_NOT_OPEN, "场馆未开启游戏不允许开启"),
    VENUE_FEE_ZERO(40014, ConstantsCode.VENUE_FEE_ZERO, "负盈利费率不能小于0"),
    VENUE_VALID_FEE_ZERO(40015, ConstantsCode.VENUE_VALID_FEE_ZERO, "有效流水费率不能小于0"),
    ADMIN_VENUE_CLOSE(40016, ConstantsCode.ADMIN_VENUE_CLOSE, "总控已禁用此场馆"),
    ADMIN_GAME_MAINTAIN_SYN(40017, ConstantsCode.ADMIN_GAME_MAINTAIN_SYN, "此游戏总控维护中,确认后将同步总控维护时间和状态"),
    ADMIN_GAME_NOT_OPEN(40018, ConstantsCode.ADMIN_GAME_NOT_OPEN, "总控游戏未开启"),
    ADMIN_GAME_CLOSE(40019, ConstantsCode.ADMIN_GAME_CLOSE, "总控游戏禁用中"),
    SITE_VENUE_CLOSE(40020, ConstantsCode.SITE_VENUE_CLOSE, "站点场馆禁用中"),
    ADMIN_VENUE_NOT_MAINTAIN(40021, ConstantsCode.ADMIN_VENUE_NOT_MAINTAIN, "此场馆总控维护中,确认后将同步总控维护时间和状态"),
    SITE_VENUE_NOT_MAINTAIN(40022, ConstantsCode.SITE_VENUE_NOT_MAINTAIN, "站点场馆未开启"),
    SITE_GAME_NOT_MAINTAIN(40023, ConstantsCode.SITE_GAME_NOT_MAINTAIN, "站点游戏未开启"),

    PLEASE_TRY_AGAIN_LATER(40024, ConstantsCode.PLEASE_TRY_AGAIN_LATER, "操作频繁,请稍后重试"),
    ADMIN_VENUE_MAINTAIN(40025, ConstantsCode.ADMIN_VENUE_MAINTAIN, "总控已维护此场馆"),
    CROSS_VENUE(40026, ConstantsCode.CROSS_VENUE, "不可以跨场馆"),
    /*===================== system系统异常定义 5xxxx=====================*/
    GOOGLE_AUTH_NO_PASS(50001, ConstantsCode.GOOGLE_AUTH_NO_PASS, "谷歌身份验证码错误!!!"),
    ADMIN_NAME_NOT_EXIST(50002, ConstantsCode.ADMIN_NAME_NOT_EXIST, "用户名或密码错误!!!"),
    ACCOUNT_DISABLED(50003, ConstantsCode.ACCOUNT_DISABLED, "账号已禁用!!!"),
    ACCOUNT_LOCK(50004, ConstantsCode.ACCOUNT_LOCK, "账号已锁定!!!"),
    ADMIN_NAME_IS_EXIST(50005, ConstantsCode.ADMIN_NAME_IS_EXIST, "用户名已存在!!!"),
    PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT(50006, ConstantsCode.PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT, "两次输入密码不一致!!!"),

    BUSINESS_ADMIN_EDIT_ERROR(50007, ConstantsCode.BUSINESS_ADMIN_EDIT_ERROR, "先禁用职员?再进行编辑!!!"),

    SUPER_ADMIN_PASSWORD_NOT_RESET(50008, ConstantsCode.SUPER_ADMIN_PASSWORD_NOT_RESET, "超管密码不能重置,请登录超管账号在个人中心进行修改!!!"),

    TWO_PASSWORDS_ENTERED_NOT_MATCH(50009, ConstantsCode.TWO_PASSWORDS_ENTERED_NOT_MATCH, "两次输入的新密码不一致!!!"),

    OLD_PASSWORD_NOT_MATCH(50010, ConstantsCode.OLD_PASSWORD_NOT_MATCH, "旧密码不匹配!!!"),

    ROLE_NAME_IS_EXIST(50011, ConstantsCode.ROLE_NAME_IS_EXIST, "角色名称已存在!!!"),

    MENU_KEY_IS_EXIST(50012, ConstantsCode.MENU_KEY_IS_EXIST, "菜单KEY已存在!!!"),
    BUSINESS_ADMIN_DELETE_ERROR(50013, ConstantsCode.BUSINESS_ADMIN_DELETE_ERROR, "先禁用职员?再进行删除!!!"),
    ROLE_EXIST_USER(50014, ConstantsCode.ROLE_EXIST_USER, "角色存在关联用户?不允许删除!!!"),
    RISK_ACCOUNT_EXIST(50015, ConstantsCode.RISK_ACCOUNT_EXIST, "该黑名单已存在"),
    RISK_LEVEL_IS_NULL(50016, ConstantsCode.RISK_LEVEL_IS_NULL, "风控层级是空"),

    RISK_CONTROL_TYPE(50017, ConstantsCode.RISK_CONTROL_TYPE, "风控类型必填!!!"),
    RISK_CONTROL_LEVEL(50018, ConstantsCode.RISK_CONTROL_LEVEL, "风控层级字符长度最少2个,最多10个"),
    RISK_CONTROL_LEVEL_DESCRIBE(50019, ConstantsCode.RISK_CONTROL_LEVEL_DESCRIBE, "风控层级描述必填!!!"),
    RISK_CONTROL_LEVEL_DESCRIBE_LEN(50020, ConstantsCode.RISK_CONTROL_LEVEL_DESCRIBE_LEN, "风控层级描述最多字符上限50!!!"),
    RISK_CONTROL_LEVEL_DONE(50021, ConstantsCode.RISK_CONTROL_LEVEL_DONE, "风控层级已存在!!!"),
    RISK_CTRL_TYPE_NULL(50022, ConstantsCode.RISK_CTRL_TYPE_NULL, "风控类型为空"),
    RISK_CTRL_ACCOUNT_NULL(50023, ConstantsCode.RISK_CTRL_ACCOUNT_NULL, "风控账户为空"),
    RISK_MEMBER_MAX_LENGHT(50024, ConstantsCode.RISK_MEMBER_MAX_LENGHT, "风控账号的字数最大11位"),
    RISK_AGENT_MAX_LENGHT(50025, ConstantsCode.RISK_AGENT_MAX_LENGHT, "风险代理的字数最大11位"),
    RISK_BANK_MAX_LENGHT(50026, ConstantsCode.RISK_BANK_MAX_LENGHT, "风险银行卡的字数最大25位"),
    RISK_VIRTUAL_MAX_LENGHT(50027, ConstantsCode.RISK_VIRTUAL_MAX_LENGHT, "风险虚拟币的字数最大100位"),
    RISK_IP_MAX_LENGHT(50028, ConstantsCode.RISK_IP_MAX_LENGHT, "风险IP的字数最大15位"),
    RISK_DEVICE_MAX_LENGHT(50029, ConstantsCode.RISK_DEVICE_MAX_LENGHT, "风险终端设备号的字数最大100位"),
    RISK_CONTROLLER_TYPE_IS_ERROR(50030, ConstantsCode.RISK_CONTROLLER_TYPE_IS_ERROR, "传递的风控层级有错误"),
    RISK_USER_IS_NOT_EXIST(50031, ConstantsCode.RISK_USER_IS_NOT_EXIST, "会员不存在"),
    DEVELOPING(50032, ConstantsCode.DEVELOPING, "还在开发中"),
    RISK_RANK_NO_IS_NOT_EXIST(50033, ConstantsCode.RISK_RANK_NO_IS_NOT_EXIST, "银行卡号不存在"),
    RISK_VIRTUAL_CURRENCY_NO_IS_NOT_EXIST(50034, ConstantsCode.RISK_VIRTUAL_CURRENCY_NO_IS_NOT_EXIST, "虚拟币地址不存在"),
    RISK_IP_NO_IS_NOT_EXIST(50035, ConstantsCode.RISK_IP_NO_IS_NOT_EXIST, "该IP没有被风控"),
    RISK_DEVICE_NO_IS_NOT_EXIST(50036, ConstantsCode.RISK_DEVICE_NO_IS_NOT_EXIST, "该设备没有被风控"),
    RISK_LEVEL_ID_IS_NULL(50037, ConstantsCode.RISK_LEVEL_ID_IS_NULL, "该设备没有被风控"),
    RISK_ACCOUNT_IS_NULL(50038, ConstantsCode.RISK_ACCOUNT_IS_NULL, "风控账号是空"),
    RISK_DESC_IS_NULL(50039, ConstantsCode.RISK_DESC_IS_NULL, "风控原因是空"),
    RISK_DESC_MAX_LENGHT_LIMIT(50040, ConstantsCode.RISK_DESC_MAX_LENGHT_LIMIT, "风控原因的字数最大50位"),
    RISK_LEVEL_IS_NOT_EXIST(50041, ConstantsCode.RISK_LEVEL_IS_NOT_EXIST, "风控层级记录不存在"),
    RISK_LEVEL_IS_ALREAD_DELETE(50042, ConstantsCode.RISK_LEVEL_IS_ALREAD_DELETE, "风控层级记录已经删除"),
    RISK_RECORD_ADD_IS_FAIL(50043, ConstantsCode.RISK_RECORD_ADD_IS_FAIL, "保存风控层级记录失败"),
    RISK_RECORD_SAVE_IS_FAIL(50044, ConstantsCode.RISK_RECORD_SAVE_IS_FAIL, "保存风险账号表的记录失败"),
    RISK_RECORD_UPDATE_IS_FAIL(50045, ConstantsCode.RISK_RECORD_UPDATE_IS_FAIL, "更新风险账号表的记录失败"),
    RISK_LEVEL_UPDATE_IS_FAIL(50046, ConstantsCode.RISK_LEVEL_UPDATE_IS_FAIL, "更新用户风险等级失败"),
    RISK_RANK_UPDATE_IS_FAIL(50047, ConstantsCode.RISK_RANK_UPDATE_IS_FAIL, "更新银行卡风险等级失败"),
    RISK_VIRTUAL_CURRENCY_UPDATE_IS_FAIL(50048, ConstantsCode.RISK_VIRTUAL_CURRENCY_UPDATE_IS_FAIL, "更新虚拟币风险等级失败"),
    RISK_CONTROL_TYPE_NOT_EXIST(50049, ConstantsCode.RISK_CONTROL_TYPE_NOT_EXIST, "风控黑名单不存在"),

    PERCENT_ADJUST_HINT(50050, ConstantsCode.PERCENT_ADJUST_HINT, "百分比调整:整数-50到50之间"),
    FIXED_VALUE_ADJUST_HINT(50051, ConstantsCode.FIXED_VALUE_ADJUST_HINT, "固定值调整:-1到1之间,可以带2位小数"),

    QUERY_SITE_INFO_ERROR(50052, ConstantsCode.QUERY_SITE_INFO_ERROR, "查询站点列表发生错误"),

    ADD_SITE_BASIC_ERROR(50053, ConstantsCode.ADD_SITE_BASIC_ERROR, "保存站点基本信息发生错误"),
    UPDATE_SITE_CONFIG_ERROR(50054, ConstantsCode.UPDATE_SITE_CONFIG_ERROR, "更新站点配置发生错误"),
    UPDATE_SITE_VENUE_ERROR(50055, ConstantsCode.UPDATE_SITE_VENUE_ERROR, "更新站点场馆授权发生错误"),
    UPDATE_SITE_DEPOSIT_ERROR(50056, ConstantsCode.UPDATE_SITE_DEPOSIT_ERROR, "更新站点存款授权发生错误"),
    UPDATE_SITE_WITHDRAW_ERROR(50057, ConstantsCode.UPDATE_SITE_WITHDRAW_ERROR, "更新站点提款授权发生错误"),
    UPDATE_SITE_MESSAGE_ERROR(50058, ConstantsCode.UPDATE_SITE_MESSAGE_ERROR, "更新站点短信通道发生错误"),
    UPDATE_SITE_EMAIL_ERROR(50059, ConstantsCode.UPDATE_SITE_EMAIL_ERROR, "更新站点邮箱授权发生错误"),
    UPDATE_SITE_CUSTOMER_ERROR(50060, ConstantsCode.UPDATE_SITE_CUSTOMER_ERROR, "更新站点客服授权发生错误"),
    SET_HANDING_FEE_ERROR(50061, ConstantsCode.SET_HANDING_FEE_ERROR, "站点手续费不能低于总台配置"),

    BIG_MONEY_GT_SINGLE_TOTAL_AMOUNT(50062, ConstantsCode.BIG_MONEY_GT_SINGLE_TOTAL_AMOUNT, "大额标记金额不能大于单日提款总额"),

    BANK_CARD_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT(50063, ConstantsCode.BANK_CARD_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT, "银行卡单次最高提款额度不能大于单日提款总额"),

    BANK_CARD_MIN_AMOUNT_GT_BANK_MAX_AMOUNT(50064, ConstantsCode.BANK_CARD_MIN_AMOUNT_GT_BANK_MAX_AMOUNT, "银行卡单次最低提款额度不能大于等于银行卡单次最高提款额度"),

    CRYPTO_CURRENCY_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT(50065, ConstantsCode.CRYPTO_CURRENCY_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT, "加密货币单次提款额度不能大于单日提款总额"),

    CRYPTO_CURRENCY_MIN_AMOUNT_GT_VIRTUAL_MAX_AMOUNT(50066, ConstantsCode.CRYPTO_CURRENCY_MIN_AMOUNT_GT_VIRTUAL_MAX_AMOUNT, "加密货币单次最低提款额度不能大于等于虚拟币单次最高提款额度"),

    ELECTRONIC_WALLET_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT(50067, ConstantsCode.ELECTRONIC_WALLET_MAX_AMOUNT_GT_SINGLE_TOTAL_AMOUNT, "电子钱包单次提款额度不能大于单日提款总额"),

    ELECTRONIC_WALLET_MIN_AMOUNT_GT_ELECTRONIC_WALLET_MAX_AMOUNT(50068, ConstantsCode.ELECTRONIC_WALLET_MIN_AMOUNT_GT_ELECTRONIC_WALLET_MAX_AMOUNT, "电子钱包单次提款额度不能大于等于电子钱包单次最高提款额度"),
    BK_NAME_IS_EXIST(50069, ConstantsCode.BK_NAME_IS_EXIST, "站点后台名称不可重复"),
    BK_NAME_LENGTH_MORE(50070, ConstantsCode.BK_NAME_LENGTH_MORE, "站点后台名称不能超过最大长度20"),
    SITE_NAME_IS_EXIST(500671, ConstantsCode.SITE_NAME_IS_EXIST, "站点名称不可重复"),
    SITE_PREFIX_IS_EXIST(50072, ConstantsCode.SITE_PREFIX_IS_EXIST, "站点前缀不可重复"),
    SITE_INCLUDES_RISK_CONTROL(50073, ConstantsCode.SITE_INCLUDES_RISK_CONTROL, "当前站点包含风控,不允许配置"),
    SKIN_CODE_EXIST(50074, ConstantsCode.SKIN_CODE_EXIST, "皮肤包代码已存在"),
    SKIN_NAME_EXIST(50075, ConstantsCode.SKIN_NAME_EXIST, "皮肤名称已存在"),

    CANNOT_DISABLE_YOURSELF(50076, ConstantsCode.CANNOT_DISABLE_YOURSELF, "不能对自己进行操作"),
    SITE_CURRENCY_INIT_ERROR(50077, ConstantsCode.SITE_CURRENCY_ERROR, "站点币种初始化失败"),
    SITE_VIP_INIT_ERROR(50078, ConstantsCode.SITE_VIP_INIT_ERROR, "站点VIP初始化失败"),

    BUSINESS_ROLE_EDIT_ERROR(50079, ConstantsCode.BUSINESS_ROLE_EDIT_ERROR, "先禁用角色?再进行编辑!!!"),

    BUSINESS_ROLE_DELETE_ERROR(50080, ConstantsCode.BUSINESS_ROLE_DELETE_ERROR, "先禁用角色?再进行删除!!!"),

    CHANNEL_CODE_IS_EXIST(50081, ConstantsCode.CHANNEL_CODE_IS_EXIST, "通道编码已存在!!!"),
    CENTER_LANGUAGE_DISABLE(50082, ConstantsCode.CENTER_LANGUAGE_DISABLE, "总站已禁用该语言"),

    CURRENCY_NAME_REPEAT(50083, ConstantsCode.CURRENCY_NAME_REPEAT, "币种名称存在重复"),
    GAME_ONE_NAME_REPEAT(50084, ConstantsCode.GAME_ONE_NAME_REPEAT, "一级分类名称重复"),
    GAME_TWO_NAME_REPEAT(50085, ConstantsCode.GAME_TWO_NAME_REPEAT, "二级分类名称重复"),

    RECHARGE_WAY_IS_NOT_EXIST(50086, ConstantsCode.RECHARGE_WAY_IS_NOT_EXIST, "充值方式不存在!"),

    WITHDRAW_WAY_IS_NOT_EXIST(50087, ConstantsCode.WITHDRAW_WAY_IS_NOT_EXIST, "提现方式不存在!"),
    VENUE_CHOOSE_ERROR(50088, ConstantsCode.VENUE_CHOOSE_ERROR, "场馆/游戏授权数据错误，请检查数据!"),
    DEPOSIT_CHOOSE_ERROR(50089, ConstantsCode.DEPOSIT_CHOOSE_ERROR, "存款方式/通道授权数据错误，请检查数据！"),
    WITHDRAW_CHOOSE_ERROR(50090, ConstantsCode.WITHDRAW_CHOOSE_ERROR, "取款方式/通道授权数据错误，请检查数据！"),
    NOT_DELETABLE(50091, ConstantsCode.NOT_DELETABLE, "启用状态数据不允许删除!"),
    ONLY_ONE_ENABLED(50092, ConstantsCode.ONLY_ONE_ENABLED, "当前区域只能启用一条banner"),
    CURRENCY_CODE_NOT_EXIT(50093, ConstantsCode.CURRENCY_CODE_NOT_EXIT, "必须要选择币种"),
    VERSION_NUMBER_ERROR(50094, ConstantsCode.VERSION_NUMBER_ERROR, "版本号错误"),
    SITE_CUSTOMER_CHANNEL_MUST_ONE_ENABLE(50095, ConstantsCode.SITE_CUSTOMER_CHANNEL_MUST_ONE_ENABLE, "站点下只允许存在一条启用的客服通道"),
    CUSTOMER_CHANNEL_AL_USED(50096, ConstantsCode.CUSTOMER_CHANNEL_AL_USED, "当前客服通道已被站点使用,必须要先去编辑站点去掉关联后才能禁用"),
    //name already exists

    NAME_ALREADY_EXIST(50097, ConstantsCode.NAME_ALREADY_EXIST, "名称已经存在"),
    DELETED_AFTER_DISABLED(50098, ConstantsCode.DELETED_AFTER_DISABLED, "禁用后才能删除"),
    CATEGORY_IS_DISABLED(50099, ConstantsCode.CATEGORY_IS_DISABLED, "教程大类未启用"),
    CLASS_IS_DISABLED(50100, ConstantsCode.CLASS_IS_DISABLED, "教程分类未启用"),
    TABS_IS_DISABLED(50101, ConstantsCode.TABS_IS_DISABLED, "教程页签未启用"),

    VIRTUAL_ADDRESS_ALREADY_EXIST(50102, ConstantsCode.ADDRESS_ALREADY_EXIST, "地址已经存在"),

    VIRTUAL_ADDRESS_ILLEGAL(50103, ConstantsCode.VIRTUAL_ADDRESS_ILLEGAL, "虚拟币地址不合法"),

    MERCHANT_NO_EXIST(50104, ConstantsCode.MERCHANT_NO_EXIST, "商户号已经存在"),

    CANT_ENABlE_BANNER(50105, ConstantsCode.CANT_ENABlE_BANNER, "当前banner展示结束时间已过期,不允许启用"),

    MNEMONIC_PHRASE_IS_NULL(50106, ConstantsCode.MNEMONIC_PHRASE_IS_NULL, "助记词不能为空"),

    CLASS_NOT_BELONG_CATEGORY(50107, ConstantsCode.CLASS_NOT_BELONG_CATEGORY, "所选分类不属于所选大类"),
    TBAS_NOT_BELONG_CLASS(50108, ConstantsCode.TBAS_NOT_BELONG_CLASS, "所选页签不属于所选分类"),

    SITE_ONLY_ONE_DOMAIN(50109, ConstantsCode.SITE_ONLY_ONE_DOMAIN, "当前站点当前域名类型下,只能存在一个主域名"),

    SITE_NAME_NOT_CHINESE(50110, ConstantsCode.SITE_NAME_NOT_CHINESE, "站点名称只能是英文"),

    AL_BIND_DOMAIN_CANT_OPER(50111, ConstantsCode.AL_BIND_DOMAIN_CANT_OPER, "已绑定域名不能进行此操作,请解绑后操作"),
    NOT_BIND_DOMAIN_CANT_OPER(50112, ConstantsCode.NOT_BIND_DOMAIN_CANT_OPER, "没有绑定的域名不能进行此操作,请绑定后操作"),
    PARTNER_ENABLE_ERROR(50113, ConstantsCode.PARTNER_ENABLE_ERROR, "当前启用赞助商数量小于6，不允许禁用"),
    PARTNER_ENABLE_MAX_ERROR(50114, ConstantsCode.PARTNER_ENABLE_MAX_ERROR, "超出可启用数量"),
    PARTNER_CENTER_DISABLE_ERROR(50115, ConstantsCode.PARTNER_CENTER_DISABLE_ERROR, "总控禁用的赞助商，站点可不启用"),

    PAYMENT_CENTER_DISABLE_ERROR(50116, ConstantsCode.PAYMENT_CENTER_DISABLE_ERROR, "总控禁用的支付商，站点可不启用"),

    PAYMENT_VENDOR_ENABLE_ERROR(50117, ConstantsCode.PAYMENT_VENDOR_ENABLE_ERROR, "当前启用支付商数量小于12，不允许禁用"),

    SKIN_DISABLED(50118, ConstantsCode.SKIN_DISABLED, "该皮肤已被禁用"),

    LOGIN_GAME_NOT_WAGERING(50119, ConstantsCode.LOGIN_GAME_NOT_WAGERING, "由于您参与了存款活动，尚有【%s】流水未完成，无法进入其他类型游戏"),

    DOWNLOAD_EXPORT_NOTICE(50120, ConstantsCode.DOWNLOAD_EXPORT_NOTICE, "【%s】秒后才能导出下载"),


    FREE_NUM_GT_DAY_NUM(50121, ConstantsCode.FREE_NUM_GT_DAY_NUM, "单日免费提款次数不能大于单日提款次数上限"),

    FREE_AMOUNT_GT_DAY_AMOUNT(50122, ConstantsCode.FREE_AMOUNT_GT_DAY_AMOUNT, "单日免费提款金额不能大于单日提款金额上限"),

    EXISTS_VENUE_NON_REBATE_CONFIG(50123, ConstantsCode.EXISTS_VENUE_NON_REBATE_CONFIG, "已存在相同场馆的配置"),

    CHOOSE_GAME_WITHOUT_CASHBACK(50124, ConstantsCode.CHOOSE_GAME_WITHOUT_CASHBACK, "已存在相同场馆的配置"),

    EXECUTE_NOTICE(50125, ConstantsCode.EXECUTE_NOTICE, "【%s】秒后才能点击执行"),

    NEW_PASSWORD_MATCH_OLD(50126, ConstantsCode.NEW_PASSWORD_MATCH_OLD, "新密码不能与原密码相同，请重新输入"),

    /*===================== agent系统异常定义  6xxxx=====================*/
    AGENT_CHANGE_TYPE_ERROR(60000, ConstantsCode.AGENT_CHANGE_TYPE_ERROR, "代理信息变更类型不存在"),
    AGENT_EXISTS_TO_REVIEWED(60001, ConstantsCode.AGENT_EXISTS_TO_REVIEWED, "存在待审核数据,无法提交"),

    AGENT_NOT_EXISTS(60002, ConstantsCode.AGENT_NOT_EXISTS, "代理账号不存在,请重新输入"),
    QUERY_AGENT_REGISTER_RECORD_ERROR(60003, ConstantsCode.QUERY_AGENT_REGISTER_RECORD_ERROR, "查询代理注册信息错误!!!"),
    INSERT_AGENT_REGISTER_RECORD_ERROR(60004, ConstantsCode.INSERT_AGENT_REGISTER_RECORD_ERROR, "记录代理注册记录信息错误!!!"),

    AGENT_PASSWORD_SAME(60007, ConstantsCode.AGENT_PASSWORD_SAME, "账号和密码不能一致"),

    AGENT_CATEGORY_ERROR(60008, ConstantsCode.AGENT_CATEGORY_ERROR, "流量代理的代理线层级上限只能选1"),
    AGENT_CATEGORY_WHITE_LIST_ERROR(60009, ConstantsCode.AGENT_CATEGORY_WHITE_LIST_ERROR, "流量代理的IP白名单不能为空"),
    AGENT_CATEGORY_WHITE_LIST_STYLE_ERROR(60010, ConstantsCode.AGENT_CATEGORY_WHITE_LIST_STYLE_ERROR, "IP白名单格式错误"),

    AGENT_ACCOUNT_REPEAT_ERROR(60011, ConstantsCode.AGENT_ACCOUNT_REPEAT_ERROR, "代理账号不能重复"),

    USER_ACCOUNT_ERROR(60012, ConstantsCode.USER_ACCOUNT_ERROR, "请输入4-11位字母和数字组成的账号,首位必须是字母,最少2个字母"),

    AGENT_REVIEW_STATUS_ERROR(60013, ConstantsCode.AGENT_REVIEW_STATUS_ERROR, "代理审核状态不合法"),


    AMOUNT_LT_ZERO(60014, ConstantsCode.AMOUNT_LT_ZERO, "账变金额变更数量需大于0！！！"),

    FLOW_AGENT_SECRET_KEY_ERROR(60015, ConstantsCode.FLOW_AGENT_SECRET_KEY_ERROR, "只有流量代理才可以查看密钥"),
    FLOW_AGENT_WHITE_LIST_ERROR(60016, ConstantsCode.FLOW_AGENT_WHITE_LIST_ERROR, "只有流量代理才可以更新白名单"),

    AGENT_DOMAIN_TYPE_IS_ERROR(60017, ConstantsCode.AGENT_DOMAIN_TYPE_IS_ERROR, "域名类型不正确"),
    AGENT_DOMAIN_STATE_IS_ERROR(60018, ConstantsCode.AGENT_DOMAIN_STATE_IS_ERROR, "域名状态不正确"),
    AGENT_DOMAIN_IS_NULL(60019, ConstantsCode.AGENT_DOMAIN_IS_NULL, "域名是空"),

    AGENT_SUPER_AGENT_EMPTY_ERROR(60020, ConstantsCode.AGENT_SUPER_AGENT_EMPTY_ERROR, "代理账号为空"),
    AGENT_ACCOUNT_STATUS_EMPTY(60021, ConstantsCode.AGENT_ACCOUNT_STATUS_EMPTY, "代理账号状态为空"),
    AGENT_RISK_LEVEL_EMPTY(60022, ConstantsCode.AGENT_RISK_LEVEL_EMPTY, "代理风控层级为空"),
    AGENT_LABEL_EMPTY(60023, ConstantsCode.AGENT_LABEL_EMPTY, "代理标签为空"),
    AGENT_ACCOUNT_REMARK_EMPTY(60024, ConstantsCode.AGENT_ACCOUNT_REMARK_EMPTY, "代理账号备注为空"),
    AGENT_AGENT_ATTRIBUTION_EMPTY(60025, ConstantsCode.AGENT_AGENT_ATTRIBUTION_EMPTY, "代理归属不能为空"),
    AGENT_PLAN_CODE_EMPTY(60026, ConstantsCode.AGENT_PLAN_CODE_EMPTY, "代理佣金方案不能为空"),
    AGENT_USER_BENEFIT_EMPTY(60027, ConstantsCode.AGENT_USER_BENEFIT_EMPTY, "会员福利code不能为空"),
    AGENT_FORCE_CONTRACT_EFFECT_EMPTY(60028, ConstantsCode.AGENT_FORCE_CONTRACT_EFFECT_EMPTY, "代理强制契约生效为空"),
    AGENT_ENTRANCE_PERM_EMPTY(60029, ConstantsCode.AGENT_ENTRANCE_PERM_EMPTY, "代理入口权限为空"),
    AGENT_SUPER_AGENT_NOT_ERROR(60030, ConstantsCode.AGENT_SUPER_AGENT_NOT_ERROR, "当前代理的上级非此账号"),
    AGENT_SUPER_AGENT_NOT_VALID_ERROR(60031, ConstantsCode.AGENT_SUPER_AGENT_NOT_VALID_ERROR, "该上级不是有效的代理账号"),
    AGENT_LOWEST_LEVEL_AGENT_ERROR(60032, ConstantsCode.AGENT_LOWEST_LEVEL_AGENT_ERROR, "该上级为底层代理,无法绑定!"),
    AGENT_REMOVE_RECHARGE_RESTRICTIONS_EMPTY(60033, ConstantsCode.AGENT_REMOVE_RECHARGE_RESTRICTIONS_EMPTY, "代理解除充值限制标识为空"),
    AGENT_PAYMENT_PASSWORD_RESET_EMPTY(60034, ConstantsCode.AGENT_PAYMENT_PASSWORD_RESET_EMPTY, "代理支付密码重置标识为空"),
    AGENT_ATTRIBUTION_EMPTY(60035, ConstantsCode.AGENT_ATTRIBUTION_EMPTY, "代理归属为空"),
    ONLY_MAIN_AGENT_CAN_MODIFY(60036, ConstantsCode.ONLY_MAIN_AGENT_CAN_MODIFY, "只有总代才可以修改代理归属"),
    EMAIL_TYPE_ERROR(60037, ConstantsCode.EMAIL_TYPE_ERROR, "请输入有效的电子邮箱"),
    EMAIL_ALREADY_USED(60038, ConstantsCode.EMAIL_ALREADY_USED, "电子邮箱已被绑定,请更换电子邮箱"),
    QUERY_AGENT_DETAIL_ERROR(60039, ConstantsCode.QUERY_AGENT_DETAIL_ERROR, "查询代理详情基本信息错误！！！"),
    QUERY_AGENT_REMARK_ERROR(60040, ConstantsCode.QUERY_AGENT_REMARK_ERROR, "查询代理详情备注信息错误！！！"),
    QUERY_AGENT_LOGIN_ERROR(60041, ConstantsCode.QUERY_AGENT_LOGIN_ERROR, "查询代理登录日志错误！！！"),
    AGENT_LABEL_EXISTED(60042, ConstantsCode.AGENT_LABEL_EXISTED, "标签名称已经存在"),

    RECORD_IS_NOT_EXIST(60043, ConstantsCode.RECORD_IS_NOT_EXIST, "标签记录不存在"),

    AGENT_LEVEL_CONFIG_NAME_EXIST_ERROR(60044, ConstantsCode.AGENT_LEVEL_CONFIG_NAME_EXIST_ERROR, "代理层级配置名称已存在"),
    AGENT_LEVEL_CONFIG_LEVEL_EXIST_ERROR(60045, ConstantsCode.AGENT_LEVEL_CONFIG_LEVEL_EXIST_ERROR, "代理层级已存在"),
    AGENT_LEVEL_CONFIG_LIMIT_ERROR(60046, ConstantsCode.AGENT_LEVEL_CONFIG_LIMIT_ERROR, "代理层级最多只可配置四层"),
    UP_AGENT_ACCOUNT_ERROR(60047, ConstantsCode.UP_AGENT_ACCOUNT_ERROR, "直属上级不存在"),
    AGENT_ACCOUNT_NOT(60048, ConstantsCode.AGENT_ACCOUNT_NOT, "直属上级是三级代理,该代理下不可创建下级代理"),

    RECORD_IS_NOT_FAIL(60049, ConstantsCode.RECORD_IS_NOT_FAIL, "查询代理下用户标签记录失败"),
    AGENT_WITHDRAW_CONFIG_COMMON_ERROR(60050, ConstantsCode.AGENT_WITHDRAW_CONFIG_COMMON_ERROR, "通用账号已配置,不可再新增通用账号"),
    AGENT_WITHDRAW_CONFIG_ONLY_ERROR(60051, ConstantsCode.AGENT_WITHDRAW_CONFIG_ONLY_ERROR, "当前代理账号已存在提款设置"),
    AGENT_WITHDRAW_CONFIG_COMMON_DEL_ERROR(60052, ConstantsCode.AGENT_WITHDRAW_CONFIG_COMMON_DEL_ERROR, "通用账号不可删除"),
    AGENT_WITHDRAW_CONFIG_CLOSE_DEL_ERROR(60053, ConstantsCode.AGENT_WITHDRAW_CONFIG_CLOSE_DEL_ERROR, "关闭状态才可删除"),
    AGENT_WITHDRAW_CONFIG_EMPTY_ERROR(60054, ConstantsCode.AGENT_WITHDRAW_CONFIG_EMPTY_ERROR, "未查询到代理提款配置"),
    AGENT_BANK_MIN_GT_BANK_MAX_ERROR(60055, ConstantsCode.AGENT_BANK_MIN_GT_BANK_MAX_ERROR, "法币单次提款最低限额不能大于法币单次提款最高限额"),
    AGENT_VIRTUAL_MIN_GT_VIRTUAL_MAX_ERROR(60056, ConstantsCode.AGENT_VIRTUAL_MIN_GT_VIRTUAL_MAX_ERROR, "虚拟币单次提款最低限额不能大于虚拟币单次提款最高限额"),

    DEPOSIT_FAIL(60057, ConstantsCode.DEPOSIT_FAIL, "充值失败"),

    MEMBER_DEPOSIT_LIST(60058, ConstantsCode.MEMBER_DEPOSIT_LIST, "代理存款记录列表显示失败"),

    USER_MANUAL_UP_REVIEW_ONE_REVIEWING(60059, ConstantsCode.USER_MANUAL_UP_REVIEW_ONE_REVIEWING, "一审审核"),
    USER_MANUAL_UP_REVIEW_TWO_REVIEWING(60060, ConstantsCode.USER_MANUAL_UP_REVIEW_TWO_REVIEWING, "二审审核"),

    AGENT_ACCOUNT_NOT_EXIS(60061, ConstantsCode.AGENT_ACCOUNT_NOT_EXIS, "代理账号不存在"),
    ADJUST_TYPE_IS_ERROR(60062, ConstantsCode.ADJUST_TYPE_IS_ERROR, "调整类型错误"),
    WALLET_TYPE_IS_ERROR(60063, ConstantsCode.WALLET_TYPE_IS_ERROR, "钱包类型错误"),
    ALREADY_PENDING_DATA(60064, ConstantsCode.ALREADY_PENDING_DATA, "已存在相同类型的审核流程，无需再次申请"),

    AGENT_ARREARS_NOT_TRANSFER(60071, ConstantsCode.AGENT_ARREARS_NOT_TRANSFER, "该代理存在欠款,不能发生转账"),

    QUERY_AGENT_TRANSFER_WALLET_ERROR(60072, ConstantsCode.QUERY_AGENT_TRANSFER_WALLET_ERROR, "代理转账钱包信息查询错误！！！"),

    PAYPASSWORD_ERROR(60073, ConstantsCode.PAYPASSWORD_ERROR, "支付密码校验错误,转账失败"),
    AGENT_ACCOUNT_NOT_EXISTS(60074, ConstantsCode.AGENT_ACCOUNT_NOT_EXISTS, "账号不存在"),
    TRANS_MORE_DAY(60075, ConstantsCode.TRANS_MORE_DAY, "当天累计转账金额超出限制,可尝试小额转账"),
    AGENT_TRANSFER_ERROR(60076, ConstantsCode.AGENT_TRANSFER_ERROR, "您的账号存在异常,请联系客服咨询"),
    AGENT_PARENT_ERROR(60077, ConstantsCode.AGENT_PARENT_ERROR, "输入账号不是您的直属下级"),
    SAVE_AGENT_TRANSFER_ERROR(60078, ConstantsCode.SAVE_AGENT_TRANSFER_ERROR, "代理转账错误"),
    AGENT_TEMPLATE_NAME_MAX(60079, ConstantsCode.AGENT_TEMPLATE_NAME_MAX, "模板名称长度不能大于50"),
    AGENT_TRANSFER_RECORD_ERROR(60080, ConstantsCode.AGENT_TRANSFER_RECORD_ERROR, "查询代理转账记录错误"),
    AGENT_TRANSFER_COIN_NOT_ENOUGH(60102, ConstantsCode.AGENT_TRANSFER_COIN_NOT_ENOUGH, "钱包余额不足,不能转账"),
    AGENT_MANUAL_DEP_ERROR(60104, ConstantsCode.AGENT_MANUAL_DEP_ERROR, "没有查询到代理人工加额审核开关"),

    COMMISSION_CHANGE_RECORD_ADD_FAIL(60105, ConstantsCode.COMMISSION_CHANGE_RECORD_ADD_FAIL, "佣金钱包加额异常"),

    QUOTA_CHANGE_RECORD_ADD_FAIL(60106, ConstantsCode.QUOTA_CHANGE_RECORD_ADD_FAIL, "额度钱包加额异常"),

    UPDATE_AGENT_FAIL(60107, ConstantsCode.UPDATE_AGENT_FAIL, "更新代理失败"),
    AGENT_MANUAL_DOWN_COIN_AMOUNT_NOT_ENOUGH(60108, ConstantsCode.AGENT_MANUAL_DOWN_COIN_AMOUNT_NOT_ENOUGH, "钱包余额不足,不能进行人工减额"),
    AGENT_ACCOUNT_NOT_EXIST(60109, ConstantsCode.AGENT_ACCOUNT_NOT_EXIST, "账号不存在"),

    USER_LOGIN_ERROR(60110, ConstantsCode.USER_LOGIN_ERROR, "账号或密码错误"),
    INSERT_AGENT_LOGIN_ERROR(60111, ConstantsCode.INSERT_AGENT_LOGIN_ERROR, "记录代理登录日志错误"),
    AGENT_LOGIN_CODE_ERROR(60112, ConstantsCode.AGENT_LOGIN_CODE_ERROR, "验证码错误"),
    AGENT_LOGIN_LOCK(60113, ConstantsCode.AGENT_LOGIN_LOCK, "账号存在异常,可联系客服咨询"),

    AGENT_BINDING_NUMBER_LIMIT(60114, ConstantsCode.AGENT_BINDING_NUMBER_LIMIT, "代理最多只能绑定10个USDT地址"),

    AGENT_LOGIN_PASSWROD_ERROR(60115, ConstantsCode.AGENT_LOGIN_PASSWROD_ERROR, "登录密码错误"),
    VIRTUAL_CURRENCY_BLACK_STATUS_NOT(60116, ConstantsCode.VIRTUAL_CURRENCY_BLACK_STATUS_NOT, "该地址为黑名单禁用状态,请联系管理员"),
    VIRTUAL_CURRENCY_ALREADY_BIND(60117, ConstantsCode.VIRTUAL_CURRENCY_ALREADY_BIND, "您已绑定该地址,请核对信息"),
    VIRTUAL_CURRENCY_ALREADY_BIND_NOT(60118, ConstantsCode.VIRTUAL_CURRENCY_ALREADY_BIND_NOT, "该地址已被其他玩家绑定,请核对信息"),
    ALREADY_UNBIND_CAN_NOT_BIND(60119, ConstantsCode.ALREADY_UNBIND_CAN_NOT_BIND, "解绑后的地址不能再次绑定"),
    BLACK_STATUS_INCORRECT(60120, ConstantsCode.BLACK_STATUS_INCORRECT, "黑名单状态异常"),
    BINDING_STATUS_INCORRECT(60121, ConstantsCode.BINDING_STATUS_INCORRECT, "绑定状态异常"),

    CONFIRM_VIRTUAL_CURRENCY_ADDRESS_ERROR(60122, ConstantsCode.CONFIRM_VIRTUAL_CURRENCY_ADDRESS_ERROR, "两次输入的账户地址不一致"),
    AGENT_QA_NOT_SET(60123, ConstantsCode.AGENT_QA_NOT_SET, "代理密保问题未设置"),
    AGENT_QA_VERIFY_ERROR(60124, ConstantsCode.AGENT_QA_VERIFY_ERROR, "代理密保验证失败"),
    AGENT_SECURITY_QA_VERIFY_ERROR(60125, ConstantsCode.AGENT_SECURITY_QA_VERIFY_ERROR, "代理密保验证失败"),
    AGENT_SECURITY_QA_INCORRECT_ERROR(60126, ConstantsCode.AGENT_SECURITY_QA_INCORRECT_ERROR, "密保问题不正确"),
    AGENT_CURRENT_ALREADY_BIND_EMAIL(60127, ConstantsCode.AGENT_CURRENT_ALREADY_BIND_EMAIL, "当前用户已绑定邮箱"),
    GOOGLE_AUTH_KEY_EXSIT(60128, ConstantsCode.GOOGLE_AUTH_KEY_EXSIT, "谷歌验证秘钥已存在"),
    DYNAMIC_VERIFICATION_CODE_ERR(60129, ConstantsCode.DYNAMIC_VERIFICATION_CODE_ERR, "动态码错误"),
    AGENT_PAYPASSWORD_EDIT_ERROR(16188, ConstantsCode.AGENT_PAYPASSWORD_EDIT_ERROR, "支付密码已设置,如您已遗忘支付密码或修改支付密码,请联系在线客服"),
    AGENT_QUOTA_INCREASE_ERROR(60131, ConstantsCode.AGENT_QUOTA_INCREASE_ERROR, "额度账户加款失败"),
    AGENT_COMISSION_DECREASE_ERROR(60132, ConstantsCode.AGENT_COMISSION_DECREASE_ERROR, "佣金账户扣款失败"),
    AGENT_PAY_PASSWORD_NOT_SET_ERROR(60133, ConstantsCode.AGENT_PAY_PASSWORD_NOT_SET_ERROR, "为了您的账户安全,请先设置支付密码"),
    AGENT_COMMISSION_COIN_INSUFFICIENT_BALANCE(60134, ConstantsCode.AGENT_COMMISSION_COIN_INSUFFICIENT_BALANCE, "佣金账户余额不足"),
    AGENT_ACCOUNT_NOT_A(60135, ConstantsCode.AGENT_ACCOUNT_NOT_A, "不允许继续发展下级"),
    AGENT_ACCOUNT_ERROR(60136, ConstantsCode.AGENT_ACCOUNT_ERROR, "输入账号格式不正确"),
    AGENT_ACCOUNT_EXIST_ERROR(60137, ConstantsCode.AGENT_ACCOUNT_EXIST_ERROR, "代理账号已存在"),
    AGENT_PASSWORD_ERROR(60138, ConstantsCode.AGENT_PASSWORD_ERROR, "输入密码格式不正确"),
    QUERY_THIRTY_RANGE(60139, ConstantsCode.QUERY_THIRTY_RANGE, "查询时间范围不能超过30天"),

    QUERY_CLIENT_ORDER_ERROR(60140, ConstantsCode.QUERY_CLIENT_ORDER_ERROR, "查询投注记录失败!!!"),

    AGENT_WITHDRAW_APPLY_FAIL(60141, ConstantsCode.AGENT_WITHDRAW_APPLY_FAIL, "提款失败"),
    AGENT_WITHDRAW_AMOUNT_NEED_WHOLE(60142, ConstantsCode.AGENT_WITHDRAW_AMOUNT_NEED_WHOLE, "取款金额必须为正整数"),

    AGENT_CURRENT_ACCOUNT_NOT_WITHDRAW(60143, ConstantsCode.AGENT_CURRENT_ACCOUNT_NOT_WITHDRAW, "您的账户类型不支持存款和取款，请联系在线客服"),

    AGENT_STATUS_PAY_LOCK(60144, ConstantsCode.AGENT_STATUS_PAY_LOCK, "你的账户已被锁定，请联系在线客服！"),

    AGENT_VIRTUAL_CURRENCY_ADDRESS_BLACK(60145, ConstantsCode.AGENT_VIRTUAL_CURRENCY_ADDRESS_BLACK, "该虚拟币地址已禁用"),

    AGENT_ADDRESS_NOT_BUILDING(60146, ConstantsCode.AGENT_ADDRESS_NOT_BUILDING, "该数字币地址已解绑"),

    AGENT_WITHDRAWAL_PAY_PASSWORD_ERROR(60147, ConstantsCode.AGENT_WITHDRAWAL_PAY_PASSWORD_ERROR, "支付密码校验错误,取款失败"),


    AGENT_GOOGLE_AUTH_KEY_NOT_SET(60148, ConstantsCode.AGENT_GOOGLE_AUTH_KEY_NOT_SET, "谷歌秘钥未设置"),

    AGENT_GOOGLE_AUTH_CODE_ERROR(60149, ConstantsCode.AGENT_GOOGLE_AUTH_CODE_ERROR, "谷歌验证码错误,取款失败"),

    AGENT_GOOGLE_AUTH_KEY_NOT_BLANK(60150, ConstantsCode.AGENT_GOOGLE_AUTH_KEY_NOT_BLANK, "谷歌CODE不能为空"),


    AGENT_FUND_ORDER_EXIST(60151, ConstantsCode.AGENT_FUND_ORDER_EXIST, "存在未处理的订单"),

    AGENT_GREATER_MAX_AMOUNT(60152, ConstantsCode.AGENT_GREATER_MAX_AMOUNT, "高于单次最高限额"),

    AGENT_LESS_MIN_AMOUNT(60153, ConstantsCode.AGENT_LESS_MIN_AMOUNT, "低于单次最低限额"),

    AGENT_CONTRACT_EXISTED(60154, ConstantsCode.AGENT_CONTRACT_EXISTED, "代理已签约!"),

    INPUT_ACCOUNT_NOT_DOWN(60155, ConstantsCode.INPUT_ACCOUNT_NOT_DOWN, "输入账号不是您的直属下级"),

    AGENT_PLAN_DELETE_FAIL(60156, ConstantsCode.AGENT_PLAN_DELETE_FAIL, "删除失败,当前方案存在使用代理"),
    AGENT_LABEL_NAME_DUPLICATE(60157, ConstantsCode.AGENT_LABEL_NAME_DUPLICATE, "代理标签名称重复"),
    AGENT_EMAIL_ERROR(60158, ConstantsCode.AGENT_EMAIL_ERROR, "请先绑定电子邮箱"),

    AGENT_NOT_MATCH_SUPER(60159, ConstantsCode.AGENT_NOT_MATCH_SUPER, "当前代理基础信息和上级不一致"),
    AGENT_PLAN_REPEAT(60160, ConstantsCode.AGENT_PLAN_REPEAT, "方案名称不能重复"),
    AGENT_FLOW_SUB_ERROR(60161, ConstantsCode.AGENT_FLOW_SUB_ERROR, "流量代理不能创建下级代理"),
    USER_ACCOUNT_TYPE_NOT_EQ_AGENT(60162, ConstantsCode.USER_ACCOUNT_TYPE_NOT_EQ_AGENT, "会员溢出只能到同类型的代理"),
    AGENT_MSG_NOT_CHANGE(60163, ConstantsCode.AGENT_MSG_NOT_CHANGE, "信息未发生变化"),
    AGENT_H5_PASSWORD_NOT_EMPTY(60164, ConstantsCode.AGENT_H5_PASSWORD_NOT_EMPTY, "支付密码不能为空"),
    AGENT_H5_PASSWORD_AMOUNT_NOT_EMPTY(60165, ConstantsCode.AGENT_H5_PASSWORD_AMOUNT_NOT_EMPTY, "代存金额不能为空"),
    AGENT_H5_WALLET_NOT_EMPTY(60166, ConstantsCode.AGENT_H5_WALLET_NOT_EMPTY, "代存钱包不能为空"),
    USER_ACCOUNT_NOT_EMPTY(60167, ConstantsCode.USER_ACCOUNT_NOT_EMPTY, "会员账号不能为空"),
    REMARK_NOT_BLANK(60168, ConstantsCode.REMARK_NOT_BLANK, "备注信息不能为空"),

    AMOUNT_CAN_ONLY_BE_INTEGER(60169, ConstantsCode.AMOUNT_CAN_ONLY_BE_INTEGER, "金额只能为整数"),
    AMOUNT_CANNOT_BE_ZERO(60170, ConstantsCode.AMOUNT_CANNOT_BE_ZERO, "金额不能为0"),
    AGENT_NOT_EXIT(60171, ConstantsCode.AGENT_NOT_EXIT, "代理信息不存在"),
    QUERY_SIXTY_RANGE(60172, ConstantsCode.QUERY_SIXTY_RANGE, "查询时间范围不能超过60天"),
    ALREADY_REJECT(60173, ConstantsCode.ALREADY_REJECT, "该会员历史已被拒绝"),
    DEALING(60174, ConstantsCode.DEALING, "该会员正在处理中"),
    REGISTER_TIME_EARLY_AGENT_TIME(60175, ConstantsCode.REGISTER_TIME_EARLY_AGENT_TIME, "该会员注册时间大于代理注册时间"),
    ONLY_MAIN_AGENT_MODIFY_PLAN(60036, ConstantsCode.ONLY_MAIN_AGENT_MODIFY_PLAN, "只有总代才可以修改佣金方案"),
    AGENT_LABEL_AL_USED(60037, ConstantsCode.AGENT_LABEL_AL_USED, "该标签存在已使用代理,不允许删除"),
    AGENT_MERCHANT_NOT_EXISTS(60038, ConstantsCode.AGENT_MERCHANT_NOT_EXISTS, "商务信息不存在"),
    AGENT_MERCHANT_NOT_MATCH(60039, ConstantsCode.AGENT_MERCHANT_NOT_MATCH, "商务信息不匹配"),
    NOT_MATCH_PASSWORD(60190, ConstantsCode.NOT_MATCH_PASSWORD, "新密码和确认密码不一致"),
    NOT_EMPTY_PASSWORD(60191, ConstantsCode.NOT_EMPTY_PASSWORD, "旧密码不能为空"),
    NOT_EMPTY_NEW_PASSWORD(60192, ConstantsCode.NOT_EMPTY_NEW_PASSWORD, "新密码不能为空"),
    NOT_EMPTY_CONFIRM_PASSWORD(60193, ConstantsCode.NOT_EMPTY_CONFIRM_PASSWORD, "确认密码不能为空"),
    ICCORRECT_PASSWORD_VERIFY(60194, ConstantsCode.ICCORRECT_PASSWORD_VERIFY, "账号或密码错误，请核实后在输入"),

    ERROR_EMAIL(60195, ConstantsCode.ERROR_EMAIL, "电子邮箱错误，请核实"),

    ERR_OLD_PASSWORD(60196, ConstantsCode.ERR_OLD_PASSWORD, "旧密码错误"),

    NEW_PASSWORD_SAME_OLD_PASSWORD(60197, ConstantsCode.NEW_PASSWORD_SAME_OLD_PASSWORD, "新密码不能与旧密码相同"),


    BIND_FAILED_ERROR_PASSWORD(60198, ConstantsCode.BIND_FAILED_ERROR_PASSWORD, "绑定失败，登录密码错误"),

    OTHER_BIND(60199, ConstantsCode.OTHER_BIND, "已被其他账号绑定，不能绑定"),

    BIND_OTHER_BIND(60200, ConstantsCode.BIND_OTHER_BIND, "请选择您绑定的电子邮箱"),

    FIEXD_FEE_AMOUNT_INVALID(60201, ConstantsCode.FIEXD_FEE_AMOUNT_INVALID, "固定手续费必须为正整数"),

    AGENT_CORRECTION_ERROR(60202, ConstantsCode.AGENT_CORRECTION_ERROR, "修改错误，请修改所属总代账号"),



    RE_ENTER_AMOUNT(60203, ConstantsCode.RE_ENTER_AMOUNT, "请重新输入，无法扣减至<=0"),

    CANNOT_BE_CREATED_REPEATEDLY(60204, ConstantsCode.CANNOT_BE_CREATED_REPEATEDLY, "不可重复创建"),

    NEW_OLD_PASSWORD_SAME(60205, ConstantsCode.NEW_OLD_PASSWORD_SAME, "新密码不能与旧密码相同，请重新输入"),

    AGENT_EXCEED_THE_MAX_LEVEL(60206, ConstantsCode.AGENT_EXCEED_THE_MAX_LEVEL, "超出最大层级"),

    AGENT_PLAN_TURNOVER_NOT_EXIST(60207, ConstantsCode.AGENT_PLAN_TURNOVER_NOT_EXIST, "佣金方案(有效流水)不存在"),

    AGENT_PLAN_TURNOVER_NO_CONFIG_ITEMS(60208, ConstantsCode.AGENT_PLAN_TURNOVER_NO_CONFIG_ITEMS, "佣金方案(有效流水)未设置配置项"),

    AGENT_PLAN_TURNOVER_CONFIG_MAX_LIMIT(60209, ConstantsCode.AGENT_PLAN_TURNOVER_CONFIG_MAX_LIMIT, "佣金方案(有效流水)配置项最大支持50条"),

    AGENT_PLAN_TURNOVER_CONFIG_REPEAT(60210, ConstantsCode.AGENT_PLAN_TURNOVER_CONFIG_REPEAT, "佣金方案(有效流水)每条配置项不能重复"),


    /*===================== pay系统异常定义  7xxxx=====================*/
    CHANNEL_NOT_EXISTS(70000, ConstantsCode.CHANNEL_NOT_EXISTS, "通道不存在"),
    ADDRESS_NOT_MATCH_CHAIN(70001, ConstantsCode.ADDRESS_NOT_MATCH_CHAIN, "地址与链不匹配"),
    ADDRESS_OWNER_INNER(70002, ConstantsCode.ADDRESS_OWNER_INNER, "转入地址为内部地址,不允许操作"),
    OUT_ADDRESS_NOT_EXISTS(70003, ConstantsCode.OUT_ADDRESS_NOT_EXISTS, "出款地址不存在"),
    OUT_GAS_NOT_ENOUGH(70004, ConstantsCode.OUT_GAS_NOT_ENOUGH, "出金地址Gas不足"),
    OUT_BALANCE_NOT_ENOUGH(70005, ConstantsCode.OUT_BALANCE_NOT_ENOUGH, "出金地址余额不足"),

    /*===================== 活动服务 异常定义  8xxxx=====================*/
    RED_BAG_SESSION_TIME_AFTER_ERROR(80000, ConstantsCode.RED_BAG_SESSION_TIME_AFTER_ERROR, "场次开始时间与上一场次结束时间间隔需要大于30分钟"),
    RED_BAG_SESSION_TIME_ERROR(80001, ConstantsCode.RED_BAG_SESSION_TIME_ERROR, "结束时间不得早于等于开始时间"),
    RED_BAG_ADVANCE_TIME_ERROR(80002, ConstantsCode.RED_BAG_ADVANCE_TIME_ERROR, "红包雨提前提示时间不得大于600秒"),
    RED_BAG_DROP_TIME_ERROR(80003, ConstantsCode.RED_BAG_DROP_TIME_ERROR, "红包雨掉落持续时间不得大于30秒"),
    RED_BAG_DEPOSIT_LIMIT_ERROR(80004, ConstantsCode.RED_BAG_DEPOSIT_LIMIT_ERROR, "存款奖励金额需要大于0"),
    RED_BAG_BET_DEPOSIT_LIMIT_ERROR(80005, ConstantsCode.RED_BAG_BET_DEPOSIT_LIMIT_ERROR, "存款或流水金额限制至少需要设置一项"),
    RED_BAG_TOTAL_AMOUNT_ERROR(80006, ConstantsCode.RED_BAG_TOTAL_AMOUNT_ERROR, "红包总金额必须大于0"),
    RED_BAG_HIT_CONFIG_ERROR(80007, ConstantsCode.RED_BAG_HIT_CONFIG_ERROR, "所选段位红包中奖设置不能为空"),
    RED_BAG_MAXIMUM_ERROR(80008, ConstantsCode.RED_BAG_MAXIMUM_ERROR, "有效红包数量上限必须大于0"),
    RED_BAG_AMOUNT_TYPE_ERROR(80009, ConstantsCode.RED_BAG_AMOUNT_TYPE_ERROR, "请输入有效红包金额类型"),
    RED_BAG_RANK_CONFIG_ERROR(80010, ConstantsCode.RED_BAG_RANK_CONFIG_ERROR, "段位红包配置不能为空"),
    RED_BAG_RANK_HATE_ERROR(80011, ConstantsCode.RED_BAG_RANK_HATE_ERROR, "段位红包配置概率总和需要等于100"),
    RED_BAG_RANDOM_AMOUNT_ERROR(80012, ConstantsCode.RED_BAG_RANDOM_AMOUNT_ERROR, "段位红包配置随机金额起始金额不可大于上一个结束金额"),
    RED_BAG_SITE_REPEAT_ERROR(80013, ConstantsCode.RED_BAG_SITE_REPEAT_ERROR, "红包雨活动配置重复"),
    RED_BAG_SESSION_END_ERROR(80014, ConstantsCode.RED_BAG_SESSION_END_ERROR, "本场次已结束"),
    RED_BAG_OVER_ERROR(80015, ConstantsCode.RED_BAG_OVER_ERROR, "红包已被抢完了"),
    RED_BAG_VIP_RANK_LIMIT_ERROR(80016, ConstantsCode.RED_BAG_VIP_RANK_LIMIT_ERROR, "当前vip段位不符合参与条件"),
    ACTIVITY_NOT_YET_CLAIM_TIME(80017, ConstantsCode.ACTIVITY_NOT_YET_CLAIM_TIME, "未到领取时间"),
    RED_BAG_NO_OPEN_ERROR(80018, ConstantsCode.RED_BAG_NO_OPEN_ERROR, "红包雨活动未开启"),
    ACTIVITY_NOT_HAVE_PRIZE_TIME(80019, ConstantsCode.ACTIVITY_NOT_HAVE_PRIZE_TIME, "没有抽奖次数"),
    ACTIVITY_OPEN_ALREADY_TIME(80020, ConstantsCode.ACTIVITY_OPEN_ALREADY_TIME, "活动状态已变更"),
    REDBAG_SESSION_PROCESS_ERROR(80021, ConstantsCode.REDBAG_SESSION_PROCESS_ERROR, "该活动不可禁用,还存在进行中场次"),
    REDBAG_SESSION_PARTICIPATED_ERROR(80022, ConstantsCode.REDBAG_SESSION_PARTICIPATED_ERROR, "您已参与过红包雨,请下一场次再参与"),
    REDBAG_SESSION_NOT_ALLOWED_PARTICIPATED_ERROR(80023, ConstantsCode.REDBAG_SESSION_NOT_ALLOWED_PARTICIPATED_ERROR, "您没有参与资格"),
    ACTIVITY_TIME_ERROR(80024, ConstantsCode.ACTIVITY_TIME_ERROR, "时间参数异常,开始时间与结束时间不是同一天"),
    REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR(80025, ConstantsCode.REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR, "未达到参与红包雨存款金额要求"),
    REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR(80026, ConstantsCode.REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR, "未达到参与红包雨流水金额要求"),
    ACTIVITY_NOT_OPEN(80027, ConstantsCode.ACTIVITY_NOT_OPEN, "活动未开启"),


    ACTIVITY_NOT_YET_CLAIM_EXPIRED(80028, ConstantsCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED, "已过期"),
    ACTIVITY_NOT_YET_CLAIM_FAIL(80029, ConstantsCode.ACTIVITY_NOT_YET_CLAIM_FAIL, "领取失败"),

    TASK_PARAM_MIN_BET_NULL(80030, ConstantsCode.TASK_PARAM_MIN_BET_NULL, "最小配置金额不能为空"),
    TASK_PARAM_VENUE_TYPE_NULL(80031, ConstantsCode.TASK_PARAM_VENUE_TYPE_NULL, "游戏类别不能为空"),
    TASK_PARAM_VENUE_CODE_NULL(80032, ConstantsCode.TASK_PARAM_VENUE_CODE_NULL, "场馆不能为空"),

    REDBAG_SREPEAT_ETTLED_ERROR(80033, ConstantsCode.REDBAG_SREPEAT_ETTLED_ERROR, "场次已结算,不可再次结算"),
    RED_BAG_SESSION_NOT_START_ERROR(80034, ConstantsCode.RED_BAG_SESSION_NOT_START_ERROR, "本场次未开始"),

    SPIN_WHEEL_NOT_PRIZE_CONFIG(80035, ConstantsCode.SPIN_WHEEL_NOT_PRIZE_CONFIG, "未配置抽奖奖品"),

    ACTIVITY_BASE_SHOW_TIME_ERROR(80036, ConstantsCode.ACTIVITY_BASE_SHOW_TIME_ERROR, "活动的展示时间,大于活动开始时间"),

    INSUFFICIENT_VIP_LEVEL(80037, ConstantsCode.INSUFFICIENT_VIP_LEVEL, "vip等级不足"),

    ACTIVITY_CAN_NOT_JOIN(80038, ConstantsCode.ACTIVITY_CAN_NOT_JOIN, "你不能参与本活动,具体原因请联系在线客服"),
    RED_BAG_AMOUNT_OVER_ZERO_ERROR(80039, ConstantsCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR, "金额配置必须大于0"),

    WASH_RATIO_AMOUNT_OVER_ZERO_ERROR(80040, ConstantsCode.WASH_RATIO_AMOUNT_OVER_ZERO_ERROR, "洗码倍率必须大于0"),

    TASK_ALREADY_ENABLE(80041, ConstantsCode.TASK_ALREADY_ENABLE, "已经被启用了"),
    TASK_ALREADY_NO_ENABLE(80042, ConstantsCode.TASK_ALREADY_NO_ENABLE, "已经被禁用了"),

    CHECKIN_OUT_OFF_TIME(80044, ConstantsCode.CHECKIN_OUT_OFF_TIME, "签到已超时，请重新签到"),

    CHECKIN_OUT_ALREADY(80045, ConstantsCode.CHECKIN_OUT_ALREADY, "已经签到"),

    DEPOSIT_NOT_MEET(80046, ConstantsCode.DEPOSIT_NOT_MEET, "您尚未满足存款条件"),
    BET_NOT_MEET(80047, ConstantsCode.BET_NOT_MEET, "您尚未满足投注条件"),
    ACTIVITY_VENUE_TYPE_REPEAT(80048, ConstantsCode.ACTIVITY_VENUE_TYPE_REPEAT, "该活动场馆类型重复"),

    CHECKIN_NOT_MAKEUP_LIMIT(80049, ConstantsCode.CHECKIN_NOT_MAKEUP_LIMIT, "您本月补签次数已用完"),

    CHECKIN_NOT_MAKEUP_LIMIT_REQ(80050, ConstantsCode.CHECKIN_NOT_MAKEUP_LIMIT_REQ, "您的补签次数已用完，请先完成存款或流水获取更多补签次数"),

    CHECKIN_NOT_MEET_CURRENT_TODAY(80051, ConstantsCode.CHECKIN_NOT_MEET_CURRENT_TODAY, "请先完成今日签到"),

    TASK_REWARD_AMOUNT_OVER_ZERO_ERROR(80052, ConstantsCode.TASK_REWARD_AMOUNT_OVER_ZERO_ERROR, "彩金奖励金额配置必须大于0"),


    /*===================== 其他 异常定义  9xxxx=====================*/
    WRONG_OPERATION(90000, ConstantsCode.WRONG_OPERATION, "自己申请的数据不能自己审核"),
    LOCKED(90001, ConstantsCode.LOCKED, "已被锁单"),
    AUDITED(90002, ConstantsCode.AUDITED, "已被审核,请勿反复操作"),
    USER_REVIEW_ALREADY_LOCK_ERROR(90003, ConstantsCode.USER_REVIEW_ALREADY_LOCK_ERROR, "该单号已经被锁住"),
    QUERY_BUSINESS_CONFIG_ERR(90004, ConstantsCode.QUERY_BUSINESS_CONFIG_ERR, "查询客户端商务合作配置错误!!"),
    RISK_LEVEL_GET(90005, ConstantsCode.RISK_LEVEL_GET, "风控层级下拉框查询失败!!!"),
    IP_FORMAT_ERROR(90006, ConstantsCode.IP_FORMAT_ERROR, "ip格式错误"),

    ADMIN_ALREADY_EXISTS(90007, ConstantsCode.ADMIN_ALREADY_EXISTS, "该平台已存在此游戏名"),

    PLATFORM_NOT_CONFIGURED(90008, ConstantsCode.PLATFORM_NOT_CONFIGURED, "平台未配置"),

    PLATFORM_PARAM_REPEAT(90009, ConstantsCode.PLATFORM_PARAM_REPEAT, "该平台接入参数重复"),
    EXPORTED_NUM_LIMITED(90010, ConstantsCode.EXPORTED_NUM_LIMITED, "导出数据条数不可超过10w条"),
    NOT_SUPPORTED_YET(90011, ConstantsCode.NOT_SUPPORTED_YET, "平台暂不支持"),
    EXECUTING(90012, ConstantsCode.EXECUTING, "该场馆还有拉单任务正在处理中,请稍后再试"),
    PULL_TIME_ERR(90013, ConstantsCode.PULL_TIME_ERR, "拉单时间异常"),
    STATUS_EXCEPT(90014, ConstantsCode.STATUS_EXCEPT, "状态异常"),
    WEBSOCKET_CONNECT_EXIST(90015, ConstantsCode.WEBSOCKET_CONNECT_EXIST, "websocket连接已存在"),

    QUERY_REPORT_USER_CHARGE_FAIL(90016, ConstantsCode.QUERY_REPORT_USER_CHARGE_FAIL, "查询会员累计金额失败"),
    APPLY_UNLOCK(90017, ConstantsCode.APPLY_UNLOCK, "未锁单不能审核"),
    NOT_CURRENT_LOCK(90018, ConstantsCode.NOT_CURRENT_LOCK, "审核人不是锁单人,不能审核"),
    APPLY_IS_COMPLATE(90019, ConstantsCode.APPLY_IS_COMPLATE, "审核流程已完成,无需再次审核"),
    AL_IS_LOCK(90020, ConstantsCode.AL_IS_LOCK, "不能被多人锁单"),

    /*===================== report 异常定义  98xxx=====================*/
    DATE_MAX_SPAN_92(98001, ConstantsCode.DATE_MAX_SPAN_92, "请缩小搜索范围至92天"),
    VIP_AWARD_QUERY_ERROR(98003, ConstantsCode.VIP_AWARD_QUERY_ERROR, "VIP奖励查询错误!!!"),
    AVATAR_NAME_ERROR(98004, ConstantsCode.AVATAR_NAME_ERROR, "头像名称格式错误"),
    DATE_MAX_SPAN_31(98002, ConstantsCode.DATE_MAX_SPAN_31, "日期跨度不超过31天"),
    SYS_TERMINAL_SPLASH_USED(98003, ConstantsCode.SYS_TERMINAL_SPLASH_USED, "app屏闪页使用中，不能删除"),
    SYS_TERMINAL_SPLASH_EXPIRED(98004, ConstantsCode.SYS_TERMINAL_SPLASH_EXPIRED, "闪屏页数据已经过期，不能启用"),
    TERMINAL_ONE_ENABLE(98005, ConstantsCode.TERMINAL_ONE_ENABLE, "仅允许启用一张图片"),

    LIMIT_TIME_RANGE(98006, ConstantsCode.LIMIT_TIME_RANGE, "请缩小搜索范围至31天"),

    ACTIVITY_LIMIT_ONE_ON(98007, ConstantsCode.ACTIVITY_LIMIT_ONE_ON, "该活动类型模版活动只能开启一个"),


    REGISTER_INVITE_CODE_ERROR(98008, ConstantsCode.REGISTER_INVITE_CODE_ERROR, "该活动类型模版活动只能开启一个"),

    VENUE_JOIN_TYPE_INVITE_CODE_ERROR(98009, ConstantsCode.VENUE_JOIN_TYPE_INVITE_CODE_ERROR, "同一场馆不可同时配置数据源类型和场馆类型"),

    ACTIVITY_RECOMMENDED_EXIST(98010, ConstantsCode.ACTIVITY_RECOMMENDED_EXIST, "您已有推荐活动，请取消后再重新推荐"),

    IP_LIST_EXIST(98011, ConstantsCode.IP_LIST_EXIST, "数据与【%s】有重复，请确认, 并谨慎操作"),
    IP_WHITE_NOT_EXIST(98012, ConstantsCode.IP_WHITE_NOT_EXIST, "新增数据不在该黑名单IP段下，请重新操作"),

    ;

    private final int code;
    private final String messageCode;

    private final String desc;

    ResultCode(final int code, final String messageCode, String desc) {
        this.code = code;
        this.messageCode = messageCode;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessageCode() {
        return this.messageCode;
    }

    public String getDesc() {
        return this.desc;
    }

    public static ResultCode getResultCodeByName(String name) {
        ResultCode[] arr = ResultCode.values();
        for (ResultCode itemObj : arr) {
            if (itemObj.name().equals(name)) {
                return itemObj;
            }
        }

        return null;
    }

    public static ResultCode of(int code) {
        ResultCode[] arr = ResultCode.values();
        for (ResultCode itemObj : arr) {
            if (itemObj.getCode() == code) {
                return itemObj;
            }
        }
        return null;
    }

}
