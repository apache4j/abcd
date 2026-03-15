package com.cloud.baowang.common.core.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.constants.TimeZoneUTCMapConstants;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 时区工具类
 *
 * @author: lavine
 * @creat: 2023/8/31 17:29
 */
@Log4j2
public class TimeZoneUtils {

    // 时区：GMT-4
    public static final TimeZone gmt4TimeZone = TimeZone.getTimeZone("GMT-4");

    public static final String sbaTimeZone = "UTC-4";

    public static final String cq9TimeZone = "UTC-4";

    //美东时间
    public static final ZoneId utc5ZoneId = ZoneId.of("UTC-5");

    public static final TimeZone NewYorkTimeZone = TimeZone.getTimeZone("America/New_York");
    public static final TimeZone UTC0 = TimeZone.getTimeZone("GMT-0");
    public static final TimeZone ShangHaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");

    // 时区：GMT+8
    public static final ZoneId shanghaiZoneId = ZoneId.of("Asia/Shanghai");

    public static final String patten_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String patten_yyyyMMddHHmmssSSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String patten_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
    public static final String patten_yyyyMMdd = "yyyy-MM-dd";

    public static final String patten_yyyy_MMddHHmmss = "yyyy/MM/dd HH:mm:ss";
    public static final String pattenT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String pattenT_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String patten_ymd = "yyyy/MM/dd";
    public static final String patten_hms = "HH:mm:ss";
    public static final String patten_hm = "HH:mm";
    //2019-07-13T15:40:00Z
    public static final String patten_yMdTHmsZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String patten_yMdTHmsSSSSSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

    public static final String patten_yyyMMddHHmmss = "yyyMMddHHmmss";

    public static final String patten_jdb = "dd-MM-yyyy HH:mm:ss";



