package com.cloud.baowang.report.api.vo.userwinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "代理下会员输赢金额总计 返回")
public class UserWinLoseAgentVO {
    @Schema(description = "代理ID")
    private String agentId;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "有效投注额")
    private BigDecimal validBetAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLoseAmount;
    @Schema(description = "活动优惠金额")
    private BigDecimal activityAwardAmount;
    @Schema(description = "vip优惠金额")
    private BigDecimal vipAwardAmount;
    @Schema(description = "已使用优惠")
    private BigDecimal usedAmount;
    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;
    @Schema(description = "返水金额")
    private BigDecimal rebateAmount;
    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount;
}
