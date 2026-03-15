package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/27 15:11
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点配置提款授权对象")
public class SiteWithdrawVO implements Serializable {

    @Schema(description = "提现方式ID")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String withdrawWayId;

    @Schema(title = "提款通道id集合")
    private List<String> platform;

    @Schema(title = "提款手续费")
    @Min(value = 0, message = ConstantsCode.PARAM_ERROR)
    @Max(value = 100, message = ConstantsCode.PARAM_ERROR)
    private BigDecimal withdrawFee;

    @Schema(description = "手续费-固定金额")
    @Min(value = 0, message = ConstantsCode.PARAM_ERROR)
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "手续费类型")
    private Integer feeType;

}
