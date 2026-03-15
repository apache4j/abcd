package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionCalcVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: fangfei
 * @createTime: 2024/11/20 22:24
 * @description:
 */
@FeignClient(contextId = "remoteAgentCommissionCalcApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 佣金结算 ")
public interface AgentCommissionCalcApi {
    String PREFIX = ApiConstants.PREFIX + "/commissionCalc/api";

    @Operation(description = "佣金结算")
    @PostMapping(value = PREFIX + "/agentFinalCommissionGenerate")
    void agentFinalCommissionGenerate(@RequestBody AgentCommissionCalcVO commissionCalcVO);

    @Operation(description = "预期佣金结算")
    @PostMapping(value = PREFIX + "/agentExpectCommissionGenerate")
    void agentExpectCommissionGenerate(@RequestBody AgentCommissionCalcVO commissionCalcVO);

    @Operation(description = "返点结算")
    @PostMapping(value = PREFIX + "/agentRebateGenerate")
    void agentRebateGenerate(@RequestBody AgentCommissionCalcVO commissionCalcVO);

    @Operation(description = "预期返点结算")
    @PostMapping(value = PREFIX + "/agentRebateExpectGenerate")
    void agentRebateExpectGenerate(@RequestBody AgentCommissionCalcVO commissionCalcVO);

}
