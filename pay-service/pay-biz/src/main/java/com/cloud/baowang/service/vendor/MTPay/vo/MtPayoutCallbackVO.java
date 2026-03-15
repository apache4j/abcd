package com.cloud.baowang.service.vendor.MTPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/08 16:44
 * @description:
 */
@Data
public class MtPayoutCallbackVO {
    private String MerchantCode;
    private String MerchantTransNum;
    private String PayoutId;
    private String CurrencyTypeId;
    private String ToBankId;
    private String ToBankAccountNum;
    private String Amount;
    private String CheckString2;
    private String Result;
}
