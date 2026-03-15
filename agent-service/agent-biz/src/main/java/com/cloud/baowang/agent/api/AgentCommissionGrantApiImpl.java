package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentCommissionGrantApi;
import com.cloud.baowang.agent.api.vo.commission.AgentGranRecordPageAllVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionGranRecordReqVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionGrantRecordDetailVO;
import com.cloud.baowang.agent.api.vo.commission.IdPageVO;
import com.cloud.baowang.agent.service.commission.AgentCommissionGrantRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 0:55
 * @description:
 */
@Slf4j
@RestController
@AllArgsConstructor
public class AgentCommissionGrantApiImpl implements AgentCommissionGrantApi {
    private final AgentCommissionGrantRecordService agentCommissionGrantRecordService;

    @Override
    public ResponseVO<AgentGranRecordPageAllVO> getGrantRecordPageList(CommissionGranRecordReqVO requestVO) {
        return agentCommissionGrantRecordService.getGrantRecordPageList(requestVO);
    }

    @Override
    public ResponseVO<CommissionGrantRecordDetailVO> getCommissionDetail(IdPageVO idVO) {
        return ResponseVO.success(agentCommissionGrantRecordService.getCommissionDetail(idVO));
    }

    @Override
    public Long getGrantRecordPageCount(CommissionGranRecordReqVO requestVO) {
        return agentCommissionGrantRecordService.getGrantRecordPageCount(requestVO);
    }

    @Override
    public BigDecimal agentCommissionSum(CommissionGranRecordReqVO requestVO) {
        return agentCommissionGrantRecordService.agentCommissionSum(requestVO);
    }
}
