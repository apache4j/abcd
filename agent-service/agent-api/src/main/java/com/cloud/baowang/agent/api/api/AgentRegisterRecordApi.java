package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordParam;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordVO;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordParam;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentRegisterRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理注册信息 服务")
public interface AgentRegisterRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentRegisterRecord/api";

    @Operation(summary = "代理注册日志")
    @PostMapping(value = PREFIX + "/queryAgentRegisterRecord")
    ResponseVO<Page<AgentRegisterRecordVO>> queryAgentRegisterRecord(
            @RequestBody AgentRegisterRecordParam param);
}
