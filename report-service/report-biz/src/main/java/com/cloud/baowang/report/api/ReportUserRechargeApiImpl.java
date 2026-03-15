package com.cloud.baowang.report.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserWinLossParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.vo.ReportUserRechargeRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeUserRequestVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.*;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import com.cloud.baowang.report.service.ReportUserRechargeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class ReportUserRechargeApiImpl implements ReportUserRechargeApi {

    private final ReportUserRechargeService reportUserRechargeService;


    @Override
    public ResponseVO<Page<ReportUserRechargeResponseVO>> queryRechargeAmount(ReportUserRechargeRequestVO vo) {
        return reportUserRechargeService.queryRechargeAmount(vo);
    }

    public ResponseVO<ReportUserRechargeResponseVO> queryRechargeAmountByUserId(ReportUserRechargeUserRequestVO vo){
        return reportUserRechargeService.queryRechargeAmountByUserId(vo);
    }

    @Override
    public ResponseVO<List<ReportRechargeAgentVO>> queryByTimeAndAgent(ReportUserRechargeAgentReqVO vo) {
        return ResponseVO.success(reportUserRechargeService.queryByTimeAndAgent(vo));
    }

    @Override
    public ResponseVO<List<ReportUserRechargePayMethodAgentVO>> queryPayMethodByTimeAndAgent(ReportUserRechargePayMethodAgentReqVO vo) {
        return ResponseVO.success(reportUserRechargeService.queryPayMethodByTimeAndAgent(vo));
    }

    @Override
    public void reportRealTimeUserRechargeWithdraw(ReportRealTimeUserDepositWithdrawReqParam param) {

        reportUserRechargeService.reportRealTimeUserRechargeWithdraw(param);



    }

    @Override
    public List<ReportUserAmountVO> getUserDepAmountByAgentIds(ReportAgentWinLossParamVO vo) {
        return reportUserRechargeService.getUserDepAmountByAgentIds(vo);
    }

    @Override
    public List<ReportUserAmountVO> getUserDepAmountByUserId(ReportUserWinLossParamVO vo) {
        return reportUserRechargeService.getUserDepAmountByUserId(vo);
    }

    @Override
    public List<ReportUserAmountVO> getUserDepAmountByUserIds(ReportUserWinLossParamVO vo) {
        return reportUserRechargeService.getUserDepAmountByUserIds(vo);
    }

    @Override
    public List<ReportUserAmountVO> getUserFeeAmountByType(ReportAgentWinLossParamVO vo) {
        return reportUserRechargeService.getUserFeeAmountByType(vo);
    }


}
