package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginGetMailCodeVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginParamVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentPasswordSetVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentVerifyCodeVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.security.AgentPasswordEditVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 22:34
 * @description:
 */

@FeignClient(contextId = "remoteAgentLoginApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理登录 服务")
public interface AgentLoginApi {
    String PREFIX = ApiConstants.PREFIX + "/agent-login/api";

    @Operation(summary = "代理登录")
    @PostMapping(value = PREFIX + "/agentLogin")
    ResponseVO<AgentInfoVO> agentLogin(@RequestBody AgentLoginParamVO agentLoginParamVO);

    @Operation(summary = "记录代理登录日志")
    @PostMapping(value = PREFIX + "/saveLoginLog")
    void saveLoginLog(@RequestBody AgentLoginParamVO agentLoginParamVO,
                      @RequestParam("loginStatus") String loginStatus, @RequestParam("remark") String remark);

    @PostMapping(PREFIX + "/checkPassword")
    boolean checkPassword(@RequestBody AgentLoginParamVO agentLoginParamVO);

    @PostMapping(PREFIX + "/sendMail")
    ResponseVO sendMail(@RequestBody AgentLoginGetMailCodeVO vo);

    @Operation(summary = "验证码校验")
    @PostMapping(value = PREFIX + "/checkVerifyCode")
    ResponseVO checkVerifyCode(@RequestBody AgentVerifyCodeVO vo);

    @Operation(summary = "登录密码设置")
    @PostMapping(value = "/passwordSet")
    ResponseVO passwordSet(@RequestBody AgentPasswordSetVO vo);
}
