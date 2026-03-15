package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/02/17 14:39
 * @description: 代理佣金比例配置
 */
@Data
@Schema(title = "代理佣金比例配置更新请求对象")
public class AgentCommissionRateUpdateVO implements Serializable {

    @Schema( title = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema( title = "最小游戏亏损")
    @NotEmpty(message = "最小游戏亏损不能为空")
    private BigDecimal minWinLossAmount;

    @Schema( title = "新增玩家最低要求")
    private Integer newActiveNumber;

    @Schema( title = "活跃玩家最低要求")
    @NotEmpty(message = "活跃玩家最低要求不能为空")
    private Integer activeNumber;

    @Schema( title = "佣金比例")
    @NotEmpty(message = "佣金比例不能为空")
    private String rate;

    @Schema(title = "updater", hidden = true)
    private String updater;
}
