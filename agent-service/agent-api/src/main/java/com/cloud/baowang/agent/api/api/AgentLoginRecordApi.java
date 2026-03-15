package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordParam;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentLoginRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理登录日志 服务")
public interface AgentLoginRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentLoginRecord/api";

    @Operation(summary = "代理登录日志")
    @PostMapping(value = PREFIX + "/queryAgentLoginRecord")
    ResponseVO<AgentLoginRecordVO> queryAgentLoginRecord(@RequestBody AgentLoginRecordParam param);


    @Operation(summary = "代理登录日志")
    @PostMapping(value = PREFIX + "/getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody AgentLoginRecordParam param);
}
