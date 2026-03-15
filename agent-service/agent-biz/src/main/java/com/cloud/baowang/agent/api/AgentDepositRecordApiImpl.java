package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentDepositRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositAllRes;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositRecordReq;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalStatisticsVO;
import com.cloud.baowang.agent.service.AgentDepositRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentDepositRecordApiImpl implements AgentDepositRecordApi {

    private final AgentDepositRecordService agentDepositRecordService;

    @Override
    public ResponseVO<AgentDepositAllRes> depositListPage(AgentDepositRecordReq requestVO) {
        return agentDepositRecordService.getDepositRecordPageList(requestVO);
    }

    @Override
    public ResponseVO<Long> depositExportCount(AgentDepositRecordReq requestVO) {
        return agentDepositRecordService.getDepositRecordExportCount(requestVO);
    }

    @Override
    public AgentWithdrawalStatisticsVO getDepositTotal(AgentDepositRecordReq recordReq) {
        return agentDepositRecordService.getDepositTotal(recordReq);
    }

}
