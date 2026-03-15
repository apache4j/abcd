package com.cloud.baowang.handler;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ReportCalculateHandler {

    //private final ReportBetUserRemainingApi reportBetUserRemainingApi;

    /**
     * 重算场馆盈亏
     */
    @XxlJob(value = "betUserRemainingHandler")
    public void betUserRemainingHandler() {
        log.info("***************** 留存报表-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        ReportRecalculateVO recalculateVO = JSON.parseObject(jobParam, ReportRecalculateVO.class);
        //reportBetUserRemainingApi.calculate(recalculateVO);
        log.info("***************** 留存报表-XxlJob-end *****************");
    }
}
