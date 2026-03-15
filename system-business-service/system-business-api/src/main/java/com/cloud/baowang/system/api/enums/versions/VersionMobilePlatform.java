package com.cloud.baowang.system.api.enums.versions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param version_mobile_platform code
 */
@AllArgsConstructor
@Getter
public enum VersionMobilePlatform {
    IOS(0, "iOS"),
    ANDROID(1, "android");

    private final int code;
    private final String description;

}
