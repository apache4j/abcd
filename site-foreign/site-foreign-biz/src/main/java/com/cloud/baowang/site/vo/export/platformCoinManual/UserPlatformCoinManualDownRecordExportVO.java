package com.cloud.baowang.site.vo.export.platformCoinManual;

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
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "会员平台币下分记录")
@ExcelIgnoreUnannotated
@I18nClass
public class UserPlatformCoinManualDownRecordExportVO implements Serializable {

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @ExcelProperty("会员账号")
    @ColumnWidth(20)
    private String userAccount;

    @Schema(description = "VIP等级")
    @ExcelProperty("vip等级")
    @ColumnWidth(20)
    private String vipGradeCodeName;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_COIN_MANUAL_ADJUST_WAY)
    private Integer adjustWay;

    @Schema(description = "调整方式 - Text")
    @ExcelProperty("调整方式")
    @ColumnWidth(20)
    private String adjustWayText;

    @Schema(description = "账变状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BALANCE_CHANGE_STATUS)
    private Integer balanceChangeStatus;

    @Schema(description = "账变状态")
    @ExcelProperty("订单状态")
    @ColumnWidth(5)
    private String balanceChangeStatusText;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_COIN_MANUAL_ADJUST_DOWN_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型 - Text")
    @ExcelProperty("调整类型")
    @ColumnWidth(20)
    private String adjustTypeText;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(10)
    private String platformCurrencyCode;

    @ExcelProperty("调整金额")
    @ColumnWidth(20)
    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;


    @Schema(description = "申请人")
    @ExcelProperty("申请人")
    @ColumnWidth(10)
    private String creator;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "申请时间")
    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    @ColumnWidth(50)
    private String applyReason;


    public String getCreatedTimeStr() {
        return null == createdTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }
}
