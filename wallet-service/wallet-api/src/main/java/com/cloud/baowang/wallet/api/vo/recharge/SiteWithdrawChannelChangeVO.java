package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "站点提款渠道返回")
public class SiteWithdrawChannelChangeVO {
    @Schema(description = "提款通道ID")
    private String channelId;
    @Schema(description = "提款方式ID")
    private String withdrawWayId;
    @Schema(description = "提款通道CODE")
    private String channelCode;

}
