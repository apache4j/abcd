package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 有效流水方案配游戏类型分组VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "有效流水方案配游戏类型分组VO", description = "有效流水方案配游戏类型分组VO")
public class CommissionPlanTurnoverVenueGroupVO implements Serializable {
    @Schema(title = "游戏类型")
    private Integer venueType;

    @Schema(title = "币种维度分组")
    private List<CommissionPlanTurnoverCurrencyGroupVO> currencyGroups;
}
