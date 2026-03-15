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

@Data
@Schema(description = "会员转代导出对象")
@I18nClass
@ExcelIgnoreUnannotated
public class MemberTransferReviewExcelVO {

    @ExcelProperty("订单号")
    @ColumnWidth(20)
    private String eventId;

    @Schema(description = "申请人")
    @ExcelProperty("申请人")
    @ColumnWidth(10)
    private String applyName;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(description = "审核环节名称")
    @ExcelProperty("订单状态")
    @ColumnWidth(10)
    private String auditStatusText;

    @Schema(description = "转代会员")
    @ExcelProperty("转代会员账号")
    @ColumnWidth(10)
    private String userAccount;

    @Schema(description = "当前上级代理账号")
    @ExcelProperty("当前上级")
    @ColumnWidth(10)
    private String currentAgentName;

    @Schema(description = "转入上级代理账号")
    @ExcelProperty("转入代理账号")
    @ColumnWidth(10)
    private String transferAgentName;

    @Schema(description = "申请时间")
    private Long createdTime;

    @ExcelProperty("申请时间")
    @ColumnWidth(15)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @ExcelProperty("审核员")
    @ColumnWidth(5)
    @Schema(description = "审核人")
    private String auditName;

    @Schema(description = "审核完成时间")
    private Long auditDatetime;

    @ExcelProperty("审核时间")
    @ColumnWidth(15)
    private String auditDatetimeStr;

    public String getAuditDatetimeStr() {
        return auditDatetime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(auditDatetime, CurrReqUtils.getTimezone());
    }

    @ExcelProperty("审核用时")
    @ColumnWidth(10)
    @Schema(description = "审核用时")
    private String reviewDuration;

    @ExcelProperty("备注")
    @ColumnWidth(50)
    private String auditRemark;
}
