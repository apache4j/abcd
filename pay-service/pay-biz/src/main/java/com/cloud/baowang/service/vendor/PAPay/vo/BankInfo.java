package com.cloud.baowang.service.vendor.PAPay.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/07 0:42
 * @description:
 */
@Data
public class BankInfo {
    private String bank;
    private String account;
    private String amount;
    private String cardnumber;
    private String postscript;
    private String qrcode;
}
