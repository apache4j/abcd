package com.cloud.baowang.report.api.vo.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 15/5/23 10:34 AM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员盈亏报表下载excel")
@I18nClass
@ExcelIgnoreUnannotated
public class UserWinLoseResponseExportVO implements Serializable {


    @ExcelProperty(value = "会员账号")
    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(title = "账号类型")
    @ExcelProperty(value = "账号类型")
    private String accountTypeText;

    @Schema(title = "主货币")
    @ExcelProperty(value = "主货币")
    private String mainCurrency;

    @Schema(title = "VIP段位")
    private Integer vipRankCode;

    @I18nField
    @Schema(description = "vip段位名称")
    @ExcelProperty(value = "VIP段位")
    @ColumnWidth
    private String vipRankCodeName;

    @Schema(title = "vip等级")
    private Integer vipGradeCode;

    @Schema(title = "vVIP等级名称")
    @ExcelProperty(value = "VIP等级")
    @ColumnWidth
    private String vipGradeCodeName;

    @Schema(title = "会员标签")
    private String userLabelId;

    @Schema(title = "会员标签-Name")
    @ExcelProperty(value = "会员标签")
    private String userLabelIdName;

    @Schema(title = "上级代理")
    @ExcelProperty(value = "上级代理")
    private String superAgentAccount;

    @Schema(title = "注单量")
    @ExcelProperty(value = "注单量")
    private Integer betNum;


    @Schema(title = "投注金额")
    //@ExcelProperty(value = "投注金额")
    private BigDecimal betAmount;

    @Schema(title = "投注金额")
    @ExcelProperty(value = "投注金额")
    private String betAmountText;

    public String getBetAmountText() {
        //return betAmount  + " " + mainCurrency;
        if (!convertPlatCurrency) {
            return betAmount + mainCurrency;
        } else {
            return betAmount + platCurrencyCode;
        }
    }

    @Schema(title = "有效投注")
    //@ExcelProperty(value = "有效投注")
    private BigDecimal validBetAmount;


    @Schema(title = "有效投注金额")
    @ExcelProperty(value = "有效投注金额")
    private String validBetAmountText;

    public String getValidBetAmountText() {
        //return validBetAmount  + " " + mainCurrency;
        if (!convertPlatCurrency) {
            return validBetAmount + mainCurrency;
        } else {
            return validBetAmount + platCurrencyCode;
        }
    }

    @Schema(title = "流水纠正")
    private BigDecimal runWaterCorrect;


    @Schema(title = "会员输赢")
    //@ExcelProperty(value = "会员输赢")
    private BigDecimal betWinLose;

    @Schema(title = "会员输赢")
    @ExcelProperty(value = "会员输赢")
    private String betWinLoseText;

    public String getBetWinLoseText() {
        //return betWinLose  + " " + mainCurrency;
        if (!convertPlatCurrency) {
            return betWinLose + mainCurrency;
        } else {
            return betWinLose + platCurrencyCode;
        }
    }

    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount = BigDecimal.ZERO;
    @Schema(title = "打赏金额")
    @ExcelProperty("打赏金额")
    private String tipsAmountText;

    public String getTipsAmountText() {
        //return bettingProfitLoss + mainCurrency;  // 拼接 bettingProfitLoss 和 mainCurrency
        if (!convertPlatCurrency) {
            return tipsAmount + mainCurrency;
        } else {
            return tipsAmount + platCurrencyCode;
        }
    }


    @Schema(title = "vip优惠金额")
    //@ExcelProperty(value = "VIP福利")
    private BigDecimal vipAmount = BigDecimal.ZERO;

    @Schema(title = "vip优惠金额")
    @ExcelProperty(value = "VIP福利")
    private String vipAmountText;

    public String getVipAmountText() {
        //return vipAmount  + " " + platCurrencyCode;
        return vipAmount + platCurrencyCode;
    }

    @Schema(title = "活动优惠")
    //@ExcelProperty(value = "活动优惠")
    private BigDecimal activityAmount = BigDecimal.ZERO;

    @Schema(title = "活动优惠")
    @ExcelProperty(value = "活动优惠")
    private String activityAmountText;

    public String getActivityAmountText() {
        //return activityAmount  + " " + platCurrencyCode;
        return activityAmount + platCurrencyCode;
    }

