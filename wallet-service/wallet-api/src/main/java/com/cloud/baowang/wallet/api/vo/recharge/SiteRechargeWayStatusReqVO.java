package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
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
@Schema(description = "站点充值方式状态操作")
public class SiteRechargeWayStatusReqVO {

    @Schema(description = "充值方式ID")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

}
