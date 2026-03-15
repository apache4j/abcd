package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.agent.service.AgentWithdrawReviewRecordService;
import com.cloud.baowang.agent.service.AgentWithdrawReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentWithdrawReviewRecordApiImpl implements AgentWithdrawReviewRecordApi {

    private final AgentWithdrawReviewRecordService userWithdrawReviewRecordService;

    private final AgentWithdrawReviewService agentWithdrawReviewService;

    @Override
    public Page<AgentWithdrawReviewRecordVO> withdrawalReviewRecordPageList(AgentWithdrawReviewRecordPageReqVO vo) {
        return userWithdrawReviewRecordService.withdrawalReviewRecordPageList(vo);
    }


    @Override
    public AgentWithdrawReviewDetailsVO withdrawReviewRecordDetail(AgentWithdrawReviewDetailReqVO vo) {
        return agentWithdrawReviewService.withdrawReviewDetail(vo);
    }

    @Override
    public AgentWithdrawalStatisticsVO getWithdrawTotal(AgentWithdrawalRecordReqVO recordReqVO) {
        return agentWithdrawReviewService.getWithdrawTotal(recordReqVO);
    }

    @Override
    public Long getTotal(AgentWithdrawReviewRecordPageReqVO vo) {
        return userWithdrawReviewRecordService.getTotal(vo);
    }
}
