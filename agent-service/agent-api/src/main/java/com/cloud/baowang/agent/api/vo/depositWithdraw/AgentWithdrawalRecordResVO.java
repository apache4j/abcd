package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.agent.api.vo.withdraw.WithdrawCollectInfoVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理提款记录 分页查询 返回对象")
@I18nClass
@ExcelIgnoreUnannotated
public class AgentWithdrawalRecordResVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "三方关联id")
    private String payTxId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    @Schema(description = "订单来源")
    private Integer deviceType;

    @Schema(description = "订单来源-name")
    private String deviceTypeText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态-name")
    private String statusText;

    @Schema(description = "客户端状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;
    @Schema(description = "客户端状态-name")
    private String customerStatusText;

    @Schema(description = "提款金额")
    private BigDecimal applyAmount;

    @Schema(description = "提款类型")
    private String depositWithdrawTypeId;

    @Schema(description = "提款类型-name")
    private String depositWithdrawTypeCode;

    @Schema(description = "提款方式")
    private String depositWithdrawWayId;

    @Schema(description = "提款方式-name")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "通道code")
    private String depositWithdrawChannelCode;

    @Schema(description = "通道名")
    private String depositWithdrawChannelName;

    @Schema(description = "通道类型")
    private String depositWithdrawChannelType;

    @Schema(description = "提款币种")
    private String currencyCode;

    @Schema(description = "提款币种金额")
    private BigDecimal arriveAmount;

    @Schema(description = "账变金额的币种")
    private String tradeCurrencyAmountCurrencyCode;
    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "手续费")
    private BigDecimal feeRate;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "提款信息")
    private String withdrawalInfo;

    @Schema(description = "是否为大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "是否为大额提款-Name")
    private String isBigMoneyText;

    @Schema(description = "是否为首提")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;

    @Schema(description = "是否为首提-name")
    private String isFirstOutText;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "申请时间")
    private String createdTimeStr;



    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "修改时间")
    private Long updatedTime;

    @Schema(description = "完成时间")
    private Long payAuditTime;

    @Schema(description = "完成时间")
    private String payAuditTimeStr;

    public String getPayAuditTimeStr() {
        return payAuditTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(payAuditTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核员")
    private String auditUser;

    @Schema(description = "出款耗时")
    private Long rechargeWithdrawTimeConsuming;

    @Schema(description = "出款耗时字符串")
    private String rechargeWithdrawTimeConsumingStr;

    public String getRechargeWithdrawTimeConsumingStr() {
        return rechargeWithdrawTimeConsuming != null ? DateUtils.formatTime(rechargeWithdrawTimeConsuming) : "";
    }

    @Schema(description = "提款IP")
    private String applyIp;

    @Schema(description = "IP风控层级")
    private String ipRiskLevel;

    @Schema(description = "分隔符")
    private String applyIp_$_ipRiskLevel;

    public String getApplyIp_$_ipRiskLevel() {
        return applyIp;
    }

    @Schema(description = "提款终端设备号")
    private String deviceName;

    @Schema(description = "设备号风控层级")
    private String riskLevelDevice;

    @Schema(description = "分隔符")
    private String deviceName_$_riskLevelDevice;

    public String getDeviceName_$_riskLevelDevice() {
        return deviceName;
    }

    @Schema(description = "后台备注")
    private String remark;

}
