package com.cloud.baowang.common.core.constants;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/11/25 19:15
 * @description:
 */
public class TimeZoneUTCMapConstants {
    /**
     * 凌晨4点 所有时区对应的服务器UTC-5时间分布
     */
    public static final Map<Integer, String> timeZone4UTCMap = ImmutableMap.<Integer, String>builder()
            .put(10, "UTC-11")
            .put(9, "UTC-10")
            .put(8, "UTC-9")
            .put(7, "UTC-8")
            .put(6, "UTC-7")
            .put(5, "UTC-6")
            .put(4, "UTC-5")
            .put(3, "UTC-4")
            .put(2, "UTC-3")
            .put(1, "UTC-2")
            .put(0, "UTC-1")
            .put(23, "UTC")
            .put(22, "UTC+1")
            .put(21, "UTC+2")
            .put(20, "UTC+3")
            .put(19, "UTC+4")
            .put(18, "UTC+5")
            .put(17, "UTC+6")
            .put(16, "UTC+7")
            .put(15, "UTC+8")
            .put(14, "UTC+9")
            .put(13, "UTC+10")
            .put(12, "UTC+11")
            .put(11, "UTC+12")
            .build();

    public static final Map<Integer, String> timeZone2UTCMap = ImmutableMap.<Integer, String>builder()
            .put(8, "UTC-11")
            .put(7, "UTC-10")
            .put(6, "UTC-9")
            .put(5, "UTC-8")
            .put(4, "UTC-7")
            .put(3, "UTC-6")
            .put(2, "UTC-5")
            .put(1, "UTC-4")
            .put(0, "UTC-3")
            .put(23, "UTC-2")
            .put(22, "UTC-1")
            .put(21, "UTC")
            .put(20, "UTC+1")
            .put(19, "UTC+2")
            .put(18, "UTC+3")
            .put(17, "UTC+4")
            .put(16, "UTC+5")
            .put(15, "UTC+6")
            .put(14, "UTC+7")
            .put(13, "UTC+8")
            .put(12, "UTC+9")
            .put(11, "UTC+10")
            .put(10, "UTC+11")
            .put(9, "UTC+12")
            .build();
}
