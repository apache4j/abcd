package com.cloud.baowang.wallet.api.vo.bank;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "银行卡管理状态变更vo")
public class BankCardManagerChangStatusReqVO {
    @NotBlank(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "id")
    private String id;
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "状态 0隐藏 1显示")
    private Integer status;
}
