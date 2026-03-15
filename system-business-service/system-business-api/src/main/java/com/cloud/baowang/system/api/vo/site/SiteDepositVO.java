package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Schema(title = "站点配置存款授权对象")
public class SiteDepositVO implements Serializable {


    @Schema(description = "充值方式")
    private String rechargeWayId;

    @Schema(description = "通道ID")
    private List<String> platform;

    @Schema(description = "充值手续费")
    @Min(value = 0, message = ConstantsCode.PARAM_ERROR)
    @Max(value = 100, message = ConstantsCode.PARAM_ERROR)
    private BigDecimal depositFee;

    @Schema(description = "手续费-固定金额")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "手续费类型")
    private Integer feeType;

}
