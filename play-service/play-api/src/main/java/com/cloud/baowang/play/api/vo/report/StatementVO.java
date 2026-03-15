package com.cloud.baowang.play.api.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatementVO {

    @Schema(title = "会员账号")
    private String userId;
    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "主货币")
    private String currency;

    @Schema(title = "投注额")
    private BigDecimal betAmount;

    @Schema(title = "输赢金额")
    private BigDecimal winLossAmount;

    @Schema(title = "有效投注")
    private BigDecimal validAmount;

    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;

    @Schema(title = "注单量")
    private Integer betNumber;

    @Schema(title = "代理账号Id")
    private String agentId;
    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "优惠金额")
    private BigDecimal activityAmount;

    @Schema(title = "vip福利")
    private BigDecimal vipAmount;
    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount;
    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss;

    @Schema(title = "调整金额(其他调整)")
    private BigDecimal adjustAmount;

    @Schema(title = "调整金额(其他调整)-平台币")
    private BigDecimal platAdjustAmount;

    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount;

    @Schema(title = "封控金额-主货币")
    private BigDecimal riskAmount;
}
