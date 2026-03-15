package com.cloud.baowang.site.vo.export.siteReview;

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
@Schema(description = "总台-会员列表 导出返回")
@ExcelIgnoreUnannotated
@I18nClass
public class SiteSecurityAdjustReviewLogExportVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(20)
    private String reviewOrderNumber;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "站点名称")
    @ExcelProperty("站点名称")
    @ColumnWidth(20)
    private String siteName;

    @Schema(description = "保证金账户状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_ACCOUNT_STATUS)
    private Integer accountStatus;

    @Schema(description = "保证金账户状态")
    @ExcelProperty(value = "保证金账户状态")
    @ColumnWidth(20)
    private String accountStatusText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer reviewStatus;

    @Schema(description = "订单状态")
    @ExcelProperty(value = "订单状态")
    @ColumnWidth(20)
    private String reviewStatusText;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_SECURITY_REVIEW)
    private Integer adjustType;

    @Schema(description = "调整类型")
    @ExcelProperty(value = "调整类型")
    @ColumnWidth(20)
    private String adjustTypeText;

    @Schema(description = "币种")
    @ExcelProperty(value = "币种")
    @ColumnWidth(10)
    private String currency;

    @Schema(description = "调整金额")
    @ExcelProperty(value = "调整金额")
    @ColumnWidth(10)
    private BigDecimal adjustAmount;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "申请时间")
    @ExcelProperty(value = "申请时间")
    @ColumnWidth(25)
    private String applyTimeStr;

    public String getApplyTimeStr() {
        return applyTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(applyTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核员")
    @ExcelProperty(value = "审核员")
    @ColumnWidth(10)
    private String firstReviewer;

    @Schema(description = "审核时间")
    private Long firstReviewTime;

    @Schema(description = "审核时间")
    @ExcelProperty(value = "审核时间")
    @ColumnWidth(25)
    private String firstReviewTimeStr;

    public String getFirstReviewTimeStr() {
        return firstReviewTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(firstReviewTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核用时")
    @ExcelProperty(value = "审核用时")
    @ColumnWidth(15)
    private String reviewTotalTimeStr;

    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    @ColumnWidth(20)
    private String reviewRemark;



}
