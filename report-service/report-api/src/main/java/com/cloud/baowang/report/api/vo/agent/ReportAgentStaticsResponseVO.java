package com.cloud.baowang.report.api.vo.agent;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 代理报表 resp
 */
@Data
@Schema(description =  "代理报表 resp")
@ExcelIgnoreUnannotated
@I18nClass
public class ReportAgentStaticsResponseVO {

    /**
     * utc 整点对应时间戳
     */
    @Schema(description =  "统计日期 每日或每月1号到天的是时间戳")
    private Long dayMillis;

    @Schema(title = "报表统计类型 0:日报 1:月报")
    private String reportType;

    @Schema(title = "报表统计日期")
    //@Schema(title = "报表统计日期 天或者月 yyyy-MM-dd")
    @ExcelProperty("统计日期")
    private String reportDate;

    @Schema(description =  "站点Code")
    private String siteCode;

    @Schema(description =  "代理Id")
    private String agentId;

    @Schema(description =  "代理账号")
    @ExcelProperty("代理账号")
    private String agentAccount;


    @Schema(description =  "代理层级")
    private Integer level;

    @Schema(description =  "代理层级名称")
    @ExcelProperty("代理层级")
    private String levelName;

    @Schema(description =  "邀请码")
    @ExcelProperty("邀请码")
    private String inviteCode;

