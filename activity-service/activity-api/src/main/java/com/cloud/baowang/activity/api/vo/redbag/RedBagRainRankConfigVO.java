package com.cloud.baowang.activity.api.vo.redbag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "红包雨活动段位中奖配置附表")
public class RedBagRainRankConfigVO {

    @Schema(description = "固定金额")
    private BigDecimal fixedAmount;

    @Schema(description = "随机金额 起始值")
    private BigDecimal randomStartAmount;

    @Schema(description = "随机金额 结束值")
    private BigDecimal randomEndAmount;

    @Schema(description = "中奖概率")
    private BigDecimal hitRate;

    @Schema(description = "序号")
    private Integer sort;
}
