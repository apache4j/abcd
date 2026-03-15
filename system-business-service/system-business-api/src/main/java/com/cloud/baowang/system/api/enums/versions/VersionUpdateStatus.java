package com.cloud.baowang.system.api.enums.versions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param version_update_status code
 */
@AllArgsConstructor
@Getter
public enum VersionUpdateStatus {
    LATEST_VERSION(0, "最新版本"),
    PROMPT_UPGRADE(1, "提示升级"),
    MANDATORY_UPGRADE(2, "强制升级");

    private final int code;
    private final String description;

}
