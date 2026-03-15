package com.cloud.baowang.activity.api.enums.job;

import lombok.Getter;

@Getter
public enum ActivityXxlJobEnum {
    AWARD_ACTIVE("activityAwardActive", "活动激活奖励"),
    REDBAG_START_PUSH("activityRedBagStartPush", "红包雨活动开始时间推送任务"),
    REDBAG_END_PUSH("activityRedBagEndPush", "红包雨活动结束时间推送任务");

    private final String handler;
    private final String jobDesc;
    // 构造方法
    ActivityXxlJobEnum(String handler, String jobDesc) {
        this.handler = handler;
        this.jobDesc = jobDesc;
    }
}
