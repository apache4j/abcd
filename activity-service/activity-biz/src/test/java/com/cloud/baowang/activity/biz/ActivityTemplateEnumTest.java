package com.cloud.baowang.activity.biz;

import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;

import java.time.ZoneId;
import java.util.Set;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/25 20:40
 * @Version: V1.0
 **/
public class ActivityTemplateEnumTest {
    public static void main(String[] args) {
        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        availableZoneIds.forEach(System.out::println);

        String cronExpress=ActivityTemplateEnum.FREE_WHEEL.getCronByZoneId("Asia/Shanghai","1,2,5");
        System.err.println(cronExpress);

         cronExpress=ActivityTemplateEnum.FREE_WHEEL.getCronByZoneId("Australia/Sydney","1,2,5");
        System.err.println(cronExpress);

    }
}
