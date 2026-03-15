package com.cloud.baowang.user.api.vo.vip;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP-币种对应手续费信息")
public class SiteVipFeeRateVO {

    @Schema(description = "是否有加密货币提款手续费")
    private Integer encryCoinFee;
    @Schema(description = "单日提款次数")
    private Integer dailyWithdrawals;
    @Schema(description = "单日提款最大值")
    private BigDecimal dayWithdrawLimit;
    @Schema(description = "提款手续费 百分比/固定金额")
    private BigDecimal withdrawFee;
    @Schema(description = "手续费类型 0百分比 1固定金额")
    private Integer withdrawFeeType;

    private String withdrawWayId;
}
