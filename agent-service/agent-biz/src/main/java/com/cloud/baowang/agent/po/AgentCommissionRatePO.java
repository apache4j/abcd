package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/06/04 14:39
 * @description: 代理佣金比例配置
 */
@Data
@TableName("agent_commission_rate")
@Schema(title = "代理佣金比例配置", description = "代理佣金比例配置")
public class AgentCommissionRatePO extends BasePO {
    @Schema(title = "站点code")
    private String siteCode;

    @Schema(title = "级别")
    private String level;

    @Schema(title = "公司本月总盈利")
    private BigDecimal minWinLossAmount;

    @Schema(title = "有效新增最低要求")
    private Integer newActiveNumber;

    @Schema(title = "活跃玩家最低要求")
    private Integer activeNumber;

    @Schema(title = "返佣比例")
    private String rate;
}
