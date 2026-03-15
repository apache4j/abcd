package com.cloud.baowang.play.api.vo.order.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "客户端注单总计")
public class ClientOrderTotalVO {
    @Schema(description = "投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;
    @Schema(description = "输赢金额")
    private BigDecimal winLoseAmount = BigDecimal.ZERO;
    @Schema(description = "有效投注金额")
    private BigDecimal validAmount;
    @Schema(description = "投注笔数")
    private Long betNum = 0L;
}
