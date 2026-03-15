package com.cloud.baowang.system.api.enums.banner;

import lombok.Getter;

/**
 * system_param banner_duration code
 */
@Getter
public enum BannerDuration {
    LIMITED_TIME(0, "限时"),
    LONG_TERM(1, "长期");

    private final Integer code;
    private final String description;

    BannerDuration(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
