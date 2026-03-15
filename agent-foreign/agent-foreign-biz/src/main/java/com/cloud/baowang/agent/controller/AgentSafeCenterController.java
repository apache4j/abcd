package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentLoginApi;
import com.cloud.baowang.agent.api.api.AgentSafeCenterApi;
import com.cloud.baowang.agent.api.api.AgentSecurityApi;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentVerifyCodeVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/06/19 9:35
 * @description: 代理安全中心
 */
@Tag(name =  "代理-安全中心")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/safeCenter")
public class AgentSafeCenterController {

    private final AgentSafeCenterApi agentSafeCenterApi;
    private final AgentSecurityApi agentSecurityApi;
    private final AgentInfoApi agentInfoApi;
    private final AgentLoginApi agentLoginApi;
    
    @Operation(summary = "代理安全中心栏目")
    @PostMapping(value = "/column")
    public ResponseVO<AgentSecuritySetVO> column() {
        String agentAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(agentAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        return agentSafeCenterApi.column(CurrReqUtils.getSiteCode(),agentAccount);
    }

    @Operation(summary = "登录密码设置")
    @PostMapping(value = "/password-edit")
    ResponseVO<Boolean> passwordEdit(@Valid  @RequestBody AgentPasswordEditVO vo) {
        String agentAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(agentAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        vo.setAgentAccount(agentAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentSafeCenterApi.passwordEdit(vo);
    }


    @Operation(summary = "密保问题验证")
    @PostMapping(value = "/security-qa-verify")
    public ResponseVO<Boolean> securityQAVerify(@RequestBody AgentSafeCenterVerifyVO vo) {
        String agentAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(agentAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        vo.setAgentAccount(agentAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        AgentSecurityVerifyVO verifyVO = new AgentSecurityVerifyVO();
        BeanUtils.copyProperties(vo, verifyVO);
        return agentSecurityApi.securityQAVerify(verifyVO);
    }

    @Operation(summary = "支付密码编辑")
    @PostMapping(value = "/pay-password-edit")
    public ResponseVO<Boolean> payPasswordEdit(@Valid @RequestBody AgentPayPasswordEditVO vo) {
        String agentAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(agentAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        vo.setAgentAccount(agentAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentSafeCenterApi.payPasswordEdit(vo);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/sendMail")
    ResponseVO<?> sendMail(@Valid @RequestBody AgentGetMailCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        String agentAccount = CurrReqUtils.getAccount();
        vo.setSiteCode(siteCode);
        vo.setAgentAccount(agentAccount);

        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(siteCode, agentAccount);
        if (agentInfoVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        if (vo.getEmail() == null || vo.getEmail().length() == 0) {
            return ResponseVO.fail(ResultCode.MAIL_CODE_ERROR);
        }
        return agentSafeCenterApi.sendMail(vo);
    }

    @Operation(summary = "绑定邮箱")
    @PostMapping(value = "/bind-email")
    ResponseVO<Boolean> bindEmail(@RequestBody AgentBindEmailVO vo) {
        String agentAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(agentAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        vo.setAgentAccount(agentAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentSafeCenterApi.bindEmail(vo);
    }

    @Operation(summary = "邮箱验证码校验")
    @PostMapping("/checkVerifyCode")
    ResponseVO checkVerifyCode(@Valid @RequestBody MailVerifyCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        String agentAccount = CurrReqUtils.getAccount();
        vo.setSiteCode(siteCode);
        vo.setAgentAccount(agentAccount);

        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(siteCode, vo.getAgentAccount());
        if (agentInfoVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        if (agentInfoVO.getEmail() == null || agentInfoVO.getEmail().length() == 0) {
            return ResponseVO.fail(ResultCode.MAIL_CODE_ERROR);
        }

        if (!agentInfoVO.getEmail().equals(vo.getEmail())) {
            return ResponseVO.fail(ResultCode.SELECT_RIGHT_EMAIL);
        }
        vo.setAgentAccount(agentInfoVO.getAgentAccount());
        vo.setSiteCode(siteCode);
        AgentVerifyCodeVO codeVO = new AgentVerifyCodeVO();
        BeanUtils.copyProperties(vo, codeVO);
        return agentLoginApi.checkVerifyCode(codeVO);
    }

    @GetMapping("genGoogleAuthKey")
    @Operation(summary = "生成google key")
    public ResponseVO<String> genGoogleAuthKey() {
        return ResponseVO.success(GoogleAuthUtil.generateSecretKey());
    }


    @Operation(summary = "绑定身份验证器")
    @PostMapping(value = "/bind-authenticator")
    public ResponseVO<Boolean> bindAuthenticator(@RequestBody AgentBindAuthenticatorVO vo) {
        String agentAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(agentAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        vo.setAgentAccount(agentAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setRebind(false);
        return agentSafeCenterApi.bindAuthenticator(vo);
    }


    @Operation(summary = "重新绑定身份验证器")
    @PostMapping(value = "/rebind-authenticator")
    public ResponseVO<Boolean> rebindAuthenticator(@RequestBody AgentBindAuthenticatorVO vo) {
        String agentAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(agentAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        vo.setAgentAccount(agentAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setRebind(true);
        return agentSafeCenterApi.bindAuthenticator(vo);
    }
}
