package com.cloud.baowang.agent.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDepositReviewApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewRecordPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.agent.service.AgentDepositReviewService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentDepositReviewApiImpl implements AgentDepositReviewApi {

    private final AgentDepositReviewService agentDepositReviewService;


    @Override
    public Page<AgentDepositReviewPageResVO> depositReviewPage(AgentDepositReviewPageReqVO vo) {
        return agentDepositReviewService.depositReviewPage(vo);
    }



    @Override
    public ResponseVO<Boolean> lockOrUnLock(AgentWithdrawReviewLockOrUnLockVO vo) {
        return agentDepositReviewService.lockOrUnLock(vo);
    }



    @Override
    public ResponseVO<Boolean> paymentReviewSuccess(AgentDepositReviewReqVO vo) {
        return agentDepositReviewService.paymentReviewSuccess(vo);
    }




    @Override
    public ResponseVO<Boolean> paymentReviewFail(AgentDepositReviewReqVO vo) {
        return agentDepositReviewService.paymentReviewFail(vo);
    }

    @Override
    public Page<AgentDepositReviewRecordPageResVO> depositReviewRecordPage(AgentDepositReviewRecordPageReqVO vo) {
        return agentDepositReviewService.depositReviewRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> agentManualDepositReviewRecordExportCount(AgentDepositReviewRecordPageReqVO vo) {
        return agentDepositReviewService.agentManualDepositReviewRecordExportCount(vo);
    }

    @Override
    public ResponseVO<Long> depositReviewCount(AgentDepositReviewPageReqVO vo) {
        return agentDepositReviewService.depositReviewCount(vo);
    }


}
