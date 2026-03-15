package com.cloud.baowang.agent.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewApi;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.AgentWithdrawChannelResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewAddressReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewAddressResponseVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewReqVO;
import com.cloud.baowang.agent.service.AgentWithdrawReviewService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentWithdrawReviewApiImpl implements AgentWithdrawReviewApi {

    private final AgentWithdrawReviewService agentWithdrawReviewService;


    @Override
    public Page<AgentWithdrawReviewPageResVO> withdrawReviewPage(AgentWithdrawReviewPageReqVO vo) {
        return agentWithdrawReviewService.withdrawReviewPage(vo);
    }

    @Override
    public AgentWithdrawReviewDetailsVO withdrawReviewDetail(AgentWithdrawReviewDetailReqVO vo) {
        return agentWithdrawReviewService.withdrawReviewDetail(vo);
    }

    @Override
    public ResponseVO<Boolean> oneLockOrUnLock(AgentWithdrawReviewLockOrUnLockVO vo) {
        return agentWithdrawReviewService.oneLockOrUnLock(vo);
    }


    @Override
    public ResponseVO<Boolean> paymentLockOrUnLock(AgentWithdrawReviewLockOrUnLockVO vo) {
        return agentWithdrawReviewService.paymentLockOrUnLock(vo);
    }

    @Override
    public ResponseVO<Boolean> oneReviewSuccess(AgentWithdrawReviewReqVO vo) {
        return agentWithdrawReviewService.oneReviewSuccess(vo);
    }


    @Override
    public ResponseVO<Boolean> paymentReviewSuccess(AgentWithdrawReviewReqVO vo) {
        return agentWithdrawReviewService.paymentReviewSuccess(vo);
    }

    @Override
    public ResponseVO<Boolean> oneReviewFail(AgentWithdrawReviewReqVO vo) {
        return agentWithdrawReviewService.oneReviewFail(vo);
    }


    @Override
    public ResponseVO<Boolean> paymentReviewFail(AgentWithdrawReviewReqVO vo) {
        return agentWithdrawReviewService.paymentReviewFail(vo);
    }

    @Override
    public AgentReviewOrderNumVO getAgentWithdrawReviewNum(String siteCode) {
        return agentWithdrawReviewService.getAgentWithdrawReviewNum(siteCode);
    }

    @Override
    public long getTotalPendingReviewBySiteCode(String siteCode) {
        return agentWithdrawReviewService.getTotalPendingReviewBySiteCode(siteCode);
    }

    @Override
    public ResponseVO<List<AgentWithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(String siteCode, String channelType, String id) {
        return agentWithdrawReviewService.getChannelByChannelTypeAndReviewId(siteCode, channelType,id);
    }

    @Override
    public ResponseVO<Page<AgentWithdrawReviewAddressResponseVO>> getAddressInfoList(AgentWithdrawReviewAddressReqVO vo) {
        return agentWithdrawReviewService.getAddressInfoList(vo);
    }
}
