package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoEditApi;
import com.cloud.baowang.agent.api.vo.agent.AgentTeamVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentDetailIBasicVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoEditVO;
import com.cloud.baowang.agent.api.vo.remark.AgentRemarkRecordVO;
import com.cloud.baowang.agent.service.AgentInfoModifyReviewService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.AgentTeamService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentInfoEditApiImpl implements AgentInfoEditApi {

    private final AgentInfoModifyReviewService agentInfoModifyReviewService;
    private final AgentInfoService agentInfoService;

    private final AgentTeamService agentTeamService;

    @Override
    public ResponseVO<Void> initiateAgentInfoChange(AgentInfoEditVO vo) {
        agentInfoModifyReviewService.initiateAgentInfoChange(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<AgentDetailIBasicVO> getBasicAgentInfo(AgentDetailParam param) {
        return agentInfoService.getBasicAgentInfo(param);
    }

    @Override
    public ResponseVO<Page<AgentRemarkRecordVO>> getAgentRemark(AgentDetailParam param) {
        return agentInfoService.getAgentRemark(param);
    }

    @Override
    public ResponseVO<AgentTeamVO> getAgentTeam(AgentDetailParam param) {
        return agentTeamService.getAgentTeam(param);
    }
}
