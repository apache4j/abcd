package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentManualDownApi;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownAddVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRecordRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRecordResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpDownAccountResultVO;
import com.cloud.baowang.agent.service.AgentManualDownRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentManualDownApiImpl implements AgentManualDownApi {

    private final AgentManualDownRecordService agentManualDownRecordService;

    @Override
    public ResponseVO<Boolean> saveManualDown(AgentManualDownAddVO vo, String operator) {
        return agentManualDownRecordService.saveManualDown(vo,operator);
    }

    @Override
    public ResponseVO<AgentManualDownRecordResponseVO> listAgentManualDownRecordPage(AgentManualDownRecordRequestVO vo) {
        return ResponseVO.success(agentManualDownRecordService.listAgentManualDownRecordPage(vo));
    }

    @Override
    public ResponseVO<Long> listAgentManualDownRecordPageExportCount(AgentManualDownRecordRequestVO vo) {
        return ResponseVO.success(agentManualDownRecordService.listAgentManualDownRecordPageExportCount(vo));
    }

    @Override
    public long getTotalPendingReviewBySiteCode(String siteCode) {
        return agentManualDownRecordService.getTotalPendingReviewBySiteCode(siteCode);
    }

    @Override
    public ResponseVO<List<AgentManualUpDownAccountResultVO>> checkAgentInfo(List<AgentManualUpDownAccountResultVO> list) {
        return agentManualDownRecordService.checkAgentInfo(list);
    }
}
