package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentRechargeWithdrawOrderStatusHandleApi;
import com.cloud.baowang.agent.api.vo.recharge.AgentVirtualCurrencyRechargeOmissionsReqVO;
import com.cloud.baowang.agent.service.AgentRechargeWithdrawOrderStatusHandleService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@AllArgsConstructor
@RestController
public class AgentRechargeWithdrawOrderStatusHandleApiImpl implements AgentRechargeWithdrawOrderStatusHandleApi {

    private final AgentRechargeWithdrawOrderStatusHandleService agentRechargeWithdrawOrderStatusHandleService;


    @Override
    public ResponseVO rechargeOrderHandle() {

        return agentRechargeWithdrawOrderStatusHandleService.rechargeOrderHandle();
    }

    @Override
    public ResponseVO withdrawOrderHandle() {
        return agentRechargeWithdrawOrderStatusHandleService.withdrawOrderHandle();
    }

    @Override
    public ResponseVO virtualCurrencyRechargeOmissionsHandle(AgentVirtualCurrencyRechargeOmissionsReqVO vo) {
        return agentRechargeWithdrawOrderStatusHandleService.virtualCurrencyRechargeOmissionsHandle(vo);
    }
}
