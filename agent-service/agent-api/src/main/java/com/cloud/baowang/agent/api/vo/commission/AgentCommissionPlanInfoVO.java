package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "代理佣金方案配置信息VO", description = "代理佣金方案配置信息VO")
public class AgentCommissionPlanInfoVO extends BaseVO implements Serializable {
    @Schema(title = "站点code")
    private String siteCode;

    @Schema(title = "方案code")
    private String planCode;

    @Schema(title = "方案名称")
    @NotEmpty
    private String planName;

    @Schema(description = "货币单位")
    private String currencyUnit;

    @Schema(title = "场馆费率集合")
    @Valid
    private List<CommissionVenueFeeVO> venueFeeList;

    @Schema(title = "定义配置VO")
    @Valid
    private PlanConfigVO planConfigVO;

    @Schema(title = "盈利分成阶梯配置外层VO")
    @Valid
    private LadderConfigVO ladderConfig;

    @Schema(title = "流水返点配置VO")
    @Valid
    private RebateConfigVO rebateConfig;
}
