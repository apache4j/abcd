package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 23:30
 * @description: 返点明细
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "返点明细", description = "返点明细")
public class AgentRebateDetailVO {
    @Schema(description = "代理Id")
    private String agentId;
    @Schema(description = "总返点金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rebateAmount = BigDecimal.ZERO;
    @Schema(description = "货币")
    private String currency;
    @Schema(title = "场馆返点明细", description = "场馆返点明细")
    private List<AgentVenueRebateVO> dataList;

    @Schema(description = "佣金调整金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rebateAdjustAmount = BigDecimal.ZERO;

    @Schema(description = "有效流水返点-总返点金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rebateTotalAmount = BigDecimal.ZERO;
}
