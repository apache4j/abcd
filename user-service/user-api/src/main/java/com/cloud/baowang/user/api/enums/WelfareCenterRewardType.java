package com.cloud.baowang.user.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param welfare_center_reward_type
 *
 * 4.是勋章宝箱.固定写死,所以不需要定义在这个枚举这里
 */
@AllArgsConstructor
@Getter
public enum WelfareCenterRewardType {
    VIP_BENEFIT(0, "VIP福利"),
    EVENT_DISCOUNT(1, "活动优惠"),
    TASK_NOVICE_REWARD(2, "新手任务"),
    MEDAL_REWARD(3, "勋章奖励"),

    TASK_DAILY_REWARD(5, "每日任务"),
    TASK_WEEK_REWARD(6, "每周任务"),

    REBATE_REWARD(7, "返水"),


    ;

    private final Integer code;
    private final String value;

}
