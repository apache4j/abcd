package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "批量归集请求对象")
public class HotWalletAddressBatchCollectVO {

    @Schema(description = "链类型",hidden = true)
    private String chainType;

    @Schema(description = "归集下限金额")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Positive(message = ConstantsCode.PARAM_ERROR)
    @Digits(integer = 9,fraction = 0,message = ConstantsCode.PARAM_ERROR )
    private BigDecimal collectMinAmount;

}
