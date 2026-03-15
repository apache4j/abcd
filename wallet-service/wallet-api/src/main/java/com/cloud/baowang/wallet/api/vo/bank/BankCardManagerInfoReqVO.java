package com.cloud.baowang.wallet.api.vo.bank;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "银行卡管理详情请求vo")
public class BankCardManagerInfoReqVO {
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "id")
    private String id;
}
