package com.cloud.baowang.report.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportBetUserRemainingApi;
import com.cloud.baowang.report.api.vo.report.ReportBetUserRemainingResVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.service.ReportBetUserRemainingService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class ReportBetUserRemainingApiImpl implements ReportBetUserRemainingApi {

    private final ReportBetUserRemainingService reportBetUserRemainingService;


    @Override
    public void calculate(ReportRecalculateVO recalculateVO) {
        reportBetUserRemainingService.calculate(recalculateVO);
    }

    @Override
    public ResponseVO<Page<ReportBetUserRemainingResVO>> pageList() {
        return reportBetUserRemainingService.pageList();
    }
}
