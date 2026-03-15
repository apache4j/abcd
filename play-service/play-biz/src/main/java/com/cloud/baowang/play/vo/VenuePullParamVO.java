package com.cloud.baowang.play.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "拉单参数")
public class VenuePullParamVO {
    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;

    @Schema(title = "版本值")
    private String versionKey;

    /**
     * false = 手动拉单,true = 自动拉单
     */
    private Boolean pullType;

}
