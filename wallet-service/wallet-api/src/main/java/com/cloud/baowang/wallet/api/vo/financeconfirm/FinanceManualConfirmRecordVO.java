package com.cloud.baowang.wallet.api.vo.financeconfirm;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 人工提款确认记录
 */
@I18nClass
@Data
@Schema(title ="会员提款人工确认记录VO")
@ExcelIgnoreUnannotated
public class FinanceManualConfirmRecordVO {

    @Schema(title = "id")
    @ExcelProperty("id")
    @ColumnWidth(15)
    private String id;

    @Schema(title = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @Schema(title = "会员ID")
    @ExcelProperty("会员ID")
    @ColumnWidth(25)
    private String userAccount;

    @Schema(title = "会员注册信息")
    @ExcelProperty("会员注册信息")
    @ColumnWidth(25)
    private String userRegister;

    @Schema(title = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(title = "审核状态名称")
    @ExcelProperty("审核状态")
    @ColumnWidth(25)
    private String statusText;

    @Schema(title = "提款金额")
    @ExcelProperty("提款金额")
    @ColumnWidth(25)
    private BigDecimal applyAmount;

    @Schema(title = "到账金额")
    @ExcelProperty("到账金额")
    @ColumnWidth(25)
    private BigDecimal arriveAmount;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "申请时间-用于导出")
    @ExcelProperty("申请时间")
    @ColumnWidth(25)
    private String applyTimeExport;

    @Schema(title = "审核人")
    @ExcelProperty("审核人")
    @ColumnWidth(25)
    private String auditUser;

    @Schema(title = "审核时间")
    private Long auditTime;

    @Schema(title = "审核时间-用于导出")
    @ExcelProperty("审核时间")
    @ColumnWidth(25)
    private String auditTimeExport;

    @Schema(title = "审核用时")
    @ExcelProperty("审核用时")
    @ColumnWidth(25)
    private Long auditTimeConsuming;

    @Schema(title = "提款资料")
    private String auditInfo;
}
