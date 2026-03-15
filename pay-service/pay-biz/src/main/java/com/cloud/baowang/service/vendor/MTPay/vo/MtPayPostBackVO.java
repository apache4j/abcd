package com.cloud.baowang.service.vendor.MTPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/08 18:39
 * @description:
 */
@Data
public class MtPayPostBackVO {
    private String DepositId;
    private String Result;
    private String MerchantCode;
    private String TransNum;
    private String FromBank;
    private String ToBank;
    private String BankAccountNum;
    private String Currency;
    private String MerchantFeePercentage;
    private String MerchantFee;
    private String Amount;
    private String PaymentDesc;
    private String FirstName;
    private String LastName;
    private String EmailAddress;
    private String PhoneNum;
    private String Address;
    private String City;
    private String State;
    private String Country;
    private String Postcode;
    private String MerchantRemark;
    private String CheckString2;
}
