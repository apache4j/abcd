package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("hot_wallet_address")
public class HotWalletAddressPO extends BasePO {

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
     * 余额
     */
    private BigDecimal balance;


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
