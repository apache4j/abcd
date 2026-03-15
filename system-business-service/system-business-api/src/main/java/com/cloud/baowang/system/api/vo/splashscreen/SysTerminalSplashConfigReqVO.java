package com.cloud.baowang.system.api.vo.splashscreen;

import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "终端闪屏配置对象，包含闪屏页的相关信息")
public class SysTerminalSplashConfigReqVO implements Serializable {

    @Schema(description = "闪屏页的唯一标识符")
    private String id;

    @Schema(description = "站点CODE")
    private String siteCode;

    @Schema(description = "闪屏页的名称")
    private String name;

    @Schema(description = "显示终端  3-IOS_APP, 5-Andriod_APP")
    private String terminal;

    @Schema(description = "闪屏页的状态，1表示启用，0表示禁用")
    private Integer status;

    @Schema(description = "闪屏多语言数组")
    private List<I18nMsgFrontVO> bannerUrlList;

    @Schema(description = "闪屏页的时效类型，0:LimitTime 表示限时，1:Permanent 表示永久，数据字典：validity_period")
    private String validityPeriod;

    @Schema(description = "闪屏页的开始时间，仅在时效为 LimitTime 时有效")
    private Long startTime;

    @Schema(description = "闪屏页的结束时间，仅在时效为 LimitTime 时有效")
    private Long endTime;

    /*@Schema(description = "闪屏页显示时长，以秒为单位")
    private Integer displayDuration;*/

    @Schema(description = "记录的创建用户" )
    private String creator;

    @Schema(description = "记录的创建时间")
    private Long createdTime;

    @Schema(description = "记录的更新用户")
    private String updater;

    @Schema(description = "记录的最后更新时间")
    private Long updatedTime;

}

