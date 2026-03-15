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

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "会员人工加额记录 返回")
@ExcelIgnoreUnannotated
@I18nClass
public class UserManualUpRecordExportVO {

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @Schema(title = "会员ID")
    @ExcelProperty("会员ID")
    @ColumnWidth(15)
    private String userId;

    @Schema(title = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(20)
    private String userAccount;

    @Schema(description = "VIP等级")
    @ExcelProperty("VIP等级")
    @ColumnWidth(15)
    private String vipGradeCodeName;

    @Schema(title = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_WAY)
    private Integer adjustWay;

    @Schema(title = "调整方式 - Name")
    @ExcelProperty("调整方式")
    @ColumnWidth(20)
    private String adjustWayText;

    @Schema(title = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(title = "订单状态 - Name")
    @ExcelProperty("订单状态")
    @ColumnWidth(20)
    private String auditStatusText;

    @Schema(title = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(title = "调整类型 - Name")
    @ExcelProperty("调整类型")
    @ColumnWidth(20)
    private String adjustTypeText;
    @ExcelProperty("币种")
    @ColumnWidth(5)
    private String currencyCode;

    @ExcelProperty("调整金额")
    @ColumnWidth(15)
    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @ExcelProperty("申请人")
    @ColumnWidth(15)
    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "申请时间")
    private Long applyTime;

    @ExcelProperty("申请时间")
    @ColumnWidth(25)
    private String applyTimeStr;

    public String getApplyTimeStr() {
        return null == applyTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(applyTime, CurrReqUtils.getTimezone());
    }

    @ExcelProperty("备注")
    @ColumnWidth(25)
    @Schema(description = "备注")
    private String applyReason;
}
