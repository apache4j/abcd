package com.cloud.baowang.agent.api.vo.commission.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/19 16:56
 * @description: 盈利分成阶梯配置VO
 */
@Data
@Schema(title = "佣金比例配置展示", description="佣金比例配置展示")
public class FrontLadderConfigDetailVO implements Serializable {
    @Schema(description = "阶梯档位")
    private String levelName;
    @Schema(description = "公司本期总盈利min")
    private BigDecimal winLossAmountMin;
    @Schema(description = "公司本期总盈利max")
    private BigDecimal winLossAmountMax;
    @Schema(description = "本期有效投注金额min")
    private BigDecimal validAmountMin;
    @Schema(description = "本期有效投注金额max")
    private BigDecimal validAmountMax;
    @Schema(description = "有效活跃最低人数要求")
    private Integer activeNumber;
    @Schema(description = "有效新增最低人数要求")
    private Integer newValidNumber;
    @Schema(description = "佣金比例")
    private String rate;
}
