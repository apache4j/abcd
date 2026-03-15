package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "代理佣金方案添加对象", description = "代理佣金方案添加对象")
public class AgentCommissionPlanAddVO implements Serializable {
    @Schema(title = "创建人", hidden = true)
    private String creator;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "方案code", hidden = true)
    private String planCode;

    @Schema(description = "方案名称")
    @Size(min = 2, max = 50, message = "方案名称在2-10个字符之间")
    private String planName;

    @Schema(description = "定义配置")
    @Valid
    private PlanConfigVO planConfigVO;

    @Schema(description = "场馆费率集合")
    @Valid
    private List<CommissionVenueFeeVO> venueFeeList;

    @Schema(description = "盈利分成阶梯配置")
    @Valid
    private LadderConfigVO ladderConfig;

    @Schema(description = "流水返点配置")
    private RebateConfigVO rebateConfig;
}
