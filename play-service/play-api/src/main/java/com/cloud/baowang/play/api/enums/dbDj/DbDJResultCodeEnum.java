package com.cloud.baowang.play.api.enums.dbDj;


import lombok.Getter;

@Getter
public enum DbDJResultCodeEnum {


    MISSING_SIGN(101,"missing sign", "缺少签名参数"),
    MERCHANT_INACTIVATED(null,"merchant inactivated", "商户未激活"),
    MERCHANT_UNSUPPORTED(null,"merchant unsupported", "商户不支持"),
    WRONG_SIGNATURE(null,"wrong signature", "签名错误"),
    MERCHANT_NOT_FOUND(null,"merchant not found", "未发现商户"),
    ILLEGAL_MERCHANT(null,"illegal merchant", "无效商户参数"),
    ILLEGAL_USERNAME(null,"illegal username", "无效会员名"),
    ILLEGAL_CURRENCY_CODE(null,"illegal currency_code", "无效币种编码"),
    UNSUPPORTED_CURRENCY(null,"unsupported currency", "不支持币种"),
    ILLEGAL_PASSWORD(null,"illegal password", "无效密码"),
    ILLEGAL_TIME(null,"illegal time", "无效时间戳"),
    ILLEGAL_MERORDERID(null,"illegal merOrderId", "无效流水号"),
    ILLEGAL_TYPE(null,"illegal type", "无效转账类型"),
    BETLIMIT_NOT_POSITIVE(null,"betLimit not a positive integer", "投注限额非正整数"),
    AMOUNT_MUST_BE_POSITIVE(null,"the amount must be greater than or equal to 0.01", "转账金额必须为正整数且>=0.01"),
    USERNAME_ALREADY_REGISTERED(null,"the username is already registered", "用户名已注册"),
    LOGIN_PROHIBITED(null,"login prohibited", "禁止登陆"),
    USERNAME_OR_PASSWORD_WRONG(null,"username or password is wrong", "会员名或者密码错误"),
//    USER_DOES_NOT_EXIST(null,"username not exist", "会员名不存在"),
    USER_DOES_NOT_EXIST(601,"username not exist", "用户不存在"),
    DATA_ERROR(null,"data error", "数据错误"),
    METHOD_NOT_ALLOWED(null,"method not allowed", "请求方式不允许"),
    BALANCE_NOT_ENOUGH(600,"balance not enough", "余额不足"),
    MERORDERID_DUPLICATE(null,"merOrderId duplicate", "转账流水号重复"),
    FAILED(null,"failed", "操作失败"),
    SUCCEED(0,"succeed", "操作成功"),
    CAN_ONLY_QUERY_FIVE_MINUTES_AGO(null,"can only query bets made five minutes ago", "只能查询5分钟前的数据"),
    ONLY_QUERY_WITHIN_30_DAYS(null,"only query the bet records within 30 days", "只能查询当前时间30天前至当前时间区间的数据"),
    PROCESSING(null,"processing", "处理中，请稍后重试"),
    MERORDERID_NOT_FOUND(null,"merOrderId not found", "订单未查询到"),
    QUERY_RANGE_NOT_EXCEED_30_MINUTES(null,"the query range of bet slips does not exceed 30 minutes", "单次只能查询30分钟内的拉单数据"),
    MERCHANT_MISSING_INIT(null,"merchant missing init", "商户缺少初始化"),
    ILLEGAL_AGENCY_AND_MERCHANT(null,"illegal agency and merchant", "agency参数和merchant参数不匹配正确的应该为：agency=true merchant=总商户idagency=false merchant=站点商户id"),
    OLD_NEW_PASSWORD_DIFFERENT(null,"The old and new passwords must be different", "新旧密码必须不一样"),
    ILLEGAL_LAST_ORDER_ID(null,"illegal last_order_id", "无效last_order_id");


    private final Integer code;
    private final String messageEn;
    private final String messageZh;

    DbDJResultCodeEnum(Integer code,String messageEn, String messageZh) {
        this.code = code;
        this.messageEn = messageEn;
        this.messageZh = messageZh;
    }

}
