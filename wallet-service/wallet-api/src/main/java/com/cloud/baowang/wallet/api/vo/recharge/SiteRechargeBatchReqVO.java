package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption: 站点配置存款授权对象
 * @Author: Ford
 * @Date: 2024/7/29 18:43
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点配置存款授权对象")
public class SiteRechargeBatchReqVO {

    @Schema(description = "充值方式ID")
    private Long rechargeWayId;

    @Schema(description = "通道ID")
    private List<String> platform;

    @Schema(description = "充值手续费")
    private BigDecimal depositFee;

    @Schema(description = "充值手续费-固定金额")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "手续费类型")
    private Integer feeType;
}
