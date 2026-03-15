package com.cloud.baowang.report.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportAdminIntegrateDataApi;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataReportReqVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataTempRspVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateStaticRspVO;
import com.cloud.baowang.report.api.vo.user.complex.SiteIntegrateStaticRspVO;
import com.cloud.baowang.report.service.ReportAdminIntegrateDataService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class ReportAdminIntegrateDataApiImpl implements ReportAdminIntegrateDataApi {
    private final ReportAdminIntegrateDataService reportAdminIntegrateDataService;
    @Override
    public ResponseVO<AdminIntegrateStaticRspVO> getIntegrateDataReportPage(AdminIntegrateDataReportReqVO vo) {
        return reportAdminIntegrateDataService.getIntegrateDataReportPage(vo);
    }

    @Override
    public ResponseVO<Page<AdminIntegrateDataTempRspVO>> getIntegrateDataExportPage(AdminIntegrateDataReportReqVO vo, Boolean isAdmin) {
        return reportAdminIntegrateDataService.getIntegrateDataExportPage(vo,isAdmin);
    }

    @Override
    public ResponseVO<SiteIntegrateStaticRspVO> getSiteIntegrateDataReportPage(AdminIntegrateDataReportReqVO vo) {
        return reportAdminIntegrateDataService.getSiteIntegrateDataReportPage(vo);
    }

    @Override
    public List<String> convertFieldToExportFields(List<String> sourceList) {
        return reportAdminIntegrateDataService.convertFieldToExportFields(sourceList);
    }
}
