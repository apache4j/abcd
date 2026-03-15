package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param  agent_image_type
 */
@Getter
@AllArgsConstructor
public enum AgentImageTypeEnum {
    COMPREHENSIVE(1, "综合推广图"),
    SPORTS(2, "体育推广图"),
    LIVE(3, "真人推广图"),
    E_SPORTS(4, "电竞推广图"),
    LOTTERY(5, "彩票推广图"),
    BOARD_GAMES(6, "棋牌推广图"),
    EVENTS(7, "活动推广图");
    private final Integer type;
    private final String name;
}
