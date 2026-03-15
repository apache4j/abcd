package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.recharge.AgentVirtualCurrencyRechargeOmissionsReqVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentRechargeWithdrawOrderStatusHandleApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理存款取款订单状态处理 服务")
public interface AgentRechargeWithdrawOrderStatusHandleApi {

    String PREFIX = ApiConstants.PREFIX + "/agentRechargeWithdrawOrderStatusHandle/api/";


    /**
     * 代理充值订单处理任务
     * @return
     */
    @Operation(summary = "代理充值订单处理任务", description = "代理充值订单处理任务")
    @PostMapping(value = PREFIX+"rechargeOrderHandle")
    ResponseVO rechargeOrderHandle();


    /**
     * 代理取款订单处理任务
     * @return
     */
    @Operation(summary = "代理取款订单处理任务", description = "代理取款订单处理任务")
    @PostMapping(value = PREFIX+"withdrawOrderHandle")
    ResponseVO withdrawOrderHandle();

    /**
     * 代理虚拟币订单拉取前半小时订单，查看是否有遗漏
     * @param
     */
    @Operation(summary = "代理取款订单处理任务", description = "代理取款订单处理任务")
    @PostMapping(value = PREFIX+"virtualCurrencyRechargeOmissionsHandle")
    ResponseVO virtualCurrencyRechargeOmissionsHandle(@RequestBody AgentVirtualCurrencyRechargeOmissionsReqVO vo);
}
