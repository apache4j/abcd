package com.cloud.baowang.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.ReportUserInfoStatementApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementSyncVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.user.api.api.UserJobHandlerApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author : kimi
 * @Date : 2024-06-19
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserJobHandler {

    private final UserJobHandlerApi userJobHandlerApi;

    private final ReportUserInfoStatementApi reportUserInfoStatementApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;

    /**
     * 会员离线天数
     */
    @XxlJob(value = "userOfflineDays")
    public void userOfflineDays() {
        log.info("***************** 会员离线天数-定时任务-start *****************");
        userJobHandlerApi.userOfflineDays();
        log.info("***************** 会员离线天数-定时任务-end *****************");
    }

    /**
     * 会员报表
     */
    @XxlJob(value = "addUserInfoStatement")
    public void addUserInfoStatement() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("会员报表报表jobParam:{}", JSONObject.toJSONString(jobParam));
        XxlJobHelper.log("----------- 会员报表 满足条件job 结束统计-----------{}", JSONObject.toJSONString(jobParam));
        ReportUserInfoStatementSyncVO reportUserInfoStatementSyncVO = new ReportUserInfoStatementSyncVO();
        if (StringUtils.isBlank(jobParam)) {
            reportUserInfoStatementSyncVO.setStartTime(System.currentTimeMillis());
        } else {
            reportUserInfoStatementSyncVO = JSON.parseObject(jobParam, ReportUserInfoStatementSyncVO.class);
        }
        reportUserInfoStatementApi.saveReportUserInfoStatement(reportUserInfoStatementSyncVO);
        XxlJobHelper.log("----------- 会员报表 满足条件job 结束统计-----------");

    }

    /**
     * 会员盈亏重算报表
     */
    @XxlJob(value = "addUserWinloss")
    public void addUserWinloss() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("会员盈亏重算报表jobParam:{}", jobParam);
        XxlJobHelper.log("----------- 会员报表 满足条件job 结束统计-----------{}", jobParam);
        ReportRecalculateVO reportRecalculateVO = new ReportRecalculateVO();
        if (StringUtils.isBlank(jobParam)) {
           return;
        } else {
            reportRecalculateVO = JSON.parseObject(jobParam, ReportRecalculateVO.class);
        }
        reportUserWinLoseApi.addReportWinLoseRecord(reportRecalculateVO);
        XxlJobHelper.log("----------- 会员报表 满足条件job 结束统计-----------");

    }
}
