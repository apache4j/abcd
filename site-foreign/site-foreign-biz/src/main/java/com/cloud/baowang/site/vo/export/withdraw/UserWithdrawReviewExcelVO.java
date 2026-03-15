package com.cloud.baowang.site.vo.export.withdraw;


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

@Schema(title = "会员提款审核记录返回对象")
@I18nClass
@ExcelIgnoreUnannotated
@Data
public class UserWithdrawReviewExcelVO {
    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(12)
    private String orderNo;

    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(10)
    private String userAccount;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态名称")
    @ExcelProperty("订单状态")
    @ColumnWidth(8)
    private String statusText;

    @Schema(description = "是否大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "是否大额提款名称")
    @ExcelProperty("是否大额提款")
    @ColumnWidth(6)
    private String isBigMoneyText;

    @Schema(description = "是否首提")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;

    @Schema(description = "是否首提名称")
    @ExcelProperty("是否首提")
    @ColumnWidth(6)
    private String isFirstOutText;

    @Schema(description = "提款币种")
    @ExcelProperty("币种")
    @ColumnWidth(5)
    private String currencyCode;

    @Schema(description = "提款金额")
    @ExcelProperty("提款金额")
    @ColumnWidth(10)
    private BigDecimal applyAmount;

    @Schema(description = "提款手续费")
    @ExcelProperty("提款手续费")
    @ColumnWidth(10)
    private BigDecimal feeAmount;

    @Schema(description = "出款类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CHANNEL_TYPE)
    private String depositWithdrawChannelType;

    @Schema(description = "出款类型")
    @ExcelProperty("出款类型")
    @ColumnWidth(10)
    private String depositWithdrawChannelTypeText;

    @Schema(description = "通道名称")
    @ExcelProperty("出款通道")
    @ColumnWidth(10)
    private String depositWithdrawChannelName;

    @Schema(description = "申请时间")
    private Long createdTime;

    @ExcelProperty("申请时间")
    @ColumnWidth(25)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核人信息")
    @ExcelProperty("审核人")
    @ColumnWidth(50)
    private String auditUserInfo;

    @Schema(description = "审核时间信息")
    @ExcelProperty("审核时间")
    @ColumnWidth(50)
    private String auditTimeInfo;

    @Schema(description = "审核用时信息")
    @ExcelProperty("审核用时")
    @ColumnWidth(100)
    private String auditUseTimeInfo;

    @Schema(description = "审核备注信息")
    @ExcelProperty("审核备注")
    @ColumnWidth(150)
    private String auditRemarkInfo;



}
