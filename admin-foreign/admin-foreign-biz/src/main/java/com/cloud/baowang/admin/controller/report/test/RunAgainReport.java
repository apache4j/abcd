package com.cloud.baowang.admin.controller.report.test;

import cn.hutool.core.date.DateUtil;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.ReportComprehensiveReportVO;
import com.cloud.baowang.user.api.vo.user.request.ComprehensiveReportVO;
import com.cloud.baowang.user.api.vo.user.request.SiteBasicReportVO;
import com.cloud.baowang.report.api.api.ComprehensiveReportServiceApi;
import com.cloud.baowang.report.api.api.ReportUserVenueRebateApi;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@Tag(name = "总站-会员报表")
@RequestMapping("/report-userInfo-statement/api")
@AllArgsConstructor
public class RunAgainReport {

    private final ComprehensiveReportServiceApi comprehensiveReportServiceApi;
    private final SiteApi siteApi;

    private final ReportUserVenueRebateApi rebateApi;

    @PostMapping("/report")
    public void comprehensiveReportHandler(@RequestBody ComprehensiveReportVO jobParam) {

        log.error("10001-综合报表-------begin----------参数:{}", jobParam);
        if (jobParam != null) {
            long startTime = jobParam.getStartTime();
            long endTime = jobParam.getEndTime();
            String siteCode = jobParam.getSiteCode();
            goToRerunLogic(startTime,endTime,siteCode);
        }else {
            ReportComprehensiveReportVO info = getTimeStampInfo();
            comprehensiveReportServiceApi.execute(info);
        }
    }

    @PostMapping("/rebate")
    public void userRebateHandler(@RequestBody ReportRecalculateVO reportRecalculateVO) {
//        ReportRecalculateVO reportRecalculateVO = new ReportRecalculateVO();
        reportRecalculateVO.setTimeZone("UTC+8");
        reportRecalculateVO.setSiteCode("Lq5c4J");
        log.error("佣金计算-------test----------参数:"+reportRecalculateVO);
        rebateApi.onAgentCommissionTaskBegin(reportRecalculateVO);
    }
    public void goToRerunLogic(long startTimestamp ,long endTimestamp,String siteCode) {
        LocalDateTime startDateTime = Instant.ofEpochMilli(startTimestamp)
                .atZone(ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime endDateTime = Instant.ofEpochMilli(endTimestamp)
                .atZone(ZoneOffset.UTC).toLocalDateTime();
        System.out.println("10001-综合报表----重跑开始时间:{}"+startTimestamp+"重跑结束时间:"+endTimestamp);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 输出区间内每小时的开始和结束时间戳
        for (LocalDateTime current = startDateTime; current.isBefore(endDateTime) || current.equals(endDateTime); current = current.plusHours(1)) {
            LocalDateTime nextHour = current.plusHours(1);
            // 当前小时的开始和结束时间
            long currentStartTimestamp = current.toInstant(ZoneOffset.UTC).toEpochMilli();
            long currentEndTimestamp = nextHour.minus(1, ChronoUnit.MILLIS).toInstant(ZoneOffset.UTC).toEpochMilli();
            ReportComprehensiveReportVO vo = new ReportComprehensiveReportVO();
            vo.setStartTime(currentStartTimestamp);
            vo.setEndTime(currentEndTimestamp);
            vo.setSiteCode(siteCode);
            //找到时区对应的站点
            try {
                System.out.println("RunAgainReport.goToRerunLogic : " +vo);
                comprehensiveReportServiceApi.execute(vo);
            } catch (BaowangDefaultException e) {
                System.out.println("comprehensive report    rerun failed   " + e.getMessage());
            }
        }

        stopWatch.stop();

        log.error("10001-####综合报表组装数据#####    执行结束========执行时间:{}", stopWatch.getTotalTimeMillis());

    }

    public List<SiteBasicReportVO> buildReqSiteInfo(String timeZone){
        List<SiteBasicReportVO> siteCodeAndNameList = Lists.newArrayList();
        ResponseVO<List<SiteVO>> allSiteResponse = siteApi.allSiteInfo();
        if (null != allSiteResponse && allSiteResponse.isOk()) {
            siteCodeAndNameList = allSiteResponse.getData().stream().filter(obj -> obj.getTimezone()
                                                                                           .equals(timeZone) && obj.getStatus().equals(EnableStatusEnum.ENABLE.getCode()))
                    .map(obj -> new SiteBasicReportVO(obj.getSiteCode(),obj.getSiteName())).toList();
        }
        return siteCodeAndNameList;
    }




    private long getLimit(Long startTime, Long endTime) {

        Date date1 = new Date(startTime);
        Date date2 = new Date(endTime);
        return DateUtil.betweenDay(date2, date1, true);
    }

    /**
     * 根据当前时间戳获得这个小时开始-结束时间戳
     * @return
     */
    public ReportComprehensiveReportVO getTimeStampInfo(){
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
        String time = formatTimestampToTimeZone(1735574399999L, "UTC+8");
        System.out.println("RunAgainReport.main  " + time);

    }
}
