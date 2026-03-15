package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDepositSiteRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSiteRecordPageVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSubordinatesListPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSubordinatesPageResVO;
import com.cloud.baowang.agent.service.AgentDepositSiteRecordService;
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
public class AgentDepositSiteRecordApiImpl implements AgentDepositSiteRecordApi {

    private final AgentDepositSiteRecordService agentDepositSiteRecordService;


    @Override
    public ResponseVO<AgentDepositSubordinatesPageResVO> listPage(AgentDepositSiteRecordPageVO agentDepositQueryPageVo) {
        return agentDepositSiteRecordService.depositOfSubordinatesRecord(agentDepositQueryPageVo);
    }

    @Override
    public ResponseVO<Long> depositExportCount(AgentDepositSiteRecordPageVO requestVO) {
        return agentDepositSiteRecordService.depositOfSubordinatesRecordExportCount(requestVO);
    }

    @Override
    public Page<AgentDepositSubordinatesListPageResVO> doExport(AgentDepositSiteRecordPageVO agentDepositQueryPageVo) {
        return agentDepositSiteRecordService.doExportQuery(agentDepositQueryPageVo);
    }

    @Override
    public List<AgentDepositOfSubordinatesResVO> depositSubordinatesByTime(Long startTime, Long endTime) {
        return agentDepositSiteRecordService.depositSubordinatesByTime(startTime, endTime);
    }


}
