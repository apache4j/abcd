package com.cloud.baowang.report.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.SiteReportSyncDataVO;
import com.cloud.baowang.report.api.vo.SiteStatisticsRecordVO;
import com.cloud.baowang.report.api.vo.site.SiteReportStatisticsQueryPageQueryVO;
import com.cloud.baowang.report.api.vo.site.SiteStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteReportApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 站点（平台）报表")
public interface SiteReportApi {
    String prefix = "/siteReportApi/api";

    @PostMapping(prefix + "/getSiteReport")
    @Operation(summary = "获取平台报表")
    ResponseVO<SiteStatisticsRecordVO> getSiteReport(@RequestBody SiteReportStatisticsQueryPageQueryVO queryVO);

    @PostMapping(prefix + "/getPage")
    @Operation(summary = "获取平台分页报表(导出用)")
    ResponseVO<Page<SiteStatisticsVO>> getPage(@RequestBody SiteReportStatisticsQueryPageQueryVO queryVO);

    @PostMapping(prefix + "/syncData")
    @Operation(summary = "同步数据")
    ResponseVO<Boolean> syncData(@RequestBody SiteReportSyncDataVO dataVO);

    @PostMapping(prefix + "/getTotal")
    @Operation(summary = "获取总数")
    Long getTotal(@RequestBody SiteReportStatisticsQueryPageQueryVO vo);
}
