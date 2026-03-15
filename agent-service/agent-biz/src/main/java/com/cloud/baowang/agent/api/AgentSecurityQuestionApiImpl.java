package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentSecurityQuestionApi;
import com.cloud.baowang.agent.api.vo.security.AgentSecurityListVO;
import com.cloud.baowang.agent.service.AgentSecurityQuestionConfigService;
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
public class AgentSecurityQuestionApiImpl implements AgentSecurityQuestionApi {
    private final AgentSecurityQuestionConfigService agentSecurityQuestionConfigService;

    @Override
    public ResponseVO<List<AgentSecurityListVO>> agentSecuritySetAllSecurityQuestions() {
        return ResponseVO.success(agentSecurityQuestionConfigService.allEnableSecurityQuestions());
    }
}
