package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "反查实体")
public class SiteRechargeWayQueryVO {
    @Schema(description = "充值方式")
    private String rechargeWayId;

    @Schema(description = "通道ID")
    private List<String> platform;

    @Schema(description = "充值手续费")
    private BigDecimal depositFee;

    @Schema(description = "手续费类型")
    private Integer feeType;

    @Schema(description = "固定金额手续费")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "当前充值方式对应币种")
    private String currencyGroup;
}
