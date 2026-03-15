package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentreview.AddGeneralAgentVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteDepositOfSubordinatesAddApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代存记录 DepositOfSubordinatesAddApi")
public interface DepositOfSubordinatesAddApi {
    String PREFIX = ApiConstants.PREFIX + "/DepositOfSubordinatesAddApi/api";

    @Operation(description = "会员分配")
    @PostMapping(value = PREFIX + "/depositOfSubordinates")
    ResponseVO depositOfSubordinates(@RequestBody AgentDepositOfSubordinatesVO vo);
}
