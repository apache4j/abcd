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
 * 商务总代报表 resp
 */
@Data
@Schema(description =  "商务总代报表 resp")
@ExcelIgnoreUnannotated
@I18nClass
public class ReportTopAgentStaticsResponseVO {

    /**
     * utc 整点对应时间戳
     */
   // @Schema(description =  "统计日期 每日或每月1号到天的是时间戳")
   // private Long dayMillis;

  //  @Schema(title = "报表统计类型 0:日报 1:月报")
   // private String reportType;

   // @Schema(title = "报表统计日期")
    //@Schema(title = "报表统计日期 天或者月 yyyy-MM-dd")
   // @ExcelProperty("统计日期")
   // private String reportDate;

  //  @Schema(description =  "站点Code")
  //  private String siteCode;

    @Schema(description =  "代理Id")
    private String agentId;

    @Schema(description =  "代理账号")
    @ExcelProperty("总代账号")
    private String agentAccount;


   // @Schema(description =  "代理层级")
   // private Integer level;

    //@Schema(description =  "代理层级名称")
   // private String levelName;

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

    @Schema(title = "抽成类型")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.COMMISSION_PLAN)
    private Integer commissionPlan;

    @Schema(title = "账号类型")
    @ExcelProperty("抽成类型")
    private String commissionPlanText;

    @Schema(description =  "直属上级代理ID")
    private String parentId;

    @Schema(description =  "直属上级代理账号")
    private String parentAccount;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "代理标签id")
    private String agentLabelId;

    @Schema(description = "代理标签")
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
    @ExcelProperty("代理人数")
    private Long teamAgentNum=0L;

    @Schema(description =  "会员人数")
    @ExcelProperty("会员人数")
    private Long userNum=0L;

    @Schema(description =  "直属下级人数")
   // @ExcelProperty("直属下级人数")
    private Long directReportNum=0L;
    @Schema(description =  "注册人数")
   // @ExcelProperty("注册人数")
    private Long registerUserNum=0L;
    @Schema(description =  "首存人数")
    @ExcelProperty("首存人数")
    private Long firstDepositNum=0L;
    @Schema(description =  "首存转换率=首存人数 / 注册人数")
   // @ExcelProperty("首存转换率")
    private String firstDepositRate;
    //首存转换率 原始数据
    private BigDecimal firstDepositRateBigDecimal;


    @Schema(description =  "注单量")
    @ExcelProperty("注单量")
    private Long betUserCount=0L;

    @Schema(description =  "币种")
    @ExcelProperty("币种")
    private String currencyCode;

    @Schema(description = "存款金额")
    @ExcelProperty("存款金额")
    private BigDecimal depositAmount;

    @Schema(description = "提款金额")
    @ExcelProperty("提款金额")
    private BigDecimal withdrawAmount;

    @Schema(description = "存提手续费")
    @ExcelProperty("存提手续费")
    private BigDecimal depositWithdrawFeeAmount;


    @Schema(description =  "投注金额")
    @ExcelProperty("投注额")
    private BigDecimal betAmount = BigDecimal.ZERO;


    @Schema(description =  "有效投注额")
    @ExcelProperty("有效投注额")
    private BigDecimal validAmount= BigDecimal.ZERO;

    @Schema(description =  "返水金额")
    @ExcelProperty("返水金额")
    private BigDecimal rebateAmount= BigDecimal.ZERO;

    @Schema(description =  "会员输赢")
    @ExcelProperty("会员输赢")
    private BigDecimal winLossAmountUser = BigDecimal.ZERO;

    @Schema(description =  "平台总输赢 等于 -会员输赢-")
    @ExcelProperty("总输赢")
    private BigDecimal winLossAmountPlat= BigDecimal.ZERO;



    @Schema(description =  "调整金额(其他调整)->主货币")
    private BigDecimal adjustAmount = BigDecimal.ZERO;

    private BigDecimal winLossRateBigDecimal;

    @Schema(description =  "盈亏比例 等于 平台总输赢 / 投注额")
    private String winLossRate;

    @Schema(description =  "活动优惠")
    @ExcelProperty("活动优惠")
    private BigDecimal activityAmount = BigDecimal.ZERO;


    @Schema(description =  "vip福利")
    @ExcelProperty("vip福利")
    private BigDecimal vipAmount = BigDecimal.ZERO;


    @Schema(description =  "已使用优惠")
    @ExcelProperty("已使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @Schema(description =  "平台币")
    private String platCurrencyCode;


    public void addTeamAgentNum(Long teamAgentNum) {
        teamAgentNum= teamAgentNum==null?0L:teamAgentNum;
        this.teamAgentNum=this.teamAgentNum==null?0L: this.teamAgentNum;
        this.teamAgentNum=this.teamAgentNum+teamAgentNum;
    }

    public void addUserNum(Long userNum) {
        userNum= userNum==null?0L:userNum;
        this.userNum=this.userNum==null?0L: this.userNum;
        this.userNum=this.userNum+userNum;
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


    public void addBetUserCount(Long betUserCount) {
        betUserCount= betUserCount==null?0L:betUserCount;
        this.betUserCount=this.betUserCount==null?0L: this.betUserCount;
        this.betUserCount=this.betUserCount+betUserCount;
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

    public void addDepositAmount(BigDecimal depositAmount){
        depositAmount= depositAmount==null?BigDecimal.ZERO:depositAmount;
        this.depositAmount=this.depositAmount==null?BigDecimal.ZERO: this.depositAmount;
        this.depositAmount=this.depositAmount.add(depositAmount);
    }

    public void addWithdrawAmount(BigDecimal withdrawAmount){
        withdrawAmount= withdrawAmount==null?BigDecimal.ZERO:withdrawAmount;
        this.withdrawAmount=this.withdrawAmount==null?BigDecimal.ZERO: this.withdrawAmount;
        this.withdrawAmount=this.withdrawAmount.add(withdrawAmount);
    }

    public void addDepositWithdrawFeeAmount(BigDecimal depositWithdrawFeeAmount){
        depositWithdrawFeeAmount= depositWithdrawFeeAmount==null?BigDecimal.ZERO:depositWithdrawFeeAmount;
        this.depositWithdrawFeeAmount=this.depositWithdrawFeeAmount==null?BigDecimal.ZERO: this.depositWithdrawFeeAmount;
        this.depositWithdrawFeeAmount=this.depositWithdrawFeeAmount.add(depositWithdrawFeeAmount);
    }


    /*public void addCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount=this.commissionAmount.add(commissionAmount);
    }

    public void addPlatIncome(BigDecimal platIncome) {
        this.platIncome=this.platIncome.add(platIncome);
    }*/
}
