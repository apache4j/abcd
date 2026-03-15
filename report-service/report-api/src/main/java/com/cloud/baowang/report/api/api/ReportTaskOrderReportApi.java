package com.cloud.baowang.report.api.api;

import com.cloud.baowang.report.api.vo.task.ReportTaskReportPageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementSyncVO;
import com.cloud.baowang.report.api.vo.task.ReportTaskOrderRecordResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author wade
 * @Date 2024-11-04
 */
@FeignClient(contextId = "remoteReportTaskOrderReportApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 代理报表")
public interface ReportTaskOrderReportApi {

    String PREFIX = ApiConstants.PREFIX + "/remoteReportTaskOrderReportApi/api";

    @Operation(summary = "任务报表列表")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<ReportTaskOrderRecordResult> listPage(@RequestBody ReportTaskReportPageVO reportPageVO);

    @Operation(summary = "任务报表列表-总记录数")
    @PostMapping(value = PREFIX + "/getTotalCount")
    Long getTotalCount(@RequestBody ReportTaskReportPageVO vo);


    /*@Operation(summary = "定时任务生成任务报表")
    @PostMapping(value = PREFIX + "/addReportTaskOrderRecord")
    void addReportTaskOrderRecord(@RequestBody ReportUserInfoStatementSyncVO requestParam);*/




}
