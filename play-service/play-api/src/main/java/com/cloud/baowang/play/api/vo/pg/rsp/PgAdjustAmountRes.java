package com.cloud.baowang.play.api.vo.pg.rsp;

import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PgAdjustAmountRes {

    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @Schema(title = "调整金额")
    private BigDecimal adjust_amount;
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @Schema(title = "调整前余额")
    private BigDecimal balance_before;
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @Schema(title = "实际调整金额")
    private BigDecimal real_transfer_amount;
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    @Schema(title = "调整后余额")
    private BigDecimal balance_after;
    @Schema(title = "交易的更新时间 Unix 时间戳，以毫秒为单位 响应中的updated_time必须与请求的adjustment_time相同")
    private Long updated_time;
}
