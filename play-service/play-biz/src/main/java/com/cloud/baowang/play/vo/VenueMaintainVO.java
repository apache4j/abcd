package com.cloud.baowang.play.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "游戏平台维护中VO对象")
public class VenueMaintainVO {
    @Schema(title = "游戏平台名称")
    private String venueName;

    @Schema(title = "游戏平台code")
    private String venueCode;

    @Schema(title = "状态（ 1开启中 2 维护中 0 已禁用)")
    private Integer status;

    @Schema(title = "维护开始时间")
    private Long maintenanceStartTime;

    @Schema(title = "维护结束时间")
    private Long maintenanceEndTime;
}
