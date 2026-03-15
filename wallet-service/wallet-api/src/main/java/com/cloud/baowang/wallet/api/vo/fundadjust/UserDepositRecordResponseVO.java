package com.cloud.baowang.wallet.api.vo.fundadjust;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author: aomiao
 */
@Data
@Schema(description = "会员存款记录 返回")
@ExcelIgnoreUnannotated
@I18nClass
public class UserDepositRecordResponseVO {

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(15)
    private String userAccount;

    @Schema(description = "订单来源 - Text")
    @ExcelProperty("订单来源")
    @ColumnWidth(15)
    private String deviceTypeText;

    @Schema(description = "订单状态 - Text")
    @ExcelProperty("订单状态")
    @ColumnWidth(5)
    private String statusText;

    @Schema(description = "客户端状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private Integer customerStatus;

    @Schema(description = "客户端状态 - Text")
    @ExcelProperty("客户端状态")
    @ColumnWidth(15)
    private String customerStatusText;



    @Schema(description = "存款终端设备号+风控层级分隔标识")
    private String deviceNo_$_deviceNoRiskLevel;

    public String getDeviceNo_$_deviceNoRiskLevel() {
        return deviceNo;
    }

    @I18nField
    @Schema(description = "存款方式")
    @ExcelProperty("存款方式")
    @ColumnWidth(10)
    private String depositWithdrawWay;

    @Schema(description = "存款通道")
    @ExcelProperty("存款通道")
    @ColumnWidth(10)
    private String depositWithdrawChannelName;

    @Schema(description = "存款账号信息")
    @ExcelProperty("存款账号信息")
    @ColumnWidth(10)
    private String depositWithdrawName;


    @Schema(description = "币种")
    @ExcelProperty("存款币种")
    @ColumnWidth(5)
    private String currencyCode;


    @Schema(description = "存款金额")
    @ExcelProperty("存款金额")
    @ColumnWidth(15)
    private BigDecimal applyAmount;

    @Schema(description = "订单汇率")
    @ExcelProperty("订单汇率")
    @ColumnWidth(25)
    private BigDecimal exchangeRate;

//    @Schema(description = "实际到账金额")
//    @ExcelProperty("实际到账金额")
//    @ColumnWidth(15)
//    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "实际到账金额")
    @ExcelProperty("实际到账金额")
    @ColumnWidth(15)
    private BigDecimal arriveAmount;


    @Schema(description = "存款时间",title = "申请时间")
    private Long createdTime;

    @Schema(description = "存款时间 - 用于导出")
    @ExcelProperty("申请时间")
    @ColumnWidth(25)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "出款时间Str")
    @ExcelProperty("完成时间")
    @ColumnWidth(25)
    private String payAuditTimeStr;

    @Schema(description = "备注")
//    @ExcelProperty("备注")
    @ColumnWidth(25)
    private String remark;

    @Schema(description = "订单来源")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    private Integer deviceType;

    /**
     * {@link  DepositWithdrawalOrderStatusEnum}
     */
    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private Integer status;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "完成时间 - 用于导出")
    private String updatedTimeStr;

    private Long rechargeWithdrawTimeConsuming ;

    @Schema(description = "入款耗时")
    @ExcelProperty("入款耗时")
    @ColumnWidth(25)
    private String depositsTakeTime;

    public String getDepositsTakeTime() {
        return Objects.nonNull(rechargeWithdrawTimeConsuming)? DateUtils.formatTime(rechargeWithdrawTimeConsuming):"";
    }

    @Schema(description = "用户备注")
    @ExcelProperty("用户备注")
    @ColumnWidth(25)
    private String cashFlowRemark;

    @Schema(description = "用户上传凭证,全路径")
    @ExcelProperty("存款凭证")
    @ColumnWidth(25)
    private String cashFlowFileFullPath;

    private String cashFlowFile;

    @Schema(description = "客服上传凭证")
    private String fileKey;

    @Schema(description = "客服上传凭证Url")
    private String fileKeyUrl;

    public String getUpdatedTimeStr() {
        return updatedTimeStr == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "出款时间")
    private Long payAuditTime;

    public String getPayAuditTimeStr() {
        return payAuditTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(payAuditTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "存款IP")
    @ExcelProperty("存款IP")
    @ColumnWidth(15)
    private String applyIp;

    @Schema(description = "IP风控层级")
    @ExcelProperty("存款IP风控层级")
    @ColumnWidth(15)
    private String ipRiskLevel;

    @Schema(description = "ip+风控分隔标识")
    private String applyIp_$_ipRiskLevel;

    public String getApplyIp_$_ipRiskLevel() {
        return applyIp;
    }

    @Schema(description = "存款终端设备号")
    @ExcelProperty("存款终端设备号")
    @ColumnWidth(15)
    private String deviceNo;

    @Schema(description = "存款终端设备号 风控层级")
    @ExcelProperty("设备号风控层级")
    @ColumnWidth(20)
    private String deviceNoRiskLevel;


}
