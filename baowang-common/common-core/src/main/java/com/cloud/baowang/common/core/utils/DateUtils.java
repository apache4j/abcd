package com.cloud.baowang.common.core.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.nacos.api.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DateUtils {

    public static final String UTC5_TIME_ZONE = "UTC-5";

    //美东时间
    public static final TimeZone utc5TimeZone = TimeZone.getTimeZone(UTC5_TIME_ZONE);


    public static final String STRING_00 = " 00:00:00.000";
    public static final String STRING_59 = " 23:59:59.999";

    public static final String DATE_FORMAT_1 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_2 = "yyyy-MM";
    public static final String DATE_FORMAT_3 = "yyyy_MM";
    public static final String FULL_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FULL_FORMAT_2 = "MM/dd/yyyy HH:mm:ss";
    public static final String FULL_FORMAT_3 = "yyyy-MM-ddHH:mm:ss";
    public static final String FULL_FORMAT_4 = "yyyy-MM-dd HH.mm.ss";
    public static final String FULL_FORMAT_5 = "yyyy/MM/dd";
    public static final String FULL_FORMAT_6 = "yyyy/MM/dd HH:mm:ss";
    public static final String yyMMddHHmmss = "yyMMddHHmmss";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
    public static final String pattenT_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String pattenT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PATTEN_EASTERN_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS-04:00";

    public static final String PATTEN_EASTERN_TIME2 = "yyyy-MM-dd'T'HH:mm:ss-04:00";
    public static final String yyyyMMddTHHmmss = "yyyyMMdd'T'HHmmss";
    public static final String yyyyMMdd = "yyyyMMdd";

    public static final String yyyyMMddHH = "yyyyMMdd HH";
    public static final String HHmmss = "HH:mm:ss";
    public static final String HH = "HH";
    public static final String DD = "dd";

    public static final long MINUTES_SECOND = 60L;
    public static final long HOUR_MINUTE = 60L;
    public static final long SECOND_MILLS = 1000L;
    public static final long DAY_HOURS = 24L;
    public static final long DAY_MILLS = DAY_HOURS*HOUR_MINUTE*MINUTES_SECOND*SECOND_MILLS;

    public static final String DATE_FORMAT_JDB = "dd-MM-yyyy HH:mm:ss";


    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);


    public static String dateToyyyyMMddHHmmss(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMddHHmmss);
        return formatter.format(time);
    }

    public static String dateToyyMMddHHmmss(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat(yyMMddHHmmss);
        return formatter.format(time);
    }


    public static String dateToyyyyMMddHHmmssSSS(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMddHHmmssSSS);
        return formatter.format(time);
    }

    /**
     * 不指定轉換格式
     *
     * @param date 日期
     * @return 格式yyyy-MM-hh HH:mm:ss
     */
    public static String convertDateToString(final Date date) {
        return convertDateToString(date, yyyyMMddHHmmssSSS);
    }

    public static String convertDateToString(final Date date, final String pattern) {
        final DateTime dateTime;
        if (null == date) {
            dateTime = new DateTime();
        } else {
            dateTime = new DateTime(date);
        }

        final String returnVal;
        if (StringUtils.isBlank(pattern)) {
            returnVal = dateTime.toString(FULL_FORMAT_1);
        } else {
            returnVal = dateTime.toString(pattern);
        }

        return returnVal;
    }

    public static Date convertStringToDate(final String dateString, final String pattern) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);
        final DateTime dateTime = dateTimeFormatter.parseDateTime(dateString);
        return dateTime.toDate();
    }

    /**
     * 将日期字符串转换为日期格式
     *
     * @param dateString 日期字符串
     * @param pattern    格式化
     * @param zoneId     时区
     * @return 日期
     */
    public static Date convertStringToDate(final String dateString, final String pattern, final String zoneId) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);
        final DateTime dateTime = dateTimeFormatter.parseDateTime(dateString);
        return dateTime.toDateTime(DateTimeZone.forID(zoneId)).toDate();
    }

    /**
     * 去除時分秒
     *
     * @param date 日期
     * @return 去除時分秒的日期
     */
    public static Date truncationTime(final Date date) {
        final String dateString = convertDateToString(date, DATE_FORMAT_1);
        return convertStringToDate(dateString, DATE_FORMAT_1);
    }


    public static Date addDay(Date date, int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 24小时制
        cal.add(Calendar.DATE, num);
        return cal.getTime();
    }

    public static Long removeHHmmssToLong(final Date date) {
        final String dateString = convertDateToString(date, yyyyMMdd);
        return convertStringToDate(dateString, yyyyMMdd).getTime();
    }

    /**
     * 毫秒 转 时分秒
     *
     * @param ms 毫秒
     * @return String 时分秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
//        if (milliSecond > 0) {
//            sb.append(milliSecond + "毫秒");
//        }
        return sb.toString();
    }

    public static Date getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // 加一天
        return calendar.getTime(); // 获取明天的 Date
    }

    /**
     * 获取当前站点下当前时区的今天时间戳
     * @return
     */
    public static Long getTodayMinTime() {
        String currentTimezone = CurrReqUtils.getTimezone();
        TimeZone timeZone = TimeZone.getTimeZone(currentTimezone);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayZero = calendar.getTimeInMillis();
        return todayZero;
    }


    public static String formatToDateTime(Long time, String pattern) {
        return new SimpleDateFormat(pattern).format(time);
    }

    /**
     * 时间戳转换美东时间
     *
     * @param timeStamp
     * @param formatPattern
     * @return
     */
    public static String formatUTC5Date(Long timeStamp, String formatPattern) {
        java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern(formatPattern);
        // 创建一个 Instant 对象表示时间戳
        Instant instant = Instant.ofEpochMilli(timeStamp);

        // 将 Instant 转换为 UTC-5 时区的 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(utc5TimeZone.toZoneId());

        // 格式化 ZonedDateTime 并返回结果字符串
        return zonedDateTime.format(FORMATTER);
    }

    /**
     * 时间戳转换为指定时区时间
     *
     * @param timeStamp     时间戳
     * @param zoneId        时区
     * @return 转换结果
     */
    public static String formatDateByZoneId(Long timeStamp,  String zoneId) {
        if(!org.springframework.util.StringUtils.hasText(zoneId)){
            return "";
        }
        java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern(DateUtils.FULL_FORMAT_1);
        // 创建一个 Instant 对象表示时间戳
        Instant instant = Instant.ofEpochMilli(timeStamp);

        // 将 Instant 转换为 UTC-5 时区的 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(zoneId));

        // 格式化 ZonedDateTime 并返回结果字符串
        return zonedDateTime.format(FORMATTER);
    }

    /**
     * 时间戳转换为指定时区时间
     *
     * @param timeStamp     时间戳
     * @param zoneId        时区
     * @return 转换结果 yyyy-MM-dd格式
     */
    public static String formatDayByZoneId(Long timeStamp,  String zoneId) {
        if(!org.springframework.util.StringUtils.hasText(zoneId)){
            return "";
        }
        java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_1);
        // 创建一个 Instant 对象表示时间戳
        Instant instant = Instant.ofEpochMilli(timeStamp);

        // 将 Instant 转换为 UTC-5 时区的 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(zoneId));

        // 格式化 ZonedDateTime 并返回结果字符串
        return zonedDateTime.format(FORMATTER);
    }

    /**
     * 时间戳转换为指定时区时间
     *
     * @param timeStamp     时间戳
     * @param formatPattern 日期格式
     * @param zoneId        时区
     * @return 转换结果
     */
    public static String formatDateByZoneId(Long timeStamp, String formatPattern, String zoneId) {
        if(!org.springframework.util.StringUtils.hasText(zoneId)){
            return "";
        }
        if(timeStamp==null){
            return "";
        }
        java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern(formatPattern);
        // 创建一个 Instant 对象表示时间戳
        Instant instant = Instant.ofEpochMilli(timeStamp);

        // 将 Instant 转换为 UTC-5 时区的 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(zoneId));

        // 格式化 ZonedDateTime 并返回结果字符串
        return zonedDateTime.format(FORMATTER);
    }


    /**
     * @param startTime 日期
     * @param minute    分数
     * @return
     * @throws
     * @Title:
     * @Description: 在指定日期上添加指数定分
     * @date 2020/07/18 11:47:10
     */
    public static Date getAddMinute(Date startTime, int minute) {
        Calendar cal = null;
        cal = getCalendar(startTime);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    public static Date getAddHour(Date startTime, int hour) {
        Calendar cal = null;
        cal = getCalendar(startTime);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }

    /**
     * 给时间添加72小时
     *
     * @param startTime
     * @param hourNum
     * @return
     */
    public static Long addHour(long startTime, int hourNum) {
        Instant startTimeMills = Instant.ofEpochMilli(startTime);
        Instant afterTime = startTimeMills.plus(Duration.ofHours(hourNum));
        return afterTime.toEpochMilli();
    }

    /**
     * 给时间添加指定小时数，返回时间戳（毫秒）
     *
     * @param startTime 起始时间戳（毫秒）
     * @param hourNum   添加的小时数（支持小数）
     * @return 新的时间戳（毫秒）
     */
    public static Long addHour(long startTime, Double hourNum) {
        long addedMillis = (long) (hourNum * 60 * 60 * 1000);
        return startTime + addedMillis;
    }


    /**
     * 把Date转化成Calendar
     */
    public static Calendar getCalendar(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }


    public static Date parseDate(String dateStr, String timezone) {
        try {
            SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf8.setTimeZone(TimeZone.getTimeZone(timezone));
            return sdf8.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取两个日期之间的所有日期(格式:yyyy-MM-dd)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getBetweenDates(Long startTime, Long endTime, String timeZoneId) {
        List<String> result = new ArrayList<>();
        try {
            // 将时间戳转换为 Instant
            Instant instantStart = Instant.ofEpochMilli(startTime);
            // 将 Instant 和时区结合，转换为 ZonedDateTime
            ZonedDateTime tempStartZoneDateTime = ZonedDateTime.ofInstant(instantStart, ZoneId.of(timeZoneId));
            LocalDateTime tempStart=tempStartZoneDateTime.toLocalDateTime();
           //System.out.println(tempStart.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            //结束时间
            Instant instantEnd = Instant.ofEpochMilli(endTime);
            ZonedDateTime tempEndZoneDateTime = ZonedDateTime.ofInstant(instantEnd, ZoneId.of(timeZoneId));
            LocalDateTime tempEnd=tempEndZoneDateTime.toLocalDateTime();
           // System.out.println(tempEnd.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            while (tempStart.isBefore(tempEnd) || tempStart.equals(tempEnd)) {
                result.add(tempStart.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                tempStart=tempStart.plusDays(1);
            }
        } catch (Exception e) {
           logger.info("获取时间区间错误:{0}",e);
        }
        return result;
    }




    /**
     * 获取今天的开始时间
     */
    public static Long getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取今天的结束时间
     */
    public static Long getTodayEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 按照时区获取当前时间
     */
    public static Long getNowByTimeZone(String zoneIdStr) {
        // 指定时区
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        //ZoneId zoneId = utc5TimeZone.toZoneId();
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 计算昨天
        LocalDateTime nowDate = now.toLocalDateTime();
        return nowDate.atZone(zoneId).toInstant().getEpochSecond();
    }

    /**
     * 获取本月的开始时间 秒
     */
    public static Long getBeginOfMonth(String zoneIdStr) {
        // 指定时区
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 计算本月第一天
        LocalDate monthOneDay = now.toLocalDate().withDayOfMonth(1);
        // 获取昨天凌晨的 LocalDateTime 对象
        LocalDateTime monthStartDay = monthOneDay.atStartOfDay();
        return monthStartDay.atZone(zoneId).toInstant().getEpochSecond();
    }

    /**
     * 获取昨天的开始时间 秒
     */
    public static Long getYesTodayStartTime() {
        // 指定时区
        ZoneId zoneId = ZoneId.systemDefault();
        //ZoneId zoneId = utc5TimeZone.toZoneId();
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 计算昨天
        LocalDate yesterday = now.toLocalDate().minusDays(1);
        // 获取昨天凌晨的 LocalDateTime 对象
        LocalDateTime yesterdayMidnight = yesterday.atStartOfDay();
        return yesterdayMidnight.atZone(zoneId).toInstant().getEpochSecond();
    }

    /**
     * 获取今天的开始时间 时间戳
     *
     * @param zoneIdStr 传入参数：UTC-5
     */
    public static Long getTodayStartTime(String zoneIdStr) {
        // 使用传入的时区字符串创建 ZoneId
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 计算昨天
        LocalDate today = now.toLocalDate();
        // 获取昨天凌晨的 LocalDateTime 对象
        LocalDateTime todayMidnight = today.atStartOfDay();
        // 返回昨天开始时间的时间戳（毫秒
        return todayMidnight.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 获取今天天的结束时间戳
     */
    public static Long getTodayEndTime(String zoneIdStr) {
        // 使用传入的时区字符串创建 ZoneId
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 获取明日凌晨的 LocalDateTime 对象
        LocalDateTime tomorrowMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        tomorrowMidnight = tomorrowMidnight.minusNanos(1);
        return tomorrowMidnight.atZone(zoneId).toInstant().toEpochMilli();
    }



    /**
     * 获取昨天的开始时间 时间戳
     *
     * @param zoneIdStr 传入参数：UTC-5
     */
    public static Long getYesTodayStartTime(String zoneIdStr) {
        // 使用传入的时区字符串创建 ZoneId
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 计算昨天
        LocalDate yesterday = now.toLocalDate().minusDays(1);
        // 获取昨天凌晨的 LocalDateTime 对象
        LocalDateTime yesterdayMidnight = yesterday.atStartOfDay();
        // 返回昨天开始时间的时间戳（毫秒
        return yesterdayMidnight.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 获取昨天的结束时间戳
     */
    public static Long getYesTodayEndTime(String zoneIdStr) {
        // 使用传入的时区字符串创建 ZoneId
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 获取昨天凌晨的 LocalDateTime 对象
        LocalDateTime todayMidnight = now.toLocalDate().atStartOfDay();
        todayMidnight = todayMidnight.minusNanos(1);
        return todayMidnight.atZone(zoneId).toInstant().toEpochMilli();
    }


    /**
     * 获取昨天的结束时间 秒
     */
    public static Long getYesTodayEndTime() {
        // 指定时区
        ZoneId zoneId = ZoneId.systemDefault();
        // ZoneId zoneId = utc5TimeZone.toZoneId();
        // 获取当前时间的 ZonedDateTime 对象
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        // 获取昨天凌晨的 LocalDateTime 对象
        LocalDateTime todayMidnight = now.toLocalDate().atStartOfDay();
        todayMidnight = todayMidnight.minusNanos(1);
        return todayMidnight.atZone(zoneId).toInstant().getEpochSecond();
    }

    /**
     * 获取本周的周一的开始时间
     */
    public static long getMondayStartTimestamp() {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();

        // 根据当前日期获取周一的日期，并设置时间为 00:00:00
        LocalDateTime monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedMonday = monday.atZone(ZoneId.systemDefault());
        return zonedMonday.toInstant().toEpochMilli();
    }

    /**
     * 获取本周的最后一天的结束时间
     */
    public static long getMondayEndTimestamp() {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();

        // 获取本周的最后一天，即周日
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedEndOfWeek = endOfWeek.atZone(ZoneId.systemDefault());
        return zonedEndOfWeek.toInstant().toEpochMilli();
    }

    /**
     * 获取上个月的第一天的开始时间
     */
    public static long getStartDayBeforeMonthTimestamp(String timeZoneId) {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();
        // +1 代表上个月 -1 代表下个月 获取上个月的第一天，并设置时间为 00:00:00
        now = now.minusMonths(1);
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedFirstDay = firstDayOfMonth.atZone(ZoneId.of(timeZoneId));
        return zonedFirstDay.toInstant().toEpochMilli();
    }

    /**
     * 获取上个月的最后一天的结束时间
     */
    public static long getEndDayBeforeMonthTimestamp(String timeZoneId) {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();

        // 获取上个月的最后一天，并设置时间为 23:59:59.999
        now = now.minusMonths(1);
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedLastDay = lastDayOfMonth.atZone(ZoneId.of(timeZoneId));
        return zonedLastDay.toInstant().toEpochMilli();
    }


    /**
     * 获取本月的第一天的开始时间
     */
    public static long getStartDayMonthTimestamp(String timeZoneId) {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();

        // 获取当前月的第一天，并设置时间为 00:00:00
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedFirstDay = firstDayOfMonth.atZone(ZoneId.of(timeZoneId));
        return zonedFirstDay.toInstant().toEpochMilli();
    }

    /**
     * 获取本月的最后一天的结束时间
     */
    public static long getEndDayMonthTimestamp(String timeZoneId) {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();

        // 获取当前月的最后一天，并设置时间为 23:59:59.999
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedLastDay = lastDayOfMonth.atZone(ZoneId.of(timeZoneId));
        return zonedLastDay.toInstant().toEpochMilli();
    }



    /**
     * 获取本月的第一天的开始时间
     */
    public static Long getStartDayMonthTimestamp(Long assignTime,String timeZoneId) {
        ZoneId zoneId=ZoneId.of(timeZoneId);
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(assignTime),zoneId);

        // 获取当前月的第一天，并设置时间为 00:00:00
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedFirstDay = firstDayOfMonth.atZone(zoneId);
        return zonedFirstDay.toInstant().toEpochMilli();
    }


    /**
     * 获取上个月的第一天的开始时间
     */
    public static long getStartDayBeforeMonthTimestamp(Long assignTime,String timeZoneId) {
        ZoneId zoneId=ZoneId.of(timeZoneId);
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(assignTime),zoneId);
        // +1 代表上个月 -1 代表下个月 获取上个月的第一天，并设置时间为 00:00:00
        now = now.minusMonths(1);
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedFirstDay = firstDayOfMonth.atZone(zoneId);
        return zonedFirstDay.toInstant().toEpochMilli();
    }

    /**
     * 获取上个月的最后一天的结束时间
     */
    public static long getEndDayBeforeMonthTimestamp(Long assignTime,String timeZoneId) {
        ZoneId zoneId=ZoneId.of(timeZoneId);
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(assignTime),zoneId);

        // 获取上个月的最后一天，并设置时间为 23:59:59.999
        now = now.minusMonths(1);
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedLastDay = lastDayOfMonth.atZone(zoneId);
        return zonedLastDay.toInstant().toEpochMilli();
    }


    /**
     * 获取本月的最后一天的结束时间
     */
    public static long getEndDayMonthTimestamp(Long assignTime,String timeZoneId) {
        ZoneId zoneId=ZoneId.of(timeZoneId);
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(assignTime),zoneId);

        // 获取当前月的最后一天，并设置时间为 23:59:59.999
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedLastDay = lastDayOfMonth.atZone(zoneId);
        return zonedLastDay.toInstant().toEpochMilli();
    }

    /**
     * 获取上个月的第一天的开始时间
     */
    public static long getStartDayLastMonthTimestamp() {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth=now.minusMonths(1);
        // 获取当前月的第一天，并设置时间为 00:00:00
        LocalDateTime firstDayOfMonth = lastMonth.withDayOfMonth(1).with(LocalTime.MIN);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedFirstDay = firstDayOfMonth.atZone(ZoneId.systemDefault());
        return zonedFirstDay.toInstant().toEpochMilli();
    }

    /**
     * 获取上个月的最后一天的结束时间
     */
    public static long getEndDayLastMonthTimestamp() {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth=now.minusMonths(1);

        // 获取当前月的最后一天，并设置时间为 23:59:59.999
        LocalDateTime lastDayOfMonth = lastMonth.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedLastDay = lastDayOfMonth.atZone(ZoneId.systemDefault());
        return zonedLastDay.toInstant().toEpochMilli();
    }


    /**
     * 获取本月的第一天的开始时间
     */
    public static long getStartDayMonthTimestamp() {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();

        // 获取当前月的第一天，并设置时间为 00:00:00
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedFirstDay = firstDayOfMonth.atZone(ZoneId.systemDefault());
        return zonedFirstDay.toInstant().toEpochMilli();
    }

    /**
     * 获取本月的最后一天的结束时间
     */
    public static long getEndDayMonthTimestamp() {
        // 获取当前日期和时间的 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();

        // 获取当前月的最后一天，并设置时间为 23:59:59.999
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime zonedLastDay = lastDayOfMonth.atZone(ZoneId.systemDefault());
        return zonedLastDay.toInstant().toEpochMilli();
    }


    public static String getCalculateTypeDay() {


        return null;
    }


    /**
     * 两个时间戳相减 获取天数 是否相差超过90天
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean checkTime(final Long startTime, final Long endTime) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            return true;
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        if (startDate.after(endDate)) {
            return true;
        }
        int days = 90;
        cn.hutool.core.date.DateTime dateTime = DateUtil.offsetDay(startDate, days);
        int num = DateUtil.compare(endDate, dateTime);
        return num > 0;
    }

    public static boolean checkTime(final Long startTime, final Long endTime, int days) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            return true;
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        if (startDate.after(endDate)) {
            return true;
        }
        cn.hutool.core.date.DateTime dateTime = DateUtil.offsetDay(startDate, days);
        int num = DateUtil.compare(endDate, dateTime);
        return num > 0;
    }

    public static void main(String[] args) {
     /*  // checkTime(1730390400000L,1734278399999L,31);
        System.out.println("DateUtils.main" +getPreviousDayEndTime(1745637709458L,"UTC+8"));
        System.out.println("DateUtils.end " +getPreviousDayStartTime(1745637709458L,"UTC+8"));
    //  System.err.println(getBetweenDates(1730390400000L,System.currentTimeMillis(),"UTC+8"));
        System.out.println(getStartDayMillis(1733730513363L,"UTC+8"));
        //System.out.println(formatDateByZoneId(1732896000000L,"yyyy-MM-dd","UTC+8"));*/

    }
    public static String getYearAndWeek() {
        // 获取当前日期
        LocalDate now = LocalDate.now();

        // 获取当前年份
        int year = now.getYear();

        // 获取当前日期所属的周数
        int weekOfYear = now.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        // 返回 "年-第几周" 的格式
        return String.format("%d-%d", year, weekOfYear);
    }

    /**
     * 获取某个日期属于周几
     *
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public static int getWeekDay(String dateStr) {
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
        // 获取当天的星期几
        return localDate.getDayOfWeek().getValue();
    }

    /**
     * 根据时间戳转指定时区的周几,1=周一，7=周日
     * @param timestamp 时间戳
     * @param timeZone 时区
     * @return 周几
     */
    public static Integer getDayOfWeek(long timestamp, String timeZone) {
        try {
            // 将时间戳转换为 Instant
            Instant instant = Instant.ofEpochMilli(timestamp);

            // 将 Instant 和时区结合，转换为 ZonedDateTime
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(timeZone));

            // 返回该日期是星期几（1=星期一, 7=星期日）
            return zonedDateTime.getDayOfWeek().getValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time zone format: " + timeZone, e);
        }
    }

    /**
     * 获取指定时间的当天还剩多少时间
     */
    public static long getRemainingSecondsToday(LocalDateTime now) {
        // 获取今天的结束时间（23:59:59）
        LocalDateTime endOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);

        // 计算剩余的秒数
        Duration duration = Duration.between(now, endOfDay);
        return duration.getSeconds();
    }


    // 判断两个时间戳是否在同一天
    public static boolean isSameDay(Long startTime, Long endTime) {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTimeInMillis(startTime);
        endCalendar.setTimeInMillis(endTime);

        return startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) &&
                startCalendar.get(Calendar.DAY_OF_YEAR) == endCalendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 比较两个时区时差
     *
     * @param timeZoneId   当前时区
     * @param utc5TimeZone 美东时区
     * @return 返回小时
     */
    public static int diffZoneHour(String timeZoneId, String utc5TimeZone) {
        // 定义两个时区
        ZoneId zoneIdSelf = ZoneId.of(timeZoneId);
        ZoneId zoneIdUtc5 = ZoneId.of(utc5TimeZone);
        // 获取当前时间在这两个时区的时间
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        ZonedDateTime timeInZoneSelf = ZonedDateTime.of(localDateTime, zoneIdSelf);
        ZonedDateTime timeInZoneUtc5 = ZonedDateTime.of(localDateTime, zoneIdUtc5);
        logger.info("时差:{}", Duration.between(timeInZoneSelf.toInstant(), timeInZoneUtc5.toInstant()).getSeconds());
        long hoursDifference = Duration.between(timeInZoneSelf.toInstant(), timeInZoneUtc5.toInstant()).toHours();
        logger.info("时区2时间:{},时区1时间:{}", timeInZoneSelf.toLocalDateTime(), timeInZoneUtc5.toLocalDateTime());
        logger.info("时差:{}", hoursDifference);
        // 计算两个时区的时差
        return (int) hoursDifference;
    }


    /**
     * 根据年月 获取本月开始时间戳
     * @param yearMonth yyyy-MM 年月
     * @return
     */
    public static Long getYearMonthTime(String yearMonth){
        Long timestamp = System.currentTimeMillis();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_2);
            // 将字符串转换为日期
            Date date = sdf.parse(yearMonth);
            timestamp = date.getTime();
            return timestamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 按照开始时间戳 获取 当天的结束时间戳
     * @param startDayMillis 开始时间戳 毫秒 例如: 1725120000000
     * @return endDayMillis 结束时间戳 毫秒 例如:  1725206399000
     */
    public static Long getEndDayByStartTime(Long startDayMillis) {
        return startDayMillis+86399000;
    }

    /**
     * 将日期时间戳转换为每日开始时间戳
     * @param dayMillis 1731316617782
     * @param timeZoneId  时区
     * @return startDayMillis 1731254400000
     */
    public static Long getStartDayMillis(Long dayMillis,String timeZoneId) {
        LocalDateTime inputDayTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dayMillis),ZoneId.of(timeZoneId));
        // 获取当前日期，并设置时间为 00:00:00
        LocalDateTime starDayOfInput = inputDayTime.with(LocalTime.MIN);
        // 将 LocalDateTime 转换为 ZonedDateTime，再转换为时间戳
        ZonedDateTime startDayDateTime = starDayOfInput.atZone(ZoneId.of(timeZoneId));
        return startDayDateTime.toInstant().toEpochMilli();
    }


    /**
     * 时间戳转换为指定时区时间
     *
     * @param timeStamp     时间戳
     * @param zoneId        时区
     * @return 转换结果
     */
    public static String formatDateToStringByZoneId(Long timeStamp,  String zoneId) {
        if(!org.springframework.util.StringUtils.hasText(zoneId)){
            return "";
        }
        java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern(zoneId);
        // 创建一个 Instant 对象表示时间戳
        Instant instant = Instant.ofEpochMilli(timeStamp);

        // 将 Instant 转换为 GMT+8 时区的 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.ofHours(8));

        // 格式化 ZonedDateTime 并返回结果字符串
        return zonedDateTime.format(FORMATTER);
    }

    /**
     * 根据指定的时间戳获得前一天结束的时间戳
     * @param timestamp
     * @param zoneIdStr
     * @return
     */
    public static Long getPreviousDayEndTime(Long timestamp,String zoneIdStr) {

        ZoneId zoneId = ZoneId.of(zoneIdStr);

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId);

        // 获取该日期当天的凌晨
        LocalDateTime dayStart = zonedDateTime.toLocalDate().atStartOfDay();

        LocalDateTime previousDayEnd = dayStart.minusNanos(1);
        return previousDayEnd.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 根据指定的时间戳获得前一天开始的时间戳
     * @param timestamp
     * @param zoneIdStr
     * @return
     */
    public static Long getPreviousDayStartTime(Long timestamp,String zoneIdStr) {
        ZoneId zoneId = ZoneId.of(zoneIdStr);

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId);
        LocalDate previousDay = zonedDateTime.toLocalDate().minusDays(1);

        LocalDateTime previousDayStart = previousDay.atStartOfDay();

        return previousDayStart.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 根据指定的时间戳获得当天开始的时间戳
     * @param timestamp
     * @param zoneIdStr
     * @return
     */
    public static Long getDayStartTime(Long timestamp, String zoneIdStr) {
        ZoneId zoneId = ZoneId.of(zoneIdStr);

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId);

        LocalDateTime dayStart = zonedDateTime.toLocalDate().atStartOfDay();

        return dayStart.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 根据指定的时间戳获得当天结束的时间戳
     * @param timestamp
     * @param zoneIdStr
     * @return
     */
    public static Long getDayEndTime(Long timestamp, String zoneIdStr) {
        ZoneId zoneId = ZoneId.of(zoneIdStr);

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId);

        LocalDateTime dayEnd = zonedDateTime.toLocalDate().atTime(23, 59, 59, 999_000_000);  // 纳秒部分是999毫秒

        return dayEnd.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static long getStartOfDayBefore(long currentTimestamp, String timeZoneId, int daysBefore) {
        ZoneId zoneId = ZoneId.of(timeZoneId);

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(currentTimestamp).atZone(zoneId);
        LocalDate previousDay = zonedDateTime.toLocalDate().minusDays(daysBefore);

        LocalDateTime previousDayStart = previousDay.atStartOfDay();

        return previousDayStart.atZone(zoneId).toInstant().toEpochMilli();
    }

}

