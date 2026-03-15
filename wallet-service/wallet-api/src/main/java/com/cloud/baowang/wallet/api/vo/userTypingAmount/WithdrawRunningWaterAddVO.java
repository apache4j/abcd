package com.cloud.baowang.wallet.api.vo.userTypingAmount;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@Schema(title = "会员流水增加请求对象")
public class WithdrawRunningWaterAddVO {

    @Schema(description = "userId")
    private String userId;
    @NotNull(message = "userAccount can not be empty")
    @Schema(description="会员账号")
    private String userAccount;

    @Schema(description ="站点编码")
    private String siteCode;

    @Schema(description="添加流水金额")
    @NotNull(message = "添加流水金额不能为空")
    private BigDecimal addTypingAmount;


    @Schema(description="备注")
    private String remark;


}
