package com.cloud.baowang.wallet.api.vo.fundrecord;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员加额审核记录-列表 返回")
@I18nClass
@ExcelIgnoreUnannotated
public class GetRecordResponseResultVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(10)
    private String orderNo;

    @Schema(title = "会员ID")
    private String userId;

    @Schema(title = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(10)
    private String userAccount;

    @Schema(title = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(title = "审核状态-Name")
    @ExcelProperty("审核状态")
    @ColumnWidth(5)
    private String auditStatusText;

    @Schema(title = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(title = "调整类型-Name")
    @ExcelProperty("调整类型")
    @ColumnWidth(10)
    private String adjustTypeText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(title = "调整金额")
    @ExcelProperty("调整金额")
    @ColumnWidth(10)
    private BigDecimal adjustAmount;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "申请时间")
    @ExcelProperty("申请时间")
    @ColumnWidth(30)
    private String applyTimeStr;


    @Schema(title = "审核人")
    @ExcelProperty("审核人")
    @ColumnWidth(10)
    private String auditId;

    @Schema(title = "审核时间")
    private Long auditDatetime;
    @Schema(title = "审核时间")
    @ExcelProperty("审核时间")
    @ColumnWidth(30)
    private String auditDatetimeStr;

    @Schema(title = "审核备注")
    @ExcelProperty("审核备注")
    @ColumnWidth(50)
    private String auditRemark;

    @Schema(description = "审核用时")
    private String auditDuration;

    public String getAuditDatetimeStr() {
        return null == auditDatetime ? null : TimeZoneUtils.formatTimestampToTimeZone(auditDatetime, CurrReqUtils.getTimezone());
    }

    public String getApplyTimeStr() {
        return null == applyTime ? null : TimeZoneUtils.formatTimestampToTimeZone(applyTime, CurrReqUtils.getTimezone());
    }


}
