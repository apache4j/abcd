package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "充值订单编号请求对象")
public class OrderNoVO {

    @Schema(description = "订单编码")
    private String orderNo;

    @Schema(description = "通道类型 THIRD 三方，OFFLINE 线下,SITE_CUSTOM 站点自定义")
    private String channelType;

    @Schema(description = "三方支付URL路径")
    private String thirdPayUrl;

    @Schema(description = "三方返回是否URL 1是， 0否")
    private Integer thirdIsUrl;
}
