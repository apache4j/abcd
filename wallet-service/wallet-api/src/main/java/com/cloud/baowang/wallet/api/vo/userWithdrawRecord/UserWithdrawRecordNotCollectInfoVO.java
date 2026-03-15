package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


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

@Schema(title = "会员提款记录返回对象")
@Data
@I18nClass
@ExcelIgnoreUnannotated
public class UserWithdrawRecordNotCollectInfoVO {

    private String id;


    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @Schema(description = "会员ID")
    @ExcelProperty("会员ID")
    @ColumnWidth(25)
    private String userAccount;

    @Schema(description = "会员注册信息")
    @ExcelProperty("会员注册信息")
    @ColumnWidth(25)
    private String userRegister;

    @Schema(description = "订单来源")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    private String deviceType;

    @Schema(description = "订单来源名称")
    @ExcelProperty("订单来源")
    @ColumnWidth(25)
    private String deviceTypeText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态名称")
    @ExcelProperty("订单状态")
    @ColumnWidth(25)
    private String statusText;

    @Schema(description = "客户端状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(description = "客户端状态名称")
    @ExcelProperty("客户端状态")
    @ColumnWidth(25)
    private String customerStatusText;

    @Schema(description = "提款IP+提款ip风控层级")
    private String applyIp_$_applyIpRiskLevel;

    public String getApplyIp_$_applyIpRiskLevel() {
        return applyIp;
    }

    @Schema(description = "提款IP")
    @ExcelProperty("提款IP")
    @ColumnWidth(25)
    private String applyIp;

    @Schema(description = "提款IP风控层级")
    @ExcelProperty("提款IP风控层级")
    @ColumnWidth(25)
    private String applyIpRiskLevel;

    @Schema(description = "提款终端设备号+提款终端设备号风控层级")
    private String deviceNo_$_deviceNoRiskLevel;

    public String getDeviceNo_$_deviceNoRiskLevel() {
        return deviceNo;
    }

    @Schema(description = "提款终端设备号")
    @ExcelProperty("提款终端设备号")
    @ColumnWidth(25)
    private String deviceNo;

    @Schema(description = "提款终端设备号风控层级")
    @ExcelProperty("提款终端设备号风控层级")
    @ColumnWidth(25)
    private String deviceNoRiskLevel;

    @Schema(description = "提款方式")
    @ExcelProperty("提款方式")
    @ColumnWidth(25)
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "提款币种")
    @ExcelProperty("币种")
    @ColumnWidth(25)
    private String currencyCode;


    @Schema(description = "提款金额")
    @ExcelProperty("提款金额")
    @ColumnWidth(25)
    private BigDecimal applyAmount;

    @Schema(description = "提款类型")
    private String depositWithdrawTypeId;

    @Schema(description = "提款类型code")
    @ExcelProperty("提款类型")
    @ColumnWidth(25)
    private String depositWithdrawTypeCode;

    @Schema(description = "提款方式ID")
    private String depositWithdrawWayId;


    @Schema(description = "提款币种金额")
    @ExcelProperty("提款币种金额")
    @ColumnWidth(25)
    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "汇率")
    @ExcelProperty("汇率")
    @ColumnWidth(25)
    private BigDecimal exchangeRate;

    @Schema(description = "是否大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "是否大额提款名称")
    @ExcelProperty("是否大额提款")
    @ColumnWidth(10)
    private String isBigMoneyText;


    @Schema(description = "是否首提")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;

    @Schema(description = "是否首提名称")
    @ExcelProperty("是否首提")
    @ColumnWidth(10)
    private String isFirstOutText;

    @Schema(description = "出款通道")
    @ExcelProperty("出款通道")
    @ColumnWidth(10)
    private String depositWithdrawChannelName;

    @ExcelProperty("提款申请时间")
    @ColumnWidth(25)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "提款时间")
    private Long updatedTime;

    @Schema(description = "提款时间-用于导出")
    private String updatedTimeExport;

    @Schema(description = "图片")
    private String fileKey;

    @Schema(description = "提款申请时间")
    private Long createdTime;

    @Schema(description = "提款完成时间")
    private Long applyCompleteTime;

    @ExcelProperty("提款完成时间")
    @ColumnWidth(25)
    private String applyCompleteTimeStr;

    public String getApplyCompleteTimeStr() {
        return applyCompleteTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(applyCompleteTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "用时,单位毫秒")
    @ExcelProperty("订单处理时长")
    @ColumnWidth(10)
    private String takeTime;

    @Schema(description = "一审备注")
    private String firstAuditInfo;

    @Schema(description = "挂单备注")
    private String orderAuditInfo;

    @Schema(description = "出款备注")
    private String paymentAuditInfo;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "名")
    private String userName;

    @Schema(description = "后台备注")
    @ExcelProperty("后台备注")
    @ColumnWidth(25)
    private String withdrawRemark;

    @Schema(description = "手续费")
    @ExcelProperty("手续费")
    @ColumnWidth(25)
    private BigDecimal feeAmount;

    @Schema(description = "实际下分金额")
    @ExcelProperty("实际下分金额")
    @ColumnWidth(25)
    private BigDecimal arriveAmount;

    @Schema(description = "出款耗时")
    @ExcelProperty("出款耗时")
    @ColumnWidth(25)
    private String depositsTakeTime;

    @Schema(description = "审核员")
    @ExcelProperty("审核员")
    @ColumnWidth(25)
    private String payAuditUser;

    @Schema(description = "提款账号信息Excel")
    @ExcelProperty("提款账号信息Excel")
    @ColumnWidth(50)
    private String withdrawAccountInfoExcel;

    @Schema(description = "提款账号信息")
    private String withdrawAccountInfo;

}
