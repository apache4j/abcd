package com.cloud.baowang.play.api.vo.third;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityVenueVO {
    @Schema(title =  "活动名称")
    private String activityName;

    @Schema(title = "流水要求")
    private BigDecimal upgradeValidAmount;

    @Schema(title = "已完成投注流水")
    private BigDecimal completedRunningWater;

}
