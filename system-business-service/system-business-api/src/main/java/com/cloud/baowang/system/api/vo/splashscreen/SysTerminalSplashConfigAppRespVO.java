package com.cloud.baowang.system.api.vo.splashscreen;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@I18nClass
@Schema(description = "终端闪屏配置对象，包含闪屏页的相关信息")
public class SysTerminalSplashConfigAppRespVO implements Serializable {

    private static final long serialVersionUID = 1L;


    @Schema(description = "站点CODE")
    private String siteCode;

    @Schema(description = "闪屏页的名称")
    private String name;

    @Schema(description = "闪屏图i18")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String bannerUrl;

    @Schema(description = "闪屏图")
    private String bannerUrlFileUrl;

    @Schema(description = "闪屏页显示时长，以秒为单位")
    private Integer displayDuration;

}

