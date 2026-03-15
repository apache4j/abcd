package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "代理佣金方案配置分页VO", description = "代理佣金方案配置分页VO")
public class AgentCommissionPlanPageVO extends BaseVO implements Serializable {
    @Schema(title = "站点code")
    private String siteCode;

    @Schema(title = "方案code")
    private String planCode;

    @Schema(title = "方案名称")
    private String planName;

    @Schema(title = "使用代理人数")
    private Integer agentNumber;

    @Schema(title = "定义配置")
    private PlanConfigVO planConfigVO;

    @Schema(description = "场馆费率集合")
    private List<CommissionVenueFeeVO> venueFeeList;

    @Schema(title = "盈利分成阶梯配置")
    private LadderConfigVO ladderConfig;

    @Schema(title = "流水返点配置")
    private RebateConfigVO rebateConfig;
}
