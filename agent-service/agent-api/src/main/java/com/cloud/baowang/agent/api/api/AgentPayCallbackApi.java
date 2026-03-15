package com.cloud.baowang.agent.api.api;


import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackDepositParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackWithdrawParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentVirtualCurrencyPayCallbackVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentPayCallbackApi", value = ApiConstants.NAME)
@Tag(name = "代理RPC 充值提款支付回调 服务")
public interface AgentPayCallbackApi {

    String PREFIX = ApiConstants.PREFIX + "/agentPayCallback/api/";

    @Operation(summary = "虚拟币支付充值回调")
    @PostMapping(value = PREFIX + "virtualCurrencyDepositCallback")
    boolean virtualCurrencyDepositCallback(@RequestBody AgentVirtualCurrencyPayCallbackVO vo) ;

    @Operation(summary = "提现回调")
    @PostMapping(value = PREFIX + "withdrawCallback")
    boolean withdrawCallback(@RequestBody AgentCallbackWithdrawParamVO callbackWithdrawParamVO);

    @Operation(summary = "三方平台-支付付回调", description = "三方平台-支付回调")
    @PostMapping(value = PREFIX+"depositCallback")
    Boolean agentDepositCallback(@RequestBody AgentCallbackDepositParamVO callbackDepositParamVO);
}
