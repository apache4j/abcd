package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentSecurityApi;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoComVO;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.agent.service.AgentLoginService;
import com.cloud.baowang.agent.service.AgentSecurityService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.UserChecker;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 16:01
 * @description:
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentSecurityApiImpl implements AgentSecurityApi {

    private final AgentSecurityService agentSecurityService;
    private final AgentLoginService agentLoginService;

    @Override
    public ResponseVO<Boolean> securityQASet(AgentSecurityEditVO vo) {
        return agentSecurityService.securityQASet(vo);
    }

    @Override
    public ResponseVO<Boolean> securityQAVerify(AgentSecurityVerifyVO vo) {
        return agentSecurityService.securityQAVerify(vo);
    }

    @Override
    public ResponseVO resetPassword(AgentResetPasswordVO agentResetPasswordVO) {
        // 密码校验
        if (UserChecker.checkPassword(agentResetPasswordVO.getNewPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        if (UserChecker.checkPassword(agentResetPasswordVO.getConfirmPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }
        return agentLoginService.resetPassword(agentResetPasswordVO);
    }

    @Override
    public ResponseVO<List<AgentSecurityListVO>> getAgentSecurityQuestions(AgentInfoComVO agentInfoComVO) {
        return ResponseVO.success(agentSecurityService.getAgentSecurityQuestions(agentInfoComVO.getAgentAccount()));
    }
}
