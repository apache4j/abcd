package com.cloud.baowang.agent.api.vo.depositWithdraw;

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

@Data
@Schema(description = "代理存款记录 分页返回对象")
@I18nClass
public class AgentDepositRecordRes {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "订单来源")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    private Integer deviceType;

    @Schema(description = "订单来源-name")
    private String deviceTypeText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态name")
    private String statusText;

    @Schema(description = "客户端状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(description = "客户端状态name")
    private String customerStatusText;

    /**
     * 存取款类型CODE
     */
    @Schema(description = "存取款类型CODE")
    private String depositWithdrawTypeCode;

    @Schema(description = "存款方式")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "存款通道名")
    private String depositWithdrawChannelName;

    @Schema(description = "收款账号信息")
    private String paymentAccountInformation;
    /**
     * 存取款通道id
     */
    @Schema(description = "存取款通道id")
    private String depositWithdrawChannelId;


    /**
     * 存取款通道CODE
     */
    @Schema(description = "存取款通道CODE")
    private String depositWithdrawChannelCode;


    @Schema(description = "充值币种")
    private String currencyCode;

    @Schema(description = "存款金额+currencyCode")
    private BigDecimal applyAmount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "存款账变金额+platCurrencyCode")
    private BigDecimal arriveAmount;

    @Schema(description = "平台币编号")
    private String platCurrencyCode;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "申请时间")
    private String createdTimeStr;



    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "充值完成时间")
    private Long payAuditTime;

    @Schema(description = "充值完成时间")
    private String payAuditTimeStr;

    public String getPayAuditTimeStr() {
        return payAuditTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(payAuditTime, CurrReqUtils.getTimezone());
    }


    @Schema(description = "完成时间")
    private Long updatedTime;

    private String updatedTimeStr;

    public String getUpdatedTimeStr() {
        return updatedTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "入款耗时")
    private Long rechargeWithdrawTimeConsuming;

    private String rechargeWithdrawTimeConsumingStr;

    public String getRechargeWithdrawTimeConsumingStr() {
        return rechargeWithdrawTimeConsuming != null ? DateUtils.formatTime(rechargeWithdrawTimeConsuming) : "";
    }

    @Schema(description = "后台备注")
    private String remark;

    @Schema(description = "用户备注")
    @ColumnWidth(25)
    private String cashFlowRemark;

    @Schema(description = "客户存款凭证")
    private String cashFlowFile;

    @Schema(description = "客户存款凭证完整路径,逗号拼接")
    private String cashFlowFileUrl;

    @Schema(description = "存款ip")
    private String applyIp;

    @Schema(description = "存款ip风控层级")
    private String applyIpRiskLevel;

    @Schema(description = "分隔符")
    private String applyIp_$_applyIpRiskLevel;

    public String getApplyIp_$_applyIpRiskLevel() {
        return applyIp;
    }

    @Schema(description = "存款终端设备号")
    private String deviceNo;

    @Schema(description = "存款终端设备号风控层级")
    private String deviceNoRiskLevel;

    @Schema(description = "分隔符")
    private String deviceNo_$_deviceNoRiskLevel;

    public String getDeviceNo_$_deviceNoRiskLevel() {
        return deviceNo;
    }

    @Schema(description = "平台币符号")
    private String tradeCurrencyAmountCurrencyCode;
    @Schema(description = "手续费")
    private BigDecimal feeRate;
    @Schema(description = "账变金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "客服上传凭证")
    private String fileKey;

    @Schema(description = "客服上传凭证Url")
    private String fileKeyUrl;




}
