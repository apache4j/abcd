package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDomainApi;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainVO;
import com.cloud.baowang.agent.service.AgentDomainService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentDomainApiImpl implements AgentDomainApi {

    private final AgentDomainService agentDomainService;

    @Override
    public ResponseVO<Boolean> addAgentDomain(List<AgentDomainVO> agentDomainVO) {
        return agentDomainService.addAgentDomain(agentDomainVO);
    }

    @Override
    public ResponseVO<Boolean> updateAgentDomain(AgentDomainVO agentDomainVO) {
        return agentDomainService.updateAgentDomain(agentDomainVO);
    }

    @Override
    public ResponseVO<Boolean> deleteAgentDomain(String id) {
        return agentDomainService.deleteAgentDomain(id);
    }

    @Override
    public ResponseVO<AgentDomainResponseVO> getAgentDomainById(String id) {
        return agentDomainService.getAgentDomainById(id);
    }

    @Override
    public ResponseVO<Page<AgentDomainResponseVO>> getAgentDomainList(AgentDomainPageQueryVO agentDomainVO) {
        return agentDomainService.getAgentDomainList(agentDomainVO);
    }
}
