package com.cloud.baowang.report.api.vo.excel;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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
public class UserInfoStatementExportVO implements Serializable {
    @Schema(title = "平台币code")
    private String platCurrencyCode;

    @Schema(title = "会员Id")
    private String userId;

    @Schema(title = "会员账号")
    @ExcelProperty("会员账号")
    private String userAccount;

    @Schema(title = "账号类型 1测试 2正式 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @ExcelProperty("账号类型")
    @Schema(title = "账号类型 name")
    private String accountTypeText;

    @Schema(title = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    @Schema(title = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 Name")
    @ExcelProperty("账号状态")
    private String accountStatusText;


    @Schema(title = "主币种")
    @ExcelProperty("主币种")
    private String mainCurrency;

    @Schema(title = "VIP段位")
    private Integer vipRankCode;

    @I18nField
    @ExcelProperty("VIP段位")
    @Schema(description = "vip段位名称")
    private String vipRankCodeName;

    @Schema(title = "vip等级")
    private Integer vipGradeCode;

    @Schema(title = "vip等级名称")
    @ExcelProperty("VIP等级")
    private String vipGradeCodeName;

    @Schema(title = "会员标签 id")
    private String userLabelId;

    @Schema(title = "会员标签 name")
    @ExcelProperty("会员标签")
    private String userLabelName;

    @Schema(title = "上级代理id")
    private String superAgentId;

    @Schema(title = "上级代理账号")
    @ExcelProperty("上级代理")
    private String superAgentAccount;

    @Schema(title = "转代次数")
    @ExcelProperty("转代次数")
    private Integer transAgentTime;

    @Schema(title = "注册时间")
    private Long registerTime;

    @Schema(title = "注册时间 name")
    @ExcelProperty("注册时间")
    private String registerTimeStr;

    @Schema(title = "首存时间")
    private Long firstDepositTime;


    @Schema(title = "首存时间")
    @ExcelProperty("首存时间")
    private String firstDepositTimeStr;


    public String getFirstDepositTimeStr() {
        if (ObjectUtils.isEmpty(this.firstDepositTime)) {
            return StringUtils.EMPTY;
        }
        return TimeZoneUtils.formatTimestampToTimeZone(this.firstDepositTime, CurrReqUtils.getTimezone());
    }


    @Schema(title = "首存金额")
    //@ExcelProperty("首存金额 纯数字")
    private BigDecimal firstDepositAmount;

    @Schema(title = "首存金额")
    @ExcelProperty("首存金额")
    private String firstDepositAmountText;

    public String getFirstDepositAmountText() {
        //return firstDepositAmount + mainCurrency;
        if (!convertPlatCurrency) {
            return firstDepositAmount + mainCurrency;  // 拼接 bettingProfitLoss 和 mainCurrency
        } else {
            return firstDepositAmount + platCurrencyCode;  // 拼接 bettingProfitLoss 和 mainCurrency
        }
    }

    @Schema(title = "总存款")
    private BigDecimal totalDeposit = BigDecimal.ZERO;

    @Schema(title = "总存款")
    @ExcelProperty("总存款")
    private String totalDepositText;

    public String getTotalDepositText() {
        //return totalDeposit + mainCurrency;
        if (!convertPlatCurrency) {
            return totalDeposit + mainCurrency;
        } else {
            return totalDeposit + platCurrencyCode;
        }
    }

    @Schema(title = "存款次数")
    @ExcelProperty("存款次数")
    private Integer numberDeposit;

    @Schema(title = "上级转入")
    private BigDecimal advancedTransfer = BigDecimal.ZERO;


    @Schema(title = "上级转入")
    //@ExcelProperty("上级转入")
    private String advancedTransferText;

    public String getAdvancedTransferText() {
        //return advancedTransfer + mainCurrency;
        if (!convertPlatCurrency) {
            return advancedTransfer + mainCurrency;
        } else {
            return advancedTransfer + platCurrencyCode;
        }
    }

    @Schema(title = "上级转入次数")
    //@ExcelProperty("转入次数")
    private Integer numberTransfer = 0;

    @Schema(title = "总取款")
    private BigDecimal totalWithdrawal = BigDecimal.ZERO;

    @Schema(title = "总取款")
    @ExcelProperty("总取款")
    private String totalWithdrawalText;

    public String getTotalWithdrawalText() {
        //return totalWithdrawal + mainCurrency;
        if (!convertPlatCurrency) {
            return totalWithdrawal + mainCurrency;
        } else {
            return totalWithdrawal + platCurrencyCode;
        }
    }



    @Schema(title = "取款次数")
    @ExcelProperty("取款次数")
    private Integer numberWithdrawal = 0;

    @Schema(title = "大额取款次数")
    @ExcelProperty("大额取款次数")
    private Integer numberLargeWithdrawal = 0;

    @Schema(title = "存取差")
    private BigDecimal poorAccess = BigDecimal.ZERO;


    @Schema(title = "存取差")
    @ExcelProperty("存取差")
    private String poorAccessText;

    public String getPoorAccessText() {
        //return poorAccess + mainCurrency;
        if (!convertPlatCurrency) {
            return poorAccess + mainCurrency;
        } else {
            return poorAccess + platCurrencyCode;
        }
    }



    @Schema(title = "vip金额")
    private BigDecimal vipAmount = BigDecimal.ZERO;

    @Schema(title = "VIP福利")
    @ExcelProperty("VIP福利")
    private String vipAmountText;

    public String getVipAmountText() {
        return vipAmount + platCurrencyCode ;
        /*if (!convertPlatCurrency) {
            return vipAmount + mainCurrency;
        } else {
            return vipAmount + platCurrencyCode;
        }*/
    }


    @Schema(title = "优惠金额")
    private BigDecimal activityAmount = BigDecimal.ZERO;

    @Schema(title = "活动优惠")
    @ExcelProperty("活动优惠")
    private String activityAmountText;

    public String getActivityAmountText() {
        return activityAmount + platCurrencyCode ;
        /*if (!convertPlatCurrency) {
            return activityAmount + mainCurrency;
        } else {
            return activityAmount + platCurrencyCode;
        }*/
    }

    @Schema(title = "返水")
    private BigDecimal rebateAmount = BigDecimal.ZERO;

    @Schema(title = "返水")
    @ExcelProperty("返水")
    private String rebateAmountText;

    public String getRebateAmountText() {
        return rebateAmount + mainCurrency ;
    }


    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @Schema(title = "已使用优惠")
    @ExcelProperty("已使用优惠")
    private String alreadyUseAmountText;

    public String getAlreadyUseAmountText() {
        //return alreadyUseAmount + mainCurrency ;
        if (!convertPlatCurrency) {
            return alreadyUseAmount + mainCurrency;
        } else {
            return alreadyUseAmount + platCurrencyCode;
        }
    }



    @Schema(title = "其他调整")
    private BigDecimal otherAdjustments = BigDecimal.ZERO;

    @Schema(title = "其他调整")
    @ExcelProperty("其他调整")
    private String otherAdjustmentsText;

    public String getOtherAdjustmentsText() {
        //return otherAdjustments + mainCurrency ;
        if (!convertPlatCurrency) {
            return otherAdjustments + mainCurrency;
        } else {
            return otherAdjustments + platCurrencyCode;
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




    @Schema(title = "注单量")
    @ExcelProperty("注单量")
    private Integer placeOrderQuantity = 0;

    @Schema(title = "投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(title = "投注金额")
    @ExcelProperty("投注金额")
    private String betAmountText;

    public String getBetAmountText() {
        //return betAmount + mainCurrency;  // 拼接 betAmount 和 mainCurrency
        if (!convertPlatCurrency) {
            return betAmount + mainCurrency;
        } else {
            return betAmount + platCurrencyCode;
        }
    }


    @Schema(title = "有效投注金额")
    private BigDecimal activeBet = BigDecimal.ZERO;

    @Schema(title = "有效投注金额")
    @ExcelProperty("有效投注金额")
    private String activeBetText;

    public String getActiveBetText() {
        //return activeBet + mainCurrency;  // 拼接 activeBet 和 mainCurrency
        if (!convertPlatCurrency) {
            return activeBet + mainCurrency;
        } else {
            return activeBet + platCurrencyCode;
        }
    }

    @Schema(title = "投注盈亏就是会员输赢")
    private BigDecimal bettingProfitLoss = BigDecimal.ZERO;

    @Schema(title = "会员投注输赢")
    @ExcelProperty("会员投注输赢")
    private String bettingProfitLossText;

    public String getBettingProfitLossText() {
        //return bettingProfitLoss + mainCurrency;  // 拼接 bettingProfitLoss 和 mainCurrency
        if (!convertPlatCurrency) {
            return bettingProfitLoss + mainCurrency;
        } else {
            return bettingProfitLoss + platCurrencyCode;
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

    @Schema(title = "净盈利")
    private BigDecimal totalPreference = BigDecimal.ZERO;

    @Schema(title = "净盈利")
    @ExcelProperty("会员净盈利")
    private String totalPreferenceText;

    @Schema(title = "中心钱包余额")
    //@ExcelProperty("钱包余额(主货币)")
    private BigDecimal centerAmount = BigDecimal.ZERO;

    @Schema(title = "净盈利")
    @ExcelProperty("钱包余额(主货币)")
    private String centerAmountText;

    public String getCenterAmountText() {
        //return firstDepositAmount + mainCurrency;
        if (!convertPlatCurrency) {
            return centerAmount + mainCurrency;  // 拼接 bettingProfitLoss 和 mainCurrency
        } else {
            return centerAmount + platCurrencyCode;  // 拼接 bettingProfitLoss 和 mainCurrency
        }
    }

    /*@Schema(title = "中心钱包主货币余额")
    private BigDecimal totalMainAmount = BigDecimal.ZERO;*/

    @Schema(title = "中心钱包平台币余额")
    //@ExcelProperty("钱包余额(平台币)")
    private BigDecimal totalPlatAmount = BigDecimal.ZERO;

    @Schema(title = "中心钱包平台币余额")
    @ExcelProperty("钱包余额(平台币)")
    private String totalPlatAmountText ;

    public String getTotalPlatAmountText() {
        return totalPlatAmount + platCurrencyCode;
    }

    @Schema(title = "registerIp 注册ip")
    @ExcelProperty("注册IP")
    private String registerIp;


    @Schema(title = "registerIp 注册ip")
    @ExcelProperty("注册IP归属地")
    private String ipAddress;

    @Schema(title = "registry 注册终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    private String registry;

    @Schema(title = "registry 注册终端")
    @ExcelProperty("注册终端")
    private String registryText;


    @Schema(title = "最后登录时间")
    private Long lastLoginTime;

    @Schema(title = "最后登录时间")
    @ExcelProperty("最后登录时间")
    private String lastLoginTimeStr;

    public String getLastLoginTimeStr() {
        if (ObjectUtils.isEmpty(this.lastLoginTime)) {
            return StringUtils.EMPTY;
        }
        return TimeZoneUtils.formatTimestampToTimeZone(this.lastLoginTime, CurrReqUtils.getTimezone());
    }

    public String getTotalPreferenceText() {
        //return totalPreference + mainCurrency;  // 拼接 totalPreference 和 mainCurrency
        if (!convertPlatCurrency) {
            return totalPreference + mainCurrency;
        } else {
            return totalPreference + platCurrencyCode;
        }
    }


    // Getter for registerTimeStr
    public String getRegisterTimeStr() {
        if (ObjectUtils.isEmpty(this.registerTime)) {
            return StringUtils.EMPTY;
        }
        return TimeZoneUtils.formatTimestampToTimeZone(this.registerTime, CurrReqUtils.getTimezone());
    }

    public BigDecimal getFirstDepositAmount() {
        if (ObjectUtil.isEmpty(firstDepositAmount)) {
            firstDepositAmount = BigDecimal.valueOf(0);
        }
        return firstDepositAmount;
    }

    public Integer getNumberDeposit() {
        if (ObjectUtil.isEmpty(numberDeposit)) {
            numberDeposit = 0;
        }
        return numberDeposit;
    }

    public BigDecimal getAdvancedTransfer() {
        if (ObjectUtil.isEmpty(advancedTransfer)) {
            advancedTransfer = BigDecimal.valueOf(0);
        }
        return advancedTransfer;
    }

    @Schema(description = "是否转换为平台币")
    private Boolean convertPlatCurrency = Boolean.FALSE;
}
