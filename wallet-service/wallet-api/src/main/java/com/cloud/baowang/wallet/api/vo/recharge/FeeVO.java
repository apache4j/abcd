package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/8/12 16:01
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "充提手续费集合")
public class FeeVO {

    @Schema(description = "充提手续费id")
    private String id;

    @Schema(description = "充提手续费-百分比")
    private BigDecimal fee;

    @Schema(description = "手续费-固定金额")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "手续费类型 0百分比 1固定金额 2百分比+固定金额")
    private Integer feeType;
}
