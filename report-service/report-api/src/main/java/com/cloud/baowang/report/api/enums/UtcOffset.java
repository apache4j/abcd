package com.cloud.baowang.report.api.enums;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UtcOffset {
    UTC_MINUS_12("UTC-12", "Etc/GMT+12", 7),  // UTC-12 is 7 hours ahead of UTC-5
    UTC_MINUS_11("UTC-11", "Etc/GMT+11", 6),  // UTC-11 is 6 hours ahead of UTC-5
    UTC_MINUS_10("UTC-10", "Etc/GMT+10", 5),  // UTC-10 is 5 hours ahead of UTC-5
    UTC_MINUS_9("UTC-9", "Etc/GMT+9", 4),    // UTC-9 is 4 hours ahead of UTC-5
    UTC_MINUS_8("UTC-8", "Etc/GMT+8", 3),    // UTC-8 is 3 hours ahead of UTC-5
    UTC_MINUS_7("UTC-7", "Etc/GMT+7", 2),    // UTC-7 is 2 hours ahead of UTC-5
    UTC_MINUS_6("UTC-6", "Etc/GMT+6", 1),    // UTC-6 is 1 hour ahead of UTC-5
    UTC_MINUS_5("UTC-5", "America/New_York", 0), // UTC-5 itself
    UTC_MINUS_4("UTC-4", "America/Caracas", -1), // UTC-4 is 1 hour behind UTC-5
    UTC_MINUS_3("UTC-3", "America/Argentina/Buenos_Aires", -2), // UTC-3 is 2 hours behind UTC-5
    UTC_MINUS_2("UTC-2", "Etc/GMT+2", -3),  // UTC-2 is 3 hours behind UTC-5
    UTC_MINUS_1("UTC-1", "Etc/GMT+1", -4),  // UTC-1 is 4 hours behind UTC-5
    UTC_0("UTC", "UTC", -5), // UTC is 5 hours behind UTC-5
    UTC_1("UTC+1", "Europe/Paris", -6),  // UTC+1 is 6 hours behind UTC-5
    UTC_2("UTC+2", "Europe/Berlin", -7),  // UTC+2 is 7 hours behind UTC-5
    UTC_3("UTC+3", "Europe/Moscow", -8),  // UTC+3 is 8 hours behind UTC-5
    UTC_4("UTC+4", "Asia/Dubai", -9),    // UTC+4 is 9 hours behind UTC-5
    UTC_5("UTC+5", "Asia/Karachi", -10), // UTC+5 is 10 hours behind UTC-5
    UTC_6("UTC+6", "Asia/Dhaka", -11),   // UTC+6 is 11 hours behind UTC-5
    UTC_7("UTC+7", "Asia/Bangkok", -12), // UTC+7 is 12 hours behind UTC-5
    UTC_8("UTC+8", "Asia/Shanghai", -13), // UTC+8 is 13 hours behind UTC-5
    UTC_9("UTC+9", "Asia/Tokyo", -14),    // UTC+9 is 14 hours behind UTC-5
    UTC_10("UTC+10", "Australia/Sydney", -15), // UTC+10 is 15 hours behind UTC-5
    UTC_11("UTC+11", "Pacific/Noumea", -16), // UTC+11 is 16 hours behind UTC-5
    UTC_12("UTC+12", "Pacific/Fiji", -17);  // UTC+12 is 17 hours behind UTC-5



    private final String utcOffset;
    private final String mysqlTimeZone;
    private final Integer offsetFromUTCMinus5;


    // 根据 UTC 偏移量字符串获取枚举值
    public static String getMySQLTimeZoneByUtcOffset(String utcOffset) {
        for (UtcOffset offset : values()) {
            if (offset.utcOffset.equals(utcOffset)) {
                return offset.getMysqlTimeZone();
            }
        }
        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
    }

    // 根据 UTC 偏移量字符串获取枚举值
    public static UtcOffset getByUtc(String utcOffset) {
        for (UtcOffset offset : values()) {
            if (offset.utcOffset.equals(utcOffset)) {
                return offset;
            }
        }
        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
    }
}
