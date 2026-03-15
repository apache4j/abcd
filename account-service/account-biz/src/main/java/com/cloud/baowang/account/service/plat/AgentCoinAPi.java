package com.cloud.baowang.account.service.plat;

import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.po.AgentCommissionCoinPO;
import com.cloud.baowang.account.po.AgentQuotaCoinPO;

public interface AgentCoinAPi {


    /**
     * 佣金钱包账变
     * @param accountAgentCoinAddReqVO
     * @return
     */
    AccountCoinResultVO agentCommissionCoinAdd(AccountAgentCoinAddReqVO accountAgentCoinAddReqVO, AgentCommissionCoinPO agentCommissionCoinPO);

    /**
     * 额度钱包账变
     * @param accountAgentCoinAddReqVO
     * @return
     */
    AccountCoinResultVO agentQuotaCoinAdd(AccountAgentCoinAddReqVO accountAgentCoinAddReqVO, AgentQuotaCoinPO agentQuotaCoinPO);

    /**
     * 查询佣金钱包
     * @param agentId
     * @return
     */
    AgentCommissionCoinPO getCommissionCoinAgentId(String agentId);

    /**
     * 查询额度钱包
     * @param agentId
     * @return
     */
    AgentQuotaCoinPO getQuotaCoinAgentId(String agentId);



}
