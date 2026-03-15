package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentLabelApi;
import com.cloud.baowang.agent.api.vo.label.AgentLabelAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRecordListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListUserPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.service.AgentLabelService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class AgentLabelApiImpl implements AgentLabelApi {
    private final AgentLabelService agentLabelService;

    @Override
    public ResponseVO<Void> add(AgentLabelAddVO agentLabelAddVO) {
        return agentLabelService.add(agentLabelAddVO);
    }

    @Override
    public ResponseVO<Void> edit(AgentLabelEditVO agentLabelEditVO) {
        return agentLabelService.edit(agentLabelEditVO);
    }

    @Override
    public ResponseVO<Void> delete(AgentLabelDeleteVO vo) {
        return agentLabelService.delete(vo);
    }

    @Override
    public ResponseVO<Page<AgentLabelListVO>> listPage(AgentLabelListPageVO vo) {
        return agentLabelService.listPage(vo);
    }

    @Override
    public ResponseVO<Page<AgentLabelRecordListVO>> recordListPage(AgentLabelReordListPageVO vo) {
        return agentLabelService.recordListPage(vo);
    }

    @Override
    public ResponseVO<Page<AgentLabelUserVO>> recordListUserPage(AgentLabelReordListUserPageVO vo) {
        return agentLabelService.recordListUserPage(vo);
    }

    @Override
    public List<AgentLabelVO> getAllAgentLabel() {
        return agentLabelService.getAllAgentLabel();
    }

    @Override
    public List<AgentLabelVO> getAgentLabelByAgentLabelIds(List<String> agentLabelIds) {
        return agentLabelService.getAgentLabelByAgentLabelIds(agentLabelIds);
    }

    @Override
    public List<AgentLabelVO> getAllAgentLabelBySiteCode(String siteCode) {
        return agentLabelService.getAllAgentLabel(siteCode);
    }
}
