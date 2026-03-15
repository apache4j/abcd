package com.cloud.baowang.handler;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ReportRecalculateHandler {

    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;

    /**
     * 重算场馆盈亏
     */
    @XxlJob(value = "recalculateReportUserVenueWinLose")
    public void refreshExchangeActRate() {
        log.info("***************** 重算场馆盈亏-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        ReportRecalculateVO recalculateVO = JSON.parseObject(jobParam, ReportRecalculateVO.class);
        reportUserVenueWinLoseApi.recalculate(recalculateVO);
        log.info("***************** 重算场馆盈亏-XxlJob-end *****************");
    }
}
