package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.agent.api.vo.commission.CommissionVenueFeeVO;
import com.cloud.baowang.agent.api.vo.commission.LadderConfigVO;
import com.cloud.baowang.agent.api.vo.commission.PlanConfigVO;
import com.cloud.baowang.agent.api.vo.commission.RebateConfigVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/06 14:33
 * @description: 佣金说明
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "佣金说明", description = "佣金说明")
public class AgentCommissionExplainVO {
    @Schema(description = "有效活跃和有效新增定义配置VO")
    private PlanConfigVO planConfigVO;

    @Schema(description = "负盈利佣金")
    private FrontLadderConfigVO ladderConfig;

    @Schema(description = "流水返点配置以及人头费")
    private FrontRebateConfigVO rebateConfig;

    @Schema(description = "场馆费率集合")
    private List<CommissionVenueFeeVO> venueFeeList;

    @Schema(description = "有效流水配置")
    private List<ValidBetAmountConfigVO> validBetAmountConfig;
}
