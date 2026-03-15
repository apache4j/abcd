package com.cloud.baowang.wallet.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "处理中订单返回对象")
public class HandledDepositOrderVO {

    @Schema(description = "是否处理中订单标志 0 无 1 有")
    private Integer handleFlag;

    @Schema(description = "处理中订单详情")
    private UserDepositOrderDetailVO userDepositOrderDetailVO;

}
