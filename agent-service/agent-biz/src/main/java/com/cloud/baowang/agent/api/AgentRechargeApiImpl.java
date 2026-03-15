package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentRechargeApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentTradeRecordDetailRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentDepositOrderDetailVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentDepositOrderFileVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeConfigRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeConfigVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentOrderNoVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeRecordDetailResponseVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeReqVO;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordResponseVO;
import com.cloud.baowang.agent.service.AgentRechargeService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@AllArgsConstructor
@RestController
public class AgentRechargeApiImpl implements AgentRechargeApi {

    private final AgentRechargeService agentRechargeService;


    @Override
    public ResponseVO<AgentOrderNoVO> agentRecharge(AgentRechargeReqVO agentRechargeReqVO) {
        return agentRechargeService.agentRecharge(agentRechargeReqVO);

    }



    @Override
    public ResponseVO<AgentDepositOrderDetailVO> depositOrderDetail(AgentOrderNoVO orderNoVO) {
        return ResponseVO.success(agentRechargeService.depositOrderDetail(orderNoVO));
    }


    @Override
    public ResponseVO<Integer> uploadVoucher(AgentDepositOrderFileVO depositOrderFileVO) {
        return ResponseVO.success(agentRechargeService.uploadVoucher(depositOrderFileVO));
    }

    @Override
    public ResponseVO<Integer> cancelDepositOrder(AgentOrderNoVO orderNoVO) {
        return ResponseVO.success(agentRechargeService.cancelDepositOrder(orderNoVO));
    }
    @Override
    public void urgeOrder(AgentOrderNoVO vo) {
        agentRechargeService.urgeOrder(vo);
    }

    @Override
    public ResponseVO<AgentRechargeConfigVO> getRechargeConfig(AgentRechargeConfigRequestVO vo) {

        return  agentRechargeService.getRechargeConfig(vo);
    }

    @Override
    public Page<ClientAgentRechargeRecordResponseVO> clientAgentRechargeRecorder(ClientAgentRechargeRecordRequestVO vo) {
        return agentRechargeService.clientAgentRechargeRecorder(vo);
    }

    @Override
    public AgentRechargeRecordDetailResponseVO clientAgentRechargeRecordDetail(AgentTradeRecordDetailRequestVO vo) {
        return agentRechargeService.clientAgentRechargeRecordDetail(vo);
    }
}
