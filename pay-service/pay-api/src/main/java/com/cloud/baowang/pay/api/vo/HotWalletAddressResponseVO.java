package com.cloud.baowang.pay.api.vo;


import lombok.Data;

/**
 * 热钱包地址返回
 */
@Data
public class HotWalletAddressResponseVO {

    private String ownerUserId;

    private String chainType;

    private String addressNo;
}
