package com.cloud.baowang.agent.api.vo.depositWithdraw;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentDepositWithdrawalInfoVO {

    /**
     * 1 存款 2 提款
     */
    private Integer type;


    /**
     * 存取款金额
     */
    private BigDecimal depositWithdrawalAmount;

    /**
     * 代理账号
     */
    private String agentAccount;


    /**
     * 是否大额
     */
    private String isBigMoney;

    /**
     * 存取时间
     */
    private Long depositWithdrawTime;

    /**
     * 取款钱包类型
     */
    private String depositWithdrawMethod;

}
