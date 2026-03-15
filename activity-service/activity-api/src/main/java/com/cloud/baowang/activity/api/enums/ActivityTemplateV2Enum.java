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
public enum ActivityTemplateV2Enum {

    FIRST_DEPOSIT_V2("FIRST_DEPOSIT_V2", "首存活动", false, "", ActivityTemplateRewardEnum.REWARD_AMOUNT, "10","6"),
    SECOND_DEPOSIT_V2("SECOND_DEPOSIT_V2", "次存活动", false, "", ActivityTemplateRewardEnum.REWARD_AMOUNT, "11","7"),
    ASSIGN_DAY_V2("ASSIGN_DAY_V2", "指定日期存款", true, "0 55 0 ? * *", ActivityTemplateRewardEnum.REWARD_WHEEL, "13","8"),
    NEW_HAND("NEW_HAND", "新手活动", true, "0 50 0 ? * *", ActivityTemplateRewardEnum.REWARD_AMOUNT, "14","31"),
    STATIC_V2("STATIC_V2", "静态活动", false, "", ActivityTemplateRewardEnum.REWARD_AMOUNT, "19","102"),
    CONTEST_PAYOUT_V2("CONTEST_PAYOUT_V2","赛事包赔",false,"",null,"20", "32"),
    ;

    private final String type;
    private final String name;
    private final boolean needJobFlag;
    //表达式
    private final String cronStr;

    private final ActivityTemplateRewardEnum templateRewardEnum;
    //序号 不重复
    private final String serialNo;

    private final String accountCoinType;


    //返回可以 调用 参与接口的活动
    public static List<String> getToActivityTemplateList() {
        return Lists.newArrayList(FIRST_DEPOSIT_V2.getType(),
                SECOND_DEPOSIT_V2.getType(),
                ASSIGN_DAY_V2.getType());
    }

    //配置周期的活动模板
    public static List<String> getToActivityTemplateWeek() {
        return Lists.newArrayList(ASSIGN_DAY_V2.getType());
    }

    public static ActivityTemplateV2Enum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        ActivityTemplateV2Enum[] types = ActivityTemplateV2Enum.values();
        for (ActivityTemplateV2Enum type : types) {
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
        ActivityTemplateV2Enum[] types = ActivityTemplateV2Enum.values();
        for (ActivityTemplateV2Enum activityTemplateEnum : types) {
            if (code.equals(activityTemplateEnum.getType())) {
                return activityTemplateEnum.getName();
            }
        }
        return null;
    }

    public static ActivityTemplateV2Enum parseRewardNameByCode(String code) {
        if (null == code) {
            return null;
        }
        ActivityTemplateV2Enum[] types = ActivityTemplateV2Enum.values();
        for (ActivityTemplateV2Enum activityTemplateEnum : types) {
            if (code.equals(activityTemplateEnum.getType())) {
                return activityTemplateEnum;
            }
        }
        return null;
    }

    public static List<ActivityTemplateV2Enum> getList() {
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
            long actualHour = 24 - hourDiff + hourNum;
            if (actualHour >= 24) {
                actualHour = actualHour - 24;
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
