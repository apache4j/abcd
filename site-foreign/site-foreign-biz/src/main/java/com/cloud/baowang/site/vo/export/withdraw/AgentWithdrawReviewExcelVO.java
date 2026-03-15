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

@Schema(title = "代理提款审核记录返回对象")
@Data
@I18nClass
@ExcelIgnoreUnannotated
public class AgentWithdrawReviewExcelVO {

    private String id;


    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(10)
    private String orderNo;

    @Schema(description = "代理ID")
    private String agentId;

    @Schema(description = "代理账号")
    @ExcelProperty("代理账号")
    @ColumnWidth(10)
    private String agentAccount;

    @Schema(description = "代理名")
    @ExcelProperty("代理名")
    @ColumnWidth(10)
    private String agentName;


    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态名称")
    @ExcelProperty("订单状态")
    @ColumnWidth(10)
    private String statusText;

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
    private String isFirstOutText;


    @Schema(description = "提款金额")
    @ExcelProperty("提款金额")
    @ColumnWidth(10)
    private BigDecimal applyAmount;

    @Schema(description = "提款类型")
    private String depositWithdrawType;

    @Schema(description = "手续费")
    @ExcelProperty("手续费")
    @ColumnWidth(10)
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额")
    private BigDecimal arriveAmount;

    @Schema(description = "提款币种")
    @ExcelProperty("提款币种")
    @ColumnWidth(10)
    private String currencyCode;

    @Schema(description = "提款币种金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    /**
     * 账户类型（ 银行卡为银行名称，虚拟币为币种）
     */
    @Schema(description = "账户类型（ 银行卡为银行名称，虚拟币为币种）")
    private String accountType;


    @Schema(description = "账户分支（银行卡为开户行，虚拟币为链协议 如ERC20 TRC20)")
    private String accountBranch;

    /**
     * 存取款地址
     */
    @Schema(description = "账户地址（银行卡位账号，虚拟币为地址")
    private String depositWithdrawAddress;

    /**
     * 存取款用户名
     */
    @Schema(description = "账户名/持卡人姓名")
    private String depositWithdrawName;


    @Schema(description = "出款渠道")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CHANNEL)
    private String depositWithdrawChannel;

    @Schema(description = "出款渠道名称")
    private String depositWithdrawChannelText;

    @Schema(description = "申请时间")
    private Long createdTime;

    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    @Schema(description = "申请时间")
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "一审人员")
    private String firstAuditUser;

    @Schema(description = "一审时间")
    private Long firstAuditTime;

    @Schema(description = "一审时间 - 导出")
    private String firstAuditTimeExport;

    @Schema(description = "一审用时-秒")
    private Long firstAuditUseTime;



    @Schema(description = "待出款审核人")
    private String paymentAuditUser;

    @Schema(description = "待出款审核时间")
    private Long paymentAuditTime;


    @Schema(description = "待出款审核用时-秒")
    private Long paymentAuditUseTime;

    @Schema(description = "待出款审核备注")
    private String paymentAuditInfo;

    @Schema(description = "出款备注")
    private String payAuditRemark;

    @Schema(description = "提款时间")
    private Long updatedTime;

    @Schema(description = "审核时间-取最晚的时间")
    private Long auditTime;

    @ExcelProperty("审核人")
    private String auditUser;

    @ExcelProperty("审核时间")
    @ColumnWidth(10)
    private String auditTimeStr;

    public String getAuditTimeStr() {
        return auditTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(auditTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "审核用时-多个节点时间相加")
    @ExcelProperty("审核用时")
    @ColumnWidth(10)
    private String auditDuration;

    @Schema(description = "一审备注")
    @ExcelProperty("备注")
    @ColumnWidth(10)
    private String firstAuditInfo;

}
