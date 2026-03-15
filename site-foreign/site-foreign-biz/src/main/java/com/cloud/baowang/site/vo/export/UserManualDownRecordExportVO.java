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
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "会员人工扣除记录")
@ExcelIgnoreUnannotated
@I18nClass
public class UserManualDownRecordExportVO implements Serializable {

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

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_WAY)
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
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_DOWN_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型 - Text")
    @ExcelProperty("调整类型")
    @ColumnWidth(20)
    private String adjustTypeText;

    @ExcelProperty("币种")
    @ColumnWidth(5)
    private String currencyCode;

    @ExcelProperty("调整金额")
    @ColumnWidth(20)
    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;


    @Schema(description = "操作人")
    @ExcelProperty("操作人")
    @ColumnWidth(10)
    private String creator;

    @Schema(description = "操作时间")
    private Long createdTime;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    @ColumnWidth(50)
    private String applyReason;

    @Schema(description = "操作时间")
    @ExcelProperty("操作时间")
    @ColumnWidth(20)
    private String createdTimeStr;


    public String getCreatedTimeStr() {
        return null == createdTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }
}
