package com.cloud.baowang.report.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportUserVenueRebateApi;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.service.UserVenueRebateTaskService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
public class ReportUserVenueRebateAPiImpl implements ReportUserVenueRebateApi {
    private final UserVenueRebateTaskService userRebateTaskService;


    @Override
    public ResponseVO<Boolean> onAgentCommissionTaskBegin(ReportRecalculateVO reqVo) {
        return userRebateTaskService.onAgentCommissionTaskBegin(reqVo);
    }
}
