package com.cloud.baowang.agent.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoModifyReviewApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.*;
import com.cloud.baowang.agent.service.AgentInfoModifyReviewService;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentInfoModifyReviewImpl implements AgentInfoModifyReviewApi {

    private final AgentInfoModifyReviewService agentInfoModifyReviewService;


    @Override
    public ResponseVO<Page<AgentInfoChangeRecordPageVO>> recordPageList(AgentInfoChangeRecordQueryVO vo) {
        return ResponseVO.success(agentInfoModifyReviewService.recordPageList(vo));
    }

    @Override
    public ResponseVO<Page<AgentInfoModifyReviewPageVO>> pageList(AgentInfoModifyReviewPageQueryVO vo) {
        return ResponseVO.success(agentInfoModifyReviewService.pageList(vo));
    }

    @Override
    public ResponseVO<Void> lock(AgentInfoModifyReviewLockVO vo) {
        agentInfoModifyReviewService.lock(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> review(AgentInfoModifyReviewVO vo) {
        agentInfoModifyReviewService.review(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<AgentInfoModifyReviewDetailVO> detail(AgentInfoModifyReviewDetailQueryVO vo) {
        return ResponseVO.success(agentInfoModifyReviewService.detail(vo));
    }

    @Override
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(String siteCode) {
        return ResponseVO.success(agentInfoModifyReviewService.getDownBox(siteCode));
    }

    @Override
    public long getAgentInfoReviewRecord(String siteCode) {
        return agentInfoModifyReviewService.getAgentInfoReviewRecord(siteCode);
    }
}
