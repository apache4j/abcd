package com.cloud.baowang.site.vo.export.userTransfer;

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

@Data
@Schema(description = "excel导出vo")
@I18nClass
@ExcelIgnoreUnannotated
public class MemberOverflowReviewExcelVO implements Serializable {


    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(20)
    private String eventId;

    @Schema(description = "申请人")
    @ExcelProperty("申请人")
    @ColumnWidth(15)
    private String applyName;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "订单状态（1-待处理 2-处理中，3-审核通过，4-审核拒绝）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(description = "订单状态")
    @ExcelProperty("订单状态")
    @ColumnWidth(15)
    private String auditStatusText;

    @Schema(description = "申请代理账号")
    @ExcelProperty("申请代理账号")
    @ColumnWidth(15)
    private String transferAgentName;

    @Schema(description = "代理类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "代理类型")
    @ExcelProperty("代理类型")
    @ColumnWidth(15)
    private String agentTypeText;

    @Schema(description = "溢出会员账号")
    @ExcelProperty("溢出会员账号")
    @ColumnWidth(15)
    private String memberName;

    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(description = "账号类型")
    @ExcelProperty("账号类型")
    @ColumnWidth(15)
    private String accountTypeText;

    @Schema(description = "申请时间")
    private Long createdTime;

    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核员")
    @ExcelProperty("审核员")
    @ColumnWidth(15)
    private String auditName;

    @Schema(description = "审核时间")
    private Long auditDatetime;

    @ExcelProperty("审核时间")
    @ColumnWidth(20)
    private String auditDatetimeStr;

    public String getAuditDatetimeStr() {
        return auditDatetime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(auditDatetime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核用时")
    @ExcelProperty("审核用时")
    @ColumnWidth(15)
    private String reviewDuration;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    @ColumnWidth(50)
    private String auditRemark;


}
