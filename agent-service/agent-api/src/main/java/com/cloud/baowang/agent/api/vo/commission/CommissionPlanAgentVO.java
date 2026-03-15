package com.cloud.baowang.agent.api.vo.commission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/19 16:56
 * @description: 佣金方案使用人数
 */
@Data
@I18nClass
@Schema(title = "佣金方案使用人数", description = "佣金方案使用人数")
public class CommissionPlanAgentVO implements Serializable {
    @Schema(title = "方案名称")
    private String planName;
    @Schema(title = "代理分页详情")
    private Page<AgentPlanInfoVO> planInfoPage;
}
