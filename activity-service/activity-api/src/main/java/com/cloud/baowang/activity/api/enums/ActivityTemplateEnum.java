package com.cloud.baowang.activity.api.enums;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * system param activity_template
 * <p>
 * 活动模版类型
 */
@Getter
@AllArgsConstructor
public enum ActivityTemplateEnum {

    // 定义枚举常量

    FIRST_DEPOSIT("FIRST_DEPOSIT", "首存活动", false, "",ActivityTemplateRewardEnum.REWARD_AMOUNT,"10", "6"),
    SECOND_DEPOSIT("SECOND_DEPOSIT", "次存活动", false, "",ActivityTemplateRewardEnum.REWARD_AMOUNT,"11", "7"),
    FREE_WHEEL("FREE_WHEEL", "免费旋转", true, "0 40 0 ? * *",ActivityTemplateRewardEnum.WHEEL_NUM,"12", "8"),
    // 0 50 0 * * * ? 秒/分钟/小时/日/月/周/年
    ASSIGN_DAY("ASSIGN_DAY", "指定日期存款", true, "0 50 0 ? * *",ActivityTemplateRewardEnum.REWARD_WHEEL,"13", "8"),
    LOSS_IN_SPORTS("LOSS_IN_SPORTS", "体育负盈利", true, "0 0 1 * * ?",ActivityTemplateRewardEnum.REWARD_AMOUNT,"14", "9"),
    SPIN_WHEEL("SPIN_WHEEL", "转盘", false, "0 20 1 * * ?",ActivityTemplateRewardEnum.WHEEL_NUM,"15", "13"),
    RED_BAG_RAIN("RED_BAG_RAIN", "红包雨", false, "",ActivityTemplateRewardEnum.REWARD_AMOUNT,"16", "11"),
    DAILY_COMPETITION("DAILY_COMPETITION", "每日竞赛", true, "0 10 1 * * ?",ActivityTemplateRewardEnum.REWARD_WHEEL,"17", "10"),
    // 签到没有定时任务
    CHECKIN("CHECKIN", "签到", false, "",ActivityTemplateRewardEnum.REWARD_WHEEL,"18", "12"),

    STATIC("STATIC", "静态活动", false, "",ActivityTemplateRewardEnum.REWARD_AMOUNT,"19", "6"),
    ;

    private final String type;
    private final String name;
    private final boolean needJobFlag;
    //表达式
    private final String cronStr;

    private  final ActivityTemplateRewardEnum templateRewardEnum;
    //序号 不重复
    private final String serialNo;
    private final String accountCoinType;

    //返回可以 调用 参与接口的活动
    public static List<String> getToActivityTemplateList() {
        return Lists.newArrayList(FIRST_DEPOSIT.getType(),
                SECOND_DEPOSIT.getType(),
                ASSIGN_DAY.getType(),
                LOSS_IN_SPORTS.getType(),
                SPIN_WHEEL.getType(),
                FREE_WHEEL.getType());
    }

    //配置周期的活动模板
    public static List<String> getToActivityTemplateWeek() {
        return Lists.newArrayList(FREE_WHEEL.getType(),
                ASSIGN_DAY.getType());
    }

    public static ActivityTemplateEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        ActivityTemplateEnum[] types = ActivityTemplateEnum.values();
        for (ActivityTemplateEnum type : types) {
            if (code.equals(type.getType())) {
                return type;
            }
        }
        return null;
    }

    public static String parseNameByCode(String code) {
        if (null == code) {
            return null;
        }
        ActivityTemplateEnum[] types = ActivityTemplateEnum.values();
        for (ActivityTemplateEnum activityTemplateEnum : types) {
            if (code.equals(activityTemplateEnum.getType())) {
                return activityTemplateEnum.getName();
            }
        }
        return null;
    }

    public static ActivityTemplateEnum parseRewardNameByCode(String code) {
        if (null == code) {
            return null;
        }
        ActivityTemplateEnum[] types = ActivityTemplateEnum.values();
        for (ActivityTemplateEnum activityTemplateEnum : types) {
            if (code.equals(activityTemplateEnum.getType())) {
                return activityTemplateEnum;
            }
        }
        return null;
    }

    public static List<ActivityTemplateEnum> getList() {
        return Arrays.asList(values());
    }

    public String getCronByZoneId(String timeZoneId, String weekDays) {
        if (!StringUtils.hasText(this.getCronStr())) {
            return this.getCronStr();
        }
        String[] cronArray = this.getCronStr().split(CommonConstant.BLANK_STRING);
        //周
        if (StringUtils.hasText(weekDays)) {
            cronArray[5] = weekDays;
        }
        if (!timeZoneId.equalsIgnoreCase(DateUtils.UTC5_TIME_ZONE)) {
            //System.out.println("当前cron表达式:"+this.getCronStr());
            int hourDiff = DateUtils.diffZoneHour(timeZoneId, DateUtils.UTC5_TIME_ZONE);
            // System.out.println("时差:"+hourDiff);
            int hourNum = Integer.valueOf(cronArray[2]);
            long actualHour = 24 - hourDiff+hourNum;
            if(actualHour>=24){
                actualHour=actualHour-24;
            }
            //小时
            cronArray[2] = String.valueOf(actualHour);
        }
        return String.join(CommonConstant.BLANK_STRING, cronArray);
    }

    /*public static void main(String[] args) {
        System.err.println(ActivityTemplateEnum.FREE_WHEEL.getCronByZoneId("UTC-4","1,2,3,4,5"));
        System.err.println(ActivityTemplateEnum.ASSIGN_DAY.getCronByZoneId("UTC-4","1,2,3,4,5"));
    }*/
}
