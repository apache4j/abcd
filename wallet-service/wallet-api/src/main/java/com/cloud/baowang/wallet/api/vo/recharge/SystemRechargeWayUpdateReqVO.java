package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值方式修改")
public class SystemRechargeWayUpdateReqVO extends SystemRechargeWayNewReqVO{

    @Schema(description = "主键ID")
    @NotNull(message = "Id不能为空")
    private String id;

}
