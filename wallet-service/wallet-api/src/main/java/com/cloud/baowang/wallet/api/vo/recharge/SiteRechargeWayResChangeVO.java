package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(description = "站点充值方式返回")
public class SiteRechargeWayResChangeVO {
    @Schema(description = "充值配置ID")
    private String rechargeWayId;

    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "百分比手续费")
    private BigDecimal wayFee;

    @Schema(description = "单笔固定金额手续费")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "查询中文i18充值方式多语言")
    private String rechargeWayName;

}
