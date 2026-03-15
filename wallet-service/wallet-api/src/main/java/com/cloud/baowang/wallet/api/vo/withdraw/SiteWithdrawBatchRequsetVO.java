package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption: 站点配置提现授权对象
 * @Author: qiqi
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点配置提现授权对象")
public class SiteWithdrawBatchRequsetVO {

    @Schema(description = "提现方式ID")
    private String withdrawWayId;

    @Schema(description = "通道ID")
    private List<String> platform;

    @Schema(description = "提现手续费")
    private BigDecimal withdrawFee;

    @Schema(description = "手续费类型")
    private Integer feeType;
    @Schema(description = "手续费-固定金额")
    private BigDecimal wayFeeFixedAmount;

}
