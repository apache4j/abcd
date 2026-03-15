package com.cloud.baowang.play.api.vo.order;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注单列表查询-中控后台总计返回参数")
public class OrderRecordAdminTotalRespVO {
    @Schema(description = "已结算注单总计")
    private Long settledCount;
    @Schema(description = "未结算注单总计")
    private Long unsettledCount;
    @Schema(description = "注单总计")
    private Long totalNum;
    @Schema(description = "投注额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalBetAmount;
    @Schema(description = "有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalValidAmount;
    @Schema(description = "输赢金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalWinLossAmount;

}
