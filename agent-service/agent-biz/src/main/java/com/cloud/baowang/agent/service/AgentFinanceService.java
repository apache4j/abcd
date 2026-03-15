package com.cloud.baowang.agent.service;


import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCommissionInfoVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositWithdrawStatisticsVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentProxyDepositVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentRebateInfoVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferInfoVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.service.commission.AgentCommissionReviewRecordService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AgentFinanceService {

    private final AgentInfoService agentInfoService;

    private final AgentQuotaCoinService agentQuotaCoinService;

    private final AgentCommissionCoinService agentCommissionCoinService;

    private final AgentDepositOfSubordinatesService depositOfSubordinatesService;

    private final AgentCommissionReviewRecordService agentCommissionReviewRecordService;

    private final AgentDepositWithdrawService agentDepositWithdrawService;

    private final AgentTransferRecordService agentTransferRecordService;

    public ResponseVO<AgentFinanceVO> getAgentFinanceInfo(AgentFinanceRequestVO agentFinanceRequestVO){

        String agentAccount = agentFinanceRequestVO.getAgentAccount();
        String siteCode = agentFinanceRequestVO.getSiteCode();
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode,siteCode);
        queryWrapper.eq(AgentInfoPO::getAgentAccount, agentAccount);
        AgentInfoPO po = agentInfoService.getOne(queryWrapper);
        if(null == po){
            return ResponseVO.fail(ResultCode.AGENT_NOT_EXISTS);
        }
        String agentId = po.getAgentId();
        AgentFinanceVO agentFinanceVO = new AgentFinanceVO();
        agentFinanceVO.setPlatformCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        //获取代理余额信息
        AgentBalanceVO agentBalanceVO = getAgentBalance(agentAccount,siteCode);
        agentFinanceVO.setAgentBalanceVO(agentBalanceVO);
        //获取代理佣金信息
        AgentCommissionInfoVO agentCommissionInfoVO = agentCommissionReviewRecordService.sumCommissionPayment(agentId);
        agentFinanceVO.setAgentCommissionInfoVO(agentCommissionInfoVO);
        //代理代存信息
        AgentProxyDepositVO agentProxyDepositVO = depositOfSubordinatesService.getAgentProxyDepositVO(agentId);
        agentFinanceVO.setAgentProxyDepositVO(agentProxyDepositVO);

        //代理充提信息
        List<String> agentAccounts = new ArrayList<>();
        agentAccounts.add(agentAccount);
        AgentDepositWithdrawStatisticsVO agentDepositWithdrawStatisticsVO =  agentDepositWithdrawService.getAgentDepositWithdraws(siteCode,agentAccounts).get(agentAccount);
        agentFinanceVO.setAgentDepositWithdrawStatisticsVO(agentDepositWithdrawStatisticsVO);

        //代理转账信息
        AgentTransferInfoVO agentTransferInfoVO = agentTransferRecordService.getAgentTransferInfo(agentId);
        agentFinanceVO.setAgentTransferInfoVO(agentTransferInfoVO);

        return ResponseVO.success(agentFinanceVO);

    }

    private AgentBalanceVO getAgentBalance(String agentAccount,String siteCode) {

        BigDecimal commissionBalance = BigDecimal.ZERO,quotaBalance = BigDecimal.ZERO,freezeBalance = BigDecimal.ZERO;
        //佣金钱包余额
        AgentCoinBalanceVO commissionBalanceVO = agentCommissionCoinService.getCommissionCoinBalanceSite(agentAccount,siteCode);
        if(null != commissionBalanceVO && null != commissionBalanceVO.getAvailableAmount()  ){
            commissionBalance = commissionBalance.add(commissionBalanceVO.getAvailableAmount());
            freezeBalance =  freezeBalance.add(commissionBalanceVO.getFreezeAmount());
        }
        //额度钱包余额
        AgentCoinBalanceVO quotaBalanceVO = agentQuotaCoinService.getQuotaCoinBalanceSite(agentAccount,siteCode);
        AgentBalanceVO agentBalanceVO = new AgentBalanceVO();
        if(quotaBalanceVO != null && null != quotaBalanceVO.getAvailableAmount() ){
            quotaBalance = quotaBalance.add(quotaBalanceVO.getAvailableAmount());
            freezeBalance = freezeBalance.add(quotaBalanceVO.getFreezeAmount());
        }
        agentBalanceVO.setCommissionBalance(commissionBalance);
        agentBalanceVO.setQuotaBalance(quotaBalance);
        agentBalanceVO.setFreezeBalance(freezeBalance);
        agentBalanceVO.setTotalBalance(agentBalanceVO.getCommissionBalance().add(agentBalanceVO.getQuotaBalance()).add(agentBalanceVO.getFreezeBalance()));
        return  agentBalanceVO;
    }
}
