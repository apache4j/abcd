package com.cloud.baowang.user.vo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param welfare_center_reward_type
 */
@AllArgsConstructor
@Getter
public enum BenefitTypeEnum {
    VIP_BENEFIT(0, "VIP福利"),
    EVENT_DISCOUNT(1, "活动优惠"),
    TASK_NOVICE_REWARD(2, "新手任务"),
    TASK_DAILY_REWARD(5, "每日任务"),
    TASK_WEEK_REWARD(6, "每周任务"),
    MEDAL_REWARD(3, "勋章奖励"),
    REBATE(7, "返水"),
    ;

    private final Integer code;
    private final String description;

}
