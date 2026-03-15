package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentLoginRecordApi;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordParam;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordVO;
import com.cloud.baowang.agent.service.AgentLoginRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentLoginRecordApiImpl implements AgentLoginRecordApi {

    private AgentLoginRecordService agentLoginRecordService;

    @Override
    public ResponseVO<AgentLoginRecordVO> queryAgentLoginRecord(AgentLoginRecordParam vo) {
        return agentLoginRecordService.queryAgentLoginRecord(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(AgentLoginRecordParam vo) {
        return agentLoginRecordService.getTotalCount(vo);
    }
}
