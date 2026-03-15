package com.cloud.baowang.service.vendor.EzPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/10 16:08
 * @description: EZPay代收回调对象
 */
@Data
public class EZPayCallbackVO {
    /**
     * 商户订单号
     */
    private String mchOrderId;
    /**
     * 平台订单号
     */
    private String orderId;
    /**
     * 订单金额(Order amount)
     */
    private String amount;
    /**
     * 时间戳
     */
    private String time;
    /**
     * 订单状态(Order Status)
     * 2:支付成功(Payment Successful)
     */
    private String status;
    /**
     * 订单标记(Order mark)
     */
    private String remark;
    /**
     * 签名
     */
    private String sign;

}
