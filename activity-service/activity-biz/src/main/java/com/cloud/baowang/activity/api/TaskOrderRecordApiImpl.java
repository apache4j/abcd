package com.cloud.baowang.activity.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.task.TaskOrderRecordApi;
import com.cloud.baowang.activity.api.vo.report.ReportTaskReportPageCopyVO;
import com.cloud.baowang.activity.api.vo.task.*;
import com.cloud.baowang.activity.service.SiteTaskOrderRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
public class TaskOrderRecordApiImpl implements TaskOrderRecordApi {


    private final SiteTaskOrderRecordService siteTaskOrderRecordService;


    @Override
    public ResponseVO<List<ReportTaskOrderRecordResVO>> reportList(Long startTime, Long endTime, String siteCode,String timeZoneDb) {
        return ResponseVO.success(siteTaskOrderRecordService.reportList(startTime, endTime, siteCode,timeZoneDb));
    }

    @Override
    public Page<ReportTaskOrderRecordResVO> reportListPage(ReportTaskReportPageCopyVO reportPageVO) {
        return siteTaskOrderRecordService.reportListPage(reportPageVO);
    }

    @Override
    public ReportTaskOrderRecordResVO reportListPageTotal(ReportTaskReportPageCopyVO reportPageVO) {
        return siteTaskOrderRecordService.reportListPageTotal(reportPageVO);
    }

    @Override
    public long getTotalCountReport(ReportTaskReportPageCopyVO reportPageVO) {
        return siteTaskOrderRecordService.getTotalCountReport(reportPageVO);
    }

    @Override
    public ReportTaskOrderRecordResVO reportListAll(ReportTaskReportPageCopyVO reportPageVO) {
        return siteTaskOrderRecordService.reportListAll(reportPageVO);
    }

    @Override
    public Integer noviceStatus(TaskAppReqVO taskAppReqVO) {
        return siteTaskOrderRecordService.noviceStatus(taskAppReqVO);
    }
}
