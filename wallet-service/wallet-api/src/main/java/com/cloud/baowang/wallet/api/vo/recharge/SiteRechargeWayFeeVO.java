package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "站点充值方式费率信息返回")
public class SiteRechargeWayFeeVO {

    /**
     * 充值配置ID
     * SystemRechargeWay.id
     */
    private String rechargeWayId;

    /**
     * 站点代码
     */
    private String siteCode;


    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    private Integer feeType;

    /**
     * 手续费 5 代表5%
     */
    private BigDecimal wayFee;

    /**
     * 百分比金额
     */
    private BigDecimal wayFeePercentageAmount;

    /**
     * 固定金额手续费
     */
    private BigDecimal wayFeeFixedAmount;

    /**
     * 总手续费
     */
    private BigDecimal wayFeeAmount;
}
