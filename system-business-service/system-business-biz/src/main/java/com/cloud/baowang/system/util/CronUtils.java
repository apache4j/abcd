package com.cloud.baowang.system.util;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * @className: CronUtils
 * @author: wade
 * @description: corn 生成表达式
 * @date: 2024/6/13 10:52
 */
public class CronUtils {


    /**
     * 生成星期几的Cron表达式
     * 传入（星期几，时间） ，生成cron表达式，如果大于7，就默认是是周日
     *
     * @param dayOfWeek 星期几，1代表星期一，2代表星期二，以此类推
     * @param time      时间，格式为17:25
     * @return 例如：传入(3, "17:25")，输出：0 25 17 ? * WED
     */
    public static String generateCronExpressionForDayOfWeek(int dayOfWeek, String time) {
        // 解析时间
        LocalTime localTime = LocalTime.parse(time);
        int minute = localTime.getMinute();
        int hour = localTime.getHour();
        // 获取星期几的枚举值
        DayOfWeek dow = DayOfWeek.of(dayOfWeek);
        // 如果dayOfWeek大于7，默认设为7（周日）
        if (dayOfWeek > 7) {
            dayOfWeek = 7;
        }
        // 构建Cron表达式
        return String.format("0 %d %d ? * %s", minute, hour, dow.name().substring(0, 3));
    }

    // 方法二：生成每月的第几天的Cron表达式

    /**
     * 方法二： 传入（一个月的第几天5，时间） ，生成cron表达式
     * 例如： 传入("2","18:30") ，输出：0 30 18 2 * ?
     * 如果大于28，则对于其他月份都是28。则默认是每个月最后一天
     *
     * @param dayOfMonth 2
     * @param time       18:30
     * @return 输出：0 30 18 2 * ?
     */
    public static String generateCronExpressionForDayOfMonth(int dayOfMonth, String time) {
        // 解析时间
        LocalTime localTime = LocalTime.parse(time);
        int minute = localTime.getMinute();
        int hour = localTime.getHour();
        // 如果dayOfMonth大于28，则设为L表示每个月最后一天
        String dayExpression = (dayOfMonth > 28) ? "L" : String.valueOf(dayOfMonth);

        // 构建Cron表达式
        return String.format("0 %d %d %s * ?", minute, hour, dayExpression);
    }

    // 测试方法
    public static void main(String[] args) {
        // 测试方法一
        System.out.println("方法一测试：");
        System.out.println(generateCronExpressionForDayOfWeek(3, "17:25")); // 输出：0 25 17 ? * WED

        // 测试方法二
        System.out.println("方法二测试：");
        System.out.println(generateCronExpressionForDayOfMonth(2, "18:30")); // 输出：0 30 18 2 * ?
    }
}
