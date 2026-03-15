package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 会员福利
 * 0 VIP奖励  1 任务奖励 2 勋章奖励  3 优惠活动 4 转盘奖励
 * system_param 里的 agent_user_benefit
 */
@Getter
public enum AgentUserBenefitEnum {

    VIP_REWARD(0, "VIP奖励"),
    TASK_REWARD(1, "任务奖励"),
    MEDAL_REWARD(2, "勋章奖励"),
    DISCOUNT_ACTIVITY(3, "优惠活动"),
    SPIN_REWARD(4, "转盘奖励"),

    SPORT_RETURN(5, "体育返水"),

    E_SPORT_RETURN(6, "电竞返水"),

    VEDIO_RETURN(7, "视讯返水"),

    CHESS_RETURN(8, "棋牌返水"),

    ELECTRONIC_RETURN(9, "电子返水"),

    LOTTERY_RETURN(10, "彩票返水"),

    COCKFIGHTING_RETURN(11, "斗鸡返水"),

    FISHING_RETURN(12, "捕鱼返水"),

    MARBLES_RETURN(13, "娱乐返水"),
    ;

    private final Integer code;
    private final String name;

    AgentUserBenefitEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentUserBenefitEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentUserBenefitEnum[] types = AgentUserBenefitEnum.values();
        for (AgentUserBenefitEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentUserBenefitEnum> getList() {
        return Arrays.asList(values());
    }
}
