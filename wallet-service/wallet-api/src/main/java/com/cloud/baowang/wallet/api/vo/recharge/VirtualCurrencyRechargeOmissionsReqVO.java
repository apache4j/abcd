package com.cloud.baowang.wallet.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "虚拟币遗漏订单")
public class VirtualCurrencyRechargeOmissionsReqVO {

    private Long startTime;

    private Long endTime;
}
