package com.cloud.baowang.handler;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.ReportTaskOrderReportApi;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementSyncVO;
import com.cloud.baowang.user.api.api.UserJobHandlerApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author : wade
 * @Date : 2024-06-19
 */
@Slf4j
@Component
@AllArgsConstructor
public class TaskJobHandler {

    private final UserJobHandlerApi userJobHandlerApi;

    private final ReportTaskOrderReportApi reportTaskOrderReportApi;




    /**
     * 任务领取记录报表，按照小时统计，支持重结算
     */
    /*@XxlJob(value = "addReportTaskOrderRecord")
    public void addReportTaskOrderRecord() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("任务领取记录报表jobParam:{}", jobParam);
        // 检查 jobParam 是否为空
        ReportUserInfoStatementSyncVO reportUserInfoStatementSyncVO = new ReportUserInfoStatementSyncVO();
        if (StringUtils.isBlank(jobParam)) {
            reportUserInfoStatementSyncVO.setStartTime(System.currentTimeMillis());
        } else {
            reportUserInfoStatementSyncVO = JSON.parseObject(jobParam, ReportUserInfoStatementSyncVO.class);
        }
        log.info("*****************任务领取记录报表-定时任务-start *****************");
        //reportTaskOrderReportApi.addReportTaskOrderRecord(reportUserInfoStatementSyncVO);
        log.info("***************** 任务领取记录报表-定时任务-end *****************");
    }*/
}
