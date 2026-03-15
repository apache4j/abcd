package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(description = "站点充值方式返回")
public class SiteRechargeWayResVO {
    @Schema(description = "支付方式id")
    private String payMethodId;

    @Schema(description = "充值方式编码-多语言")
    private String rechargeWayI18;

}
