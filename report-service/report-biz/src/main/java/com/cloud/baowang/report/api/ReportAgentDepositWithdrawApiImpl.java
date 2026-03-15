package com.cloud.baowang.report.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportAgentDepositWithdrawApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawResult;
import com.cloud.baowang.report.service.ReportAgentDepositWithdrawService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 代理报表
 * @Author: Ford
 * @Date: 2024/11/4 18:28
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
public class ReportAgentDepositWithdrawApiImpl implements ReportAgentDepositWithdrawApi {

    private ReportAgentDepositWithdrawService reportAgentDepositWithdrawService;


    @Override
    public ResponseVO<ReportAgentDepositWithdrawResult> listPage(ReportAgentDepositWithdrawPageVO reportAgentDepositWithdrawPageVO) {
        return reportAgentDepositWithdrawService.listPage(reportAgentDepositWithdrawPageVO);
    }

    @Override
    public ResponseVO<Boolean> init(ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO) {
        return reportAgentDepositWithdrawService.init(reportAgentDepositWithdrawCondVO);
    }
}
