package com.cloud.baowang.report.api.api;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataReportReqVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataTempRspVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateStaticRspVO;
import com.cloud.baowang.report.api.vo.user.complex.SiteIntegrateStaticRspVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(contextId = "remoteReportAdminIntegrateDataApi", value = ApiConstants.NAME)
@Tag(name = "总台综合报表服务")
public interface ReportAdminIntegrateDataApi {

    String PREFIX = ApiConstants.PREFIX + "/reportAdminIntegrate/api/";


    @PostMapping(value = PREFIX + "getIntegrateDataReportPage")
    @Operation(summary = "总台综合报表列表")
    ResponseVO<AdminIntegrateStaticRspVO> getIntegrateDataReportPage(@RequestBody AdminIntegrateDataReportReqVO vo);

    @PostMapping(value = PREFIX + "getIntegrateDataExportPage")
    @Operation(summary = "报表导出")
    ResponseVO<Page<AdminIntegrateDataTempRspVO>> getIntegrateDataExportPage(@RequestBody AdminIntegrateDataReportReqVO vo , @RequestParam("isAdmin") Boolean isAdmin);

    @PostMapping(value = PREFIX + "getSiteIntegrateDataReportPage")
    @Operation(summary = "站点综合报表列表")
    ResponseVO<SiteIntegrateStaticRspVO> getSiteIntegrateDataReportPage(@RequestBody AdminIntegrateDataReportReqVO vo);

    @PostMapping(value = PREFIX + "convertFieldToExportFields")
    @Operation(summary = "导出字段转换")
    List<String> convertFieldToExportFields(@RequestBody List<String> sourceList);

}
