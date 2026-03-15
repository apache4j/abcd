package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "热钱包查询返回对象")
public class HotWalletAddressVO  {

    /**
     * 站点编码
     */
    private String siteCode;


    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 链类型
     */
    private String chainType;

    /**
     * 协议类型
     */
    private String networkType;

    /**
     * 地址
     */
    private String address;

    /**
     * 地址人员类型 USER会员购 AGENT 代理
     */
    private String walletUserType;

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
