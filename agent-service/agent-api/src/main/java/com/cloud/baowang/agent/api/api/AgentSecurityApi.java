package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoComVO;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteAgentSecurityApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理密保 服务")
public interface AgentSecurityApi {

    String PREFIX = ApiConstants.PREFIX + "/agentSecurity/api";

    @Operation(summary = "密保问题设置")
    @PostMapping(value = "/security-qa-set")
    ResponseVO<Boolean> securityQASet(@RequestBody AgentSecurityEditVO vo);

    @Operation(summary = "密保问题验证")
    @PostMapping(value = "/security-qa-verify")
    ResponseVO<Boolean> securityQAVerify(@RequestBody AgentSecurityVerifyVO vo);

    @PostMapping("resetPassword")
    @Operation(summary = "代理找回密码重置")
    ResponseVO resetPassword(@RequestBody @Validated AgentResetPasswordVO agentResetPasswordVO);

    @PostMapping("getAgentSecurityQuestions")
    @Operation(summary = "获取指定代理所有密保问题")
    ResponseVO<List<AgentSecurityListVO>> getAgentSecurityQuestions(@RequestBody AgentInfoComVO agentInfoComVO);
}
