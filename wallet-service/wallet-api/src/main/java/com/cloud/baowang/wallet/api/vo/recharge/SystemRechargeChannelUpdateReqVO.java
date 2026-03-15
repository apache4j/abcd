package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 11:38
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值渠道修改")
public class SystemRechargeChannelUpdateReqVO extends SystemRechargeChannelNewReqVO{

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @NotNull(message = "id不能为空")
    private String id;


}
