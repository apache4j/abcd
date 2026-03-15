package com.cloud.baowang.agent.controller;


import com.cloud.baowang.agent.api.api.AgentVirtualCurrencyApi;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentAccountVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyAddVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyResVO;
import com.cloud.baowang.agent.service.AgentTokenService;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name =  "代理-个人中心-钱包管理")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/agent-virtual-currency/api")
public class AgentVirtualCurrencyController {

    private final AgentVirtualCurrencyApi agentVirtualCurrencyApi;




    @PostMapping("/virtualCurrencyAdd")
    @Operation(summary = "代理数字币新增")
    public ResponseVO<Integer> virtualCurrencyAdd(@Valid @RequestBody AgentVirtualCurrencyAddVO agentVirtualCurrencyAddVO) {
        AgentAccountVO agentAccountVO =  AgentTokenService.getCurrentAgent();
        agentVirtualCurrencyAddVO.setAgentAccount(agentAccountVO.getAgentAccount());
        agentVirtualCurrencyAddVO.setAgentId(agentAccountVO.getId());
        agentVirtualCurrencyAddVO.setVirtualCurrencyAddress(agentVirtualCurrencyAddVO.getVirtualCurrencyAddress().trim());
        agentVirtualCurrencyAddVO.setConfirmVirtualCurrencyAddress(agentVirtualCurrencyAddVO.getConfirmVirtualCurrencyAddress().trim());
        return agentVirtualCurrencyApi.virtualCurrencyAdd(agentVirtualCurrencyAddVO);
    }

    @PostMapping("/virtualCurrencyDelete")
    @Operation(summary = "代理数字币删除")
    public ResponseVO<Integer> virtualCurrencyDelete(@RequestBody IdVO idVO) {
        return agentVirtualCurrencyApi.virtualCurrencyDelete(idVO);
    }

    @PostMapping("/virtualCurrencyList")
    @Operation(summary = "代理数字币列表")
    public ResponseVO<List<AgentVirtualCurrencyResVO>> virtualCurrencyList(){
        AgentAccountVO agentAccountVO =  AgentTokenService.getCurrentAgent();

        return agentVirtualCurrencyApi.virtualCurrencyList(agentAccountVO.getAgentAccount());
    }

   /* @PostMapping("/virtualCurrencyWithdrawalRecordList")
    @Operation(summary = "数字币提款记录列表")
    public ResponseVO<List<AgentWithdrawalSuccessRecordVO>> virtualCurrencyWithdrawalRecordList(){
        AgentAccountVO agentAccountVO =  JwtUtil.getCurrentAgent();
        return agentFeignResource.bankCardVirtualCurrencyWithdrawalRecordList(agentAccountVO.getAgentAccount(), WithdrawWalletTypeEnum.VIRTUAL_CURRENCY.getCode());
    }*/


}
