package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentLabelManageApi;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageResponseVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRequestVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserResponseVO;
import com.cloud.baowang.agent.api.vo.label.AgentSaveLabelUserResVO;
import com.cloud.baowang.agent.api.vo.label.GetLabelsByAgentAccountVO;
import com.cloud.baowang.agent.api.vo.label.SaveUserAssociationLabelResVO;
import com.cloud.baowang.agent.service.AgentLabelManageService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class AgentLabelManageApiImpl implements AgentLabelManageApi {
    private final AgentLabelManageService agentLabelManageService;

    @Override
    public ResponseVO<Void> add(AgentLabelManageAddVO agentLabelAddVO) {
        return agentLabelManageService.add(agentLabelAddVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> edit(AgentLabelManageEditVO editVO) {
        return agentLabelManageService.edit(editVO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> delete(AgentLabelManageDeleteVO deleteVO) {
        return agentLabelManageService.delete(deleteVO);
    }

    @Override
    public ResponseVO<Map<String, AgentLabelManageResponseVO>> listAllLabel(AgentLabelRequestVO requestVO) {
        return agentLabelManageService.listAllLabel(requestVO);
    }


    /**
     * 代理保存会员标签
     * 添加标签
     */
    @Override
    public ResponseVO<Void> saveUserLabel(AgentSaveLabelUserResVO saveResVO) {
        return agentLabelManageService.saveUserLabel(saveResVO);
    }

    @Override
    public List<GetLabelsByAgentAccountVO> getLabelsByAgentAccount(String siteCode, String agentAccount, String userAccount) {
        return agentLabelManageService.getLabelsByAgentAccount(siteCode,agentAccount, userAccount);
    }

    @Override
    public ResponseVO<Map<String, AgentLabelUserResponseVO>> queryUserLabelRecord(AgentLabelRequestVO vo) {
        return agentLabelManageService.queryUserLabelRecord(vo);
    }

    @Override
    public ResponseVO<?> saveUserAssociationLabel(SaveUserAssociationLabelResVO vo) {
        return agentLabelManageService.saveUserAssociationLabel(vo);

    }
}