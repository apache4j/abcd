package com.cloud.baowang.common.core.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class CronUtil {

    /**
     * 生成每天凌晨一点执行的cron表达式(计算时区差异)
     *
     * @param targetTimeZone 当前站点时区
     * @return 计算差异后的表达式
     */
    public static String generateCronByCurrentAndTargetTimeZone(String targetTimeZone) {
        // 获取当前时区和目标时区的 ZoneId
        ZoneId currentZoneId = ZoneId.of("UTC-5");
        ZoneId targetZoneId = ZoneId.of(targetTimeZone);

        // 获取当前时间的 ZonedDateTime
        ZonedDateTime currentZonedDateTime = ZonedDateTime.now(currentZoneId);
        ZonedDateTime targetZonedDateTime = ZonedDateTime.now(targetZoneId);

        // 当前时区和目标时区的偏移（小时）
        int currentOffset = currentZonedDateTime.getOffset().getTotalSeconds() / 3600; // 当前时区偏移（小时）
        int targetOffset = targetZonedDateTime.getOffset().getTotalSeconds() / 3600; // 目标时区偏移（小时）

        // 计算相差的小时数
        int hourDifference = currentOffset - targetOffset;

        // 计算对应的 UTC 小时
        int utcHour = (24 + (1 - hourDifference)) % 24; // 0 表示整点执行

        // 生成对应的 Cron 表达式
        return String.format("0 0 %d * * ?", utcHour);
    }

    /**
     * 每周一凌晨一点10分执行定时任务
     *
     * @param currentTimeZone 站点时区
     * @return 站点时区对应的cron表达式
     */
    public static String generateWeeklyCronInUTC5(String currentTimeZone) {
        // 基准时区为 UTC-5
        ZoneId baseZoneId = ZoneId.of("UTC-5");

        // 获取基准时区的下一个周一凌晨一点十分的 ZonedDateTime
        ZonedDateTime baseZonedDateTime = ZonedDateTime.now(baseZoneId)
                .with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)) // 下一个或当前周一
                .withHour(1)
                .withMinute(10)
                .withSecond(0)
                .withNano(0);

        // 获取当前时区的偏移（秒）
        ZoneId currentZoneId = ZoneId.of(currentTimeZone);
        int currentOffset = currentZoneId.getRules().getOffset(baseZonedDateTime.toInstant()).getTotalSeconds();
        int baseOffset = baseZonedDateTime.getOffset().getTotalSeconds();

        // 计算与当前时区的时间差（小时）
        int hourDifference = (baseOffset - currentOffset) / 3600;
        ZonedDateTime targetTime = null;
        if (hourDifference > 0) {
            //当前时区比UTC-5慢
            // 将 UTC-5 的时间转换为当前时区时间
            targetTime = baseZonedDateTime.minusHours(hourDifference);
        } else if (hourDifference < 0) {
            //当前时区比UTC-5快
            // 当前时区比 UTC-5 快
            targetTime = baseZonedDateTime.plusHours(-hourDifference); // 减去正的小时差
        } else {
            //当前是UTC-5
            targetTime = baseZonedDateTime;
        }


        // 确保小时在 0-23 范围内
        int targetHour = targetTime.getHour();
        int week = targetTime.getDayOfWeek().getValue();

        // 返回 Cron 表达式
        return String.format("0 10 %d ? * %d", targetHour, week);
    }


    /**
     * 计算每月一号中午十二点 utc-5时区与站点时区差异
     *
     * @param currentTimeZone 站点时区
     * @return cron表达式
     */
    public static String generateMonthlyCron(String currentTimeZone) {
        // 获取当前日期和时间
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(currentTimeZone));

        // 基准时区为 UTC-5
        ZoneId baseZoneId = ZoneId.of("UTC-5");

        // 获取基准时区的下一个月一号中午12点的 ZonedDateTime
        ZonedDateTime baseZonedDateTime = now.withZoneSameInstant(baseZoneId)
                .with(TemporalAdjusters.firstDayOfNextMonth()) // 下个月的一号
                .withHour(12) // 设置为中午12点
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // 获取当前时区的偏移（小时）
        int currentOffset = now.getOffset().getTotalSeconds() / 3600;
        int baseOffset = baseZonedDateTime.getOffset().getTotalSeconds() / 3600;

        // 计算与当前时区的时间差
        int hourDifference = baseOffset - currentOffset;

        // 将 UTC-5 的时间转换为当前时区时间
        ZonedDateTime targetTime = baseZonedDateTime.minusHours(hourDifference);

        // 处理目标日期，确保不超过该月最大天数
        int maxDayOfMonth = targetTime.getMonth().length(targetTime.toLocalDate().isLeapYear());
        int targetDay = targetTime.getDayOfMonth();

        if (targetDay > maxDayOfMonth) {
            targetDay = maxDayOfMonth; // 调整为该月最大天数
        }

        targetTime = targetTime.withDayOfMonth(targetDay); // 更新目标时间的日期

        // 生成对应的 Cron 表达式
        int targetHour = (targetTime.getHour() + 24) % 24; // 确保小时在 0-23 范围内

        // 返回 Cron 表达式
        return String.format("0 0 %d %d * ?", targetHour, targetDay); // 每月的对应日期
    }


    /**
     * 生成 时分秒 表达式
     *
     * @param time hh:mm:ss 格式的时间
     * @return Cron 表达式
     */
    public static String generateCronExpression(String time) {
        String[] parts = time.split(":");
        String hour = parts[0];
        String minute = parts[1];
        String seconds = ObjUtil.isEmpty(parts[2]) ? "0" : parts[2];
        return String.format("%s %s %s * * ?", seconds, minute, hour);
    }

    /**
     * 生成 时分秒 表达式
     * 15:00:00 或者 15:00 都可以
     *
     * @param timeMap {"UTC+8":"15:00:00","UTC+9":"16:00:00"}
     * @return {"UTC+8":"CRON1","UTC+8":"CRON2"}
     */
    public static Map<String, String> generateCronExpression(Map<String, String> timeMap) {
        if (CollectionUtil.isEmpty(timeMap)) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> result = new HashMap<>();
        Set<String> time = timeMap.keySet();
        for (String currentTimeZone : time) {
            //utc-5时间
            String convertTime = timeMap.get(currentTimeZone);
            String[] parts = convertTime.split(":", 3);
            String hour = parts[0];
            String minute = parts[1];
            String seconds = parts.length > 2 ? parts[2] : "0";
            LocalDateTime localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(seconds));
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(currentTimeZone));
            ZonedDateTime utc5ZoneTime = zonedDateTime.withZoneSameInstant(TimeZoneUtils.utc5ZoneId);
            LocalDateTime utc5LocalDateTime = utc5ZoneTime.toLocalDateTime();
            String cron = String.format("%s %s %s * * ?", utc5LocalDateTime.getSecond(), utc5LocalDateTime.getMinute(), utc5LocalDateTime.getHour());
            result.put(currentTimeZone, cron);
        }
        return result;
    }

    /**
     * 生成每周固定几天的 cron 表达式
     *
     * @param daysOfWeek 指定的星期几 (0 = 周日, 1 = 周一, ..., 6 = 周六)
     * @param hour       指定执行任务的小时
     * @param minute     指定执行任务的分钟
     * @return cron 表达式
     */
    public static String generateWeeklyCronExpression(List<Integer> daysOfWeek, int hour, int minute) {
        StringBuilder cronExpression = new StringBuilder();

        // 拼接秒、分、时，秒固定为0
        cronExpression.append("0 ").append(minute).append(" ").append(hour).append(" ")
                .append("? * "); // 日字段用 ? 占位，月字段用 * 表示每月

        // 拼接星期几
        for (int i = 0; i < daysOfWeek.size(); i++) {
            cronExpression.append(daysOfWeek.get(i));
            if (i < daysOfWeek.size() - 1) {
                cronExpression.append(",");
            }
        }

        return cronExpression.toString();
    }

    /**
     * 生成每月固定几天的 cron 表达式
     *
     * @param daysOfMonth 指定的日期列表 (1 - 31)
     * @param hour        指定执行任务的小时
     * @param minute      指定执行任务的分钟
     * @return cron 表达式
     */
    public static String generateMonthlyCronExpression(List<Integer> daysOfMonth, int hour, int minute) {
        StringBuilder cronExpression = new StringBuilder();

        // 拼接秒、分、时，秒固定为0
        cronExpression.append("0 ").append(minute).append(" ").append(hour).append(" ");

        // 拼接日期
        for (int i = 0; i < daysOfMonth.size(); i++) {
            cronExpression.append(daysOfMonth.get(i));
            if (i < daysOfMonth.size() - 1) {
                cronExpression.append(",");
            }
        }

        cronExpression.append(" * ?"); // 月字段用 * 表示每月，星期字段用 ? 占位

        return cronExpression.toString();
    }

    public static void main(String[] args) {
        //String u_5 = generateWeeklyCronInUTC5("UTC-5");
        //System.out.println("UTC-5: "+u_5);
        //String s = generateWeeklyCronInUTC5("UTC+5");
        //System.out.println("UTC+5: " + s);
        //String utc = generateWeeklyCronInUTC5("UTC");
        //System.out.println("UTC: "+utc);
        //String utc_10 = generateWeeklyCronInUTC5("UTC-10");
        //System.out.println("utc_10: "+utc_10);
        String utc_5 = generateMonthlyCron("UTC-5");
        System.out.println("UTC-5:  "+utc_5);
        String utc_10 = generateMonthlyCron("UTC-10");
        System.out.println("UTC-10: "+utc_10);
        String utc = generateMonthlyCron("UTC");
        System.out.println("UTC:    "+utc);
        String utc5 = generateMonthlyCron("UTC+5");

        System.out.println("UTC+5:  "+utc5);
        String utc12 = generateMonthlyCron("UTC+12");
        System.out.println("UTC+12: "+utc12);

    }
}
