package com.cloud.baowang.activity.api.vo.redbag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "红包雨场次信息")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagSessionInfoVO {
    @Schema(description = "红包场次id")
    private String redbagSessionId;
    @Schema(description = "开始时间 时间戳")
    private Long startTime;
    @Schema(description = "结束时间 时间戳")
    private Long endTime;
    @Schema(description = "结束标识  0 未开始 1 进行中 2 已结束")
    private Integer status;
}
