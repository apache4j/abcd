package com.cloud.baowang.report.api.vo.agent;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商务报表 resp
 */
@Data
@Schema(description =  "商务报表 resp")
@ExcelIgnoreUnannotated
@I18nClass
public class ReportAgentMerchantStaticsResponseVO {

    /**
     * utc 整点对应时间戳
     */
    @Schema(description =  "统计日期 每日或每月1号到天的是时间戳")
    private Long dayMillis;

    @Schema(title = "报表统计类型 0:日报 1:月报")
    private String reportType;

    @Schema(title = "报表统计日期-excel")
    //@Schema(title = "报表统计日期 天或者月 yyyy-MM-dd")
    @ExcelProperty("统计日期")
    private String reportDate;

    @Schema(description =  "站点Code")
    private String siteCode;

    @Schema(description = "商务账号-excel")
    @ExcelProperty("商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称-excel")
    @ExcelProperty("商务名称")
    private String merchantName;


    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级-excel")
    @ExcelProperty("风控层级")
    private String riskLevel;


    @Schema(description =  "注册时间")
    private Long registerTime;

    @Schema(description =  "注册时间-excel")
    @ExcelProperty("注册时间")
    private String registerTimeStr;

    @Schema(description =  "直属总代人数-excel")
    @ExcelProperty("直属总代人数")
    private Long directAgentNum=0L;

    @Schema(description =  "团队代理人数-excel")
    @ExcelProperty("团队代理人数")
    private Long teamAgentNum=0L;


    @Schema(description =  "注册人数-excel")
    @ExcelProperty("注册人数")
    private Long registerUserNum=0L;
    @Schema(description =  "首存人数-excel")
    @ExcelProperty("首存人数")
    private Long firstDepositNum=0L;

    @Schema(description =  "注单量-excel")
    @ExcelProperty("注单量")
    private Long betUserNum=0L;

    @Schema(description =  "币种-excel")
    @ExcelProperty("币种")
    private String currencyCode;

    @Schema(description = "存款金额-excel")
    @ExcelProperty("存款金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositAmount= BigDecimal.ZERO;

    @Schema(description = "取款金额-excel")
    @ExcelProperty("取款金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal withdrawAmount= BigDecimal.ZERO;

    @Schema(description = "存提手续费-excel")
    @ExcelProperty("存提手续费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositWithdrawFee= BigDecimal.ZERO;

    @Schema(description =  "投注金额-excel")
    @ExcelProperty("投注额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount = BigDecimal.ZERO;
    @Schema(description =  "投注金额 ")
    private String betAmountCurrency;

    @Schema(description =  "有效投注额-excel")
    @ExcelProperty("有效投注额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validAmount= BigDecimal.ZERO;

    @Schema(description =  "打赏金额")
    @ExcelProperty("打赏金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal tipsAmount = BigDecimal.ZERO;


    @Schema(description =  "会员输赢-excel")
    @ExcelProperty("会员输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLossAmountUser = BigDecimal.ZERO;


    @Schema(title = "调整金额(其他调整)-主货币")
    @ExcelProperty("其他调整")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal adjustAmount;

    @Schema(description =  "平台总输赢 等于 -会员输赢-excel")
    @ExcelProperty("平台总输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLossAmountPlat= BigDecimal.ZERO;


    @Schema(description =  "已使用优惠-excel")
    @ExcelProperty("已使用优惠")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;


    @Schema(description =  "平台币代码")
    private String platCurrencyCode;

    @Schema(description =  "返水金额")
   // @ExcelProperty("返水金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rebateAmount = BigDecimal.ZERO;





    public void addTeamAgentNum(Long teamAgentNum) {
        teamAgentNum= teamAgentNum==null?0L:teamAgentNum;
        this.teamAgentNum=this.teamAgentNum==null?0L: this.teamAgentNum;
        this.teamAgentNum=this.teamAgentNum+teamAgentNum;
    }

    public void addDirectAgentNum(Long directAgentNum) {
        directAgentNum= directAgentNum==null?0L:directAgentNum;
        this.directAgentNum=this.directAgentNum==null?0L: this.directAgentNum;
        this.directAgentNum=this.directAgentNum+directAgentNum;
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

    public void addBetUserNum(Long betUserNum){
        betUserNum= betUserNum==null?0L:betUserNum;
        this.betUserNum=this.betUserNum==null?0L: this.betUserNum;
        this.betUserNum=this.betUserNum+betUserNum;
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
    public void addDepositWithdrawFee(BigDecimal depositWithdrawFeeAmount){
        depositWithdrawFeeAmount= depositWithdrawFeeAmount==null?BigDecimal.ZERO:depositWithdrawFeeAmount;
        this.depositWithdrawFee=this.depositWithdrawFee==null?BigDecimal.ZERO: this.depositWithdrawFee;
        this.depositWithdrawFee=this.depositWithdrawFee.add(depositWithdrawFeeAmount);
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


    public void addAlreadyUseAmount(BigDecimal alreadyUseAmount) {
        alreadyUseAmount= alreadyUseAmount==null?BigDecimal.ZERO:alreadyUseAmount;
        this.alreadyUseAmount=this.alreadyUseAmount==null?BigDecimal.ZERO: this.alreadyUseAmount;
        this.alreadyUseAmount=this.alreadyUseAmount.add(alreadyUseAmount);
    }


    public void addRebateAmount(BigDecimal rebateAmount){
        rebateAmount= rebateAmount==null?BigDecimal.ZERO:rebateAmount;
        this.rebateAmount=this.rebateAmount==null?BigDecimal.ZERO: this.rebateAmount;
        this.rebateAmount=this.rebateAmount.add(rebateAmount);
    }

    public void addTipsAmount(BigDecimal tipsAmount){
        tipsAmount= tipsAmount==null?BigDecimal.ZERO:tipsAmount;
        this.tipsAmount=this.tipsAmount==null?BigDecimal.ZERO: this.tipsAmount;
        this.tipsAmount=this.tipsAmount.add(tipsAmount);
    }

    public void addAdjustAmount(BigDecimal adjustAmount){
        adjustAmount= adjustAmount==null?BigDecimal.ZERO:adjustAmount;
        this.adjustAmount=this.adjustAmount==null?BigDecimal.ZERO: this.adjustAmount;
        this.adjustAmount=this.adjustAmount.add(adjustAmount);
    }


}
