package com.cloud.baowang.service.vendor.JZPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/10 16:08
 * @description: 代收回调对象
 */
@Data
public class JZPayCallbackVO {
    /**
     * 订单金额
     */
    private String amount;
    /**
     * 结算金额
     */
    private String applyamount;
    /**
     * 商户编号
     */
    private String mer_id;
    /**
     * 商户订单号
     */
    private String mer_no;
    /**
     * 状态    00成功     01失败
     */
    private String returncode;
    /**
     * 签名
     */
    private String sign;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 平台流水号
     */
    private String transaction_id;
}
