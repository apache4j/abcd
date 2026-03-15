package com.cloud.baowang.system.api.enums.banner;

import lombok.Getter;

/**
 * system_param banner_target
 */
@Getter
public enum BannerTarget {
    INTERNAL(0, "内部"),
    GAME(1, "游戏");

    private final int code;
    private final String description;

    BannerTarget(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
