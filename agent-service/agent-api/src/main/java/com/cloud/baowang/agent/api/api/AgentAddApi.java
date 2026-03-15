package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentreview.AddGeneralAgentVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteAgentAddApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 新增代理商 AgentAddApi")
public interface AgentAddApi {
    String PREFIX = ApiConstants.PREFIX+"/agent-add/api";

    @Operation(description = "新增总代")
    @PostMapping(value = PREFIX+"/addGeneralAgent")
    ResponseVO addGeneralAgent(@RequestBody AddGeneralAgentVO vo) ;
}
