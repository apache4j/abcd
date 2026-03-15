package com.cloud.baowang.agent.api.vo.agentCoin;


import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理钱包添加请求对象")
public class AgentCoinAddVO {

    private String siteCode;

    private String agentId;


    /**
     * 代理账号
     */
    private String agentAccount;



    /**
     * 币种
     */
    private String currency;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 三方单号
     */
    private String thirdOrderNo;


    /**
     * 三方充值渠道code
     */
    private String toThridCode;


    /**
     * 代理钱包类型 AgentCoinRecordTypeEnum.AgentWalletTypeEnum
     */
    private String agentWalletType;

    /**
     * 业务类型
     * 对应枚举类 AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum
     *
     */
    private String businessCoinType;

    /**
     * 账变类型
     * 对应枚举 AgentCoinRecordTypeEnum.AgentCoinTypeEnum
     */
    private String coinType;

    /**
     * 客户端账变类型
     * 对应枚举 AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum
     */
    private String customerCoinType;

    /**
     * 收支类型 1收入 2支出 3冻结 4解冻
     * 对应枚举类 AgentCoinRecordTypeEnum.AgentBalanceTypeEnum
     */
    private String balanceType;

    /**
     * 金额改变数量
     */
    private BigDecimal coinValue;


    /**
     * 备注
     */
    private String remark;

    /**
     * 取款标记
     */
    public Integer withdrawFlag;

    /**
     * 代理信息
     */
    private AgentInfoVO agentInfo;

    /**
     * 账变时间
     */
    private Long coinTime;

}
