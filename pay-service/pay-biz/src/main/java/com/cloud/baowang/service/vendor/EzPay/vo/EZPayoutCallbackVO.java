package com.cloud.baowang.service.vendor.EzPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/10 16:55
 * @description:
 */
@Data
public class EZPayoutCallbackVO {
    /**
     * 代付订单号(Withdraw Order Number)
     */
    private String orderId;
    /**
     * 代付金额(Withdrawal Amount)
     */
    private String amount;
    /**
     * 状态    waiting review：等待审核，此为订单成立的初始状态(Waiting for review, this is the initial status of the order establishment.)。
     *
     * refuse：驳回，此为订单审核不通过的最终状态(Rejected, this is the final status of the order not passing the review.)。
     *
     * success：成功，此为订单所有金额都成功处理完毕的最终状态(Success, this is the final status when all amounts of the order are successfully processed.)。
     *
     * 备注：若有代付金额未成功处理(部分出款)，则不会回调，采人工通知。
     *
     * Note: If the payment amount is not successfully processed (partial withdrawal), there will be no callback and manual notification will be adopted.
     */
    private String status;

    /**
     * 时间戳
     */
    private String time;

    /**
     * 签名
     */
    private String sign;

    /**
     * 商户订单号(Mch Order Number)
     */
    private String mchOrderId;

    /**
     * 实际出款金额(Actual Payment Amount)
     */
    private String realAmount;

    /**
     * 商户分支号(Merchant Branch Number)
     */
    private String mchBranchNum;



}
