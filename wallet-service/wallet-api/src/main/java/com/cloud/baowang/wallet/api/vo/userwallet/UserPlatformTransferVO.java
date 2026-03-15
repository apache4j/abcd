package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/16 10:01
 * @Version: V1.0
 **/
@Data
@Schema(title = "平台币兑换参数")
public class UserPlatformTransferVO {
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;
    @Schema(description = "用户Id",hidden = true)
    private String userId;
    @Schema(description = "用户名称",hidden = true)
    private String userName;
    @Schema(description = "用户编号",hidden = true)
    private String userAccount;
    @Schema(description = "转换金额")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @DecimalMin(value = "0.0001",message = ConstantsCode.PARAM_ERROR)
    @Digits(integer = 22,fraction = 4,message = ConstantsCode.PARAM_ERROR)
    private BigDecimal transferAmount;
    @Schema(description = "转换汇率",hidden = true)
    private BigDecimal transferRate;

}
