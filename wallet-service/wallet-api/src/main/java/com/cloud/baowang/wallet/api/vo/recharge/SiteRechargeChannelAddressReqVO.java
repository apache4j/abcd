package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.enums.NetWorkTypeEnum;
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
public class SiteRechargeChannelAddressReqVO {

    @Schema(description = "主键ID")
    @NotNull(message = "虚拟币地址不能为空")
    private String addressNo;

    @Schema(description = "虚拟币类型 TRC20 ERC20")
    @NotNull(message = "虚拟币类型不能为空")
    private NetWorkTypeEnum netWorkTypeEnum;

}
