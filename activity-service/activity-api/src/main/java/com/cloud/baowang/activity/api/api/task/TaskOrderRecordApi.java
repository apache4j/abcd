package com.cloud.baowang.activity.api.api.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.report.ReportTaskReportPageCopyVO;
import com.cloud.baowang.activity.api.vo.task.ReportTaskOrderRecordResVO;
import com.cloud.baowang.activity.api.vo.task.TaskAppReqVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @className: TaskOrderRecordApi
 * @author: wade
 * @description: TaskOrderRecordApi 页面
 * @date: 5/8/24 09:41
 */
@FeignClient(contextId = "remoteTaskOrderRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC remoteTaskOrderRecordApi 服务")
public interface TaskOrderRecordApi {
    String PREFIX = ApiConstants.PREFIX + "/remoteTaskOrderRecordApi/api";


    @Operation(summary = "统计记录")
    @PostMapping(PREFIX + "/reportList")
    ResponseVO<List<ReportTaskOrderRecordResVO>> reportList(@RequestParam("startTime") Long startTime,
                                                            @RequestParam("endTime") Long endTime,
                                                            @RequestParam("siteCode") String siteCode,
                                                            @RequestParam("timeZoneDb") String timeZoneDb);


    @Operation(summary = "统计记录-每页明细")
    @PostMapping(PREFIX + "/reportListPage")
    Page<ReportTaskOrderRecordResVO> reportListPage(@RequestBody ReportTaskReportPageCopyVO reportPageVO);

    @Operation(summary = "统计记录-每页汇总人数")
    @PostMapping(PREFIX + "/reportListPageTotal")
    ReportTaskOrderRecordResVO reportListPageTotal(@RequestBody ReportTaskReportPageCopyVO reportPageVO);

    @Operation(summary = "统计记录数")
    @PostMapping(PREFIX + "/getTotalCountReport")
    long getTotalCountReport(@RequestBody ReportTaskReportPageCopyVO reportPageVO);


    @Operation(summary = "统计记录")
    @PostMapping(PREFIX + "/reportListAll")
    ReportTaskOrderRecordResVO reportListAll(@RequestBody ReportTaskReportPageCopyVO reportPageVO);

    @Operation(summary = "新人任务领取状态")
    @PostMapping(PREFIX + "/noviceStatus")
    Integer noviceStatus(@RequestBody TaskAppReqVO taskAppReqVO);

}
