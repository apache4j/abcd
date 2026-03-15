package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentSecurityApi;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentAccountCheckVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoComVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.agent.service.CaptchaService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 9:03
 * @description:
 */
@Tag(name =  "代理-忘记密码")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/agentLogin")
public class AgentRePasswordController {

    private final CaptchaService captchaService;
    private final AgentInfoApi agentInfoApi;
    private final AgentSecurityApi agentSecurityApi;

    @PostMapping("/checkAgentAccount")
    @Operation(summary = "找回密码第一步-身份验证")
    public ResponseVO checkAgentAccount(@Valid @RequestBody AgentAccountCheckVO checkVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        //校验验证码
        boolean isTrue = captchaService.check(checkVO.getCodeKey(), checkVO.getVerifyCode());
        if(!isTrue) {
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }
        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(siteCode, checkVO.getAgentAccount());
        if (agentInfoVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        return ResponseVO.success();
    }

    @Operation(summary = "获取指定代理所有密保问题")
    @PostMapping(value = "/getAgentSecurityQuestions")
    public ResponseVO<List<AgentSecurityListVO>> getAgentSecurityQuestions(@Valid @RequestBody AgentInfoComVO agentInfoComVO) {
        return agentSecurityApi.getAgentSecurityQuestions(agentInfoComVO);
    }

    @Operation(summary = "找回密码第二步-密保问题验证")
    @PostMapping(value = "/security-qa-verify")
    public ResponseVO<Boolean> securityQAVerify(@RequestBody AgentSecurityVerifyVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(siteCode, vo.getAgentAccount());
        vo.setAgentAccount(agentInfoVO.getAgentAccount());
        return agentSecurityApi.securityQAVerify(vo);
    }

    @PostMapping("resetPassword")
    @Operation(summary = "代理找回密码重置-修改密码")
    public ResponseVO resetPassword(@RequestBody @Validated AgentResetPasswordVO agentResetPasswordVO) {
        agentResetPasswordVO.setAgentAccount(CurrReqUtils.getAccount());
        if(RedisUtil.getValue(String.format(RedisConstants.AGENT_FORGET_PASSWORD_VERIFY,agentResetPasswordVO.getAgentAccount())) == null) {
            return ResponseVO.fail(ResultCode.AGENT_SECURITY_QA_VERIFY_ERROR);
        }
        return agentSecurityApi.resetPassword(agentResetPasswordVO);
    }
}
