package com.cloud.baowang.system.api.vo.splashscreen;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


import io.swagger.v3.oas.annotations.media.Schema;

@Data
@I18nClass
@Schema(description = "终端闪屏配置对象，包含闪屏页的相关信息")
public class SysTerminalSplashConfigRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "闪屏页的唯一标识符")
    private String id;

    @Schema(description = "站点CODE")
    private String siteCode;

    @Schema(description = "闪屏页的名称")
    private String name;

    @Schema(description = "显示终端  3-IOS_APP, 5-Andriod_APP")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String terminal;
    @Schema(description = "显示终端  3-IOS_APP, 5-Andriod_APP")
    private String terminalText;

    @Schema(description = "闪屏页的状态，1表示启用，0表示禁用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    /* 状态文本 */
    @Schema(description = "状态")
    private String statusText;

    @Schema(description = "闪屏图i18")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String bannerUrl;

    @Schema(description = "闪屏图")
    private String bannerUrlFileUrl;

    @Schema(description = "闪屏页的时效类型，0:LimitTime 表示限时，1:Permanent 表示永久，数据字典：validity_period")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VALIDITY_PERIOD)
    private String validityPeriod;

    @Schema(description = "闪屏页的时效类型，LimitTime 表示限时，Permanent 表示永久")
    private String validityPeriodText;


    @Schema(description = "闪屏页的开始时间，仅在时效为 LimitTime 时有效")
    private Long startTime;

    @Schema(description = "闪屏页的结束时间，仅在时效为 LimitTime 时有效")
    private Long endTime;


    @Schema(description = "是否允许启用 启用按钮 0.否,1.是 -- (启用按钮置灰)")
    private Integer isEnable;

    @Schema(description = "闪屏页显示时长，以秒为单位")
    private Integer displayDuration;

    @Schema(description = "记录的创建用户")
    private String creator;

    @Schema(description = "记录的创建时间")
    private Long createdTime;

    @Schema(description = "记录的更新用户")
    private String updater;

    @Schema(description = "记录的最后更新时间")
    private Long updatedTime;
}

