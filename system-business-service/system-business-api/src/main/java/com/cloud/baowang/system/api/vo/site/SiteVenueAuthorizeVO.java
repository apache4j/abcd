package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
 * @Date : 2024/7/27 14:02
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点配置场馆授权对象")
public class SiteVenueAuthorizeVO implements Serializable {

    @Schema(description = "场馆ID")
    private String venueId;
    @Schema(description = "场馆code")
    private String venueCode;

    @Schema(description = "关联游戏ID集合")
    private List<String> gameId;

    @Schema(description = "负盈利手续费")
    @Min(value = 0, message = ConstantsCode.PARAM_ERROR)
    @Max(value = 100, message = ConstantsCode.PARAM_ERROR)
    private BigDecimal handlingFee;
    @Schema(description = "有效流水手续费")
    private BigDecimal validProportion;
}
