package com.cloud.baowang.site.vo.export;

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
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
@Schema(title = "代理提款记录导出对象")
@I18nClass
@ExcelIgnoreUnannotated
public class AgentWithdrawalRecordExcelVO {

    @Schema(title = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(20)
    private String orderNo;

    @Schema(title = "代理账号")
    @ExcelProperty("代理账号")
    @ColumnWidth(20)
    private String agentAccount;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    @Schema(title = "订单来源")
    private Integer deviceType;

    @Schema(title = "订单来源-name")
    @ExcelProperty("订单来源")
    @ColumnWidth(10)
    private String deviceTypeText;

    @Schema(title = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(title = "订单状态-name")
    @ExcelProperty("订单状态")
    @ColumnWidth(10)
    private String statusText;

    @Schema(title = "客户端状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(title = "客户端状态-name")
    @ExcelProperty("客户端状态")
    @ColumnWidth(5)
    private String customerStatusText;

    @Schema(title = "提款方式-name")
    @I18nField
    @ExcelProperty("提款方式")
    @ColumnWidth(20)
    private String depositWithdrawWay;

    @Schema(title = "通道名")
    @ExcelProperty("提款通道")
    @ColumnWidth(20)
    private String depositWithdrawChannelName;

    @Schema(title = "提款信息")
    @ExcelProperty("提款信息")
    @ColumnWidth(20)
    private String withdrawalInfo;

    public String getWithdrawalInfo() {
        return StringUtils.isNotBlank(withdrawalInfo) ? withdrawalInfo.replaceAll("\\$\\$", "\n") : "";
    }

    @Schema(title = "提款币种")
    @ExcelProperty("币种")
    @ColumnWidth(20)
    private String currencyCode;

    @Schema(title = "订单金额")
    @ExcelProperty("订单金额")
    @ColumnWidth(20)
    private BigDecimal applyAmount;

    @Schema(description = "汇率")
    @ExcelProperty("订单汇率")
    @ColumnWidth(20)
    private BigDecimal exchangeRate;

    @Schema(description = "手续费")
    @ExcelProperty("手续费")
    @ColumnWidth(20)
    private BigDecimal feeAmount;

    @Schema(title = "账变金额的币种")
    @ExcelProperty("提款账变金额")
    @ColumnWidth(20)
    private BigDecimal arriveAmount;

    @Schema(title = "是否为大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(title = "是否为大额提款-Name")
    @ExcelProperty("是否为大额提款")
    @ColumnWidth(20)
    private String isBigMoneyText;

    @Schema(title = "是否为首提")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;

    @Schema(title = "是否为首提-name")
    @ExcelProperty("是否为首提")
    @ColumnWidth(20)
    private String isFirstOutText;

    @Schema(title = "申请时间")
    private Long createdTime;

    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "完成时间")
    private Long payAuditTime;

    @Schema(description = "完成时间")
    private String payAuditTimeStr;

    public String getPayAuditTimeStr() {
        return payAuditTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(payAuditTime, CurrReqUtils.getTimezone());
    }


    @Schema(description = "审核员")
    @ExcelProperty("审核员")
    @ColumnWidth(20)
    private String auditUser;

    @Schema(description = "出款耗时")
    private Long rechargeWithdrawTimeConsuming;

    @Schema(description = "出款耗时字符串")
    @ExcelProperty("出款耗时")
    @ColumnWidth(20)
    private String rechargeWithdrawTimeConsumingStr;

    public String getRechargeWithdrawTimeConsumingStr() {
        return rechargeWithdrawTimeConsuming != null ? DateUtils.formatTime(rechargeWithdrawTimeConsuming) : "";
    }

    @Schema(description = "后台备注")
    @ExcelProperty("后台备注")
    @ColumnWidth(20)
    private String remark;

    @Schema(title = "提款IP")
    @ExcelProperty("提款IP")
    @ColumnWidth(20)
    private String applyIp;

    @Schema(title = "IP风控层级")
    @ExcelProperty("IP风控层级")
    @ColumnWidth(20)
    private String ipRiskLevel;

    @Schema(title = "提款终端设备号")
    @ExcelProperty("提款终端设备号")
    @ColumnWidth(20)
    private String deviceName;

    @Schema(title = "设备号风控层级")
    @ExcelProperty("设备号风控层级")
    @ColumnWidth(20)
    private String riskLevelDevice;


}
