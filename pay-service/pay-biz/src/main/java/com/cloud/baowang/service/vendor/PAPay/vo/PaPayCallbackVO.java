package com.cloud.baowang.service.vendor.PAPay.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/07 3:08
 * @description:
 */
@Data
public class PaPayCallbackVO {
    private String mchId;
    private String outTradeNo;
    private String payAmount;
    private String transactionId;
    private String nonceStr;
    private String success;
    private String sign;
    private String attach;
}
