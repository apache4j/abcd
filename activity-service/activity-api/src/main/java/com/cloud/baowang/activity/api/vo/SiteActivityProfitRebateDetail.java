package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SiteActivityProfitRebateDetail implements Serializable {

    @Schema(description = "亏损开始区间")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal startAmount;

    @Schema(description = "亏损结束区间")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal endAmount;

    @Schema(description = "返回")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal rebateAmount;
}
