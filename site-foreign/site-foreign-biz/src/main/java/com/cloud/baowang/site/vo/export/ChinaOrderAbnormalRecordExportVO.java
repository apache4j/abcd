package com.cloud.baowang.site.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@I18nClass
@ExcelIgnoreUnannotated
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "异常注单导出对象")
public class ChinaOrderAbnormalRecordExportVO implements Serializable {
    @Schema(title = "注单ID")
    @ExcelProperty("注单号")
    @ColumnWidth(15)
    private String orderId;
    @Schema(title = "三方注单ID")
    @ExcelProperty("三方注单号")
    @ColumnWidth(15)
    private String thirdOrderId;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "游戏场馆")
    @ExcelProperty("游戏场馆")
    @ColumnWidth(15)
    private String venueCodeText;
    @Schema(description = "游戏类别")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;
    @Schema(description = "游戏类别-文本")
    @ExcelProperty("游戏类别")
    @ColumnWidth(15)
    private String venueTypeText;
    @Schema(title = "局号/赛事id")
    @ExcelProperty("局号/赛事id")
    @ColumnWidth(15)
    private String gameNo;
    @Schema(title = "异常类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ABNORMAL_TYPE)
    private Integer abnormalType;
    @Schema(title = "异常类型名称")
    @ExcelProperty("异常类型")
    @ColumnWidth(15)
    private String abnormalTypeText;
    @Schema(title = "重算状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.RESETTLE_STATUS)
    private Integer reSettleStatus;
    @Schema(title = "重算状态名称")
    @ExcelProperty("重算状态")
    @ColumnWidth(15)
    private String reSettleStatusText;
    @Schema(title = "重算结果")
    @ExcelProperty("重算结果")
    @ColumnWidth(15)
    private String reSettleResult;
    @Schema(title = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(10)
    private String userAccount;
    @Schema(title = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;
    @Schema(title = "账号类型名字")
    @ExcelProperty("账号类型")
    @ColumnWidth(10)
    private String accountTypeText;
    @Schema(title = "VIP等级")
    @ExcelProperty("VIP等级")
    @ColumnWidth(10)
    private String zhVipGradeText;
    @Schema(title = "上级代理账号")
    @ExcelProperty("上级代理账号")
    @ColumnWidth(10)
    private String agentAcct;
    @Schema(title = "游戏账号")
    @ExcelProperty("游戏账号")
    @ColumnWidth(10)
    private String casinoUserName;
    @Schema(title = "注单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(title = "注单状态名称")
    @ExcelProperty("注单状态")
    @ColumnWidth(15)
    private String orderClassifyText;
    @ExcelProperty("币种")
    @ColumnWidth(15)
    @Schema(title = "币种")
    private String currency;
    @Schema(title = "投注金额")
    @ExcelProperty("投注金额")
    @ColumnWidth(15)
    private BigDecimal betAmount;
    @Schema(title = "会员输赢")
    @ExcelProperty("会员输赢")
    @ColumnWidth(15)
    private BigDecimal winLossAmount;
    @Schema(title = "有效投注")
    @ExcelProperty("有效投注")
    @ColumnWidth(15)
    private BigDecimal validAmount;
    @Schema(title = "投注时间")
    private Long betTime;

    @Schema(title = "投注时间-导出字段")
    @ExcelProperty("投注时间")
    @ColumnWidth(15)
    private String betTimeStr;

    @Schema(title = "结算时间")
    private Long firstSettleTime;

    @Schema(title = "结算时间-导出字段")
    @ExcelProperty("结算时间")
    @ColumnWidth(15)
    private String settleTimeStr;


    @Schema(title = "变更次数")
    @ExcelProperty("变更次数")
    @ColumnWidth(15)
    private Integer changeCount;
    @Schema(title = "最近变更时间")
    private Long changeTime;

    @Schema(title = "最近变更时间")
    @ExcelProperty("最近变更时间")
    @ColumnWidth(15)
    private String changeTimeStr;

    @Schema(title = "备注")
    @ExcelProperty("备注")
    @ColumnWidth(15)
    private String remark;

    public String getBetTimeStr() {
        return null == betTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(betTime, CurrReqUtils.getTimezone());
    }

    public String getSettleTimeStr() {
        return null == firstSettleTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(firstSettleTime, CurrReqUtils.getTimezone());
    }

    public String getChangeTimeStr() {
        return null == changeTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(changeTime, CurrReqUtils.getTimezone());
    }
}
