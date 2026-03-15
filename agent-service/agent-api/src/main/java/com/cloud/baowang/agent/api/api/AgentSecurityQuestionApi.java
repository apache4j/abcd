package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordParam;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordVO;
import com.cloud.baowang.agent.api.vo.security.AgentSecurityListVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteAgentSecurityQuestionApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理密保配置 服务")
public interface AgentSecurityQuestionApi {

    String PREFIX = ApiConstants.PREFIX + "/agentSecurityQuestion/api";

    @Operation(summary = "获取所有密保问题")
    @PostMapping(value = PREFIX + "/all-security-questions")
    ResponseVO<List<AgentSecurityListVO>> agentSecuritySetAllSecurityQuestions();
}
