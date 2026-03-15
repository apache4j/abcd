package com.cloud.baowang.report.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.SiteReportApi;
import com.cloud.baowang.report.api.vo.SiteReportSyncDataVO;
import com.cloud.baowang.report.api.vo.SiteStatisticsRecordVO;
import com.cloud.baowang.report.api.vo.site.SiteReportStatisticsQueryPageQueryVO;
import com.cloud.baowang.report.api.vo.site.SiteStatisticsVO;
import com.cloud.baowang.report.service.SiteReportService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class SiteReportApiImpl implements SiteReportApi {
    private final SiteReportService reportService;

    @Override
    public ResponseVO<SiteStatisticsRecordVO> getSiteReport(SiteReportStatisticsQueryPageQueryVO queryVO) {
        return reportService.getSiteReport(queryVO);
    }

    @Override
    public ResponseVO<Page<SiteStatisticsVO>> getPage(SiteReportStatisticsQueryPageQueryVO queryVO) {
        return reportService.getPage(queryVO);
    }

    @Override
    public ResponseVO<Boolean> syncData(SiteReportSyncDataVO dataVO) {
        return reportService.syncData(dataVO);
    }

    @Override
    public Long getTotal(SiteReportStatisticsQueryPageQueryVO vo) {
        return reportService.getTotal(vo);
    }
}
