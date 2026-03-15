package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentAddApi;
import com.cloud.baowang.agent.api.vo.agentreview.AddGeneralAgentVO;
import com.cloud.baowang.agent.service.AgentReviewService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/30 14:11
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentAddApiImpl implements AgentAddApi {
    @Autowired
    private AgentReviewService agentReviewService;

    @Override
    public ResponseVO addGeneralAgent(AddGeneralAgentVO addGeneralAgentVO) {
        return agentReviewService.addGeneralAgent(addGeneralAgentVO);
    }
}
