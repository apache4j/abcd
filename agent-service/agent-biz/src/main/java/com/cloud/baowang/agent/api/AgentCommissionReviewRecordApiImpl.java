package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionReviewRecordApi;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewDetailVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewRecordVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionReviewCalculateReq;
import com.cloud.baowang.agent.api.vo.commission.CommissionReviewReq;
import com.cloud.baowang.agent.service.commission.AgentCommissionReviewRecordService;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/29 10:25
 * @description: 佣金审核记录
 */
@Slf4j
@RestController
@AllArgsConstructor
public class AgentCommissionReviewRecordApiImpl implements AgentCommissionReviewRecordApi {

    private final AgentCommissionReviewRecordService agentCommissionReviewRecordService;

    @Override
    public ResponseVO<Page<AgentCommissionReviewRecordVO>> getReviewRecordPage(CommissionReviewReq vo) {
        return ResponseVO.success(agentCommissionReviewRecordService.getReviewRecordPage(vo));
    }

    @Override
    public ResponseVO<AgentCommissionReviewDetailVO> getAgentCommissionRecordDetail(IdVO idVO) {
        return ResponseVO.success(agentCommissionReviewRecordService.getAgentCommissionRecordDetail(idVO));
    }

    @Override
    public Long getReviewRecordPageCount(CommissionReviewReq reviewReq) {
        return agentCommissionReviewRecordService.getReviewRecordPageCount(reviewReq);
    }

    @Override
    public ResponseVO<BigDecimal> calculateAgentCommission(CommissionReviewCalculateReq commissionReviewCalculateReq) {
        return agentCommissionReviewRecordService.calculateAgentCommission(commissionReviewCalculateReq);
    }

    @Override
    public CommissionReviewReq buildExportFields(CommissionReviewReq vo) {

        String oneReviewRemark_$_secondReviewRemark = "";
        String firstHandleTime_$_secondHandleTime = "";
        String oneReviewStartTime_$_secondReviewStartTime = "";
        String oneReviewer_$_secondReviewer = "";
        List<String> columnList = vo.getIncludeColumnList();
        if (columnList.contains(oneReviewRemark_$_secondReviewRemark)){
            columnList.remove(oneReviewRemark_$_secondReviewRemark);
            columnList.add("oneReviewRemark");
            columnList.add("secondReviewRemark");
        }
        if (columnList.contains(firstHandleTime_$_secondHandleTime)){
            columnList.remove(firstHandleTime_$_secondHandleTime);
            columnList.add("firstHandleTime");
            columnList.add("secondHandleTime");
        }
        if (columnList.contains(oneReviewStartTime_$_secondReviewStartTime)){
            columnList.remove(oneReviewStartTime_$_secondReviewStartTime);
            columnList.add("oneReviewStartTime");
            columnList.add("secondReviewStartTime");
        }
        if (columnList.contains(oneReviewer_$_secondReviewer)){
            columnList.remove(oneReviewer_$_secondReviewer);
            columnList.add("oneReviewer");
            columnList.add("secondReviewer");
        }
        return vo;
    }
}
