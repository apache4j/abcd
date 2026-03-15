package com.cloud.baowang.site.vo.export.daily;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "每日盈亏导出vo")
@ExcelIgnoreUnannotated
@I18nClass
public class DailyWinLoseExportVO {
    /**
     * utc 整点对应时间戳
     */
    @Schema(description = "yyyy-MM-dd 00:00:00对应的时间戳")
    private Long dayMillisTime;

    @ExcelProperty("日期")
    @ColumnWidth(10)
    private String dayMillisTimeStr;

    public String getDayMillisTimeStr() {
        return dayMillisTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(dayMillisTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "yyyy-MM-dd HH:00:00对应的时间戳")
    private Long dayHourMillis;

    @Schema(description = "当天时间")
    private String dayStr;

    @Schema(description = "主货币")
    @ExcelProperty("主币种")
    @ColumnWidth(8)
    private String mainCurrency;

    @Schema(description = "平台币")
    private String platformCurrency;

    @Schema(description = "vip福利")
    @ExcelProperty("vip福利")
    @ColumnWidth(8)
    private BigDecimal vipAmount = BigDecimal.ZERO;

    /*@ExcelProperty("vip福利")
    @ColumnWidth(8)
    private String vipAmountStr;

    public String getVipAmountStr() {
        return vipAmount.toString() + platformCurrency;
    }*/

    @Schema(description = "优惠金额")
    @ExcelProperty("活动优惠")
    @ColumnWidth(8)
    private BigDecimal activityAmount = BigDecimal.ZERO;

    /*@ExcelProperty("活动优惠")
    @ColumnWidth(8)
    private String activityAmountStr;

    public String getActivityAmountStr() {
        return activityAmount.toString() + platformCurrency;
    }*/

    @Schema(description = "返水")
    private BigDecimal rebateAmount = BigDecimal.ZERO;

    @Schema(title = "返水")
    @ExcelProperty("返水")
    private String rebateAmountText;

    public String getRebateAmountText() {
        return rebateAmount + mainCurrency ;
    }

    @ExcelProperty("已使用优惠")
    @ColumnWidth(8)
    @Schema(description = "已经使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    /*@ExcelProperty("已使用优惠")
    @ColumnWidth(8)
    private String alreadyUseAmountStr;

    public String getAlreadyUseAmountStr() {
        if (isToPlatCurr()) {
            alreadyUseAmountStr = alreadyUseAmount.toString() + platformCurrency;
        } else {
            alreadyUseAmountStr = alreadyUseAmount.toString() + mainCurrency;
        }
        return alreadyUseAmountStr;
    }*/

    @Schema(description = "调整金额(其他调整)")
    @ExcelProperty("其他调整")
    @ColumnWidth(8)
    private BigDecimal adjustAmount = BigDecimal.ZERO;


    @Schema(description = "平台币其他调整")
    @ExcelProperty("平台币其他调整")
    @ColumnWidth(12)
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;


    @Schema(description = "打赏金额")
    @ExcelProperty("打赏金额")
    @ColumnWidth(8)
    private BigDecimal tipsAmount = BigDecimal.ZERO;


    /*@ExcelProperty("其他调整")
    @ColumnWidth(8)
    private String adjustAmountStr;

    public String getAdjustAmountStr() {
        if (isToPlatCurr()) {
            adjustAmountStr = adjustAmount.toString() + platformCurrency;
        } else {
            adjustAmountStr = adjustAmount.toString() + mainCurrency;
        }
        return adjustAmountStr;
    }*/

    @Schema(description = "投注人数")
    @ExcelProperty("投注人数")
    @ColumnWidth(5)
    private Integer betMemNum = 0;

    @Schema(description = "注单量")
    @ExcelProperty("注单量")
    @ColumnWidth(5)
    private Integer betNum = 0;

    @Schema(description = "投注金额")
    @ExcelProperty("投注金额")
    @ColumnWidth(15)
    private BigDecimal betAmount = BigDecimal.ZERO;

    /*@ExcelProperty("投注金额")
    @ColumnWidth(15)
    private String betAmountStr;

    public String getBetAmountStr() {
        if (isToPlatCurr()) {
            betAmountStr = betAmount.toString() + platformCurrency;
        } else {
            betAmountStr = betAmount.toString() + mainCurrency;
        }
        return betAmountStr;
    }*/

    @Schema(description = "有效投注金额")
    @ExcelProperty("有效投注金额")
    @ColumnWidth(15)
    private BigDecimal validBetAmount = BigDecimal.ZERO;

    /*@ExcelProperty("有效投注")
    @ColumnWidth(15)
    private String validBetAmountStr;

    public String getValidBetAmountStr() {
        if (isToPlatCurr()) {
            validBetAmountStr = validBetAmount.toString() + platformCurrency;
        } else {
            validBetAmountStr = validBetAmount.toString() + mainCurrency;
        }
        return validBetAmountStr;
    }*/

    @Schema(description = "平台投注输赢")
    @ExcelProperty("平台投注输赢")
    @ColumnWidth(15)
    private BigDecimal betWinLose = BigDecimal.ZERO;

    /*@ExcelProperty("平台输赢")
    @ColumnWidth(15)
    private String betWinLoseStr;

    public String getBetWinLoseStr() {
        if (isToPlatCurr()) {
            betWinLoseStr = betWinLose.toString() + platformCurrency;
        } else {
            betWinLoseStr = betWinLose.toString() + mainCurrency;
        }
        return betWinLoseStr;
    }*/

    @Schema(description = "平台净输赢")
    @ExcelProperty("平台净输赢")
    @ColumnWidth(15)
    private BigDecimal profitAndLoss = BigDecimal.ZERO;




    /*@ExcelProperty("净盈亏")
    @ColumnWidth(15)
    private String profitAndLossStr;

    public String getProfitAndLossStr() {
        if (isToPlatCurr()) {
            profitAndLossStr = profitAndLoss.toString() + platformCurrency;
        } else {
            profitAndLossStr = profitAndLoss.toString() + mainCurrency;
        }
        return profitAndLossStr;
    }*/

    @Schema(title = "转换为平台币")
    private boolean toPlatCurr;

}
