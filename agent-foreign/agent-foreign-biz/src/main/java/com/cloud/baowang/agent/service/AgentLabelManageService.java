package com.cloud.baowang.agent.service;

import com.cloud.baowang.agent.api.api.AgentLabelManageApi;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRequestVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserResponseVO;
import com.cloud.baowang.agent.api.vo.label.AgentSaveLabelUserResVO;
import com.cloud.baowang.agent.api.vo.label.SaveUserAssociationLabelResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 22:40
 * @description: 会员管理服务
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentLabelManageService {
    private final AgentLabelManageApi agentLabelManageApi;
    private final UserInfoApi userInfoApi;


    public ResponseVO<Map<String, AgentLabelUserResponseVO>>queryUserLabelRecord(AgentLabelRequestVO vo) {
        return agentLabelManageApi.queryUserLabelRecord(vo);
    }

    public ResponseVO<?> listAllLabel(AgentLabelRequestVO vo) {
        return agentLabelManageApi.listAllLabel(vo);
    }

    public ResponseVO<Void> saveUserLabel(AgentSaveLabelUserResVO vo) {
        return agentLabelManageApi.saveUserLabel(vo);
    }


    public ResponseVO<Void> add(AgentLabelManageAddVO agentLabelAddVO) {
        return agentLabelManageApi.add(agentLabelAddVO);
    }

    public ResponseVO<Void> edit(AgentLabelManageEditVO labelManageEditVO) {
        return agentLabelManageApi.edit(labelManageEditVO);
    }

    public ResponseVO<Void> delete(AgentLabelManageDeleteVO vo) {
        return agentLabelManageApi.delete(vo);
    }

    public ResponseVO<?> saveUserAssociationLabel(SaveUserAssociationLabelResVO vo) {
        return agentLabelManageApi.saveUserAssociationLabel(vo);
    }
}
