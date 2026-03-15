package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/10/28 10:57
 * @description:
 */
@Data
@I18nClass
@Schema(title = "佣金审核详情")
public class AgentCommissionReviewDetailVO {
    @Schema(title = "代理基础信息VO", description = "代理基础信息VO")
    private  AgentBaseInfoVO agentBaseInfoVO;
    @Schema(title = "负盈利佣金账单信息VO", description = "负盈利佣金账单信息VO")
    private CommissionBillVO commissionBillVO;
    @Schema(title = "有效流水佣金账单信息VO",description = "有效流水佣金账单信息VO")
    private RebateCommissionBillVO rebateCommissionBillVO;
    @Schema(title = "人头费佣金账单信息VO",description = "人头费佣金账单信息VO")
    private PersonCommissionBillVO personCommissionBillVO;
    @Schema(title = "审核信息VO",description = "审核信息VO")
    private CommissionReviewInfoVO commissionReviewInfoVO;
}
