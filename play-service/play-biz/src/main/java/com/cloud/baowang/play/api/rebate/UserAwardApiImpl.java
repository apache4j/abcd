package com.cloud.baowang.play.api.rebate;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.wallet.api.enums.wallet.VIPAwardEnum;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.api.rebate.UserAwardApi;
import com.cloud.baowang.play.api.vo.vip.VIPSendReqVO;
import com.cloud.baowang.play.service.OrderRecordService;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author : 小智
 * @Date : 13/6/24 3:46 PM
 * @Version : 1.0
 */
@Slf4j
@RestController
@AllArgsConstructor
public class UserAwardApiImpl implements UserAwardApi {

    private final OrderRecordService orderRecordService;

    private final SystemDictConfigApi systemDictConfigApi;

    /**
     * 周红包规则 每周五12点发放上周五到这周四会员流水奖励 (包含24个时区)
     * 0 0 19-18 ? * THU,FRI
     */
    @Override
    public void weekRebate(VIPSendReqVO vo) {
        Long startTime = null;
        Long endTime = null;
        String timezone = "";
        String siteCode = "";
        try {
            // 决定是哪个时区
            timezone = TimeZoneUtils.get12TimeZone();
            siteCode = vo.getSiteCode();
            if(ObjectUtil.isNotEmpty(vo.getTimeZone())){
                timezone = vo.getTimeZone();
            }
            // 判断现在是周几如果大于等于周一开始时间默认往前推一周，如果是在周日开始时间取这周。主要是因为时区问题
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            startTime = week >= Calendar.MONDAY ? TimeZoneUtils.getAnyWeekdayStartTimestamp(1, timezone,2)
                : TimeZoneUtils.getAnyWeekdayStartTimestamp(1, timezone,1);
            endTime = week == Calendar.SUNDAY ? TimeZoneUtils.getAnyWeekdayEndTimestamp(7, timezone, 2)
                : TimeZoneUtils.getAnyWeekdayEndTimestamp(7, timezone,1);
            String dateStr = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN);
            log.info("周红包返水start,开始时间:{}, 时区:{}, 站点:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                    siteCode, startTime,endTime);
            orderRecordService.sendUserRebate(timezone, startTime, endTime, VIPAwardEnum.WEEK_BONUS, siteCode);
            log.info("周红包返水end,开始时间:{}, 时区:{}, 站点:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                    siteCode, startTime,endTime);
        } catch (Exception e) {
            log.error("周红包统计发放 发生异常, 统计开始时间:{}, 统计结束时间:{}, 站点编码:{}, 时区:{}", startTime,
                    endTime, siteCode, timezone, e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(TimeZoneUtils.getAnyWeekdayStartTimestamp(1, "UTC-12",1));
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC+8"));
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        DayOfWeek dayOfStartWeek = DayOfWeek.of(1);
        if(week >= Calendar.MONDAY){
            System.out.println(now.with(TemporalAdjusters.nextOrSame(dayOfStartWeek))
                    .toLocalDate().minusWeeks(2));
        }else{
            System.out.println(now.with(TemporalAdjusters.nextOrSame(dayOfStartWeek))
                    .toLocalDate().minusWeeks(1));
        }
        DayOfWeek dayOfEndWeek = DayOfWeek.of(7);
        if(week == Calendar.SUNDAY) {
            System.out.println(now.with(TemporalAdjusters.nextOrSame(dayOfEndWeek))
                    .toLocalDate().minusWeeks(2)
                    .atTime(23, 59, 59, 999_999_999));
        }else{
            System.out.println(now.with(TemporalAdjusters.nextOrSame(dayOfEndWeek))
                    .toLocalDate().minusWeeks(1)
                    .atTime(23, 59, 59, 999_999_999));
        }
    }

    /**
     * 月红包规则 每个月15号发放上个月1号到月底会员流水奖励 (包含24个时区)
     */
    @Override
    public void monthRebate(VIPSendReqVO vo) {
        Long startTime = null;
        Long endTime = null;
        String timezone = "";
        String siteCode = "";
        try {
            // 决定是哪个时区
            timezone = TimeZoneUtils.get12TimeZone();
            siteCode = vo.getSiteCode();
            if(ObjectUtil.isNotEmpty(vo.getTimeZone())){
                timezone = vo.getTimeZone();
            }
            startTime = TimeZoneUtils.getStartOfMonthInTimeZone(DateUtil.lastMonth().getTime(), timezone);
            endTime = TimeZoneUtils.getEndOfMonthInTimeZone(DateUtil.lastMonth().getTime(), timezone);
            String dateStr = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN);
            log.info("月红包返水start, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                    startTime, endTime);
            orderRecordService.sendUserRebate(timezone, startTime, endTime, VIPAwardEnum.MONTH_BONUS, siteCode);
            log.info("月红包返水end, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                    startTime, endTime);
        } catch (Exception e) {
            log.error("月红包统计发放 发生异常, 统计开始时间:{}, 统计结束时间:{}, 站点编码:{}, 时区:{}", startTime,
                    endTime, siteCode, timezone, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 周体育红包规则 每周六12点发放上周六到这周五会员体育有效流水奖励 (包含24个时区)
     *  0 0 19-18 ? * THU,FRI
     */
    @Override
    public void weekSportRebate(VIPSendReqVO vo) {
        Long startTime = null;
        Long endTime = null;
        String timezone = "";
        String siteCode = "";
        try {
            // 决定是哪个时区
            timezone = TimeZoneUtils.get12TimeZone();
            siteCode = vo.getSiteCode();
            if(ObjectUtil.isNotEmpty(vo.getTimeZone())){
                timezone = vo.getTimeZone();
            }
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            startTime = week >= Calendar.MONDAY ? TimeZoneUtils.getAnyWeekdayStartTimestamp(1, timezone,2)
                    : TimeZoneUtils.getAnyWeekdayStartTimestamp(1, timezone,1);
            endTime = week == Calendar.SUNDAY ? TimeZoneUtils.getAnyWeekdayEndTimestamp(7, timezone, 2)
                    : TimeZoneUtils.getAnyWeekdayEndTimestamp(7, timezone,1);
            String dateStr = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN);
            log.info("周体育红包返水start, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                    startTime, endTime);
            orderRecordService.sendUserRebate(timezone, startTime, endTime, VIPAwardEnum.WEEK_SPORT_BONUS, siteCode);
            log.info("周体育红包返水end, 开始时间:{}, 时区:{}, 统计开始时间:{}, 统计结束时间:{}", dateStr, timezone,
                    startTime, endTime);
        } catch (Exception e) {
            log.error("周体育红包统计发放 发生异常, 统计开始时间:{}, 统计结束时间:{}, 站点编码:{}, 时区:{}", startTime,
                    endTime, siteCode, timezone, e);
            throw new RuntimeException(e);
        }
    }
}
