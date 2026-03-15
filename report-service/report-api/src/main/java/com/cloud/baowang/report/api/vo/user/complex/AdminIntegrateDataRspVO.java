package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema(title = "综合数据报表vo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminIntegrateDataRspVO implements Serializable {
    @Schema( description = "日期")
    private String staticDate;
    private String siteCode;
    private String siteName;
    @Schema( description = "币种")
    private String currencyCode;
    @Schema(name = "memberRegister-会员注册人数", description = "会员注册人数")
    private RegisterLoginBasicInfoVO memberRegister;

    @Schema(name = "memberLogin-会员登陆人数", description = "会员登陆人数")
    private RegisterLoginBasicInfoVO memberLogin;

    @Schema(name = "memberTotalDeposit-会员总存款", description = "会员总存款")
    private MemberDepositInfoVO memberTotalDeposit;

    @Schema(name = "memberTotalWithdraw-会员总取款" ,description = "会员总取款")
    private MemberWithdrawInfoVO memberTotalWithdraw;

    @Schema(name = "memberAccessDifference-会员存取差", description = "会员存取差")
    private MemberAccessDifferenceVO memberAccessDifference;

    @Schema(name = "memberFirstDeposit-会员首存", description = "会员首存")
    private MemberBasicInfoVO memberFirstDeposit;

    @Schema(name = "memberBetInfo-会员投注", description = "会员投注")
    private MemberBetInfoVO memberBetInfo;

    @Schema(description = "会员输赢")
    private MemberWinOrLoseVO memberWinOrLose;

    @Schema(name = "vipWelfare-会员vip福利", description = "会员VIP福利")
    private MemberBasicInfoVO vipWelfare;

    @Schema(name = "memberPromotion-会员活动优惠", description = "会员活动优惠")
    private MemberBasicInfoVO memberPromotion;

    @Schema( description = "已使用优惠")
    private MemberWinOrLoseVO usedPromotion;

    @Schema(name = "memberAdjustment-会员调整", description = "会员调整")
    private MemberAdjustInfoVO memberAdjustment;

    @Schema(name = "agentRegister-代理注册人数", description = "代理注册人数")
    private RegisterLoginBasicInfoVO agentRegister;

//    @Schema(name = "agentTotalDeposit-代理总存款", description = "代理总存款")
//    private MemberDepositInfoVO agentTotalDeposit;
//
//    @Schema(name = "agentTotalWithdraw-代理总取款", description = "代理总取款")
//    private MemberWithdrawInfoVO agentTotalWithdraw;
//
//    @Schema( description = "代理存取差")
//    private BigDecimal agentAccessDifference;

    @Schema(name = "agentDepositInfo-代存会员", description = "代存会员")
    private AgentCreditInfoVO agentDepositInfo;

//    @Schema( description = "代理转账")
//    private AgentDepositTransferInfoVO agentTransferInfo;
//
//    @Schema( description = "代理总优惠")
//    private MemberBasicInfoVO agentTotalOffer;
//
//    @Schema( description = "代理调整")
//    private MemberAdjustInfoVO agentAdjustment;
}

