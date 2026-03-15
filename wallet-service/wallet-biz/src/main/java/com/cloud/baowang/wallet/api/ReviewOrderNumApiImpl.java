package com.cloud.baowang.wallet.api;

import com.cloud.baowang.agent.api.api.AgentCommissionReviewApi;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewApi;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.ReviewOrderNumApi;
import com.cloud.baowang.wallet.api.vo.ReviewOrderNumVO;
import com.cloud.baowang.wallet.service.UserManualUpReviewService;
import com.cloud.baowang.wallet.service.UserPlatformCoinManualUpReviewService;
import com.cloud.baowang.wallet.service.UserWithdrawReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class ReviewOrderNumApiImpl implements ReviewOrderNumApi {

    private UserManualUpReviewService userManualUpReviewService;

    private final UserWithdrawReviewService userWithdrawReviewService;

    private final AgentWithdrawReviewApi agentWithdrawReviewApi;

    private final AgentManualUpApi agentManualUpApi;

    private final AgentCommissionReviewApi agentCommissionReviewApi;

    private final UserPlatformCoinManualUpReviewService userPlatformCoinManualUpReviewService;

    @Override
    public ResponseVO<List<ReviewOrderNumVO>> getFundsReviewNums(String siteCode) {
        List<ReviewOrderNumVO> result = new ArrayList<>();
        // 会员人工加额审核-页面
        ReviewOrderNumVO manualUpOrderNumVo = userManualUpReviewService.getNotReviewNum(siteCode);
        //会员提款审核页面
        ReviewOrderNumVO withdrawReviewNumVO = userWithdrawReviewService.getWithdrawReviewNum(siteCode);
        //代理提款审核页面
        AgentReviewOrderNumVO agentWithdrawReviewNumVO = agentWithdrawReviewApi.getAgentWithdrawReviewNum(siteCode);
        //代理人工加额
        AgentReviewOrderNumVO agentManualUpReviewNumVO = agentManualUpApi.getNotReviewNum(siteCode);
        //代理佣金审核
        AgentReviewOrderNumVO commissionReviewNumVO = agentCommissionReviewApi.getNotReviewNum(siteCode);
        // 会员平台币上分审核
        ReviewOrderNumVO platformCoinUpReviewNumVO = userPlatformCoinManualUpReviewService.getNotReviewNum(siteCode);
        result.add(manualUpOrderNumVo);
        result.add(withdrawReviewNumVO);
        ReviewOrderNumVO agentReviewOrderNum=new ReviewOrderNumVO();
        BeanUtils.copyProperties(agentWithdrawReviewNumVO,agentReviewOrderNum);
        result.add(agentReviewOrderNum);
        ReviewOrderNumVO agentManualOrderNum=new ReviewOrderNumVO();
        BeanUtils.copyProperties(agentManualUpReviewNumVO,agentManualOrderNum);
        result.add(agentManualOrderNum);
        ReviewOrderNumVO agentCommissionNum=new ReviewOrderNumVO();
        BeanUtils.copyProperties(commissionReviewNumVO,agentCommissionNum);
        result.add(agentCommissionNum);
        result.add(platformCoinUpReviewNumVO);
        return ResponseVO.success(result);
    }
}
