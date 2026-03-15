package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentSafeCenterApi;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.agent.service.AgentSafeCenterService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.verify.SenderServiceApi;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import com.cloud.baowang.user.api.vo.user.UserGetMailCodeVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/06/19 15:05
 * @description:
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentSafeCenterApiImpl implements AgentSafeCenterApi {
    private final AgentSafeCenterService agentSafeCenterService;
    private final SenderServiceApi senderServiceApi;

    @Override
    public ResponseVO<AgentSecuritySetVO> column(String siteCode,String agentAccount) {
        return ResponseVO.success(agentSafeCenterService.column(siteCode,agentAccount));
    }

    @Override
    public ResponseVO<Boolean> passwordEdit(AgentPasswordEditVO vo) {
        return agentSafeCenterService.passwordEdit(vo);
    }

    @Override
    public ResponseVO<Boolean> payPasswordEdit(AgentPayPasswordEditVO vo) {
        return agentSafeCenterService.payPasswordEdit(vo);
    }

    @Override
    public ResponseVO sendMail(AgentGetMailCodeVO vo) {
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
    public ResponseVO<Boolean> bindEmail(AgentBindEmailVO vo) {
        return agentSafeCenterService.bindEmail(vo);
    }

    @Override
    public ResponseVO<Boolean> bindAuthenticator(AgentBindAuthenticatorVO vo) {
        return agentSafeCenterService.bindAuthenticator(vo);
    }
}
