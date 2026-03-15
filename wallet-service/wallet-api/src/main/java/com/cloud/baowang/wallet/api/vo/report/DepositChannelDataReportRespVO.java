package com.cloud.baowang.wallet.api.vo.report;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/10 19:26
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值渠道统计报表")
@ExcelIgnoreUnannotated
@I18nClass
public class DepositChannelDataReportRespVO {
    @Schema(description = "统计日期")
    @ExcelProperty("时间")
    private String staticDate;
    @Schema(description = "通道名称")
    @I18nField(type=I18nFieldTypeConstants.DICT,value = CommonConstant.PAY_CHANNEL_NAME)
    private String channelName;
    @Schema(description = "通道名称多语言")
    @ExcelProperty("通道名称")
    private String channelNameText;
    @Schema(description = "通道代码")
    private String channelCode;
    @Schema(description = "通道类型代码")
    @I18nField(type=I18nFieldTypeConstants.DICT,value = CommonConstant.CHANNEL_TYPE)
    private String channelType;
    @Schema(description = "通道类型名称")
    @ExcelProperty("通道类型")
    private String channelTypeText;

    @Schema(description = "站点代码")
    private String siteCode;
    @Schema(description = "站点名称")
    @ExcelProperty("站点名称")
    private String siteName;
    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currencyCode;
    @Schema(description = "充值类型")
    @I18nField(type=I18nFieldTypeConstants.DICT,value = CommonConstant.RECHARGE_TYPE)
    private String depositWithdrawTypeCode;
    @Schema(description = "充值类型名称")
    @ExcelProperty("充值类型")
    private String depositWithdrawTypeCodeText;

    @Schema(description = "充值方式")
    @ExcelProperty("充值方式")
    @I18nField
    private String depositWithdrawWay;
    @Schema(description = "充值区间")
    @ExcelProperty("充值区间")
    private String rechargeRange;
    @Schema(description = "手续费率")
    private BigDecimal feeRate;

    @Schema(description = "单笔固定手续费")
    private BigDecimal feeFixedAmount;
    @Schema(description = "手续费")
    private BigDecimal feeAmount;
    @ExcelProperty("充值笔数")
    @Schema(description = "充值笔数")
    private Long depositNum;
    @Schema(description = "充值人数")
    @ExcelProperty("充值人数")
    private Long depositUserNum;
    @Schema(description = "充值总金额")
    @ExcelProperty("充值总金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositSumAmount;
    @Schema(description = "充值成功笔数")
    @ExcelProperty("充值成功笔数")
    private Long depositSuccessNum;
    @Schema(description = "充值成功人数")
    @ExcelProperty("充值成功人数")
    private Long depositSuccessUserNum;
    @Schema(description = "'充值成功金额")
    @ExcelProperty("充值成功金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositSuccessSumAmount;
    @Schema(description = "充值成功率")
    @ExcelProperty("充值成功率")
    private String depositSuccessRate;

    public DepositChannelDataReportRespVO addFeeAmount(BigDecimal feeAmount){
        BigDecimal resultAmount = this.getFeeAmount()==null?BigDecimal.ZERO:this.getFeeAmount();
        feeAmount=feeAmount==null?BigDecimal.ZERO:feeAmount;
        resultAmount=resultAmount.add(feeAmount);
        this.feeAmount=resultAmount;
        return this;
    }

    public DepositChannelDataReportRespVO addDepositNum(Long depositNum){
        Long resultNum = this.getDepositNum()==null?0:this.getDepositNum();
        long addNum=depositNum==null?0:depositNum;
        resultNum=resultNum+addNum;
        this.depositNum=resultNum;
        return this;
    }

    public DepositChannelDataReportRespVO addDepositSumAmount(BigDecimal depositSumAmount){
        BigDecimal resultAmount = this.getDepositSumAmount()==null?BigDecimal.ZERO:this.getDepositSumAmount();
        depositSumAmount=depositSumAmount==null?BigDecimal.ZERO:depositSumAmount;
        resultAmount=resultAmount.add(depositSumAmount);
        this.depositSumAmount=resultAmount;
        return this;
    }
    public DepositChannelDataReportRespVO addDepositSuccessNum(Long depositSuccessNum){
        Long resultNum = this.getDepositSuccessNum()==null?0:this.getDepositSuccessNum();
        long addNum=depositSuccessNum==null?0:depositSuccessNum;
        resultNum=resultNum+addNum;
        this.depositSuccessNum=resultNum;
        return this;
    }

    public DepositChannelDataReportRespVO addDepositSuccessSumAmount(BigDecimal depositSuccessSumAmount){
        BigDecimal resultAmount = this.getDepositSuccessSumAmount()==null?BigDecimal.ZERO:this.getDepositSuccessSumAmount();
        depositSuccessSumAmount=depositSuccessSumAmount==null?BigDecimal.ZERO:depositSuccessSumAmount;
        resultAmount=resultAmount.add(depositSuccessSumAmount);
        this.depositSuccessSumAmount=resultAmount;
        return this;
    }

    /**
     * 充值成功率=充值成功笔数➗充值笔数
     * @return 充值成功率
     */
    public String getDepositSuccessRate(){
        if(this.getDepositNum()==null||this.getDepositNum()==0){
            return "0%";
        }
        if(this.getDepositSuccessNum()==null||this.getDepositSuccessNum()==0){
            return "0%";
        }
        BigDecimal successRate= AmountUtils.divide(new BigDecimal(this.getDepositSuccessNum()),new BigDecimal(this.getDepositNum()),4);
        return AmountUtils.format(successRate.multiply(new BigDecimal("100"))).concat("%");
    }
}
