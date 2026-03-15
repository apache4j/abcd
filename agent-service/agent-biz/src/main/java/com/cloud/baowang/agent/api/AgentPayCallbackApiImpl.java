package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentPayCallbackApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackDepositParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackWithdrawParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentVirtualCurrencyPayCallbackVO;
import com.cloud.baowang.agent.service.AgentDepositWithdrawCallbackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentPayCallbackApiImpl implements AgentPayCallbackApi {

    private final AgentDepositWithdrawCallbackService agentDepositWithdrawCallbackService;



    @Override
    public boolean virtualCurrencyDepositCallback(AgentVirtualCurrencyPayCallbackVO vo) {
        return agentDepositWithdrawCallbackService.virtualCurrencyDepositCallback(vo);
    }

    @Override
    public boolean withdrawCallback(AgentCallbackWithdrawParamVO callbackWithdrawParamVO) {
        return agentDepositWithdrawCallbackService.agentWithdrawCallback(callbackWithdrawParamVO);
    }

    @Override
    public Boolean agentDepositCallback(AgentCallbackDepositParamVO callbackDepositParamVO) {
        return agentDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
    }
}
