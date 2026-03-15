package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 有效流水方案配置详情VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "有效流水方案配置详情VO", description = "有效流水方案配置详情VO")
public class CommissionPlanTurnoverDetailVO extends BaseVO implements Serializable {
    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "方案编码")
    private String planCode;

    @Schema(title = "游戏类型维度分组")
    private List<CommissionPlanTurnoverVenueGroupVO> venueGroups;
}
