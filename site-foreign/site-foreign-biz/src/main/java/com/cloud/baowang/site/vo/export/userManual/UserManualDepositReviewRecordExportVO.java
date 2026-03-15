package com.cloud.baowang.site.vo.export.userManual;

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
 * @author: qiqi
 */
@Data
@I18nClass
@Schema(title = "会员提款审核记录导出对象")
@ExcelIgnoreUnannotated
public class UserManualDepositReviewRecordExportVO {


    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(20)
    private String userAccount;


    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(20)
    private String orderNo;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态名称")
    @ExcelProperty("订单状态")
    @ColumnWidth(20)
    private String statusText;


    @Schema(description = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(20)
    private String currencyCode;

    @Schema(description = "存款方式")
    @I18nField
    @ExcelProperty("存款方式")
    @ColumnWidth(20)
    private String depositWithdrawWay;

    @Schema(description = "存款金额")
    @ExcelProperty("存款金额")
    @ColumnWidth(20)
    private BigDecimal applyAmount;


    @Schema(description = "手续费")
    @ExcelProperty("手续费")
    @ColumnWidth(20)
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额")
    @ExcelProperty("到账金额")
    @ColumnWidth(20)
    private BigDecimal arriveAmount;


    @Schema(description = "申请时间")
    private Long createdTime;


    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核人")
    @ExcelProperty("审核人")
    @ColumnWidth(20)
    private String updater;

    @Schema(description = "审核时间")
    private Long updatedTime;

    @ExcelProperty("审核时间")
    @ColumnWidth(20)
    private String updatedTimeStr;


    public String getUpdatedTimeStr() {
        return updatedTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }



}
