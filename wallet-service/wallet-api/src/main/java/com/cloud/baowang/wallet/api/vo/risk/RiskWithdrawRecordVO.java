package com.cloud.baowang.wallet.api.vo.risk;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@ExcelIgnoreUnannotated
public class RiskWithdrawRecordVO {

    @Schema(description = "代理")
    private String userLabelId;

    @Schema(description = "会员标签")
    private String userLabelName;

    @Schema(description = "代理")
    private String agentAccount;

    private Long userId;
    private String accountBranch;
    private String depositWithdrawAddress;
    private String depositWithdrawSurname;
    private String email;
    private String telephone;

    private String areaCode_$_telephone;
    public String getAreaCode_$_areaCode() {
        return areaCode;
    }
    private String province;
    private String city;
    private String address;
    private String updatedTimeExport;
    private String depositWithdrawName;
    private String cpf;
    private String postalCode;
    private String rechargeWithdrawTimeConsuming;
    private String deviceTypeText;
    private String status;
    private String statusText;
    private String customerStatusText;
    private String applyIp;
    private String applyIpRiskLevel;
    private String deviceNo;
    private String deviceNoRiskLevel;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "会员ID")
    private String userAccount;
    @Schema(description = "会员注册信息")
    private String userRegister;
    @Schema(description = "订单来源")
    private String deviceType;
    @Schema(description = "客户端状态")
    private String customerStatus;
    @Schema(description = "提款IP+提款ip风控层级")
    private String applyIp_$_applyIpRiskLevel;
    @Schema(description = "提款终端设备号+提款终端设备号风控层级")
    private String deviceNo_$_deviceNoRiskLevel;
    @Schema(description = "提款方式")
    @I18nField
    private String depositWithdrawWay;
    @Schema(description = "提款币种")
    private String currencyCode;
    @Schema(description = "提款金额")
    private BigDecimal applyAmount;
    @Schema(description = "提款类型")
    private String depositWithdrawTypeId;
    @Schema(description = "提款类型code")
    private String depositWithdrawTypeCode;
    @Schema(description = "提款类型名称")
    private String depositWithdrawTypeName;
    @Schema(description = "提款方式ID")
    private String depositWithdrawWayId;
    @Schema(description = "提款币种金额")
    private BigDecimal tradeCurrencyAmount;
    @Schema(description = "汇率")
    private BigDecimal exchangeRate;
    @Schema(description = "是否大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;
    @Schema(description = "是否大额提款名称")
    private String isBigMoneyText;
    @Schema(description = "是否首提")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;
    @Schema(description = "是否首提名称")
    private String isFirstOutText;
    @Schema(description = "出款通道")
    private String depositWithdrawChannelName;
    private String createdTimeStr;
    @Schema(description = "提款时间")
    private Long updatedTime;
    @Schema(description = "图片")
    private String fileKey;
    @Schema(description = "提款申请时间")
    private Long createdTime;
    @Schema(description = "提款完成时间")
    private Long applyCompleteTime;
    private String applyCompleteTimeStr;
    @Schema(description = "用时,单位毫秒")
    private String takeTime;
    @Schema(description = "一审备注")
    private String firstAuditInfo;
    @Schema(description = "挂单备注")
    private String orderAuditInfo;
    @Schema(description = "出款备注")
    private String paymentAuditInfo;
    @Schema(description = "账户类型")
    private String accountType;
    @Schema(description = "银行名称")
    private String bankName;
    @Schema(description = "银行编码")
    private String bankCode;
    @Schema(description = "银行卡号")
    private String bankCard;
    @Schema(description = "姓")
    private String surname;
    @Schema(description = "名")
    private String userName;
    /*@Schema(description = "邮箱")
    private String userEmail;*/
    @Schema(description = "手机区号")
    private String areaCode;
    /*@Schema(description = "手机号")
    private String userPhone;*/
    @Schema(description = "省")
    private String provinceName;
    @Schema(description = "市")
    private String cityName;
    @Schema(description = "详细地址")
    private String detailAddress;
    @Schema(description = "网络协议")
    private String networkType;
    @Schema(description = "加密货币收款地址")
    private String addressNo;
    @Schema(description = "后台备注")
    private String withdrawRemark;
    @Schema(description = "手续费")

    private BigDecimal feeAmount;
    @Schema(description = "实际下分金额")

    private BigDecimal arriveAmount;
    @Schema(description = "出款耗时")

    private String depositsTakeTime;
    @Schema(description = "审核员")

    private String payAuditUser;
    @Schema(description = "提款账号信息Excel")

    private String withdrawAccountInfoExcel;
    @Schema(description = "提款信息")
    private String withdrawInfo;
    @Schema(description = "提款账号信息")
    private String withdrawAccountInfo;
    @Schema(description = "提款账号类型名称")
    private String withdrawAccountTypeName;

    public String getAreaCode_$_telephone() {
        return areaCode;
    }

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    public String getApplyCompleteTimeStr() {
        return applyCompleteTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(applyCompleteTime, CurrReqUtils.getTimezone());
    }


}
