package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "段位提款方式手续费配置vo")
@Data
public class SiteVipRankCurrencyWithdrawConfigVO {
    @Schema(description = "提款方式id")
    private String withdrawWayId;
    @Schema(description = "提款手续费类型")
    private Integer withdrawFeeType;

    @Schema(description = "提款手续费")
    @Valid
    @Min(message = ConstantsCode.PARAM_ERROR, value = 0)
    @Max(message = ConstantsCode.PARAM_ERROR, value = 100000000)
    private BigDecimal withdrawFee;
}
