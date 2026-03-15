package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.manualup.*;
import com.cloud.baowang.agent.service.AgentManualService;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/12 16:43
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentManualUpApiImpl implements AgentManualUpApi {

    private AgentManualService agentManualService;
    @Override
    public ResponseVO<Boolean> agentSubmit(AgentManualUpSubmitVO vo,  String operator) {
        return agentManualService.agentSubmit(vo,operator);
    }

    @Override
    public ResponseVO<List<GetAgentBalanceVO>> getAgentBalance(GetAgentBalanceQueryVO vo) {
        return agentManualService.getAgentBalance(vo);
    }

    @Override
    public AgentManualUpRecordResult getUpRecordPage(AgentManualUpRecordPageVO vo) {
        return agentManualService.getUpRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getUpRecordPageCount(AgentManualUpRecordPageVO vo) {
        return agentManualService.getUpRecordPageCount(vo);
    }

    @Override
    public ResponseVO<?> lockManualUp(StatusListVO vo, String operator) {
        return agentManualService.lockManualUp(vo,operator);
    }

    @Override
    public ResponseVO<?> oneReviewSuccessManualUp(ReviewListVO vo, String operator) {
        return agentManualService.oneReviewSuccessManualUp(vo,operator);
    }

    @Override
    public ResponseVO<?> oneReviewFailManualUp(ReviewListVO vo, String operator) {
        return agentManualService.oneReviewFailManualUp(vo,operator);
    }

    @Override
    public Page<AgentManualUpReviewResponseVO> getUpReviewPageManualUp(AgentManualUpReviewPageVO vo, String adminName) {
        return agentManualService.getUpReviewPageManualUp(vo,adminName);
    }

    @Override
    public ResponseVO<AgentUpReviewDetailsVO> getUpReviewDetailsManualUp(IdVO vo) {
        return agentManualService.getUpReviewDetailsManualUp(vo);
    }

    @Override
    public Page<AgentGetRecordResponseResultVO> getRecordPage(AgentGetRecordPageVO vo) {
        return agentManualService.getRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(AgentGetRecordPageVO vo) {
        return agentManualService.getTotalCount(vo);
    }

    public AgentReviewOrderNumVO getNotReviewNum(String siteCode){
        return agentManualService.getNotReviewNum(siteCode);
    }

    @Override
    public ResponseVO<List<AgentManualUpDownAccountResultVO>> checkAgentInfo(List<AgentManualUpDownAccountResultVO> agentList) {
        return agentManualService.checkAgentInfo(agentList);
    }
}
