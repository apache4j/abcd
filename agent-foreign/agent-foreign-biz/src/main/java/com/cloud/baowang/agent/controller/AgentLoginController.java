package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentLoginApi;
import com.cloud.baowang.agent.api.api.AgentSecurityApi;
import com.cloud.baowang.agent.api.api.AgentSecurityQuestionApi;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.agentLogin.*;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.service.AgentLoginService;
import com.cloud.baowang.agent.service.AgentTokenService;
import com.cloud.baowang.agent.service.CaptchaService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.LoginTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 9:03
 * @description:
 */
@Tag(name =  "代理-登录")
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/agentLogin")
public class AgentLoginController {

    private final CaptchaService captchaService;
    private final SystemParamApi systemParamApi;
    private final AgentLoginApi agentLoginApi;
    private final AgentInfoApi agentInfoApi;
    private final AgentLoginService agentLoginService;
    private final AgentSecurityQuestionApi agentSecurityQuestionApi;
    private final AgentSecurityApi agentSecurityApi;

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public void captcha(@RequestParam("codeKey") String codeKey, HttpServletResponse response) throws Exception {
        captchaService.create(codeKey, response);
    }

    @PostMapping("login")
    @Operation(summary = "代理后台登录")
    public ResponseVO<AgentLoginResultVO> login(@Valid @RequestBody AgentLoginParamVO agentLoginParamVO, HttpServletRequest request) {
        String siteCode = CurrReqUtils.getSiteCode();
        agentLoginParamVO.setSiteCode(siteCode);
        agentLoginParamVO.setDeviceNo(CurrReqUtils.getReqDeviceId());

        //校验验证码
        boolean isTrue = captchaService.check(agentLoginParamVO.getCodeKey(), agentLoginParamVO.getVerifyCode());
        if(!isTrue) {
            agentLoginApi.saveLoginLog(agentLoginParamVO, LoginTypeEnum.FAIL.getCode().toString(), ResultCode.AGENT_LOGIN_CODE_ERROR.getDesc());
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }

        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(siteCode, agentLoginParamVO.getAgentAccount().trim());
        if (agentInfoVO == null) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }

        if(agentInfoVO.getStatus().contains(AgentStatusEnum.LOGIN_LOCK.getCode())) {
            agentLoginApi.saveLoginLog(agentLoginParamVO, LoginTypeEnum.FAIL.getCode().toString(), ResultCode.AGENT_LOGIN_LOCK.getDesc());
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_LOCK);
        }

        boolean isSuc = agentLoginApi.checkPassword(agentLoginParamVO);
        if (!isSuc) {
            agentLoginApi.saveLoginLog(agentLoginParamVO, LoginTypeEnum.FAIL.getCode().toString(), ResultCode.USER_LOGIN_ERROR.getDesc());
            return ResponseVO.fail(ResultCode.USER_LOGIN_ERROR);
        }

        ResponseVO<AgentLoginResultVO> responseVO =  agentLoginService.agentLogin(agentLoginParamVO);
        return responseVO;
    }

    @Operation(summary = "代理退出")
    @PostMapping(value = "/agentLoginOut")
    public ResponseVO agentLoginOut() {
        String siteCode = CurrReqUtils.getSiteCode();
        AgentAccountVO agentAccountVO =  AgentTokenService.getCurrentAgent();
        if (agentAccountVO == null) {
            return ResponseVO.fail(ResultCode.SIGN_EMPTY);
        }
        return agentLoginService.agentLogOut(siteCode,agentAccountVO.getAgentId());
    }

    @Operation(summary = "代理找回密码--身份验证")
    @PostMapping(value = "/loginCheckAgentAccount")
    public ResponseVO<AgentAccountCheckResVO> loingCheckAgentAccount(@Valid @RequestBody AgentAccountCheckVO checkVO) {
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

        Boolean isSetGoogle = true;
        if (StringUtils.isEmpty(agentInfoVO.getGoogleAuthKey())) {
            isSetGoogle = false;
        }

        AgentAccountCheckResVO resVO = new AgentAccountCheckResVO();
        resVO.setAgentAccount(agentInfoVO.getAgentAccount());
        resVO.setIsSetGoogle(isSetGoogle);

        return ResponseVO.success(resVO);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/sendMail")
    ResponseVO<?> sendMail(@Valid  @RequestBody AgentLoginGetMailCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送邮箱验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);

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
        vo.setSiteCode(siteCode);
        return agentLoginApi.sendMail(vo);
    }

    @Operation(summary = "邮箱验证码校验")
    @PostMapping("/checkVerifyCode")
    ResponseVO checkVerifyCode(@Valid @RequestBody AgentVerifyCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);

        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(siteCode, vo.getAgentAccount());
        if (agentInfoVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        if (agentInfoVO.getEmail() == null || agentInfoVO.getEmail().length() == 0) {
            return ResponseVO.fail(ResultCode.AGENT_EMAIL_ERROR);
        }

        if (!agentInfoVO.getEmail().equals(vo.getEmail())) {
            return ResponseVO.fail(ResultCode.SELECT_RIGHT_EMAIL);
        }

        vo.setSiteCode(siteCode);
        return agentLoginApi.checkVerifyCode(vo);
    }

    @Operation(summary = "Google验证码校验")
    @PostMapping("/googleCheckVerifyCode")
    ResponseVO googleCheckVerifyCode(@Valid @RequestBody GoogleVerifyCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);

        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountSite(siteCode, vo.getAgentAccount());
        if (agentInfoVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        if (!verifyCode(vo.getVerifyCode(), agentInfoVO.getGoogleAuthKey())) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }

        RedisUtil.setValue(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getAgentAccount()), 1, 5 * 60L);
        return ResponseVO.success();
    }

    private boolean verifyCode(String verifyCode, String googleAuthKey) {
        return GoogleAuthUtil.checkCode(googleAuthKey, Integer.parseInt(verifyCode));
    }


    @Operation(summary = "设置新密码")
    @PostMapping(value = "/passwordSet")
    ResponseVO passwordSet(@Valid  @RequestBody AgentPasswordSetVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);

        //再次校验验证码
        Integer result = RedisUtil.getValue(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getAgentAccount()));
        if (result == null || result != 1) {
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }

        if (RedisUtil.isKeyExist(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getAgentAccount()))) {
            RedisUtil.deleteKey(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getAgentAccount()));
        }

        return agentLoginApi.passwordSet(vo);
    }

}
