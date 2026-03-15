package com.cloud.baowang.site.vo.export.agentManual;

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
@Schema(title = "代理加额审核记录-列表 返回")
@I18nClass
@ExcelIgnoreUnannotated
public class AgentGetRecordResponseExcelVO {

    @Schema(title = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(10)
    private String orderNo;

    @Schema(title = "代理账号")
    @ExcelProperty("代理账号")
    @ColumnWidth(5)
    private String agentAccount;

    @Schema(title = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(title = "订单状态")
    @ExcelProperty("订单状态")
    @ColumnWidth(5)
    private String orderStatusText;

    @Schema(description = "调整钱包")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_WALLET_TYPE)
    private Integer walletType;

    @Schema(description = "调整钱包")
    @ExcelProperty("调整钱包")
    @ColumnWidth(5)
    private String walletTypeText;

    @Schema(title = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(title = "调整类型-Name")
    @ExcelProperty("调整类型")
    @ColumnWidth(10)
    private String adjustTypeText;

    @Schema(title = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(5)
    private String currencyCode;

    @Schema(title = "调整金额")
    @ExcelProperty("调整金额")
    @ColumnWidth(10)
    private BigDecimal adjustAmount;

    @Schema(title = "申请时间")
    private Long applyTime;

    @ExcelProperty("申请时间")
    @ColumnWidth(10)
    private String applyTimeStr;

    public String getApplyTimeStr() {
        return applyTime == null ? "" : TimeZoneUtils.formatDateByTimeZone(applyTime, CurrReqUtils.getTimezone());
    }

    @Schema(title = "一审人")
    @ExcelProperty("审核人")
    @ColumnWidth(5)
    private String oneReviewer;

    @Schema(title = "一审完成时间")
    private Long oneReviewFinishTime;

    @ExcelProperty("审核时间")
    @ColumnWidth(15)
    private String oneReviewFinishTimeStr;

    public String getOneReviewFinishTimeStr() {
        return oneReviewFinishTime == null ? "" : TimeZoneUtils.formatDateByTimeZone(oneReviewFinishTime, CurrReqUtils.getTimezone());
    }

    @Schema(title = "一审审核用时")
    @ExcelProperty("审核用时")
    @ColumnWidth(10)
    private String oneReviewUseTime;

    @Schema(title = "一审备注")
    @ExcelProperty("备注")
    @ColumnWidth(50)
    private String oneReviewRemark;

}
