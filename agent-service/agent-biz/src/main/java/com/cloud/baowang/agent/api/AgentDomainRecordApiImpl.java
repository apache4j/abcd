package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDomainRecordApi;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordVO;
import com.cloud.baowang.agent.service.AgentDomainRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentDomainRecordApiImpl implements AgentDomainRecordApi {

    private final AgentDomainRecordService agentDomainRecordService;

    @Override
    public ResponseVO<Page<AgentDomainRecordResponseVO>> getAgentDomainRecordList(AgentDomainRecordVO agentDomainRecordVO) {
        return ResponseVO.success(agentDomainRecordService.getAgentDomainRecordList(agentDomainRecordVO));
    }
}
