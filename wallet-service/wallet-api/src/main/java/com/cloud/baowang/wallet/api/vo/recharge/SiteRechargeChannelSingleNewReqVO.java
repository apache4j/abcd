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
@Schema(description = "充值通道单条新增 SiteRechargeChannelSingleNewReqVO")
public class SiteRechargeChannelSingleNewReqVO {

    @Schema(description = "通道编号")
    private String channelId;
    @Schema(description = "系统充值方式id")
    private Long rechargeWayId;

}
