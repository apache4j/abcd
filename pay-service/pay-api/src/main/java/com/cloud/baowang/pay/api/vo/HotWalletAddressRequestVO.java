package com.cloud.baowang.pay.api.vo;


import lombok.Data;

@Data
public class HotWalletAddressRequestVO {

    /**
     * 链类型
     */
    private String chainType;

    /**
     * 会员ID/代理账号
     */
    private String ownerUserId;

    /**
     * 类型 USER会员  AGENT代理
     */
    private String ownerUserType;

    /**
     * 平台编码
     */
    private String platNo;


    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 唯一编号 和链上地址一一映射
     */
    private String outAddressNo;

    /**
     * 扩展字段
     */
    private String extractParam;
}
