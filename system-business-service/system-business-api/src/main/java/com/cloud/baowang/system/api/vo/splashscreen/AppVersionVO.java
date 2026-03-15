package com.cloud.baowang.system.api.vo.splashscreen;

import io.swagger.v3.oas.annotations.media.Schema;

public class AppVersionVO {

    /**
     * 平台类型
     * {@link com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform}
     */
    @Schema(description = "平台类型")
    private Integer deviceTerminal;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "版本号")
    private String versionNumber;


    @Schema(description = "文件地址")
    private String fileUrl;
}
