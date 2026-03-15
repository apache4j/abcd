package com.cloud.baowang.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.report.api.vo.ReportComprehensiveReportVO;
import com.cloud.baowang.user.api.vo.user.request.ComprehensiveReportVO;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.ComprehensiveReportServiceApi;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
 * @Description 综合报表
 * @auther amos
 * @create 2024-11-06
 */
@Slf4j
@Component
@AllArgsConstructor
public class ComprehensiveReportHandler {
    private final ComprehensiveReportServiceApi comprehensiveReportServiceApi;

    @XxlJob(value = "comprehensiveReportHandler")
    public void comprehensiveReportHandler() {
        String jobParam = XxlJobHelper.getJobParam();
        XxlJobHelper.log("综合报表-------begin----------参数07:"+jobParam);
        if (ObjectUtil.isNotEmpty(jobParam)) {
            ReportRecalculateVO recalculateVO = JSON.parseObject(jobParam, ReportRecalculateVO.class);
            assert recalculateVO != null;
            long startTime = recalculateVO.getStartTime();
            long endTime = recalculateVO.getEndTime();
            String siteCode = recalculateVO.getSiteCode();
            goToRerunLogic(startTime,endTime,siteCode);
        }else {
            ReportComprehensiveReportVO info = getTimeStampInfo();
            XxlJobHelper.log("综合报表-------first build ----------参数 : " +info);
            comprehensiveReportServiceApi.execute(info);
        }
    }

    public void goToRerunLogic(long startTimestamp ,long endTimestamp,String siteCode) {
        LocalDateTime startDateTime = Instant.ofEpochMilli(startTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime endDateTime = Instant.ofEpochMilli(endTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 每个小时区间跑一次
        for (LocalDateTime current = startDateTime; current.isBefore(endDateTime) || current.equals(endDateTime); current = current.plusHours(1)) {
            LocalDateTime nextHour = current.plusHours(1);
            long currentStartTimestamp = current.toInstant(ZoneOffset.UTC).toEpochMilli();
            long currentEndTimestamp = nextHour.minus(1, ChronoUnit.MILLIS).toInstant(ZoneOffset.UTC).toEpochMilli();
            ReportComprehensiveReportVO vo = new ReportComprehensiveReportVO();
            vo.setStartTime(currentStartTimestamp);
            vo.setEndTime(currentEndTimestamp);
            vo.setSiteCode(siteCode);
            XxlJobHelper.log("综合报表重跑执行参数对比：[{}]:{}-{}-->{}", siteCode,"--------"+ vo.getStartTime(), vo.getEndTime(),vo.getSiteCode());
            comprehensiveReportServiceApi.execute(vo);
        }

        stopWatch.stop();
        XxlJobHelper.log("10001-####综合报表组装数据#####    执行结束========执行时间:{}", stopWatch.getTotalTimeMillis());

    }

    /**
     * 获得上个小时开始-结束时间戳
     * @return
     */
    public static ReportComprehensiveReportVO getTimeStampInfo(){
        ReportComprehensiveReportVO vo = new ReportComprehensiveReportVO();

        long timestamp = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // 获取上一个小时的开始时间
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfLastHour = calendar.getTimeInMillis();
        vo.setStartTime(startOfLastHour);
        // 获取上一个小时的结束时间
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endOfLastHour = calendar.getTimeInMillis();
        vo.setEndTime(endOfLastHour);
        return vo;
    }

    public static String formatTimestampToTimeZone(Long timeStamp, String zoneId) {
        if (timeStamp == null || timeStamp <= 0) {
            return "";
        }
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(ZoneId.of(zoneId));
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // 定义时间格式
        return formatter.format(zonedDateTime);
    }

    public static void main(String[] args) {
        long nowZoneTime = System.currentTimeMillis();
        String timeZone = "UTC-5";
        String date = DateUtils.formatDateByZoneId(nowZoneTime, DateUtils.FULL_FORMAT_1, timeZone);
        System.out.println("ComprehensiveReportHandler.main ++++ date +++"+date);
        String hourStr=DateUtils.formatDateByZoneId(nowZoneTime,DateUtils.HH,timeZone);
        System.out.println("ComprehensiveReportHandler.main hourStr -----"+hourStr);
//        ComprehensiveReportVO info = getTimeStampInfo();
//        System.out.println("ComprehensiveReportHandler.main begin "+info.getStartTime());
//        System.out.println("ComprehensiveReportHandler.main end "+info.getEndTime());
//        System.out.println("ComprehensiveReportHandler.main cur "+System.currentTimeMillis());
    }
}
