package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "站点充值渠道返回")
public class SiteRechargeChannelChangeVO {
    @Schema(description = "充值通道ID")
    private String channelId;
    @Schema(description = "充值方式ID")
    private String rechargeWayId;
    @Schema(description = "充值通道CODE")
    private String channelCode;

}
