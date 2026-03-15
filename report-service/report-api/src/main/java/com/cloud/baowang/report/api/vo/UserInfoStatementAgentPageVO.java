package com.cloud.baowang.report.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "会员报表-会员代理列表分页查询返回实体")
public class UserInfoStatementAgentPageVO {
    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "上级代理id")
    private String superAgentId;

    @Schema(title = "上级代理账号")
    private String superAgentAccount;

    @Schema(title = "总存款")
    private BigDecimal totalDeposit;

    @Schema(title = "总取款")
    private BigDecimal totalWithdrawal;

    @Schema(title = "总优惠")
    private BigDecimal totalPreference;

    @Schema(title = "总返水")
    private BigDecimal grossRecoil;

    @Schema(title = "其他调整")
    private BigDecimal otherAdjustments;

    @Schema(title = "注单量")
    private Integer placeOrderQuantity;

    @Schema(title = "投注金额")
    private BigDecimal betAmount;

    @Schema(title = "有效投注金额")
    private BigDecimal activeBet;

    @Schema(title = "投注盈亏")
    private BigDecimal bettingProfitLoss;

    @Schema(title = "日期")
    private Long createdTime;
}
