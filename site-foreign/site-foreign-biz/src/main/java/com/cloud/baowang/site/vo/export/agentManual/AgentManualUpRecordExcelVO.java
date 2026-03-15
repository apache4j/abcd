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
@Schema(title = "代理人工加额记录 返回")
@I18nClass
@ExcelIgnoreUnannotated
public class AgentManualUpRecordExcelVO {

    @Schema(title = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(15)
    private String orderNo;

    @Schema(title = "代理账号")
    @ExcelProperty("代理账号")
    @ColumnWidth(15)
    private String agentAccount;

    @Schema(title = "代理姓名")
    @ExcelProperty("代理姓名")
    @ColumnWidth(15)
    private String agentName;

    @Schema(title = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_WAY)
    private Integer adjustWay;

    @Schema(title = "调整方式")
    @ExcelProperty("调整方式")
    @ColumnWidth(15)
    private String adjustWayText;

    @Schema(title = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(title = "订单状态-Name")
    @ExcelProperty("订单状态")
    @ColumnWidth(10)
    private String orderStatusText;

    @Schema(title = "钱包类型 1额度钱包 2佣金钱包")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_WALLET_TYPE)
    private Integer walletType;

    @Schema(title = "钱包类型 1额度钱包 2佣金钱包-Name")
    @ExcelProperty("钱包类型")
    @ColumnWidth(15)
    private String walletTypeText;

    @Schema(title = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(title = "调整类型-Name")
    @ExcelProperty("调整类型")
    @ColumnWidth(15)
    private String adjustTypeText;

    @Schema(title = "调整金额")
    @ExcelProperty("调整金额")
    @ColumnWidth(15)
    private BigDecimal adjustAmount;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "申请时间-Name")
    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    private String applyTimeStr;

    public String getApplyTimeStr() {
        String timeStr = TimeZoneUtils.formatTimestampToTimeZone(applyTime, CurrReqUtils.getTimezone());
        return applyTime == null ? "" : timeStr;
    }


    @Schema(title = "申请人")
    @ExcelProperty("申请人")
    @ColumnWidth(15)
    private String applicant;

    @Schema(title = "申请备注")
    @ExcelProperty("申请备注")
    @ColumnWidth(50)
    private String applyReason;

    @Schema(description = "审核人")
    @ExcelProperty("审核人")
    @ColumnWidth(15)
    private String oneReviewer;

    @Schema(description = "审核时间")
    private Long oneReviewFinishTime;

    @Schema(description = "审核时间")
    @ExcelProperty("审核时间")
    @ColumnWidth(20)
    private String oneReviewFinishTimeStr;

    public String getOneReviewFinishTimeStr() {
        String timeStr = TimeZoneUtils.formatTimestampToTimeZone(oneReviewFinishTime, CurrReqUtils.getTimezone());
        return oneReviewFinishTime == null ? "" : timeStr;
    }

    @Schema(description = "审核备注")
    @ExcelProperty("审核备注")
    @ColumnWidth(50)
    private String oneReviewRemark;

}
