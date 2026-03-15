package com.cloud.baowang.play.api.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "后台管理注单列表聚合查询返回")
public class OrderRecordAggregationDTO {
    @Schema(description = "注单归类")
    private Integer orderClassify;
    @Schema(description = "数量")
    private Long num;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(description = "有效投注金额")
    private BigDecimal validAmount;

    private String gameId;
}
