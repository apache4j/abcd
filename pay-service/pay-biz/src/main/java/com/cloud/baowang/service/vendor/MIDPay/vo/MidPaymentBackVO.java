package com.cloud.baowang.service.vendor.MIDPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/07 0:24
 * @description:
 */
@Data
public class MidPaymentBackVO {
    private String id;
    private String orderId;
    private String status;
    private String amount;
    private String currency;
    private String hash;
}