    public static String formatLocalDateTime(LocalDateTime dateTime, String patten) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten);
        return dateTime.format(formatter);
    }

    /**
     * 将时间戳转换为指定时区的时间格式 -
     *
     * @param timeStamp 时间戳（以秒为单位）
     * @param zoneId    时区ID
     * @return 格式化后的时间字符串
     */
    public static String formatTimestampToTimeZone(Long timeStamp, String zoneId) {
        if (timeStamp == null || timeStamp <= 0) {
            return "";
        }
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(ZoneId.of(zoneId));
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten_yyyyMMddHHmmss);
        // 定义时间格式
        return formatter.format(zonedDateTime);
    }

    /**
     * 将时间戳转换为指定时区的时间格式 -
     *
     * @param timeStamp 时间戳（以秒为单位）
     * @param zoneId    时区ID
     * @return 格式化后的时间字符串
     */
    public static String formatTimestampToTimeZoneYyyyMMdd(Long timeStamp, String zoneId) {

        if (timeStamp == null || timeStamp <= 0) {
            return "";
        }

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(ZoneId.of(zoneId));
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten_yyyyMMdd);
        // 定义时间格式
        return formatter.format(zonedDateTime);
    }

    /**
     * 判断给定的时间戳在指定时区内，是否属于当天的 0 点到 1 点之间。
     *
     * @param time       Unix 时间戳（毫秒），表示要检查的时间
     * @param timeZoneId 时区 ID，符合 {@link ZoneId} 的标准时区字符串 (例如: "Asia/Shanghai", "America/New_York")
     * @return 如果时间在指定时区的 0 点到 1 点之间，返回 true；否则返回 false
     * <p>
     * <p>
     * 示例用法:
     * <pre>
     *     long currentTimeMillis = System.currentTimeMillis();
     *     String timeZoneId = "UTC-8";
     *     boolean isInZeroToOne = isInZeroToOneHour(currentTimeMillis, timeZoneId);
     *     System.out.println("是否在 0 点到 1 点之间: " + isInZeroToOne);
     * </pre>
     */
    public static boolean isInZeroToOneHour(long time, String timeZoneId) {
        // 将时间戳转换为 ZonedDateTime 对象，基于传入的时区 ID
        ZoneId zoneId = parseZoneId(timeZoneId);
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(time).atZone(zoneId);

        // 获取该时区内当前时间的小时数
        int currentHour = zonedDateTime.getHour();

        // 如果当前时间的小时数在 0 点和 1 点之间，则返回 true
        return currentHour >= 0 && currentHour < 1;
    }

    public static void main(String[] args) throws Exception {
        String sexyDate = formatTimestampToSexyDate(1756972108488L);
        System.out.println("TimeZoneUtils.main sexyDate - "+sexyDate);
        String timezone = "UTC-5";
   /*     LocalDateTime now = TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), timezone);
        System.err.println(TimeZoneUtils.formatLocalDateTime(now, TimeZoneUtils.patten_yyyyMMdd));*/
        timezone = "UTC-4";
        String timeDay = TimeZoneUtils.getDayStringInTimeZone(System.currentTimeMillis(), timezone);
        System.err.println("timeDay="+timeDay);

       /* String time = TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), TimeZoneUtils.sbaTimeZone), DateUtils.FULL_FORMAT_5);


        String yestDay = TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(TimeZoneUtils.adjustTimestamp(System.currentTimeMillis(), -1, TimeZoneUtils.sbaTimeZone),
                TimeZoneUtils.sbaTimeZone), DateUtils.FULL_FORMAT_5);


        System.err.println("time=" + time);
        System.err.println("yestDay=" + yestDay);
        String time1 = TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(1741662000000L, TimeZoneUtils.sbaTimeZone),DateUtils.PATTEN_EASTERN_TIME);

        System.err.println("time=" + time1);*/
        //System.out.println(isInZeroToOneHour(1727873381352L, "UTC+8"));
        //System.out.println(isInZeroToOneHour(System.currentTimeMillis(), "UTC+8"));
        //System.out.println(formatTimestampToTimeZone(1729527552000L, "UTC+8"));
        //System.out.println(isInZeroToOneHour(1729527552000L, "UTC+8"));
        /*System.out.println(formatTimestampToTimeZone(1727873381352L, "UTC+8"));
        System.out.println(formatTimestampToTimeZoneYyyyMMdd(1727873381352L, "UTC+8"));*/

        //System.err.println(parseLocalDateTime4TimeZoneToTime(LocalDateTime.now(), TimeZone.getTimeZone(utc5ZoneId)));
//        System.out.println(getDayStringInTimeZone(1727015706000L, "Asia/Shanghai"));
//        System.out.println(getStartOfWeekStringInTimeZone(1727015706000L, "Asia/Shanghai"));
        // System.out.println(formatTimestampToTimeZone(1725123600000L, "Asia/Shanghai"));
//        System.out.println(formatTimestampToTimeZone(1727873381352L, "UTC-5"));
//        System.out.println(formatTimestampToTimeZone(1727873381352L, "UTC+8"));
        /*long timestamp = System.currentTimeMillis(); // 示例时间戳（当前时间）
        long utcStartOfHour = convertToUtcStartOfHour(timestamp);

        System.out.println("原始时间戳: " + timestamp);
        System.out.println("UTC 整点开始时间戳: " + utcStartOfHour);*/

//        System.err.println(DateUtils.formatDateByZoneId(System.currentTimeMillis(), DateUtils.pattenT_Z,"UTC+8"));

//        System.out.println(getWeekOfSaturdayEndTimeInTimeZone(System.currentTimeMillis(), "UTC+8"));
//        System.out.println(getDayStringInTimeZone(System.currentTimeMillis(), "UTC+8"));
//
//        System.out.println(getWeekOfSaturdayEndTimeInTimeZone(1727919042000L, "UTC+8"));
//        System.out.println(getDayStringInTimeZone(1727919042000L, "UTC+8"));

//        System.out.println(getStartOfMonthInTimeZone(System.currentTimeMillis(),"UTC+8"));
//        System.out.println(getEndOfMonthInTimeZone(System.currentTimeMillis(),"UTC+8"));
//        System.out.println(getLastWeekdayStartTimestamp(5, "UTC"));
//        System.out.println(getLastWeekdayStartTimestamp(5, "UTC+1"));
//        System.out.println(getStartOfMonthInTimeZone(1729137600000L, "UTC-4"));


        //System.err.println(convertStringToTimestamp("2024-10-14T11:02:31.85", "UTC+7"));
        //System.err.println(convertStringToTimestamp("2024-10-14T11:06:39.347", "UTC+7" ));

//        System.err.println(getStartOfDayInTimeZone(adjustTimestamp(System.currentTimeMillis(),-1,"UTC+7"),"UTC+7"));

        //long starttime = getStartOfMonthInTimeZone(System.currentTimeMillis(),"UTC+8");
        //long endTime = getEndOfMonthInTimeZone(System.currentTimeMillis(),"UTC+8");
        //
        //  System.out.println(getBetweenDates(starttime,endTime,"UTC+8"));

        //System.out.println(getStartOfDayInTimeZone(System.currentTimeMillis(), "UTC+8"));
        //System.out.println(getEndOfDayInTimeZone(System.currentTimeMillis(), "UTC+8"));

        //System.out.println(getStartOfLastWeekInTimeZone(1730084528000L, "UTC+8"));
        //System.out.println(getStartOfLastWeekInTimeZone(1730170928000L, "UTC+8"));
        //System.out.println(getStartOfLastWeekInTimeZone(1730257328000L, "UTC+8"));
        //System.out.println(getStartOfLastWeekInTimeZone(1730343728000L, "UTC+8"));
        //System.out.println(getStartOfLastWeekInTimeZone(1730430128000L, "UTC+8"));
        //System.out.println(getStartOfLastWeekInTimeZone(1730516528000L, "UTC+8"));
        //System.out.println(getStartOfLastWeekInTimeZone(1730602928000L, "UTC+8"));
        //
        //System.out.println(getEndOfLastWeekInTimeZone(1730084528000L, "UTC+8"));
        //System.out.println(getEndOfLastWeekInTimeZone(1730170928000L, "UTC+8"));
        //System.out.println(getEndOfLastWeekInTimeZone(1730257328000L, "UTC+8"));
        //System.out.println(getEndOfLastWeekInTimeZone(1730343728000L, "UTC+8"));
        //System.out.println(getEndOfLastWeekInTimeZone(1730430128000L, "UTC+8"));
        //System.out.println(getEndOfLastWeekInTimeZone(1730516528000L, "UTC+8"));
        //System.out.println(getEndOfLastWeekInTimeZone(1730602928000L, "UTC+8"));

        // 检验 转换为整点， 整天，然后在根据时区转换为某天

        //System.out.println(convertToUtcStartOfHour(System.currentTimeMillis()));
        //System.out.println(convertToPreviousUtcStartOfHour(System.currentTimeMillis()));
        //long curr = convertToPreviousUtcStartOfHour(System.currentTimeMillis());
        //System.out.println(convertToPreviousUtcStartOfHour(curr));
        //long curr2 = convertToPreviousUtcStartOfHour(curr);
        //System.out.println(convertToPreviousUtcStartOfHour(curr2));
        //System.out.println(convertToUtcEndOfHour(curr2));
        /*long curr3 = convertToPreviousUtcStartOfHour(System.currentTimeMillis());
        for (int i =0;i<50;i++){
            System.out.println(convertToPreviousUtcStartOfHour(curr3));
            curr3 = convertToPreviousUtcStartOfHour(curr3);
        }*/

        // System.out.println(formatTimestampToTimeZone(1731292326254L,"UTC+8"));

//        String time = "2024-11-26T23:25:46.65";
//        System.err.println(convertToTimestamp(time,"UTC-4",null));
        //1733107620000 convertToUtcStartOfHour

        //System.out.println(convertToPreviousUtcStartOfHour(System.currentTimeMillis()));
        //System.out.println(convertToUtcStartOfHour(System.currentTimeMillis()));
        //System.out.println(convertToUtcEndOfHour(System.currentTimeMillis()));
       /* System.out.println(getDayOfWeekInTimeZone(System.currentTimeMillis(),"UTC+8"));
        System.out.println(getMonthOfYearInTimeZone(System.currentTimeMillis(),"UTC+8"));
        System.out.println(getDayOfWeekInTimeZone(1712403235000L,"UTC+8"));
        System.out.println(getMonthOfYearInTimeZone(1711884835000L,"UTC+8"));
        //isLastDayOfMonthInTimeZone
        System.out.println(isLastDayOfMonthInTimeZone("2025-04-30","UTC+8"));
        //getBetweenDates
        System.out.println(getBetweenDates(1746028800000L,1746028800000L,"UTC+8"));
        System.out.println(getBetweenDates(1746028800000L,1745942400000L,"UTC+8"));

        System.out.println(getMonthString(1746028800000L,"UTC+8"));

        System.out.println(getStartOfDayTimestamp("2025-05-18","UTC+8"));
        System.out.println(getEndOfDayTimestamp("2025-05-18","UTC+8"));*/
    }

    /**
     * 当前时间+时区转换utc-5时间字符串
     *
     * @param time     时间，如 12:05:03
     * @param fromZone 当前时间对应的时区
     * @return cron
     */
    public static String convertTimeZone(String time, String fromZone) {
        // 定义时间格式
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(patten_hm);
        // 解析输入时间
        LocalTime localTime = LocalTime.parse(time, timeFormatter);
        // 获取输入时间在指定时区的 ZonedDateTime
        ZonedDateTime fromZonedDateTime = localTime.atDate(LocalDate.now())
                .atZone(ZoneId.of(fromZone));

        // 转换到 UTC-5 时区
        ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(utc5ZoneId);

        // 格式化为 hh:mm
        return toZonedDateTime.format(timeFormatter);
    }

    /**
     * 批量将 当前时区+时时间转换utc-5时间字符串
     *
     * @param * @param timeMap {"UTC+8":"15:00","UTC+10":"16:00"}
     * @return {"时区":"转换后的时间"}{"UTC+8":"20:00","UTC+10":"21:00"}
     */
    public static Map<String, String> convertTimeZone(Map<String, String> timesMap) {
        if (CollectionUtil.isEmpty(timesMap)) {
            return null;
        }
        // 定义时间格式
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(patten_hm);
        Map<String, String> timeMapArr = new HashMap<>();
        for (String currentTimeZone : timesMap.keySet()) {

            String currentTime = timesMap.get(currentTimeZone);

            // 解析输入时间
            LocalTime localTime = LocalTime.parse(currentTime, timeFormatter);
            // 获取输入时间在指定时区的 ZonedDateTime
            ZonedDateTime fromZonedDateTime = localTime.atDate(java.time.LocalDate.now())
                    .atZone(ZoneId.of(currentTimeZone));
            // 转换到 UTC+5 时区
            ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(utc5ZoneId);
            String formatTime = toZonedDateTime.format(timeFormatter);
            timeMapArr.put(currentTimeZone, formatTime);
        }
        // 格式化为 hh:mm:ss
        return timeMapArr;
    }

    public static String formatDateTime5TimeZone(Long date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat timeDateFormat = new SimpleDateFormat(patten_yyyyMMddHHmmss);
        timeDateFormat.setTimeZone(TimeZone.getTimeZone(utc5ZoneId));
        return timeDateFormat.format(date);
    }

    public static LocalDateTime timeByTimeZone(Long currentTimeMillis, String timezone) {
        if (currentTimeMillis == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.of(timezone));
    }

    // 将LocalDateTime转换为毫秒值，使用系统默认时区
    public static long convertLocalDateTimeToMillis(LocalDateTime localDateTime, String timezone) {
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.of(timezone));
        return zdt.toInstant().toEpochMilli();
    }

    /**
     * 将 LocalDateTime 从指定的 UTC+X 时区转换为目标时区
     *
     * @param localDateTime 要转换的时间
     * @param sourceUtcOffset UTC+时区偏移量 (例如: +7, -3)
     * @param targetTimeZone 目标时区 ID (例如: "Asia/Phnom_Penh")
     * @return 转换后的时间 (ZonedDateTime)
     */
    public static Long convertTimeZone(LocalDateTime localDateTime, String sourceUtcOffset, String targetTimeZone) {
        // 构建源时区的 ZoneId (例如 UTC+7 -> GMT+7)

        // 将 LocalDateTime 转换为源时区的 ZonedDateTime
        ZonedDateTime sourceZonedDateTime = localDateTime.atZone(ZoneId.of(sourceUtcOffset));

        // 转换为目标时区
        ZonedDateTime targetZonedDateTime = sourceZonedDateTime.withZoneSameInstant(ZoneId.of(targetTimeZone));

        // 返回时间戳 (毫秒级)
        return targetZonedDateTime.toInstant().toEpochMilli();
    }

    public static String formatDateByTimeZone(Long date, String zoneId) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat timeDateFormat = new SimpleDateFormat(patten_ymd);
        timeDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of(zoneId)));
        return timeDateFormat.format(date);
    }


    public static String formatDate5TimeZone(Long date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat timeDateFormat = new SimpleDateFormat(patten_ymd);
        timeDateFormat.setTimeZone(TimeZone.getTimeZone(utc5ZoneId));
        return timeDateFormat.format(date);
    }

    /**
     * 根据指定的时区将字符串转换为日期对象(将日期字符串转换为Long类型)
     *
     * @param dateTime LocalDateTime对象
     * @param timeZone 时区
     */
    public static Long parseLocalDateTime4TimeZoneToTime(LocalDateTime dateTime, TimeZone timeZone) {
        // 使用预定义格式化器格式化日期时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattenT_SSS);
        String date = dateTime.format(formatter);
        return parseDate4TimeZoneToTime(date, pattenT_SSS, timeZone);
    }

    /**
     * 获取当前时间 100 年后的时间戳 (Long 类型)，不带时区处理。
     *
     * @return 100 年后的时间戳 (毫秒)
     */
    public static Long parseLocalDateTime100YearsLater() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 计算 100 年后的时间
        LocalDateTime dateTime100YearsLater = now.plusYears(100);

        // 将 LocalDateTime 转换为毫秒时间戳
        return dateTime100YearsLater.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 根据指定的时区将字符串转换为日期对象(将日期字符串转换为Long类型)
     *
     * @param date
     * @param pattern
     * @param timeZone
     * @return
     */
    public static Long parseDate4TimeZoneToTime(String date, String pattern, TimeZone timeZone) {
        Date cDate = parseDate4TimeZone(date, pattern, timeZone);
        if (cDate == null) {
            return null;
        }
        return cDate.getTime();
    }

    /**
     * 根据指定的时区将字符串转换为日期对象
     *
     * @param date
     * @param pattern
     * @param timeZone
     * @return
     */
    public static Date parseDate4TimeZone(String date, String pattern, TimeZone timeZone) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(timeZone);

        try {
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            log.warn(String.format("method:parseDate4TimeZone() data: %s, pattern: %s", date, pattern), e);
        }
        return null;
    }

    /**
     * 将指定的日期由A时区，转换为B时区
     *
     * @param date
     * @param pattern
     * @param beforeTimeZone
     * @param afterTimeZone
     * @return
     */
    public static String convertTimeZone2Str(String date, String pattern, TimeZone beforeTimeZone, TimeZone afterTimeZone) {
        if (StringUtils.isBlank(date)) {
            return "";
        }
        Date newDate = parseDate4TimeZone(date, pattern, beforeTimeZone);

        SimpleDateFormat timeDateFormat = new SimpleDateFormat(pattern);
        timeDateFormat.setTimeZone(afterTimeZone);
        String newDateStr = timeDateFormat.format(newDate);

        log.info(String.format("转换前: %s, 转换后: %s", date, newDateStr));

        return newDateStr;
    }

    // 定义一个静态方法，用于把秒数转为LocalDateTime
    public static String convertSecondsToLocalDateTimeOfPatten(long seconds, ZoneId zoneId, String patten) {
        // 获取当前的Instant对象，表示UTC时间
        Instant instant = Instant.now();
        // 根据时区ID获取ZoneId对象
        ZoneOffset offset = zoneId.getRules().getOffset(instant);
        // 根据秒数和ZoneId对象创建LocalDateTime对象
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(seconds, 0, offset);
        // 返回LocalDateTime对象
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(patten);
        return dtf.format(ldt);
    }


    /**
     * 根据传入的周几,获取出当天的开始时间戳
     *
     * @param weekday  周几
     * @param timezone 时区
     * @return 时间戳
     */
    public static long getWeekdayStartTimestamp(int weekday, String timezone) {
        // 将 1-7 的星期转换为 DayOfWeek (1=Monday, ..., 7=Sunday)
        DayOfWeek dayOfWeek = DayOfWeek.of(weekday);

        // 获取指定时区的当前时间
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timezone));

        // 找到该周的指定周几的开始时间
        ZonedDateTime dayStart = now.with(TemporalAdjusters.nextOrSame(dayOfWeek))
                .toLocalDate()
                .atStartOfDay(ZoneId.of(timezone));

        // 返回毫秒级时间戳
        return dayStart.toInstant().toEpochMilli();
    }

    /**
     * 根据传入的周几,获取出上周当天的开始时间戳
     *
     * @param weekday    上周几
     * @param timezone   时区
     * @param weekOffset 1 本周，2上周
     * @return 时间戳
     */
    public static long getAnyWeekdayStartTimestamp(int weekday, String timezone, int weekOffset) {
        // 将 1-7 的星期转换为 DayOfWeek (1=Monday, ..., 7=Sunday)
        DayOfWeek dayOfWeek = DayOfWeek.of(weekday);

        // 获取指定时区的当前时间
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timezone));

        // 找到该周的指定周几的开始时间
        ZonedDateTime dayStart = now.with(TemporalAdjusters.nextOrSame(dayOfWeek))
                .toLocalDate().minusWeeks(weekOffset)
                .atStartOfDay(ZoneId.of(timezone));

        // 返回毫秒级时间戳
        return dayStart.toInstant().toEpochMilli();
    }

    /**
     * 根据传入的周几,获取出上周当天的结束时间戳
     *
     * @param weekday    上周几
     * @param timezone   时区
     * @param weekOffset 1 本周，2上周
     * @return 时间戳
     */
    public static long getAnyWeekdayEndTimestamp(int weekday, String timezone, int weekOffset) {
        // 将 1-7 的星期转换为 DayOfWeek (1=Monday, ..., 7=Sunday)
        DayOfWeek dayOfWeek = DayOfWeek.of(weekday);

        // 获取指定时区的当前时间
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timezone));

        // 找到该周的指定周几的开始时间
        ZonedDateTime dayStart = now.with(TemporalAdjusters.nextOrSame(dayOfWeek))
                .toLocalDate().minusWeeks(weekOffset)
                .atTime(23, 59, 59, 999_999_999)
                .atZone(ZoneId.of(timezone));

        // 返回毫秒级时间戳
        return dayStart.toInstant().toEpochMilli();
    }


    /**
     * 获取指定时区的今天开始时间（00:00:00）
     */
    public static long getStartOfDayInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 获取今天的开始时间
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(ZoneId.of(timezone));

        // 返回开始时间的时间戳
        return startOfDay.toInstant().toEpochMilli();
    }

    /**
     * 获取指定时区的今天结束时间（23:59:59.999）
     */
    public static long getEndOfDayInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 获取今天的结束时间
        ZonedDateTime endOfDay = now.toLocalDate()
                .atTime(23, 59, 59, 999_999_999)
                .atZone(ZoneId.of(timezone));

        // 返回结束时间的时间戳
        return endOfDay.toInstant().toEpochMilli();
    }

    /**
     * 获取指定时区的昨天开始时间（00:00:00）
     */
    public static long getStartOfYesterdayInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 获取昨天的开始时间
        ZonedDateTime startOfYesterday = now.minusDays(1).toLocalDate().atStartOfDay(ZoneId.of(timezone));

        // 返回开始时间的时间戳
        return startOfYesterday.toInstant().toEpochMilli();
    }

    /**
     * 获取指定时区的昨天结束时间（23:59:59.999）
     */
    public static long getEndOfYesterdayInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 获取昨天的结束时间
        ZonedDateTime endOfYesterday = now.minusDays(1)
                .toLocalDate()
                .atTime(23, 59, 59, 999_999_999)
                .atZone(ZoneId.of(timezone));

        // 返回结束时间的时间戳
        return endOfYesterday.toInstant().toEpochMilli();
    }


    /**
     * 获取指定时区本月开始时间（1号 00:00:00）
     */
    public static long getStartOfMonthInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到本月的第一天的00:00:00
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate()
                .atStartOfDay();

        // 转换为 ZonedDateTime 带上时区
        return convertLocalDateTimeToMillis(startOfMonth, timezone);
    }

    /**
     * 获取指定时区本月结束时间（最后一天 23:59:59.999）
     */
    public static long getEndOfMonthInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到本月的最后一天的23:59:59.999999999
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .toLocalDate()
                .atTime(23, 59, 59, 999_999_999);  // 精确到纳秒

        // 转换为 ZonedDateTime 带上时区
        return convertLocalDateTimeToMillis(endOfMonth, timezone);
    }


    /**
     * 获取指定时区本年第一天（1月1日 00:00:00）
     */
    public static long getStartOfYearInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到本年的第一天的00:00:00
        LocalDateTime startOfYear = now.with(TemporalAdjusters.firstDayOfYear())
                .toLocalDate()
                .atStartOfDay();

        // 转换为带时区的时间戳
        return convertLocalDateTimeToMillis(startOfYear, timezone);
    }

    /**
     * 获取指定时区本年最后一天（12月31日 23:59:59.999）
     */
    public static long getEndOfYearInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到本年的最后一天的23:59:59.999999999
        LocalDateTime endOfYear = now.with(TemporalAdjusters.lastDayOfYear())
                .toLocalDate()
                .atTime(23, 59, 59, 999_999_999);  // 精确到纳秒

        // 转换为带时区的时间戳
        return convertLocalDateTimeToMillis(endOfYear, timezone);
    }

    /**
     * 获取指定时区上一年第一天（1月1日 00:00:00）
     */
    public static long getStartOfLastYearInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到去年的第一天的00:00:00
        LocalDateTime startOfLastYear = now.minusYears(1)
                .with(TemporalAdjusters.firstDayOfYear())
                .toLocalDate()
                .atStartOfDay();

        // 转换为带时区的时间戳
        return convertLocalDateTimeToMillis(startOfLastYear, timezone);
    }

    /**
     * 获取指定时区上一年最后一天（12月31日 23:59:59.999）
     */
    public static long getEndOfLastYearInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到去年的最后一天的23:59:59.999999999
        LocalDateTime endOfLastYear = now.minusYears(1)
                .with(TemporalAdjusters.lastDayOfYear())
                .toLocalDate()
                .atTime(23, 59, 59, 999_999_999);  // 精确到纳秒

        // 转换为带时区的时间戳
        return convertLocalDateTimeToMillis(endOfLastYear, timezone);
    }
    /**
     * 获取指定时间戳在指定时区下所属月份的天数
     *
     * @param timestampMillis 毫秒时间戳（一般为当前时间或业务时间）
     * @param timezone        时区 ID（例如 "Asia/Shanghai", "UTC", "America/New_York"）
     * @return 当前月份的总天数（例如：2 月为 28 或 29，4 月为 30，1 月为 31）
     */
    public static int getDaysInMonth(long timestampMillis, String timezone) {
        // 根据传入的时区 ID 获取对应的 ZoneId 对象
        ZoneId zoneId = parseZoneId(timezone);

        // 将时间戳转换为指定时区的 LocalDate 对象（只保留年月日）
        LocalDate localDate = Instant.ofEpochMilli(timestampMillis)
                .atZone(zoneId)
                .toLocalDate();

        // 使用 LocalDate 提供的 lengthOfMonth() 方法获取该月的天数
        return localDate.lengthOfMonth();
    }

    /**
     * 获取两个日期之间的所有日期(格式:yyyy-MM-dd)（考虑时区）
     * 如果开始与结束时间一致，则返回开始时间的天数
     * 如果结束时间小于开始时间，则返回为空 []
     *
     * @param startTime 起始时间戳（毫秒）
     * @param endTime   结束时间戳（毫秒）
     * @param timeZone  时区ID（例如 "Asia/Shanghai"）
     * @return 日期列表
     */
    public static List<String> getBetweenDates(Long startTime, Long endTime, String timeZone) {
        List<String> result = new ArrayList<>();
        try {
            // 创建指定时区的DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 将时间戳转换为指定时区的 ZonedDateTime
            ZonedDateTime startDate = Instant.ofEpochMilli(startTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);  // 清除时间部分，确保从当天开始

            ZonedDateTime endDate = Instant.ofEpochMilli(endTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);  // 清除时间部分，确保从当天结束

            // 循环添加日期
            while (!startDate.isAfter(endDate)) {
                result.add(startDate.format(formatter));  // 格式化为 yyyy-MM-dd
                startDate = startDate.plusDays(1);        // 增加一天
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 获取指定日期在指定时区下的月份字符串（格式：yyyy/MM）
     *
     * @param dateStr  日期字符串，格式为 yyyy-MM-dd
     * @param timezone 时区，例如 "UTC+8"、"Asia/Shanghai"
     * @return 当前月字符串，例如 "2024/04"
     */
    public static String getCurrentMonth(String dateStr, String timezone) {
        return getMonthString(dateStr, timezone, 0);
    }
    /**
     * 获取指定日期在指定时区下的上一个月字符串（格式：yyyy/MM）
     *
     * @param dateStr  日期字符串，格式为 yyyy-MM-dd
     * @param timezone 时区，例如 "UTC+8"、"Asia/Shanghai"
     * @return 上一个月字符串，例如 "2024/03"
     */
    public static String getPreviousMonth(String dateStr, String timezone) {
        return getMonthString(dateStr, timezone, -1);
    }

    /**
     * 解析时区字符串为 ZoneId 对象，支持格式如：
     * - "UTC+8"、"UTC-5"
     * - "GMT+3"、"GMT-2"
     * - 标准时区 ID（如 "Asia/Shanghai"、"Europe/London"）
     *
     * @param timezone 时区字符串
     * @return 对应的 ZoneId
     * @throws DateTimeException 如果时区格式不合法
     */
    public static ZoneId parseZoneId(String timezone) {
        if (timezone == null || timezone.isEmpty()) {
            throw new IllegalArgumentException("Timezone must not be null or empty");
        }

        if (timezone.startsWith("UTC") || timezone.startsWith("GMT")) {
            // 提取偏移部分，例如 "+8" 或 "-5"
            String offset = timezone.substring(3);
            return ZoneId.ofOffset("UTC", ZoneOffset.of(offset));
        } else {
            // 标准时区 ID，例如 "Asia/Shanghai"
            return ZoneId.of(timezone);
        }
    }
    /**
     * 通用方法：根据指定日期和时区，偏移指定的月份数量后返回 yyyy/MM 格式字符串
     *
     * @param dateStr       日期字符串，格式为 yyyy-MM-dd
     * @param timezone      时区，例如 "UTC+8"、"Asia/Shanghai"
     * @param offsetMonths  月份偏移量（0 表示当前月，-1 表示上个月，+1 表示下个月）
     * @return 返回格式为 yyyy/MM 的月份字符串
     */
    private static String getMonthString(String dateStr, String timezone, int offsetMonths) {
        // 定义输入和输出的日期格式
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM");

        // 解析日期字符串
        LocalDate localDate = LocalDate.parse(dateStr, inputFormatter);

        // 构建时区对象
        ZoneId zoneId = parseZoneId(timezone);

        // 应用时区并偏移月份
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId).plusMonths(offsetMonths);

        // 格式化为 yyyy/MM 字符串
        return zonedDateTime.format(outputFormatter);
    }
    /**
     * 通用方法：根据指定时间戳和时区（仅支持 UTC+/-偏移），返回 yyyy-MM 格式的月份字符串
     *
     * @param timestampMillis 时间戳（毫秒）
     * @param timezone        时区，例如 "UTC+8"
     * @return 返回格式为 yyyy-MM 的月份字符串
     */
    public static String getMonthString(long timestampMillis, String timezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        ZoneId zoneId = parseZoneId(timezone);

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timestampMillis).atZone(zoneId);
        return zonedDateTime.format(formatter);
    }


    /**
     * 计算两个时间戳之间相隔多少天，包括起始和结束日期（考虑时区）
     *
     * @param startTime 起始时间戳（毫秒）
     * @param endTime   结束时间戳（毫秒）
     * @param timeZone  时区ID（例如 "Asia/Shanghai"）
     * @return 相隔的天数，包括起始和结束日期
     */
    public static int getDaysBetweenInclusive(Long startTime, Long endTime, String timeZone) {
        try {
            // 将时间戳转换为指定时区的 ZonedDateTime 并清除时间部分
            ZonedDateTime startDate = Instant.ofEpochMilli(startTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);

            ZonedDateTime endDate = Instant.ofEpochMilli(endTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);

            // 计算日期差并包括起始和结束日期
            return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 计算两个时间戳之间相隔多少天，包括起始和结束日期（考虑时区）
     *
     * @param startTime 起始时间戳（毫秒）
     * @param endTime   结束时间戳（毫秒）
     * @param timeZone  时区ID（例如 "Asia/Shanghai"）
     * @return 相隔的天数，包括起始,不包括结束日期
     */
    public static int getDaysBetweenExclusive(Long startTime, Long endTime, String timeZone) {
        try {
            // 将时间戳转换为指定时区的 ZonedDateTime 并清除时间部分
            ZonedDateTime startDate = Instant.ofEpochMilli(startTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);

            ZonedDateTime endDate = Instant.ofEpochMilli(endTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);

            // 计算日期差并包括起始和结束日期
            return (int) ChronoUnit.DAYS.between(startDate, endDate) ;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取指定时区本周开始时间（周一 00:00:00）
     */
    public static long getStartOfWeekInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到本周一的00:00:00
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();

        // 转换为 ZonedDateTime 带上时区
        return convertLocalDateTimeToMillis(startOfWeek, timezone);
    }

    /**
     * 获取指定时区本周结束时间（周日 23:59:59.999）
     */
    public static long getEndOfWeekInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到本周日的23:59:59.999999999
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .toLocalDate()
                .atTime(23, 59, 59, 999_999_999);  // 精确到纳秒

        // 转换为 ZonedDateTime 带上时区
        return convertLocalDateTimeToMillis(endOfWeek, timezone);
    }

    /**
     * 获取指定时区上周周一的开始时间（上周一 00:00:00）
     */
    public static long getStartOfLastWeekInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 获取当前周的周一
        LocalDateTime startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 确保得到上周一的时间
        LocalDateTime startOfLastWeek = startOfCurrentWeek.minusWeeks(1);

        // 将上周一的日期设置为 00:00:00
        LocalDateTime startOfLastWeekAtStart = startOfLastWeek.toLocalDate().atStartOfDay();

        // 转换为毫秒时间戳
        return convertLocalDateTimeToMillis(startOfLastWeekAtStart, timezone);
    }


    /**
     * 获取指定时区上周周日的结束时间（上周日 23:59:59.999）
     */
    public static long getEndOfLastWeekInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);
        // 获取本周的周一，并退回到上周的周一
        // 获取上一个自然周的周一
        LocalDateTime startOfLastWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);

        // 基于上周一找到上周日，并设置时间到当天的结束
        LocalDateTime endOfLastWeek = startOfLastWeek.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                .toLocalDate()
                .atTime(23, 59, 59, 999_999_999);

        // 转换为毫秒时间戳
        return convertLocalDateTimeToMillis(endOfLastWeek, timezone);

    }

    /**
     * 获取指定时区下周周一的开始时间（下周一 00:00:00）
     *
     * @param currentTimeMillis 当前时间戳 (毫秒)
     * @param timezone          指定时区
     * @return 下周一 00:00:00 的时间戳 (毫秒)
     */
    public static long getStartOfNextWeekInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 获取当前周的周一
        LocalDateTime startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 确保得到下周一的时间
        LocalDateTime startOfNextWeek = startOfCurrentWeek.plusWeeks(1);

        // 将下周一的日期设置为 00:00:00
        LocalDateTime startOfNextWeekAtStart = startOfNextWeek.toLocalDate().atStartOfDay();

        // 转换为毫秒时间戳
        return convertLocalDateTimeToMillis(startOfNextWeekAtStart, timezone);
    }


    /**
     * 获取指定时区本周周六的结束时间（周六 23:59:59.999）
     *
     * @param currentTimeMillis 指定的时间（毫秒），通常使用 System.currentTimeMillis() 获取当前时间
     * @param timezone          指定的时区（例如 "UTC+8"）。表示将时间转换为哪个时区的时间
     * @return 本周周六 23:59:59.999 的时间戳（精确到毫秒）
     */
    public static long getWeekOfSaturdayEndTimeInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 调整到本周日的23:59:59.999999999
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .toLocalDate()
                .atTime(23, 59, 59, 999_999_999);  // 精确到纳秒

        // 转换为 ZonedDateTime 带上时区
        ZonedDateTime zonedEndOfWeek = endOfWeek.atZone(ZoneId.of(timezone));

        // 返回结束时间的时间戳（精确到毫秒）
        return zonedEndOfWeek.toInstant().toEpochMilli();
    }

