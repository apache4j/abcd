package com.cloud.baowang.activity.api.vo.redbag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "红包雨活动实时信息")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagRealTimeInfo {
    @Schema(description = "场次id")
    private String redbagSessionId;
    @Schema(description = "开始时间 时间戳")
    private Long startTime;
    @Schema(description = "结束时间 时间戳")
    private Long endTime;
    @Schema(description = "提前倒计时 秒")
    private Integer advanceTime;
    @Schema(description = "红包掉落时间")
    private Integer dropTime;
}