    @Schema(title = "账号类型")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_TYPE)
    private Integer agentType;
    @Schema(title = "账号类型")
    @ExcelProperty("账号类型")
    private String agentTypeText;

    @Schema(description = "代理类别")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_CATEGORY)
    private Integer agentCategory;

    @Schema(title = "代理类别")
    @ExcelProperty("代理类别")
    private String agentCategoryText;

    @Schema(description =  "直属上级代理ID")
    private String parentId;

    @Schema(description =  "直属上级代理账号")
    @ExcelProperty("直属上级")
    private String parentAccount;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级")
    @ExcelProperty("风控层级")
    private String riskLevel;

    @Schema(description = "代理标签id")
    private String agentLabelId;

    @Schema(description = "代理标签")
    @ExcelProperty("代理标签")
    private String agentLabel;

    @Schema(description =  "注册时间")
    private Long registerTime;

    @ExcelProperty("注册时间")
    private String registerTimeStr;

    @Schema(description =  "层次")
    private String path;

    @Schema(description =  "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(description =  "团队代理人数")
    @ExcelProperty("团队代理人数")
    private Long teamAgentNum=0L;

    @Schema(description =  "直属下级人数")
    @ExcelProperty("直属下级人数")
    private Long directReportNum=0L;
    @Schema(description =  "注册人数")
    @ExcelProperty("注册人数")
    private Long registerUserNum=0L;
    @Schema(description =  "首存人数")
    @ExcelProperty("首存人数")
    private Long firstDepositNum=0L;
    @Schema(description =  "首存转换率=首存人数 / 注册人数")
    @ExcelProperty("首存转换率")
    private String firstDepositRate;
    //首存转换率 原始数据
    private BigDecimal firstDepositRateBigDecimal;
    @Schema(description =  "投注人数")
    @ExcelProperty("投注人数")
    private Long betUserNum=0L;

    @Schema(description =  "币种")
    @ExcelProperty("币种")
    private String currencyCode;


    @Schema(title = "存款总额 平台币")
    @ExcelProperty("存款总额")
    private BigDecimal depositTotalAmount;

    @Schema(description =  "存款总额 币种")
    private String depositTotalAmountCurrency;

    @Schema(title = "提款总额 主货币")
    @ExcelProperty("提款总额")
    private BigDecimal withdrawTotalAmount;

    @Schema(description =  "提款总额 币种")
    private String withdrawTotalAmountCurrency;


    @Schema(description =  "投注金额->主货币")
    @ExcelProperty("投注额")
    private BigDecimal betAmount = BigDecimal.ZERO;
    @Schema(description =  "投注金额 币种")
    private String betAmountCurrency;

    @Schema(description =  "有效投注额->主货币")
    @ExcelProperty("有效投注额")
    private BigDecimal validAmount= BigDecimal.ZERO;

    @Schema(description =  "有效投注额 币种")
    private String validAmountCurrency;

    @Schema(description =  "会员输赢->主货币")
    @ExcelProperty("会员输赢")
    private BigDecimal winLossAmountUser = BigDecimal.ZERO;

    @Schema(description =  "会员输赢 币种")
    private String winLossAmountUserCurrency;



    @Schema(description =  "平台总输赢 等于 -会员输赢->主货币")
    @ExcelProperty("平台总输赢")
    private BigDecimal winLossAmountPlat= BigDecimal.ZERO;

    @Schema(description =  "平台总输赢 币种")
    private String winLossAmountPlatCurrency;

    @Schema(description =  "调整金额(其他调整)->主货币")
    @ExcelProperty("调整金额")
    private BigDecimal adjustAmount = BigDecimal.ZERO;
    @Schema(description =  "调整金额 币种")
    private String adjustAmountCurrency;

    private BigDecimal winLossRateBigDecimal;

    @Schema(description =  "盈亏比例 等于 平台总输赢 / 投注额")
    @ExcelProperty("盈亏比例")
    private String winLossRate;

/*    @Schema(description =  "盈亏比例 币种")
    private String winLossRateCurrency;*/



    @Schema(description =  "活动优惠->平台币")
    @ExcelProperty("活动优惠")
    private BigDecimal activityAmount = BigDecimal.ZERO;

    @Schema(description =  "活动优惠 平台币")
    @ExcelProperty("活动优惠币种")
    private String activityAmountCurrency;

    @Schema(description =  "vip福利->平台币")
    @ExcelProperty("vip福利")
    private BigDecimal vipAmount = BigDecimal.ZERO;

    @Schema(description =  "vip福利 币种")
    @ExcelProperty("vip福利币种")
    private String vipAmountCurrency;


    @Schema(description =  "已使用优惠->主货币")
    @ExcelProperty("已使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @Schema(description =  "已使用优惠 币种")
    private String alreadyUseAmountCurrency;

    @Schema(title = "存提手续费 主货币")
    @ExcelProperty("存提手续费")
    private BigDecimal depositWithdrawFeeAmount;

    @Schema(description =  "存提手续费 币种")
    private String depositWithdrawFeeAmountCurrency;


    @Schema(title = "返水")
    private BigDecimal rebateAmount = BigDecimal.ZERO;

    @Schema(title = "返水")
    @ExcelProperty("返水")
    private String rebateAmountText;

    public String getRebateAmountText() {
        return rebateAmount + rebateAmountCurrency ;
    }

    @Schema(description =  "返水金额 币种")
    private String rebateAmountCurrency;

    /*@Schema(description =  "代理佣金->平台币")
    @ExcelProperty("代理佣金")
    private BigDecimal commissionAmount= BigDecimal.ZERO;

    @Schema(description =  "代理佣金 币种")
    @ExcelProperty("代理佣金币种")
    private String commissionAmountCurrency;

    @Schema(description =  "平台收入 等于 平台总输赢 - 调整金额 - 已使用活动优惠 - 代理佣金->平台币")
    @ExcelProperty("平台收入")
    private BigDecimal platIncome= BigDecimal.ZERO;

    @Schema(description =  "平台收入 币种")
    @ExcelProperty("平台收入币种")
    private String platIncomeCurrency;*/

    @Schema(description =  "平台币代码")
    private String platCurrencyCode=CommonConstant.PLAT_CURRENCY_CODE;

    @Schema(title = "调整金额(其他调整)-平台币")
    @ExcelProperty("平台币调整金额")
    private BigDecimal platAdjustAmount;

    @Schema(title = "调整金额(其他调整)币种")
    private String platAdjustAmountCurrency;

    @Schema(title = "打赏金额-主货币")
    @ExcelProperty("打赏金额")
    private BigDecimal tipsAmount;

    @Schema(title = "打赏金额币种")
    private String tipsAmountCurrency;

    @Schema(title = "风控金额-主货币")
    @ExcelProperty("风控金额")
    private BigDecimal riskAmount;

    @Schema(title = "风控金额币种")
    private String riskAmountCurrency;



    public void addTeamAgentNum(Long teamAgentNum) {
        teamAgentNum= teamAgentNum==null?0L:teamAgentNum;
        this.teamAgentNum=this.teamAgentNum==null?0L: this.teamAgentNum;
        this.teamAgentNum=this.teamAgentNum+teamAgentNum;
    }

    public void addDirectReportNum(Long directReportNum) {
        directReportNum= directReportNum==null?0L:directReportNum;
        this.directReportNum=this.directReportNum==null?0L: this.directReportNum;
        this.directReportNum=this.directReportNum+directReportNum;
    }

    public void addRegisterUserNum(Long registerUserNum) {
        registerUserNum= registerUserNum==null?0L:registerUserNum;
        this.registerUserNum=this.registerUserNum==null?0L: this.registerUserNum;
        this.registerUserNum=this.registerUserNum+registerUserNum;
    }

    public void addFirstDepositNum(Long firstDepositNum) {
        firstDepositNum= firstDepositNum==null?0L:firstDepositNum;
        this.firstDepositNum=this.firstDepositNum==null?0L: this.firstDepositNum;
        this.firstDepositNum=this.firstDepositNum+firstDepositNum;
    }

    public void addBetAmount(BigDecimal betAmount) {
        betAmount= betAmount==null?BigDecimal.ZERO:betAmount;
        this.betAmount=this.betAmount==null?BigDecimal.ZERO: this.betAmount;
        this.betAmount=this.betAmount.add(betAmount);
    }

    public void addValidAmount(BigDecimal validAmount) {
        validAmount= validAmount==null?BigDecimal.ZERO:validAmount;
        this.validAmount=this.validAmount==null?BigDecimal.ZERO: this.validAmount;
        this.validAmount=this.validAmount.add(validAmount);
    }

    public void addRebateAmount(BigDecimal rebateAmount) {
        rebateAmount= rebateAmount==null?BigDecimal.ZERO:rebateAmount;
        this.rebateAmount=this.rebateAmount==null?BigDecimal.ZERO: this.rebateAmount;
        this.rebateAmount=this.rebateAmount.add(rebateAmount);
    }

    public void addWinLossAmountUser(BigDecimal winLossAmountUser) {
        winLossAmountUser= winLossAmountUser==null?BigDecimal.ZERO:winLossAmountUser;
        this.winLossAmountUser=this.winLossAmountUser==null?BigDecimal.ZERO: this.winLossAmountUser;
        this.winLossAmountUser=this.winLossAmountUser.add(winLossAmountUser);
    }

    public void addWinLossAmountPlat(BigDecimal winLossAmountPlat) {
        winLossAmountPlat= winLossAmountPlat==null?BigDecimal.ZERO:winLossAmountPlat;
        this.winLossAmountPlat=this.winLossAmountPlat==null?BigDecimal.ZERO: this.winLossAmountPlat;
        this.winLossAmountPlat=this.winLossAmountPlat.add(winLossAmountPlat);
    }

    public void addAdjustAmount(BigDecimal adjustAmount) {
        adjustAmount= adjustAmount==null?BigDecimal.ZERO:adjustAmount;
        this.adjustAmount=this.adjustAmount==null?BigDecimal.ZERO: this.adjustAmount;
        this.adjustAmount=this.adjustAmount.add(adjustAmount);
    }

    public void addActivityAmount(BigDecimal activityAmount) {
        activityAmount= activityAmount==null?BigDecimal.ZERO:activityAmount;
        this.activityAmount=this.activityAmount==null?BigDecimal.ZERO: this.activityAmount;
        this.activityAmount=this.activityAmount.add(activityAmount);
    }

    public void addVipAmount(BigDecimal vipAmount) {
        vipAmount= vipAmount==null?BigDecimal.ZERO:vipAmount;
        this.vipAmount=this.vipAmount==null?BigDecimal.ZERO: this.vipAmount;
        this.vipAmount=this.vipAmount.add(vipAmount);
    }

    public void addAlreadyUseAmount(BigDecimal alreadyUseAmount) {
        alreadyUseAmount= alreadyUseAmount==null?BigDecimal.ZERO:alreadyUseAmount;
        this.alreadyUseAmount=this.alreadyUseAmount==null?BigDecimal.ZERO: this.alreadyUseAmount;
        this.alreadyUseAmount=this.alreadyUseAmount.add(alreadyUseAmount);
    }

    public void addDepositWithdrawFeeAmount(BigDecimal depositWithdrawFeeAmount){
        depositWithdrawFeeAmount= depositWithdrawFeeAmount==null?BigDecimal.ZERO:depositWithdrawFeeAmount;
        this.depositWithdrawFeeAmount=this.depositWithdrawFeeAmount==null?BigDecimal.ZERO: this.depositWithdrawFeeAmount;
        this.depositWithdrawFeeAmount=this.depositWithdrawFeeAmount.add(depositWithdrawFeeAmount);
    }

    public void addDepositTotalAmount(BigDecimal depositTotalAmount){
        depositTotalAmount= depositTotalAmount==null?BigDecimal.ZERO:depositTotalAmount;
        this.depositTotalAmount=this.depositTotalAmount==null?BigDecimal.ZERO: this.depositTotalAmount;
        this.depositTotalAmount=this.depositTotalAmount.add(depositTotalAmount);
    }

    public void addWithdrawTotalAmount(BigDecimal withdrawTotalAmount){
        withdrawTotalAmount= withdrawTotalAmount==null?BigDecimal.ZERO:withdrawTotalAmount;
        this.withdrawTotalAmount=this.withdrawTotalAmount==null?BigDecimal.ZERO: this.withdrawTotalAmount;
        this.withdrawTotalAmount=this.withdrawTotalAmount.add(withdrawTotalAmount);
    }

    public void addTipsAmount(BigDecimal tipsAmount){
        tipsAmount= tipsAmount==null?BigDecimal.ZERO:tipsAmount;
        this.tipsAmount=this.tipsAmount==null?BigDecimal.ZERO: this.tipsAmount;
        this.tipsAmount=this.tipsAmount.add(tipsAmount);
    }

    public void addRiskAmount(BigDecimal riskAmount){
        riskAmount= riskAmount==null?BigDecimal.ZERO:riskAmount;
        this.riskAmount=this.riskAmount==null?BigDecimal.ZERO: this.riskAmount;
        this.riskAmount=this.riskAmount.add(riskAmount);
    }

    public void addPlatAdjustAmount(BigDecimal platAdjustAmount){
        platAdjustAmount= platAdjustAmount==null?BigDecimal.ZERO:platAdjustAmount;
        this.platAdjustAmount=this.platAdjustAmount==null?BigDecimal.ZERO: this.platAdjustAmount;
        this.platAdjustAmount=this.platAdjustAmount.add(platAdjustAmount);
    }

    /*public void addCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount=this.commissionAmount.add(commissionAmount);
    }

    public void addPlatIncome(BigDecimal platIncome) {
        this.platIncome=this.platIncome.add(platIncome);
    }*/
}
