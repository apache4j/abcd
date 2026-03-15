package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentLoginApi;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginGetMailCodeVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginParamVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentPasswordSetVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentVerifyCodeVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.security.AgentResetPasswordVO;
import com.cloud.baowang.agent.service.AgentLoginService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.SpringBeanUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.verify.SenderServiceApi;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import com.cloud.baowang.user.api.vo.user.LoginGetMailCodeVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 22:39
 * @description:
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentLoginApiImpl implements AgentLoginApi {
    private final AgentLoginService agentLoginService;
    private final SenderServiceApi senderServiceApi;

    @Override
    public ResponseVO<AgentInfoVO> agentLogin(AgentLoginParamVO agentLoginParamVO) {
        return ResponseVO.success(agentLoginService.agentLogin(agentLoginParamVO));
    }

    @Override
    public void saveLoginLog(AgentLoginParamVO agentLoginParamVO, String loginStatus, String remark) {
        agentLoginService.saveLoginLog(agentLoginParamVO, loginStatus, remark);
    }

    @Override
    public boolean checkPassword(AgentLoginParamVO agentLoginParamVO) {
        return agentLoginService.checkPassword(agentLoginParamVO);
    }

    @Override
    public ResponseVO sendMail(AgentLoginGetMailCodeVO vo) {
        VerifyCodeSendVO verifyCodeSendVO = new VerifyCodeSendVO();
        verifyCodeSendVO.setSiteCode(vo.getSiteCode());
        verifyCodeSendVO.setAccount(vo.getEmail());
        verifyCodeSendVO.setUserAccount(vo.getAgentAccount());
        ResponseVO responseVO = senderServiceApi.sendMail(verifyCodeSendVO);
        if (!responseVO.isOk()) {
            return responseVO;
        }

        return ResponseVO.success();
    }

    @Override
    public ResponseVO checkVerifyCode(AgentVerifyCodeVO vo) {
        //校验验证码
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getAgentAccount()));
        if (code == null || !code.equals(vo.getVerifyCode())) {
            RedisUtil.setValue(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getAgentAccount()), 0, 5 * 60L);
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }

        RedisUtil.setValue(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getAgentAccount()), 1, 5 * 60L);
        RedisUtil.deleteKey(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getAgentAccount()));
        return ResponseVO.success();
    }

    @Override
    public ResponseVO passwordSet(AgentPasswordSetVO vo) {
        AgentResetPasswordVO agentResetPasswordVO = new AgentResetPasswordVO();
        BeanUtils.copyProperties(vo, agentResetPasswordVO);
        return agentLoginService.resetPassword(agentResetPasswordVO);
    }
}
