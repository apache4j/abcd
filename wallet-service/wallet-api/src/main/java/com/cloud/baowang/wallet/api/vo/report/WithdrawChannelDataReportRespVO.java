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
@Schema(description = "提现渠道统计报表")
@ExcelIgnoreUnannotated
@I18nClass
public class WithdrawChannelDataReportRespVO {
    @Schema(description = "统计日期")
    @ExcelProperty("时间")
    private String staticDate;
    @Schema(description = "通道名称")
    @ExcelProperty("通道名称")
    private String channelName;
    @Schema(description = "通道代码")
    private String channelCode;
    @Schema(description = "通道类型代码")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.CHANNEL_TYPE)
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
    @Schema(description = "提现类型")
    @I18nField(type=I18nFieldTypeConstants.DICT,value = CommonConstant.WITHDRAW_TYPE)
    private String withdrawWithdrawTypeCode;
    @Schema(description = "提现类型名称")
    @ExcelProperty("提现类型")
    private String withdrawWithdrawTypeCodeText;
    @Schema(description = "提现方式")
    @ExcelProperty("提现方式")
    @I18nField
    private String withdrawWithdrawWay;
    @Schema(description = "提现区间")
    @ExcelProperty("提现区间")
    private String withdrawRange;
    @Schema(description = "手续费率")
    private BigDecimal feeRate;

    @Schema(description = "单笔固定手续费")
    private BigDecimal feeFixedAmount;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;
    @Schema(description = "提现笔数")
    @ExcelProperty("提现笔数")
    private Long withdrawNum;
    @Schema(description = "大额提现笔数")
    @ExcelProperty("大额提现笔数")
    private Long withdrawBigNum;
    @Schema(description = "提现人数")
    @ExcelProperty("提现人数")
    private Long withdrawUserNum;
    @Schema(description = "大额提现人数")
    @ExcelProperty("大额提现人数")
    private Long withdrawBigUserNum;
    @Schema(description = "提现总金额")
    @ExcelProperty("提现总金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal withdrawSumAmount;
    @Schema(description = "提现成功笔数")
    @ExcelProperty("提现成功笔数")
    private Long withdrawSuccessNum;
    @Schema(description = "大额提现成功笔数")
    @ExcelProperty("大额提现成功笔数")
    private Long withdrawBigSuccessNum;
    @Schema(description = "提现成功人数")
    @ExcelProperty("提现成功人数")
    private Long withdrawSuccessUserNum;
    @Schema(description = "大额提现成功人数")
    @ExcelProperty("大额提现成功人数")
    private Long withdrawBigSuccessUserNum;
    @Schema(description = "'提现成功金额")
    @ExcelProperty("提现成功金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal withdrawSuccessSumAmount;
    @Schema(description = "提现成功率")
    @ExcelProperty("提现成功率")
    private String withdrawSuccessRate;

    public WithdrawChannelDataReportRespVO addFeeAmount(BigDecimal feeAmount){
        BigDecimal resultAmount = this.getFeeAmount()==null?BigDecimal.ZERO:this.getFeeAmount();
        feeAmount=feeAmount==null?BigDecimal.ZERO:feeAmount;
        resultAmount=resultAmount.add(feeAmount);
        this.feeAmount=resultAmount;
        return this;
    }

    public WithdrawChannelDataReportRespVO addWithdrawNum(Long withdrawNum){
        Long resultNum = this.getWithdrawNum()==null?0:this.getWithdrawNum();
        long addNum=withdrawNum==null?0:withdrawNum;
        resultNum=resultNum+addNum;
        this.withdrawNum=resultNum;
        return this;
    }

    public WithdrawChannelDataReportRespVO addWithdrawBigNum(Long withdrawBigNum){
        Long resultNum = this.getWithdrawBigNum()==null?0:this.getWithdrawBigNum();
        long addNum=withdrawBigNum==null?0:withdrawBigNum;
        resultNum=resultNum+addNum;
        this.withdrawBigNum=resultNum;
        return this;
    }

    public WithdrawChannelDataReportRespVO addWithdrawSumAmount(BigDecimal withdrawSumAmount){
        BigDecimal resultAmount = this.getWithdrawSumAmount()==null?BigDecimal.ZERO:this.getWithdrawSumAmount();
        withdrawSumAmount=withdrawSumAmount==null?BigDecimal.ZERO:withdrawSumAmount;
        resultAmount=resultAmount.add(withdrawSumAmount);
        this.withdrawSumAmount=resultAmount;
        return this;
    }
    public WithdrawChannelDataReportRespVO addWithdrawSuccessNum(Long withdrawSuccessNum){
        Long resultNum = this.getWithdrawSuccessNum()==null?0:this.getWithdrawSuccessNum();
        long addNum=withdrawSuccessNum==null?0:withdrawSuccessNum;
        resultNum=resultNum+addNum;
        this.withdrawSuccessNum=resultNum;
        return this;
    }

    public WithdrawChannelDataReportRespVO addWithdrawBigSuccessNum(Long withdrawBigSuccessNum){
        Long resultNum = this.getWithdrawBigSuccessNum()==null?0:this.getWithdrawBigSuccessNum();
        long addNum=withdrawBigSuccessNum==null?0:withdrawBigSuccessNum;
        resultNum=resultNum+addNum;
        this.withdrawBigSuccessNum=resultNum;
        return this;
    }

    public WithdrawChannelDataReportRespVO addWithdrawSuccessSumAmount(BigDecimal withdrawSuccessSumAmount){
        BigDecimal resultAmount = this.getWithdrawSuccessSumAmount()==null?BigDecimal.ZERO:this.getWithdrawSuccessSumAmount();
        withdrawSuccessSumAmount=withdrawSuccessSumAmount==null?BigDecimal.ZERO:withdrawSuccessSumAmount;
        resultAmount=resultAmount.add(withdrawSuccessSumAmount);
        this.withdrawSuccessSumAmount=resultAmount;
        return this;
    }

    /**
     * 提现成功率=提现成功笔数➗提现笔数
     * @return 提现成功率
     */
    public String getWithdrawSuccessRate(){
        if(this.getWithdrawNum()==null||this.getWithdrawNum()==0){
            return "0%";
        }
        if(this.getWithdrawSuccessNum()==null||this.getWithdrawSuccessNum()==0){
            return "0%";
        }
        BigDecimal successRate= AmountUtils.divide(new BigDecimal(this.getWithdrawSuccessNum()),new BigDecimal(this.getWithdrawNum()),4);
        return AmountUtils.format(successRate.multiply(new BigDecimal("100"))).concat("%");
    }
}
