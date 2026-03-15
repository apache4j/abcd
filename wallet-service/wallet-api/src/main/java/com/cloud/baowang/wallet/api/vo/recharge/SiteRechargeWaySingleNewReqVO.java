package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值方式单条新增")
public class SiteRechargeWaySingleNewReqVO {

    @Schema(description = "充值方式编号")
    private Long rechargeWayId;

    /**
     * 手续费 5 代表5%
     */
    @Schema(description = "手续费 5 代表5%")
    private BigDecimal wayFee;

    @Schema(description = "手续费类型")
    private Integer feeType;

    @Schema(description = "固定金额手续费")
    private BigDecimal wayFeeFixedAmount;


    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * VIP等级使用范围
     */
    @Schema(description = "VIP等级使用范围")
    private String vipGradeUseScope;


}
