package com.cloud.baowang.handler;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.job.api.constant.SiteJobHandlerConstant;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.MedalActiveJobApi;
import com.cloud.baowang.user.api.api.UserJobHandlerApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Desciption: 勋章解锁调度
 * @Author: Ford
 * @Date: 2024/10/8 09:49
 * @Version: V1.0
 **/
@Component
@Slf4j
@AllArgsConstructor
public class MedalJobHandler {
    private final MedalActiveJobApi medalActiveJobApi;
    private final UserJobHandlerApi userJobHandlerApi;

    /**
     * 勋章每日 满足条件job
     */
    @XxlJob(value = SiteJobHandlerConstant.SITE_MEDAL_DAY_JOB)
    public void siteMedalDayJob() {
        log.info("勋章每日 满足条件job 开始......");
        XxlJobHelper.log("----------- 勋章每日 满足条件job 开始统计-----------");
        String siteCode = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(siteCode)) {
            XxlJobHelper.log("-----------勋章每月 满足条件job 参数异常-----------");
            return;
        }
        userJobHandlerApi.userRegisterDays(siteCode);
        XxlJobHelper.log("----------- 勋章每日 满足条件job 结束统计-----------");
    }

    /**
     * 勋章每周 满足条件job
     */
    @XxlJob(value = SiteJobHandlerConstant.SITE_MEDAL_WEEK_JOB)
    public void siteMedalWeekJob() {
        log.info("勋章每周 满足条件job开始......");
        XxlJobHelper.log("-----------勋章每周 满足条件job 开始统计-----------");
        String siteCode = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(siteCode)) {
            XxlJobHelper.log("-----------勋章每月 满足条件job 参数异常-----------");
            return;
        }
        medalActiveJobApi.siteMedalActiveWeekJob(siteCode);
        XxlJobHelper.log("-----------勋章每周 满足条件job 结束统计-----------");
    }

    /**
     * 勋章每月 满足条件job
     */
    @XxlJob(value = SiteJobHandlerConstant.SITE_MEDAL_MONTH_JOB)
    public void siteMedalMonthJob() {
        log.info("勋章每月 满足条件job......");
        XxlJobHelper.log("-----------勋章每月 满足条件job 开始统计-----------");
        String siteCode = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(siteCode)) {
            XxlJobHelper.log("-----------勋章每月 满足条件job 参数异常-----------");
            return;
        }
        medalActiveJobApi.siteMedalActiveMonthJob(siteCode);
        XxlJobHelper.log("-----------勋章每月 满足条件job 结束统计-----------");
    }

}
