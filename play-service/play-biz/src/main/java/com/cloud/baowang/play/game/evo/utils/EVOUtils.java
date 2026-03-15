package com.cloud.baowang.play.game.evo.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @className: EVOUtils
 * @author: wade
 * @description: 格式
 * @date: 14/8/25 18:50
 */
public class EVOUtils {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    /**
     * 将UTC零时区时间字符串转换为毫秒时间戳
     *
     * @param utcTime UTC时间字符串，例如：2017-02-08T13:07:40.222Z
     * @return 毫秒时间戳
     */
    public static long utcToTimestamp(String utcTime) {
        return Instant.parse(utcTime).toEpochMilli();
    }

    /**
     * 根据传入 UTC 时间字符串与当前时间比较，返回符合规则的 UTC 时间字符串
     * <p>
     * 规则：
     * 1. 如果传入时间和当前时间在同一天（UTC零时区），返回当前时间的UTC字符串
     * 2. 如果不是同一天：
     * - 如果传入时间的第二天零点 <= 当前时间，返回该第二天零点的UTC字符串
     * - 否则返回今天零点的UTC字符串
     *
     * @param utcTime UTC时间字符串，例如：2017-02-08T13:07:40.222Z
     * @return UTC时间字符串，例如：2017-02-08T13:07:40.222Z
     */
    public static String processUtcTimestamp(String utcTime) {
        // 当前 UTC 时间
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);

        // 解析传入的 UTC 字符串为 ZonedDateTime
        ZonedDateTime inputUtc = Instant.parse(utcTime).atZone(ZoneOffset.UTC);

        // 传入时间当天的 UTC 零点
        ZonedDateTime inputDayStart = inputUtc.toLocalDate().atStartOfDay(ZoneOffset.UTC);

        // 传入时间的第二天 UTC 零点
        ZonedDateTime nextDayStart = inputDayStart.plusDays(1);

        // 今天 UTC 零点
        ZonedDateTime todayStart = nowUtc.toLocalDate().atStartOfDay(ZoneOffset.UTC);

        if (inputUtc.toLocalDate().equals(nowUtc.toLocalDate())) {
            // 同一天，返回当前 UTC 时间
            return utcTime;
        } else if (!nextDayStart.isAfter(nowUtc)) {
            // 不是同一天，且传入时间的第二天零点 <= 当前时间
            return nextDayStart.toInstant().toString();
        } else {
            // 否则返回今天零点（UTC）
            return todayStart.toInstant().toString();
        }
    }

    /**
     * 将给定的时间戳转换为 UTC 零时区对应的时间字符串。
     *
     * @param timestamp 毫秒时间戳
     * @return UTC零时区零点时间字符串，例如：2017-02-08T00:00:00.000Z
     */
    public static String timestampToUtcZero(long timestamp) {
        // 将时间戳转为 UTC 时区的 ZonedDateTime 并格式化
        return FORMATTER.format(Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC));
    }


    public static void main(String[] args) {
        System.out.println(timestampToUtcZero(System.currentTimeMillis()));
        System.out.println(utcToTimestamp("2025-08-16T11:43:48.252Z"));
        String nowStr = Instant.now().toString();
        System.out.println("现在（同一天）：" + processUtcTimestamp(nowStr));

        // 昨天的 UTC 字符串
        String yesterdayStr = Instant.now().minus(Duration.ofDays(1)).toString();
        System.out.println("昨天：" + processUtcTimestamp(yesterdayStr));

        // 前天的 UTC 字符串
        String beforeYesterdayStr = Instant.now().minus(Duration.ofDays(2)).toString();
        System.out.println("前天：" + processUtcTimestamp(beforeYesterdayStr));
        // 解析游戏列表与注单列表

    }

}
