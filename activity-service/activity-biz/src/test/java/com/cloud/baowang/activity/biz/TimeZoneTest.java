package com.cloud.baowang.activity.biz;

import cn.hutool.core.date.DateUtil;
import com.cloud.baowang.common.core.utils.DateUtils;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/27 17:57
 * @Version: V1.0
 **/
public class TimeZoneTest {
    public static void main(String[] args) {
        String timeZoneId="UTC-5";
        Long startTime= DateUtil.beginOfMonth(Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of(timeZoneId)))).getTimeInMillis();
        Long endTime = DateUtil.endOfMonth(Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of(timeZoneId)))).getTimeInMillis();
        System.err.println(startTime);
        System.err.println(endTime);

        long startTime1=DateUtils.getStartDayMonthTimestamp(timeZoneId);
        long endTime1=DateUtils.getEndDayMonthTimestamp(timeZoneId);
        System.err.println(startTime1);
        System.err.println(endTime1);

    }
}
