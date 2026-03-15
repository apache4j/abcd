package com.cloud.baowang.service.vendor.PAPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/07 3:08
 * @description:
 */
@Data
public class PaPayoutCallbackVO {
    private String mchId;
    private String outTradeNo;
    private String payAmount;
    private String status;
    private String sign;
}
