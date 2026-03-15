package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 有效流水佣金方案实体类
 *
 * @author remo
 */
@Data
@TableName("agent_commission_plan_turnover")
@Schema(title = "有效流水佣金方案实体类", description = "有效流水佣金方案实体类")
public class AgentCommissionPlanTurnoverPO extends BasePO {
    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "方案编码")
    private String planCode;

    @Schema(title = "方案名称")
    private String planName;

    @Schema(title = "备注")
    private String remark;

}
