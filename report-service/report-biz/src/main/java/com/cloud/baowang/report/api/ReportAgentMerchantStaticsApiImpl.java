package com.cloud.baowang.report.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportAgentMerchantStaticsApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsResult;
import com.cloud.baowang.report.service.ReportAgentMerchantStaticDayService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 商务报表
 * @Author: Ford
 * @Date: 2024/11/4 18:28
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
public class ReportAgentMerchantStaticsApiImpl implements ReportAgentMerchantStaticsApi {

    private ReportAgentMerchantStaticDayService reportAgentMerchantStaticDayService;

    @Override
    public ResponseVO<ReportAgentMerchantStaticsResult> listPage(ReportAgentMerchantStaticsPageVO reportAgentMerchantStaticsPageVO) {
        return reportAgentMerchantStaticDayService.listPage(reportAgentMerchantStaticsPageVO);
    }

    @Override
    public ResponseVO<Boolean> init(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO) {
        return reportAgentMerchantStaticDayService.init(reportAgentMerchantStaticsCondVO);
    }
}
