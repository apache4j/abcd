package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentWithdrawRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordResVO;
import com.cloud.baowang.agent.service.AgentWithdrawRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentWithdrawRecordApiImpl implements AgentWithdrawRecordApi {

    private final AgentWithdrawRecordService agentWithdrawRecordService;

    @Override
    public ResponseVO<AgentWithdrawalRecordPageResVO> getAgentWithdrawalRecordPageList(AgentWithdrawalRecordReqVO requestVO) {
        return ResponseVO.success(agentWithdrawRecordService.getAgentWithdrawalRecordPageList(requestVO));
    }

    @Override
    public ResponseVO<Long> agentWithdrawRecordRecordPageCount(AgentWithdrawalRecordReqVO vo) {
        return ResponseVO.success(agentWithdrawRecordService.agentWithdrawRecordRecordPageCount(vo));
    }

    @Override
    public AgentWithdrawalRecordResVO getRecordByOrderId(String orderId) {
        return agentWithdrawRecordService.getRecordByOrderId(orderId);
    }
}
