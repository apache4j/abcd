package com.cloud.baowang.report.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportAgentStaticsApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsResult;
import com.cloud.baowang.report.service.ReportAgentStaticDayService;
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
public class ReportAgentStaticsApiImpl implements ReportAgentStaticsApi {

    private ReportAgentStaticDayService reportAgentStaticDayService;

    @Override
    public ResponseVO<ReportAgentStaticsResult> listPage(ReportAgentStaticsPageVO reportAgentStaticsPageVO) {
        return reportAgentStaticDayService.listPage(reportAgentStaticsPageVO);
    }

    @Override
    public ResponseVO<Boolean> init(ReportAgentStaticsCondVO reportAgentStaticsCondVO) {
        return reportAgentStaticDayService.init(reportAgentStaticsCondVO);
    }
}
