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

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(title = "代理人工扣除记录")
@I18nClass
@ExcelIgnoreUnannotated
public class AgentManualDownExcelVO implements Serializable {

    @Schema(title = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(15)
    private String orderNo;

    @Schema(title = "代理账号")
    @ExcelProperty("代理账号")
    @ColumnWidth(15)
    private String agentAccount;

    @Schema(title = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_WAY)
    private Integer adjustWay;

    @Schema(title = "调整方式-名称")
    @ExcelProperty("调整方式")
    @ColumnWidth(15)
    private String adjustWayText;

    @Schema(title = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(title = "订单状态-名称")
    @ExcelProperty("订单状态")
    @ColumnWidth(15)
    private String orderStatusText;

    @Schema(title = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_DOWN_TYPE)
    private Integer adjustType;

    @Schema(title = "调整类型-名称")
    @ExcelProperty("调整类型")
    @ColumnWidth(15)
    private String adjustTypeText;

    @Schema(title = "调整金额")
    @ExcelProperty("调整金额")
    @ColumnWidth(15)
    private BigDecimal adjustAmount;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(10)
    private String currencyCode;


    @Schema(title = "申请人")
    @ExcelProperty("申请人")
    @ColumnWidth(15)
    private String applicant;

    @Schema(title = "申请时间")
    private Long applyTime;
    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    private String applyTimeStr;

    public String getApplyTimeStr() {
        return applyTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(applyTime, CurrReqUtils.getTimezone());
    }

    @Schema(title = "备注")
    @ExcelProperty("备注")
    @ColumnWidth(50)
    private String applyReason;

}
