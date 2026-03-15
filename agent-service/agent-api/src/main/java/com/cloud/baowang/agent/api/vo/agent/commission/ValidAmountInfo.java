package com.cloud.baowang.agent.api.vo.agent.commission;

import com.cloud.baowang.agent.api.vo.commission.CommissionPlanTurnoverConfigVO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ValidAmountInfo {
    private BigDecimal amount;
    private CommissionPlanTurnoverConfigVO plan;
}
