package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system param activity_template_reward
 * <p>
 * 福利类型
 */
@Getter
public enum ActivityTemplateRewardEnum {

    // 定义枚举常量
    REWARD_AMOUNT("REWARD_AMOUNT", "彩金" ),
    WHEEL_NUM("WHEEL_NUM", "旋转次数"),
    REWARD_WHEEL("REWARD_WHEEL", "彩金和旋转次数");



    private final String type;
    private final String name;

    // 构造函数
    ActivityTemplateRewardEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }



    public static ActivityTemplateRewardEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        ActivityTemplateRewardEnum[] types = ActivityTemplateRewardEnum.values();
        for (ActivityTemplateRewardEnum type : types) {
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
        ActivityTemplateRewardEnum[] types = ActivityTemplateRewardEnum.values();
        for (ActivityTemplateRewardEnum activityTemplateEnum : types) {
            if (code.equals(activityTemplateEnum.getType())) {
                return activityTemplateEnum.getName();
            }
        }
        return null;
    }

    public static List<ActivityTemplateRewardEnum> getList() {
        return Arrays.asList(values());
    }

}
