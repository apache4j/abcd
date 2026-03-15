package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 13:55
 * @Version: V1.0
 **/
@Data
@Schema(description = "站点充值通道状态操作")
public class SiteRechargeChannelStatusReqVO {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private String id;

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

}
