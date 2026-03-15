package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/02/17 14:39
 * @description: 代理佣金比例配置
 */
@Data
@Schema(title = "代理佣金说明VO")
public class AgentCommissionRateResVO extends BaseVO implements Serializable {
    @Schema( title = "级别")
    private String level;

    @Schema( title = "公司本月最小总盈利")
    private BigDecimal minWinLossAmount;

    @Schema( title = "公司本月最大总盈利")
    private BigDecimal maxWinLossAmount;

    @Schema( title = "有效活跃人数")
    private Integer activeNumber;

    @Schema( title = "返佣比例")
    private String rate;
}
