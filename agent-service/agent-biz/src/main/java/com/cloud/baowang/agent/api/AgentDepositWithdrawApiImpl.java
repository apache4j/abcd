package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDepositWithdrawApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawSumReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawSumRespVO;
import com.cloud.baowang.agent.service.AgentDepositWithdrawService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/11 14:28
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentDepositWithdrawApiImpl implements AgentDepositWithdrawApi {
    private final AgentDepositWithdrawService agentDepositWithdrawService;

    @Override
    public Page<AgentDepositWithdrawRespVO> listPage(AgentDepositWithDrawReqVO vo) {
        return agentDepositWithdrawService.listPage(vo);
    }

    @Override
    public List<AgentDepositWithdrawRespVO> getListByTypeAndAddress(String withdrawTypeCode, String riskControlAccount, String wayId,String siteCode) {
        return agentDepositWithdrawService.getListByTypeAndAddress(withdrawTypeCode,riskControlAccount,wayId,siteCode);
    }

    @Override
    public AgentDepositWithdrawRespVO getDepositWithdrawOrderByOrderNo(String orderNo) {
        return agentDepositWithdrawService.getDepositWithdrawOrderByOrderNo(orderNo);
    }

    @Override
    public List<AgentDepositWithdrawSumRespVO> queryAgentReportAmountGroupBy(AgentDepositWithDrawSumReqVO vo) {
        return agentDepositWithdrawService.queryAgentReportAmountGroupBy(vo);
    }

    @Override
    public AgentDepositWithdrawSumRespVO queryAgentReportCountGroupBy(AgentDepositWithDrawSumReqVO vo) {
        return agentDepositWithdrawService.queryAgentReportCountGroupBy(vo);
    }
}
