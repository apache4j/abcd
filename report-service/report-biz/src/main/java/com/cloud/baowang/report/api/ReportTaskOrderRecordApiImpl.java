package com.cloud.baowang.report.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportTaskOrderReportApi;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementSyncVO;
import com.cloud.baowang.report.api.vo.task.ReportTaskOrderRecordResult;
import com.cloud.baowang.report.api.vo.task.ReportTaskReportPageVO;
import com.cloud.baowang.report.service.ReportTaskOrderRecordService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 任务报表
 * @Author: wade
 * @Date: 2024/11/4 18:28
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
public class ReportTaskOrderRecordApiImpl implements ReportTaskOrderReportApi {

    private final ReportTaskOrderRecordService orderRecordService;

    @Override
    public ResponseVO<ReportTaskOrderRecordResult> listPage(ReportTaskReportPageVO reportPageVO) {
        return orderRecordService.listPageNew(reportPageVO);
    }

    @Override
    public Long getTotalCount(ReportTaskReportPageVO vo) {
        return orderRecordService.getTotalCount(vo);
    }

    /*@Override
    public void addReportTaskOrderRecord(ReportUserInfoStatementSyncVO requestParam) {
        orderRecordService.addReportTaskOrderRecord(requestParam);
    }*/
}
