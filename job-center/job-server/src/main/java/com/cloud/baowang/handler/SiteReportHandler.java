package com.cloud.baowang.handler;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.SiteReportApi;
import com.cloud.baowang.report.api.vo.SiteReportSyncDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class SiteReportHandler {
    private final SiteReportApi siteReportApi;

    @XxlJob(value = "syncSiteReportData")
    public void syncSiteReportData() {
        String jobParam = XxlJobHelper.getJobParam();
        SiteReportSyncDataVO dataVO = new SiteReportSyncDataVO();
        dataVO.setRerun(false);
        if (StringUtils.isNotBlank(jobParam)) {
            dataVO = JSON.parseObject(jobParam, SiteReportSyncDataVO.class);
        }else {
            dataVO = new SiteReportSyncDataVO();
            ZoneId systemZone = ZoneId.of("UTC-5");
            LocalDateTime now = LocalDateTime.now(systemZone);
            LocalDateTime startOfPreviousHour = now.minusHours(1).truncatedTo(ChronoUnit.HOURS);
            LocalDateTime endOfPreviousHour = startOfPreviousHour.plusMinutes(59).plusSeconds(59).plusNanos(999000000);
            long startTimestamp = startOfPreviousHour.atZone(systemZone).toInstant().toEpochMilli();
            long endTimestamp = endOfPreviousHour.atZone(systemZone).toInstant().toEpochMilli();
            dataVO.setStartTime(startTimestamp);
            dataVO.setEndTime(endTimestamp);
            dataVO.setRerun(false);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("开始同步平台报表数据,当前时间:{},当前请求参数:{}", format.format(new Date()), JSON.toJSONString(dataVO));
        siteReportApi.syncData(dataVO);
    }
}
