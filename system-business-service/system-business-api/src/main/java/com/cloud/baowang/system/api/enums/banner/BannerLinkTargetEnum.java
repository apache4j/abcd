package com.cloud.baowang.system.api.enums.banner;

import lombok.Getter;

/**
 * system_param banner_link_target
 */
@Getter
public enum BannerLinkTargetEnum {
    INTERNAL_LINK(0, "内部链接"),
    GAME_ID(1, "游戏ID"),
    ACTIVITY_ID(2, "活动ID");

    private final Integer code;
    private final String description;

    BannerLinkTargetEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
