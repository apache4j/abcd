package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentReviewApi;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewResponseVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewVO;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.service.AgentReviewService;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/30 11:29
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentReviewApiImpl implements AgentReviewApi {
    @Resource
    private AgentReviewService agentReviewService;
    @Override
    public ResponseVO lock(StatusVO statusVO, String adminName) {
        return agentReviewService.lock(statusVO,adminName);
    }

    @Override
    public ResponseVO reviewSuccess(ReviewVO vo, String registerIp, String adminId, String adminName) {
        return agentReviewService.reviewSuccess(vo,registerIp,adminId,adminName);
    }

    @Override
    public ResponseVO reviewFail(ReviewVO vo, String adminId, String adminName) {
        return agentReviewService.reviewFail(vo,adminId,adminName);
    }

    @Override
    public ResponseVO<Page<AgentReviewResponseVO>> getReviewPage(AgentReviewPageVO vo, String adminName) {
        return   agentReviewService.getReviewPage(vo,adminName);
    }

    @Override
    public ResponseVO<AgentReviewDetailsVO> getReviewDetails(IdVO vo) {
        return agentReviewService.getReviewDetails(vo);
    }

    @Override
    public ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum(String siteCode) {
        return agentReviewService.getNotReviewNum(siteCode);
    }

    @Override
    public ResponseVO<Map<String, Long>> getNotReviewNumMap(String siteCode) {
        return agentReviewService.getNotReviewNumMap(siteCode);
    }
}