//    public static long convertStringToTimestamp(String dateString, String timeZoneId, String format) {
//        ZoneId zoneId = ZoneId.of(timeZoneId);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
//        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
//        return localDateTime.atZone(zoneId).toInstant().toEpochMilli(); // 返回时间戳（毫秒）
//    }


    public static long convertStringToTimestamp(String dateString, String timeZoneId) {
        ZoneId zoneId = ZoneId.of(timeZoneId);

        // 创建一个可接受多种格式的 DateTimeFormatter
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // 例如：2024-10-14T11:02:31
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")) // 例如：2024-10-14T11:02:31.85
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")) // 例如：2024-10-14T11:06:39.347
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) // 例如：2024-10-14T11:06:39
                .toFormatter();

        // 解析字符串为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);

        // 获取时间戳
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        return instant.toEpochMilli(); // 返回毫秒时间戳
    }


    /**
     * 判断指定时间（毫秒）在指定时区下，是否为该自然周的最后一天（周日）
     *
     * @param currentTimeMillis 指定的时间（毫秒），通常使用 System.currentTimeMillis() 获取当前时间
     * @param timezone          指定的时区（例如 "UTC+8""）。表示将时间转换为哪个时区的时间
     * @return 如果指定的时间在该时区下为自然周的最后一天（周日），则返回 true；否则返回 false
     */
    public static Boolean isEndOfWeekDayTimeZone(long currentTimeMillis, String timezone) {
        ZoneId zoneId;
        // 兼容 "UTC+8" / "UTC-5" 这类偏移格式
        if (timezone.startsWith("UTC") || timezone.startsWith("GMT")) {
            zoneId = ZoneId.ofOffset("UTC", ZoneOffset.of(timezone.substring(3)));
        } else {
            zoneId = ZoneId.of(timezone); // 标准 IANA 时区
        }

        // 设置自然周的最后一天为 SUNDAY（周日）
        DayOfWeek lastDayOfWeek = DayOfWeek.SUNDAY;

        // 转换为 ZonedDateTime
        ZonedDateTime dateTime = Instant.ofEpochMilli(currentTimeMillis).atZone(zoneId);

        return dateTime.getDayOfWeek() == lastDayOfWeek;
    }
    /**
     * 将指定日期（格式如 2025-09-01）在指定时区（如 UTC+8）下转换为时间戳（毫秒）
     *
     * @param dateStr 日期字符串（格式为 yyyy-MM-dd）
     * @param timezone 时区标识，例如 "UTC+8" 或 "Asia/Shanghai"
     * @return 指定日期的 00:00:00 在该时区下的时间戳（毫秒）
     */
    public static long getStartOfDayTimestamp(String dateStr, String timezone) {
        // 解析日期字符串
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 解析时区
        ZoneId zoneId = parseZoneId(timezone);

        // 将 LocalDate 转换为 ZonedDateTime 的 00:00:00
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);

        // 转换为时间戳（毫秒）
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 将指定日期（格式如 2025-09-01）在指定时区（如 UTC+8）下转换为当天结束时间的时间戳（毫秒）
     *
     * @param dateStr 日期字符串（格式为 yyyy-MM-dd）
     * @param timezone 时区标识，例如 "UTC+8" 或 "Asia/Shanghai"
     * @return 指定日期的 23:59:59.999 在该时区下的时间戳（毫秒）
     */
    public static long getEndOfDayTimestamp(String dateStr, String timezone) {
        // 解析日期
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 解析时区
        ZoneId zoneId = parseZoneId(timezone);

        // 获取当天 23:59:59.999（+999ms）
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59, 999_000_000); // 999ms = 999_000_000ns

        // 转为 ZonedDateTime 再转为时间戳
        ZonedDateTime zonedDateTime = endOfDay.atZone(zoneId);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 获取指定时间在指定时区下是一周中的第几天（1 = 周一，7 = 周日）
     *
     * @param currentTimeMillis 当前时间（毫秒）
     * @param timezone          时区（例如 "UTC+8" 或 "Asia/Shanghai"）
     * @return 返回值范围 1（周一）到 7（周日）
     */
    public static int getDayOfWeekInTimeZone(long currentTimeMillis, String timezone) {
        // 创建一个 ZoneId 对象，表示指定的时区
        ZoneId zoneId = parseZoneId(timezone);

        // 将时间戳转化为指定时区的 ZonedDateTime 对象
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(currentTimeMillis).atZone(zoneId);

        // 使用 ISO 标准（周一为第一天）
        WeekFields weekFields = WeekFields.ISO;

        // 获取指定时区的自然周的第几天，返回值 1（周一）到 7（周日）
        return zonedDateTime.get(weekFields.dayOfWeek());
    }

    /**
     * 获取指定日期在指定时区下是一周中的第几天（1 = 周一，7 = 周日）
     *
     * @param dateStr  日期字符串，格式为 "yyyy-MM-dd"
     * @param timeZone 时区，例如 "UTC+8" 或 "Asia/Shanghai"
     * @return 返回值范围 1（周一）到 7（周日）
     */
    public static int getDayOfWeekInTimeZone(String dateStr, String timeZone) {
        // 解析日期字符串为 LocalDate
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 获取指定时区
        ZoneId zoneId = parseZoneId(timeZone);

        // 转换为 ZonedDateTime 的开始时刻（当天 00:00）
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);

        // 使用 ISO 标准（周一为第一天）
        WeekFields weekFields = WeekFields.ISO;

        // 返回周几，1 = 周一，7 = 周日
        return zonedDateTime.get(weekFields.dayOfWeek());
    }
    /**
     * 判断指定时间在指定时区下是否为自然周的最后一天（周日）
     *
     * @param currentTimeMillis 当前时间（毫秒）
     * @param timezone          时区（例如 "UTC+8" 或 "Asia/Shanghai"）
     * @return 如果是周日，返回 true；否则返回 false
     */
    public static boolean isSundayInTimeZone(long currentTimeMillis, String timezone) {
        // 创建一个 ZoneId 对象，表示指定的时区
        ZoneId zoneId = ZoneId.of(timezone);

        // 将时间戳转换为指定时区的 ZonedDateTime 对象
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(currentTimeMillis).atZone(zoneId);

        // 获取这一天是星期几（DayOfWeek），SUNDAY 表示周日
        return zonedDateTime.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * 判断指定日期（yyyy-MM-dd 格式）在指定时区下是否为自然周的最后一天（周日）
     *
     * @param dateStr  日期字符串，格式为 yyyy-MM-dd
     * @param timezone 时区（例如 "UTC+8" 或 "Asia/Shanghai"）
     * @return 如果是周日，返回 true；否则返回 false
     */
    public static boolean isSundayInTimeZone(String dateStr, String timezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateStr, formatter);

        ZoneId zoneId;
        if (timezone.startsWith("UTC") || timezone.startsWith("GMT")) {
            zoneId = ZoneId.ofOffset("UTC", ZoneOffset.of(timezone.substring(3)));
        } else {
            zoneId = ZoneId.of(timezone);
        }

        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);

        return zonedDateTime.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * 获取指定时间（毫秒时间戳）对应的月份（1-12）
     *
     * @param currentTimeMillis 当前时间戳（毫秒）
     * @param timezone          时区（例如 "UTC+8" 或 "Asia/Shanghai"）
     * @return 返回值范围 1（1月）到 12（12月）
     */
    public static int getMonthOfYearInTimeZone(long currentTimeMillis, String timezone) {
        ZoneId zoneId;

        // 支持 "UTC+8" / "UTC-5" 偏移格式
        if (timezone.startsWith("UTC") || timezone.startsWith("GMT")) {
            zoneId = ZoneId.ofOffset("UTC", ZoneOffset.of(timezone.substring(3)));
        } else {
            zoneId = ZoneId.of(timezone); // 标准时区
        }

        // 获取指定时区下的时间
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(currentTimeMillis).atZone(zoneId);

        // 获取当前月（1-12）
        return zonedDateTime.getMonthValue();
    }
    /**
     * 判断指定时间（毫秒时间戳）在指定时区下是否为该月的最后一天
     *
     * @param currentTimeMillis 当前时间戳（毫秒）
     * @param timezone          时区（例如 "UTC+8" 或 "Asia/Shanghai"）
     * @return 如果是当月最后一天，返回 true；否则返回 false
     */
    public static boolean isLastDayOfMonthInTimeZone(long currentTimeMillis, String timezone) {
        ZoneId zoneId;

        // 支持 "UTC+8" / "UTC-5" 偏移格式
        if (timezone.startsWith("UTC") || timezone.startsWith("GMT")) {
            zoneId = ZoneId.ofOffset("UTC", ZoneOffset.of(timezone.substring(3)));
        } else {
            zoneId = ZoneId.of(timezone); // 标准时区
        }

        // 获取指定时区下的时间
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(currentTimeMillis).atZone(zoneId);

        // 获取当前日期与该月的最后一天
        int currentDay = zonedDateTime.getDayOfMonth();
        int lastDayOfMonth = zonedDateTime.toLocalDate().lengthOfMonth();

        return currentDay == lastDayOfMonth;
    }

    /**
     * 判断指定日期（yyyy-MM-dd 格式）在指定时区下是否为该月的最后一天
     *
     * @param dateStr  日期字符串，格式为 yyyy-MM-dd
     * @param timezone 时区（例如 "UTC+8" 或 "Asia/Shanghai"）
     * @return 如果是当月最后一天，返回 true；否则返回 false
     */
    public static boolean isLastDayOfMonthInTimeZone(String dateStr, String timezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateStr, formatter);

        ZoneId zoneId;
        if (timezone.startsWith("UTC") || timezone.startsWith("GMT")) {
            zoneId = ZoneId.ofOffset("UTC", ZoneOffset.of(timezone.substring(3)));
        } else {
            zoneId = ZoneId.of(timezone);
        }

        // 将本地日期转为指定时区下的 ZonedDateTime
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);

        // 判断是否为该月的最后一天
        int dayOfMonth = zonedDateTime.getDayOfMonth();
        int lastDayOfMonth = zonedDateTime.toLocalDate().lengthOfMonth();

        return dayOfMonth == lastDayOfMonth;
    }



    /**
     * 获取指定时区的 指定时间的日期（不包含时分秒，格式为）
     */
    public static String convertTimestampToString(long currentTimeMillis, String timezone, String pattern) {
        // 创建 Instant 对象
        Instant instant = Instant.ofEpochMilli(currentTimeMillis);

        // 创建 ZonedDateTime 对象
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timezone));

        // 创建 DateTimeFormatter 对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // 格式化并返回字符串
        return zonedDateTime.format(formatter);
    }


    /**
     * 获取指定时区的 指定时间的日期（不包含时分秒，格式为yyyy-MM-dd）
     */
    public static String getDayStringInTimeZone(long currentTimeMillis, String timezone) {
        return convertTimestampToString(currentTimeMillis, timezone, patten_yyyyMMdd);
    }

    /**
     * 获取指定时区当前时间这周的第一天日期（不包含时分秒，格式为yyyy-MM-dd）
     */
    public static String getStartOfWeekStringInTimeZone(long currentTimeMillis, String timezone) {
        // 将当前时间戳转换为指定时区的 LocalDateTime
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        // 获取指定时区本周的第一天（周一）
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate();

        // 使用 DateTimeFormatter 格式化日期为 yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten_yyyyMMdd);

        // 返回格式化后的字符串
        return startOfWeek.format(formatter);
    }

    /**
     * 字符串转时间戳
     *
     * @param dateString 时间
     * @param timeZoneId 时区
     * @param dateFormat 时间格式
     * @return 毫秒时间戳
     */
    public static long convertToTimestamp(String dateString, String timeZoneId, String dateFormat) {
        ZoneId zoneId = ZoneId.of(timeZoneId);
        DateTimeFormatter formatter = null;
        if (!StringUtils.isBlank(dateFormat)) {
            formatter = DateTimeFormatter.ofPattern(dateFormat);
        } else {
            formatter = new DateTimeFormatterBuilder()
                    .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // 例如：2024-10-14T11:02:31
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")) // 例如：2024-10-14T11:02:31.85
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")) // 例如：2024-10-14T11:06:39.347
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) // 例如：2024-10-14T11:06:39
                    .toFormatter();
        }

        // 解析字符串为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli(); // 返回毫秒时间戳
    }

    /**
     * 获取时区上月初始时间戳
     */
    public static Long getLastMonStartTimeInTimeZone(String timezone) {
        // 获取上一个自然月的第一天
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDateTime firstDayOfLastMonth = LocalDate.now(zoneId)
                .minusMonths(1) // 上个月
                .withDayOfMonth(1) // 设为第一天
                .atStartOfDay(); // 转换为零时零分零秒

        // 获取时间戳
        return firstDayOfLastMonth.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 获取时区上月结束时间戳
     */
    public static Long getLastMonEndTimeInTimeZone(String timezone) {
        // 获取上一个自然月的最后一天
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDateTime lastDayOfLastMonth = LocalDateTime.now(zoneId)
                .minusMonths(1) // 上个月
                .withDayOfMonth(LocalDate.now(zoneId).minusMonths(1).lengthOfMonth()) // 设为最后一天
                .withHour(23) // 设置为 23 点
                .withMinute(59) // 设置为 59 分
                .withSecond(59) // 设置为 59 秒
                .withNano(999_999_999); // 设置为999纳秒

        // 获取时间戳
        return lastDayOfLastMonth.atZone(zoneId).toInstant().toEpochMilli();
    }


    /**
     * 获取出指定天数的时间戳
     *
     * @param currentTimeMillis 系统时间
     * @param days              -1往前推指定天数的时间戳,1往后推指定
     * @return 时间戳
     */
    public static long getTimestampByDays(long currentTimeMillis, int days) {
        // 将当前时间戳转换为 LocalDateTime
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault());

        // 根据天数往前或往后推算
        LocalDateTime targetDateTime = now.plusDays(days);

        // 返回目标时间的时间戳
        return targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将给定的时间戳转换为 UTC 整点开始的时间戳。
     *
     * @param timestamp 要转换的时间戳（毫秒）
     * @return 转换后的 UTC 整点开始时间戳（毫秒）
     */


    public static long convertToUtcStartOfHour(long timestamp) {
        // 将时间戳转换为 UTC LocalDateTime
        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toLocalDateTime();

        // 设置分钟和秒为 0，返回整点时间
        dateTime = dateTime.withMinute(0).withSecond(0).withNano(0);

        // 将整点时间转换回时间戳
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * 将给定的时间戳转换为 UTC 整点结束的时间戳。
     *
     * @param timestamp 要转换的时间戳（毫秒）
     * @return 转换后的 UTC 整点结束时间戳（毫秒）
     */
    public static long convertToUtcEndOfHour(long timestamp) {
        // 将时间戳转换为 UTC LocalDateTime
        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toLocalDateTime();

        // 设置分钟和秒为 59，纳秒为 999999999，表示整点结束时间
        dateTime = dateTime.withMinute(59).withSecond(59).withNano(999_999_999);

        // 将整点结束时间转换回时间戳
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * 将给定的时间戳转换为上一个 UTC 整点开始的时间戳。
     *
     * @param timestamp 要转换的时间戳（毫秒）
     * @return 上一个 UTC 整点开始时间戳（毫秒）
     */
    public static long convertToPreviousUtcStartOfHour(long timestamp) {
        // 将时间戳转换为 UTC LocalDateTime
        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toLocalDateTime();

        // 无论当前时间是否为整点，先减去 1 小时
        dateTime = dateTime.minusHours(1);

        // 设置分钟和秒为 0，返回上一个整点时间
        dateTime = dateTime.withMinute(0).withSecond(0).withNano(0);

        // 将整点时间转换回时间戳
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }


    /**
     * 将 UTC 当天的开始时间戳转换为指定时区的年月日格式
     *
     * @param utcStartOfDayTimestamp UTC 当天的开始时间戳（毫秒）
     * @param timezone               指定的时区 ID，例如 "Asia/Shanghai"
     * @return 指定时区的年月日字符串（格式：yyyy-MM-dd）
     */
    public static String convertUtcStartOfDayToLocalDate(long utcStartOfDayTimestamp, String timezone) {
        // 将 UTC 时间戳转换为 ZonedDateTime
        ZonedDateTime utcDateTime = Instant.ofEpochMilli(utcStartOfDayTimestamp).atZone(ZoneOffset.UTC);

        // 转换为指定时区的 ZonedDateTime
        ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.of(timezone));

        // 提取 LocalDate 并格式化为 "yyyy-MM-dd"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(formatter);
    }

    private static final Map<String, String> timeZoneMap = ImmutableMap.<String, String>builder()
            .put("UTC-11", "Etc/GMT+11")
            .put("UTC-10", "Pacific/Honolulu")
            .put("UTC-9", "America/Anchorage")
            .put("UTC-8", "America/Los_Angeles")
            .put("UTC-7", "America/Chihuahua")
            .put("UTC-6", "America/Chicago")
            .put("UTC-5", "America/New_York")
            .put("UTC-4", "America/Asuncion")
            .put("UTC-3", "America/Sao_Paulo")
            .put("UTC-2", "Etc/GMT+2")
            .put("UTC-1", "Atlantic/Cape_Verde")
            .put("UTC", "UTC")
            .put("UTC+1", "Europe/Berlin")
            .put("UTC+2", "Europe/Istanbul")
            .put("UTC+3", "Europe/Minsk")
            .put("UTC+4", "Europe/Moscow")
            .put("UTC+5", "Asia/Tashkent")
            .put("UTC+6", "Asia/Dhaka")
            .put("UTC+7", "Asia/Bangkok")
            .put("UTC+8", "Asia/Shanghai")
            .put("UTC+9", "Asia/Tokyo")
            .put("UTC+10", "Asia/Yakutsk")
            .put("UTC+11", "Asia/Vladivostok")
            .put("UTC+12", "Etc/GMT-12")
            .build();


    private static final Map<String, String> timeZoneUTCMap = ImmutableMap.<String, String>builder()
            .put("UTC-12", "-12:00")
            .put("UTC-11", "-11:00")
            .put("UTC-10", "-10:00")
            .put("UTC-9", "-09:00")
            .put("UTC-8", "-08:00")
            .put("UTC-7", "-07:00")
            .put("UTC-6", "-06:00")
            .put("UTC-5", "-05:00")
            .put("UTC-4", "-04:00")
            .put("UTC-3", "-03:00")
            .put("UTC-2", "-02:00")
            .put("UTC-1", "-01:00")
            .put("UTC", "+00:00")
            .put("UTC+1", "+01:00")
            .put("UTC+2", "+02:00")
            .put("UTC+3", "+03:00")
            .put("UTC+4", "+04:00")
            .put("UTC+5", "+05:00")
            .put("UTC+6", "+06:00")
            .put("UTC+7", "+07:00")
            .put("UTC+8", "+08:00")
            .put("UTC+9", "+09:00")
            .put("UTC+10", "+10:00")
            .put("UTC+11", "+11:00")
            .put("UTC+12", "+12:00")
            .put("UTC+13", "+13:00")
            .put("UTC+14", "+14:00")
            .build();

    /**
     * 统一12点 所有时区对应的服务器UTC-5时间分布
     */
    private static final Map<Integer, String> timeZone12UTCMap = ImmutableMap.<Integer, String>builder()
            .put(18, "UTC-11")
            .put(17, "UTC-10")
            .put(16, "UTC-9")
            .put(15, "UTC-8")
            .put(14, "UTC-7")
            .put(13, "UTC-6")
            .put(12, "UTC-5")
            .put(11, "UTC-4")
            .put(10, "UTC-3")
            .put(9, "UTC-2")
            .put(8, "UTC-1")
            .put(7, "UTC")
            .put(6, "UTC+1")
            .put(5, "UTC+2")
            .put(4, "UTC+3")
            .put(3, "UTC+4")
            .put(2, "UTC+5")
            .put(1, "UTC+6")
            .put(0, "UTC+7")
            .put(23, "UTC+8")
            .put(22, "UTC+9")
            .put(21, "UTC+10")
            .put(20, "UTC+11")
            .put(19, "UTC+12")
            .build();

    /**
     * 统一0点 所有时区对应的服务器UTC-5时间分布
     */
    private static final Map<Integer, String> timeZone0UTCMap = ImmutableMap.<Integer, String>builder()
            .put(6, "UTC-11")
            .put(5, "UTC-10")
            .put(4, "UTC-9")
            .put(3, "UTC-8")
            .put(2, "UTC-7")
            .put(1, "UTC-6")
            .put(0, "UTC-5")
            .put(23, "UTC-4")
            .put(22, "UTC-3")
            .put(21, "UTC-2")
            .put(20, "UTC-1")
            .put(19, "UTC")
            .put(18, "UTC+1")
            .put(17, "UTC+2")
            .put(16, "UTC+3")
            .put(15, "UTC+4")
            .put(14, "UTC+5")
            .put(13, "UTC+6")
            .put(12, "UTC+7")
            .put(11, "UTC+8")
            .put(10, "UTC+9")
            .put(9, "UTC+10")
            .put(8, "UTC+11")
            .put(7, "UTC+12")
            .build();


    public static String getTimeZoneName(String zone) {
        return timeZoneMap.get(zone);
    }

    public static String get12TimeZone() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return timeZone12UTCMap.get(hour);
    }

    public static String get0TimeZone() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return timeZone0UTCMap.get(hour);
    }

    public static String get0TimeZoneByUtc() {
        // 获取当前时间
        Date now = new Date();

        // 创建一个 Calendar 实例，指定时区为 UTC-5
        TimeZone timeZoneUTC5 = TimeZone.getTimeZone("GMT-5");
        Calendar calendar = Calendar.getInstance(timeZoneUTC5);
        calendar.setTime(now);
        // 获取 UTC-5 时区的当前小时数（24小时制）
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // 根据小时数从 timeZone0UTCMap 中获取对应的时区数据
        return timeZone0UTCMap.get(hour);
    }

    /**
     * UTC+8 转换为 +8:00。作为数据库时间转换
     *
     * @param zone 时区
     * @return 函数使用字段
     */
    public static String getTimeZoneUTC(String zone) {
        return timeZoneUTCMap.get(zone);
    }


    /**
     * 根据给定的时间戳、天数偏移量和时区返回新的时间戳。
     *
     * @param timestamp 当前的时间戳（毫秒）
     * @param days      正数往后推，负数往前推
     * @param timeZone  时区字符串，例如 "UTC+8" 或 "America/New_York"
     * @return 新的时间戳（毫秒）
     */
    public static long adjustTimestamp(long timestamp, int days, String timeZone) {
        // 根据时区创建 ZoneId
        ZoneId zoneId = ZoneId.of(timeZone);

        // 将时间戳转换为 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, zoneId.getRules().getOffset(LocalDateTime.now()));

        // 根据传入的天数进行调整
        LocalDateTime adjustedDateTime = dateTime.plusDays(days);

        // 转换回时间戳
        return adjustedDateTime.atZone(zoneId).toEpochSecond() * 1000; // 转换为毫秒
    }

    public static long lastDayTimestamp(int days, String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDateTime beginDayDate = LocalDate.now(zoneId)
                .minusDays(days) // 具体相差天数 1:昨天, -1:明天
                .atStartOfDay(); // 转换为零时零分零秒
        return beginDayDate.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static long lastDayEndTimestamp(int days, String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDateTime beginDayDate = LocalDate.now(zoneId)
                .minusDays(days) // 具体相差天数 1:昨天, -1:明天
                .atStartOfDay()
                .withHour(23) // 设置为 23 点
                .withMinute(59) // 设置为 59 分
                .withSecond(59) // 设置为 59 秒
                .withNano(999_999_999); // 转换为零时零分零秒
        return beginDayDate.atZone(zoneId).toInstant().toEpochMilli();
    }


    /**
     * 增加指定时间后的时间戳
     *
     * @param currentTimestamp 当前的时间戳（单位：毫秒）
     * @param timeAmount       增加的时间数量
     * @param timeUnit         时间单位
     * @return 增加指定时间后的新时间戳（单位：毫秒）
     */
    public static Long addTime(long currentTimestamp, long timeAmount, TimeUnit timeUnit) {
        // 将增加的时间转换为毫秒
        long timeToAddInMillis = timeUnit.toMillis(timeAmount);

        // 返回增加后的新时间戳
        return currentTimestamp + timeToAddInMillis;
    }

    public static String get4TimeZone() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return TimeZoneUTCMapConstants.timeZone4UTCMap.get(hour);
    }

    public static String get2TimeZone() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return TimeZoneUTCMapConstants.timeZone2UTCMap.get(hour);
    }
    /**
     * 获取指定时区的当前时间
     *
     * @param zoneId
     * @return
     */
    public static Date getCurrentDate4ZoneId(ZoneId zoneId) {
        LocalDateTime dateTime = LocalDateTime.now(zoneId);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }
    /**
     * 根据时区、格式、格式化当前日期
     *
     * @param date
     * @param timeZone
     * @param pattern
     * @return
     */
    public static String formatDate4TimeZone(Date date, TimeZone timeZone, String pattern) {
        SimpleDateFormat timeDateFormat = new SimpleDateFormat(pattern);
        timeDateFormat.setTimeZone(timeZone);
        return timeDateFormat.format(date);
    }



    /**
     * 根据时区、格式、格式化当前日期
     *
     * @param date
     * @param pattern
     * @return
     */
   /* public static String formatDate4TimeZone(Long date, TimeZone beforeTimeZone, TimeZone afterTimeZone, String pattern) {
        SimpleDateFormat timeDateFormat = new SimpleDateFormat(pattern);
        timeDateFormat.setTimeZone(beforeTimeZone);
        String format = timeDateFormat.format(date);
        return convertTimeZone2Str(format, pattern, beforeTimeZone, afterTimeZone);
    }*/

    /**
     * 获取指定时区的当前时间
     *
     * @param zoneId
     * @return
     */
   /* public static Date getCurrentDate4ZoneId(ZoneId zoneId) {
        LocalDateTime dateTime = LocalDateTime.now(zoneId);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }*/

    /**
     * 根据时区、格式、格式化当前日期
     *
     * @param date
     * @param timeZone
     * @param pattern
     * @return
     */
    /*public static String formatDate4TimeZone(Date date, TimeZone timeZone, String pattern) {
        SimpleDateFormat timeDateFormat = new SimpleDateFormat(pattern);
        timeDateFormat.setTimeZone(timeZone);
        return timeDateFormat.format(date);
    }*/


    /**
     * 将字符串格式的日期，按指定的时区进行解析成Date
     *
     * @param date
     * @param timeZone
     * @param pattern
     * @return
     */
    public static Date parseDate4TimeZoneCQ9(String date, TimeZone timeZone, String pattern) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        SimpleDateFormat timeDateFormat = new SimpleDateFormat(pattern);
        timeDateFormat.setTimeZone(timeZone);
        try {
            return timeDateFormat.parse(date);
        } catch (Exception e) {
            log.warn("TimeZoneUtils:parseDate4TimeZone() exception!!, date: {}, pattern: {}", date, pattern);
        }
        return null;
    }

    /**
     * 根据时区、格式、格式化当前日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate4TimeZone(Long date, TimeZone beforeTimeZone, TimeZone afterTimeZone, String pattern) {
        SimpleDateFormat timeDateFormat = new SimpleDateFormat(pattern);
        timeDateFormat.setTimeZone(beforeTimeZone);
        String format = timeDateFormat.format(date);
        return convertTimeZone2Str(format, pattern, beforeTimeZone, afterTimeZone);
    }


    /**
     * // 方法：将时间戳转换为 ISO 8601 格式字符串，时区为 UTC+0
     *
     * @param timestamp
     * @return
     */
    public static String convertTimestampToIso8601(long timestamp) {

        // 示例：时间戳（毫秒级，表示自 1970 年 1 月 1 日以来的毫秒数）
        // 将时间戳转换为 Instant 对象
        Instant instant = Instant.ofEpochMilli(timestamp);

        // 转换为 OffsetDateTime 并指定为 UTC+0 时区 (ZoneOffset.UTC)
        OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC);

        // 格式化为 ISO 8601 格式
        return offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * 将 ISO 8601 格式的时间字符串转换为时间戳（毫秒）
     *
     * @param iso8601DateTime ISO 8601 格式的时间字符串
     * @return 时间戳（自1970年1月1日 00:00:00 UTC以来的毫秒数）
     */
    public static long convertIso8601ToTimestamp(String iso8601DateTime) {
        if(StringUtils.isBlank(iso8601DateTime)){
            return 0L;
        }
        // 将 ISO 8601 字符串解析为 OffsetDateTime 对象
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(iso8601DateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // 将 OffsetDateTime 转换为时间戳（毫秒数）
        return offsetDateTime.toInstant().toEpochMilli();
    }

    /**
     * 获得前一天日期
     * @param timeStamp
     * @param zoneId
     * @return
     */
    public static String formatTimestampToPredateStr(Long timeStamp, String zoneId) {
        if (timeStamp == null || timeStamp <= 0) {
            return "";
        }
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(ZoneId.of(zoneId));
        // 定义时间格式
        ZonedDateTime previousDay = zonedDateTime.minusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 定义时间格式
        return previousDay.format(formatter);
    }

    /**
     * 获得当天开始的时间戳
     * @param timestamp
     * @param zoneId
     * @return
     */
    public static Long formatTimestampToCurTimeStamp(Long timestamp, String zoneId) {
        if (timestamp == null || timestamp <= 0) {
            return null;
        }
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of(zoneId));
        ZonedDateTime startOfDay = zonedDateTime.toLocalDate().atStartOfDay(zonedDateTime.getZone());
        return startOfDay.toInstant().toEpochMilli();
    }

    /**
     * 将时间戳字符串（如 "20250605091011"）转换为 6 位 Base36 编码
     */
    public static String encodeToBase36(String timestampStr) {
        try {

            if (StrUtil.isEmpty(timestampStr)){
                timestampStr = DateUtil.format(new Date(), patten_yyyMMddHHmmss);
            }

            long timestamp = Long.parseLong(timestampStr);
            String base36 = Long.toString(timestamp, 36).toUpperCase();

            // 保证结果为 6 位，取最后6位（可调）
            if (base36.length() >= 6) {
                return base36.substring(base36.length() - 6);
            } else {
                return String.format("%6s", base36).replace(' ', '0');  // 不足6位左补0
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp input: " + timestampStr);
        }
    }

    /**
     * 将时间戳转换为指定时区的时间格式 -
     *
     * @param timeStamp 时间戳（以秒为单位）
     * @return 格式化后的时间字符串
     */
    public static String formatTimestampToJdbDate(Long timeStamp) {
        if (timeStamp == null || timeStamp <= 0) {
            return "";
        }
        String zoneId = "UTC+8";
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(ZoneId.of(zoneId)).withSecond(0).withNano(0);
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten_jdb);
        // 定义时间格式
        return formatter.format(zonedDateTime);
    }


    public static String formatTimestampToSexyDate(Long timeStamp) {

        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(zoneId);

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(zonedDateTime);
    }

    public static Long formatDateStrToTimestamp(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        return zonedDateTime.toInstant().toEpochMilli();

    }

    public static String formatTimestampToGMT(Long timestamp, String pattern) {
        if (timestamp == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date(timestamp));
    }

    public static String formatTimestampToDBDate(Long timeStamp) {

        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timeStamp).atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten_yyyyMMddHHmmss);
        return formatter.format(zonedDateTime);
    }


}
