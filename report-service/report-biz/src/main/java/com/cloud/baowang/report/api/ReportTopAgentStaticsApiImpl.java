package com.cloud.baowang.report.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportTopAgentStaticsApi;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsResult;
import com.cloud.baowang.report.service.ReportTopAgentStaticDayService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 商务总代报表
 * @Author: Ford
 * @Date: 2024/11/4 18:28
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
public class ReportTopAgentStaticsApiImpl implements ReportTopAgentStaticsApi {

    private ReportTopAgentStaticDayService reportTopAgentStaticDayService;

    @Override
    public ResponseVO<ReportTopAgentStaticsResult> listPage(ReportTopAgentStaticsPageVO reportTopAgentStaticsPageVO) {
        return reportTopAgentStaticDayService.listPage(reportTopAgentStaticsPageVO);
    }

    @Override
    public ResponseVO<Boolean> init(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO) {
        return reportTopAgentStaticDayService.init(reportTopAgentStaticsCondVO);
    }
}
