package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "总台-综合数据报表")
public class AdminIntegrateDataExportVO {
    @Schema( description = "日期")
    private String staticDate;
    private String siteCode;
    private String currencyCode;
    @Schema( description = "会员注册人数")
    private String memberRegister;

    @Schema( description = "会员登陆人数")
    private String memberLogin;

    @Schema( description = "会员总存款")
    private String memberTotalDeposit;

    @Schema( description = "会员总取款")
    private String memberTotalWithdraw;

    @Schema( description = "会员存取差")
    private BigDecimal memberAccessDifference;

    @Schema( description = "会员首存")
    private String memberFirstDeposit;

    @Schema( description = "会员投注")
    private String memberBetInfo;

    @Schema( description = "会员输赢")
    private BigDecimal memberWinOrLose;

    @Schema( description = "会员VIP福利")
    private String vipWelfare;

    @Schema( description = "会员活动优惠")
    private String memberPromotion;

    @Schema( description = "已使用优惠")
    private BigDecimal usedPromotion;

    @Schema( description = "会员调整")
    private String memberAdjustment;

    @Schema( description = "代理注册人数")
    private String agentRegister;

    @Schema( description = "代理总存款")
    private String agentTotalDeposit;

    @Schema( description = "代理总取款")
    private String agentTotalWithdraw;

    @Schema( description = "代理存取差")
    private BigDecimal agentAccessDifference;

    @Schema( description = "代存会员")
    private String agentCreditInfo;

//    @Schema( description = "代理转账")
//    private String agentTransferInfo;
//
//    @Schema( description = "代理总优惠")
//    private String agentTotalOffer;
//
//    @Schema( description = "代理调整")
//    private String agentAdjustment;

    public String convertRegisterOrLoginInfo(RegisterLoginBasicInfoVO registVo){
        return          registVo.getTotal()+"\n"+
                "后台: " + registVo.getBacked() + "人\n" +
                "PC: " + registVo.getPc() + "人\n" +
                "H5: " + registVo.getAndroidAPP() + "人\n" +
                "App: " + registVo.getAndroidH5() + "人\n"+
                "H5: " + registVo.getIosAPP() + "人\n" +
                "H5: " + registVo.getIosH5() + "人" ;
    }

    public String convertDepositInfo(MemberDepositInfoVO depositVo){
        return         depositVo.getTotalDeposit()+"\n"+
                "存款人数: " + depositVo.getDepositPeopleNums() + "人\n" +
                "存款次数: " + depositVo.getDepositNums() + "次" ;
    }


    public String convertWithdrawInfo(MemberWithdrawInfoVO withdrawVo){
        return          withdrawVo.getTotalWithdraw()+currencyCode+"\n"+
                "取款人数: " + withdrawVo.getWithdrawPeopleNums() + "人\n" +
                "取款次数: " + withdrawVo.getWithdrawNums() + "次\n" +
                "大额取款人数: " + withdrawVo.getLargeWithdrawPeopleNums() + "人\n" +
                "大额取款次数 " + withdrawVo.getLargeWithdrawPeopleNums() + "次" ;

    }

    public String convertFirstDepositInfo(MemberBasicInfoVO firstDepositVo){
        return         firstDepositVo.getAmount()+currencyCode+"\n"+
                "首存人数: " + firstDepositVo.getPeopleNums() + "次" ;
    }

    public String convertBetInfo(MemberBetInfoVO betInfo){
        return  "投注金额: " + betInfo.getBetAmount()+currencyCode+"\n"+
                "有效投注: " + betInfo.getEffectiveBetAmount()+currencyCode + "\n" +
                "投注人数: " + betInfo.getBettorNums() + "人\n" +
                "注单量 " + betInfo.getBettingOrderAmount() + "次" ;
    }

    public String convertPromotionsInfo(MemberBasicInfoVO sourceVo){
        return         sourceVo.getAmount()+currencyCode+"\n"+
                "人数: " + sourceVo.getPeopleNums() + "人" ;
    }

    /**
     * 会员调整
     * @param sourceVo
     * @return
     */
    public String convertAdjustInfo(MemberAdjustInfoVO sourceVo){
        return          sourceVo.getTotalAdjust()+currencyCode+"\n"+
                "加额: " + sourceVo.getAddAmount()+ currencyCode + "\n" +
                "加额人数: " + sourceVo.getAddAmountPeopleNum() + "人\n" +
                "减额: " + sourceVo.getReduceAmount()+currencyCode + "\n" +
                "减额人数 " + sourceVo.getReduceAmountPeopleNums() + "人" ;

    }

    public String convertAgentCreditInfo(AgentCreditInfoVO sourceVo){
        return  "额度: "+sourceVo.getCredit()+currencyCode+"\n"+
                "人数: " + sourceVo.getCreditPeopleNums() + "人\n"+
                "人数: " + sourceVo.getCreditTimes() + "次";
    }

}

