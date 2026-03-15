package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: fangfei
 * @createTime: 2024/06/19 10:33
 * @description: 安全中心
 */
@FeignClient(contextId = "remoteAgentSafeCenterApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理安全中心 服务")
public interface AgentSafeCenterApi {
    String PREFIX = ApiConstants.PREFIX + "/agentSafeCenter/api";

    @Operation(summary = "代理安全设置栏目")
    @PostMapping(value = "/column")
    ResponseVO<AgentSecuritySetVO> column(@RequestParam("siteCode") String siteCode, @RequestParam("agentAccount") String agentAccount);

    @Operation(summary = "登录密码设置")
    @PostMapping(value = "/password-edit")
    ResponseVO<Boolean> passwordEdit(@RequestBody AgentPasswordEditVO vo);

    @Operation(summary = "支付密码设置")
    @PostMapping(value = "/pay-password-edit")
    ResponseVO<Boolean> payPasswordEdit(@RequestBody AgentPayPasswordEditVO vo);

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/sendMail")
    ResponseVO<?> sendMail(@RequestBody AgentGetMailCodeVO vo);

    @Operation(summary = "绑定邮箱")
    @PostMapping(value = "/bind-email")
    ResponseVO<Boolean> bindEmail(@RequestBody AgentBindEmailVO vo);

    @Operation(summary = "绑定身份验证器")
    @PostMapping(value = "/bind-authenticator")
    ResponseVO<Boolean> bindAuthenticator(@RequestBody AgentBindAuthenticatorVO vo);


}
