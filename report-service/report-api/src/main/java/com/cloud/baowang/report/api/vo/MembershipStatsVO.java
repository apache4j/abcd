package com.cloud.baowang.report.api.vo;

/**
 * @Description
 * @auther amos
 * @create 2024-11-02
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "综合报表VO")
public class MembershipStatsVO {

    @Schema(description = "日期")
    private Date date;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "会员注册人数")
    private String memberRegistrationCount;

    @Schema(description = "会员登录人数")
    private String memberLoginCount;

    @Schema(description = "会员总存款")
    private String totalMemberDeposit;

    @Schema(description = "会员总取款")
    private String totalMemberWithdrawal;

    @Schema(description = "会员存取差")
    private String memberDepositWithdrawalDifference;

    @Schema(description = "会员首存")
    private String firstMemberDeposit;

    @Schema(description = "会员投注金额")
    private String memberBetting;

    @Schema(description = "会员输赢")
    private String memberProfitLoss;

    @Schema(description = "会员VIP福利")
    private String memberVipBenefits;

    @Schema(description = "会员活动优惠")
    private String memberActivityDiscounts;

    @Schema(description = "已使用优惠")
    private String usedDiscounts;

    @Schema(description = "会员调整")
    private String memberAdjustments;

    @Schema(description = "代理注册人数")
    private String agentRegistrationCount;

    @Schema(description = "代理总存款")
    private String agentTotalDeposit;

    @Schema(description = "代理总取款")
    private String agentTotalWithdrawal;

    @Schema(description = "代理存取差")
    private String agentDepositWithdrawalDifference;

    @Schema(description = "代存会员")
    private String storedMembers;

    @Schema(description = "代理转账")
    private String agentTransfer;

    @Schema(description = "代理总优惠金额")
    private String agentTotalDiscounts;

    @Schema(description = "代理调整")
    private String agentAdjustments;
}
