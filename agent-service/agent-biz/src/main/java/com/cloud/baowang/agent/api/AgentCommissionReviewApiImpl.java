package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionReviewApi;
import com.cloud.baowang.agent.api.vo.AdjustCommissionVO;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agent.commission.AgentValidAmountVo;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewDetailVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionReviewReq;
import com.cloud.baowang.agent.service.commission.AgentCommissionDailyCalcService;
import com.cloud.baowang.agent.service.commission.AgentCommissionReviewService;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/10/27 7:32
 * @description:
 */
@Slf4j
@RestController
@AllArgsConstructor
public class AgentCommissionReviewApiImpl implements AgentCommissionReviewApi {

    private final AgentCommissionReviewService agentCommissionReviewService;

    private final AgentCommissionDailyCalcService agentCommissionDailyCalcService;

    @Override
    public ResponseVO<Page<AgentCommissionReviewVO>> getReviewPage(CommissionReviewReq vo) {
        return ResponseVO.success(agentCommissionReviewService.getReviewPage(vo));
    }

    @Override
    public ResponseVO<?> lockCommission(StatusListVO vo) {
        for (String id : vo.getId()) {
            StatusVO statusVO = new StatusVO();
            statusVO.setStatus(vo.getStatus());
            statusVO.setId(id);
            statusVO.setOperatorName(vo.getOperatorName());
            ResponseVO responseVO = agentCommissionReviewService.lockManualUp(statusVO);
            if (responseVO.isOk()) {
                continue;
            } else {
                return responseVO;
            }
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<AgentCommissionReviewDetailVO> getAgentCommissionDetail(IdVO idVO) {
        return ResponseVO.success(agentCommissionReviewService.getAgentCommissionDetail(idVO));
    }

    @Override
    public ResponseVO<?> oneCommissionReviewSuccess(ReviewListVO vo) {
//        for (String id : vo.getId()) {
//            ReviewVO reviewVO = new ReviewVO();
//            reviewVO.setId(id);
//            reviewVO.setReviewRemark(vo.getReviewRemark());
//            reviewVO.setSiteCode(vo.getSiteCode());
//            ResponseVO responseVO = agentCommissionReviewService.oneReviewSuccessManualUp(reviewVO);
//            if (responseVO.isOk()) {
//                continue;
//            } else {
//                return responseVO;
//            }
//        }
//
//        return ResponseVO.success();
        log.info("AgentCommissionReviewService.oneCommissionReviewSuccess : "+vo);

        return agentCommissionReviewService.oneReviewSuccessManualUp(vo);
    }

    @Override
    public ResponseVO<?> oneCommissionReviewFail(ReviewListVO vo) {
//        for (String id : vo.getId()) {
//            ReviewVO reviewVO = new ReviewVO();
//            reviewVO.setId(id);
//            reviewVO.setReviewRemark(vo.getReviewRemark());
//            reviewVO.setSiteCode(vo.getSiteCode());
//            agentCommissionReviewService.oneReviewFailManualUp(reviewVO);
//        }
//        return ResponseVO.success();
        log.info("AgentCommissionReviewService.oneCommissionReviewFail : "+vo);

        return agentCommissionReviewService.oneReviewFailManualUp(vo);
    }

    @Override
    public Integer getUnreviewedRecordCount(String siteCode ) {
        return agentCommissionReviewService.getUnreviewedRecordCount(siteCode);
    }

    @Override
    public AgentReviewOrderNumVO getNotReviewNum(String siteCode) {
        return agentCommissionReviewService.getNotReviewNum(siteCode);
    }

    @Override
    public ResponseVO<?> secondLockOrUnLock(StatusListVO vo) {
        return agentCommissionReviewService.secondLockOrUnLock(vo);
    }

    @Override
    public ResponseVO<?> secondReviewSuccess(ReviewListVO vo) {
        return agentCommissionReviewService.secondReviewSuccess(vo);
    }

    @Override
    public ResponseVO<?> secondReviewRejected(ReviewListVO vo) {
        return agentCommissionReviewService.secondReviewRejected(vo);
    }

    @Override
    public ResponseVO<?> secondReviewReturned(ReviewListVO vo) {
        return agentCommissionReviewService.secondReviewReturned(vo);
    }

    @Override
    public ResponseVO<Boolean> adjustCommission(AdjustCommissionVO vo) {
        return agentCommissionReviewService.adjustCommission(vo);
    }

    @Override
    public ResponseVO<Void> calcAgentCommission(AgentValidAmountVo vo) {
        agentCommissionDailyCalcService.handleCalcAgentCommission(vo);
        return ResponseVO.success();
    }
}
