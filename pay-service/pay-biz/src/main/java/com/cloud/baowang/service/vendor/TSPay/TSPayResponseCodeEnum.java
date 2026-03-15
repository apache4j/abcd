package com.cloud.baowang.service.vendor.TSPay;

import lombok.Getter;

@Getter
public enum TSPayResponseCodeEnum {

    SUCCESS(200, "处理成功", "请求处理成功，不代表订单支付成功"),
    IP_NOT_IN_WHITELIST(403, "IP不在白名单", "代付限制，代收不限制（商户后台可配置）"),
    SYSTEM_ERROR(1001, "系统错误", "系统处理异常，稍后尝试"),
    FAILURE_TRY_LATER(1002, "处理失败，请稍后重试", "未归类错误"),
    SIGNATURE_ERROR(1003, "签名错误", "验签失败"),
    PARAMETER_ERROR(1004, "参数错误", "参数校验失败"),
    PARAMETER_PARSING_ERROR(1005, "参数解析错误", "参数body不符合规范"),
    RISK_CONTROL_INTERCEPT(1006, "风控拦截", "黑名单风控拦截"),
    TAX_NUMBER_INVALID(1007, "TaxNumber无效", "taxNumber校验不通过"),
    CHANNEL_NOT_OPEN(1301, "通道未开通", "请联系客服"),
    CHANNEL_TEMPORARILY_CLOSED(1302, "通道暂时关闭", "请联系客服"),
    BANK_CHANNEL_NOT_SUPPORTED(1304, "银行通道不支持", "请联系客服"),
    MERCHANT_NOT_EXIST(1402, "商户不存在", "请联系客服"),
    MERCHANT_INSUFFICIENT_BALANCE(1403, "商户余额不足", "可用余额不足，请联系客服"),
    MERCHANT_CLOSED(1404, "商户关闭", "请联系客服"),
    MERCHANT_DUPLICATE_ORDER_INTERCEPT(1406, "商户重复订单拦截", "相同订单号多次提交"),
    ORDER_NOT_FOUND(1407, "订单未找到", "代收、代付订单不在数据库中"),
    BANK_CARD_INFO_ERROR(1601, "银行卡信息错误", "账号为空或者账号错误"),
    BANK_CONNECTION_TIMEOUT(1603, "连接银行超时", "连接银行网络超时或无响应"),
    PAYMENT_ORDER_CREATION_FAILED(1801, "代付订单申请失败", "创建代付订单失败"),
    USER_ACCOUNT_RESTRICTED(1803, "用户账户受限或拒收", "账户银行连接问题或者拒绝接收，账户银行风控拒收等问题"),
    ORDER_CANCELLED(1805, "订单取消", "订单取消");

    Integer code;
    String description;
    String explanation;

    TSPayResponseCodeEnum(int code, String description, String explanation) {
        this.code = code;
        this.description = description;
        this.explanation = explanation;
    }
    TSPayResponseCodeEnum() {
    }


    // 根据响应码获取对应的枚举项
    public static TSPayResponseCodeEnum fromCode(int code) {
        for (TSPayResponseCodeEnum responseCode : TSPayResponseCodeEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return FAILURE_TRY_LATER;
    }
}
