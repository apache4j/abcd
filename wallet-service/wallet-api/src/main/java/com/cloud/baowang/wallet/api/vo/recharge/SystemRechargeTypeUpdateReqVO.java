package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值类型修改")
public class SystemRechargeTypeUpdateReqVO extends SystemRechargeTypeNewReqVO{

    @Schema(description = "主键ID")
    @NotNull(message = "ID不能为空")
    private String id;

}