    @Schema(title = "返水")
    private BigDecimal rebateAmount;

    @Schema(title = "返水")
    @ExcelProperty(value = "返水")
    private String rebateAmountText;

    public String getRebateAmountText() {
        //return rebateAmount  + " " + mainCurrency;
        if (!convertPlatCurrency) {
            return rebateAmount + mainCurrency;
        } else {
            return rebateAmount + platCurrencyCode;
        }
    }


    @Schema(title = "已经使用优惠金额")
    //@ExcelProperty(value = "已使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;


    @Schema(title = "已经使用优惠金额")
    @ExcelProperty(value = "已使用优惠")
    private String alreadyUseAmountText;

    public String getAlreadyUseAmountText() {
        //return alreadyUseAmount  + " " + mainCurrency;
        if (!convertPlatCurrency) {
            return alreadyUseAmount + mainCurrency;
        } else {
            return alreadyUseAmount + platCurrencyCode;
        }
    }

    @Schema(title = "调整金额(其他调整)")
    //@ExcelProperty(value = "其他调整")
    private BigDecimal adjustAmount = BigDecimal.ZERO;


    @Schema(title = "调整金额(其他调整)")
    @ExcelProperty(value = "其他调整")
    private String adjustAmountText;

    public String getAdjustAmountText() {
        //return adjustAmount  + " " + mainCurrency;
        if (!convertPlatCurrency) {
            return adjustAmount + mainCurrency;
        } else {
            return adjustAmount + platCurrencyCode;
        }
    }
    @Schema(title = "平台币其他调整")
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;

    @Schema(title = "平台币其他调整")
    @ExcelProperty("平台币其他调整")
    private String platAdjustAmountText;

    public String getPlatAdjustAmountText() {
        //return otherAdjustments + mainCurrency ;
        if (!convertPlatCurrency) {
            return platAdjustAmount + mainCurrency;
        } else {
            return platAdjustAmount + platCurrencyCode;
        }
    }
    @Schema(title = "风控调整")
    private BigDecimal riskAmount = BigDecimal.ZERO;

    @Schema(title = "风控调整")
    @ExcelProperty("风控调整")
    private String riskAmountText;

    public String getRiskAmountText() {
        //return otherAdjustments + mainCurrency ;
        return riskAmount + platCurrencyCode;
    }

    @Schema(title = "会员净输赢")
    //@ExcelProperty(value = "净盈利")
    private BigDecimal profitAndLoss;

    @Schema(title = "会员净输赢")
    @ExcelProperty(value = "会员净输赢")
    private String profitAndLossText;

    public String getProfitAndLossText() {
        //return profitAndLoss + " " + mainCurrency;
        if (!convertPlatCurrency) {
            return profitAndLoss + mainCurrency;
        } else {
            return profitAndLoss + platCurrencyCode;
        }
    }

    @Schema(title = "补单其他调整")
    private BigDecimal repairOrderOtherAdjust = BigDecimal.ZERO;


    //------


    @Schema(description = "登录时间")
    //@ExcelProperty(value = "登录时间")
    @ColumnWidth(25)
    private String loginTimeStr;


    @Schema(description = "siteCode")
    private String siteCode;


    @Schema(title = "会员账号")
    private String userId;

    @Schema(title = "姓名")
    private String userName;


    @Schema(title = "代理归属")
    private Integer agentAttribution;

    @Schema(title = "代理归属-Name")
    private String agentAttributionName;


    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;
    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 - Text")
    private String accountStatusText;
    /*@Schema(title =   "账号状态-Name")
    private List<CodeValueVO> accountStatusName;*/


    /*@Schema(title =   "账号状态 - 用于导出")
    private String accountStatusExport;*/


    @Schema(title = "风控层级")
    private String riskLevelId;

    @Schema(title = "风控层级-Name")
    private String riskLevelIdName;


    @Schema(title = "主货币")
    private String platCurrencyCode;

    public String getPlatCurrencyCode() {
        return CurrReqUtils.getPlatCurrencyCode();
    }


    @Schema(description = "同步时间")
    private Long createdTime;

    @Schema(description = "是否转换为平台币")
    private Boolean convertPlatCurrency = Boolean.FALSE;
}
