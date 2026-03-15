package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.agent.api.vo.commission.front.AgentRebateDetailVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 14:23
 * @description: 佣金发放记录佣金详情
 */
@Data
@I18nClass
@Schema(title = "佣金发放记录佣金详情", description = "佣金发放记录佣金详情")
public class CommissionGrantRecordDetailVO {
    @Schema(description = "佣金类型 1 负盈利佣金 2 有效流水返点 3 人头费")
    private String commissionType;
    @Schema(title = "负盈利佣金佣金详情", description = "负盈利佣金佣金详情")
    private CommissionVenueFeeDetailVO commissionVenueFeeDetailVO;
    @Schema(title = "有效返点佣金详情", description = "有效返点佣金详情")
    private AgentRebateDetailVO agentRebateDetailVO;
    @Schema(title = "人头费佣金详情", description = "人头费佣金详情")
    private CommissionPersonDetailVO commissionPersonDetailVO;
}
