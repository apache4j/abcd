package com.cloud.baowang.wallet.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "风控电子钱包返回vo")
@Data
public class RiskWalletAccountVO {
    @Schema(description = "总提款金额")
    private BigDecimal totalAmount;
    @Schema(description = "提款通道名称")
    private String channelName;
}
