package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: mufan
 */
@Data
@I18nClass
@Schema(title = "会员提款审核列表返回对象")
public class UserManualDepositPageResVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;


    @Schema(description = "审核员/锁单人")
    private String locker;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "订单号")
    private String orderNo;


    @Schema(description = "存款方式Id")
    private String depositWithdrawWayId;

    @Schema(description = "存款方式")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "虚拟币协议")
    private String networkType;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "存款金额")
    private BigDecimal applyAmount;


    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额")
    private BigDecimal arriveAmount;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态名称")
    private String statusText;

    @Schema(description = "审核操作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_SECURITY_REVIEW_STATUS)
    private Integer reviewOperation;

    @Schema(description = "审核操作")
    private String reviewOperationText;

    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private String isLocker;

    @Schema(description = "收款姓名/电子钱包姓名")
    private String recvUserName;

    @Schema(description = "收款银行编码")
    private String recvBankCode;

    @Schema(description = "收款银行名称")
    private String recvBankName;

    @Schema(description = "收款银行账户")
    private String depositWithdrawAddress;

    @Schema(description = "收款开户行")
    private String recvBankBranch;

    @Schema(description = "收款电子钱包账户")
    private String recvBankAccount;

    @Schema(description = "收款码")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String recvQrCode;

    @Schema(description = "收款码Url")
    private String recvQrCodeFileUrl;

    @Schema(description = "客服上传凭证")
    private String fileKey;

    @Schema(description = "客服上传凭证Url")
    private String fileKeyUrl;

    @Schema(description = "会员上传凭证")
    private String cashFlowFile;

    @Schema(description = "会员上传凭证Url")
    private String cashFlowFileUrl;

    private String depositWithdrawType;

    @Schema(description = "存款类型 crypto_currency、bank_card、electronic_wallet")
    private String depositWithdrawTypeCode;

    @Schema(description = "设备号")
    private String deviceNo;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    @Schema(description = "订单来源")
    private Integer deviceType;

    @Schema(description = "订单来源-name")
    private String deviceTypeText;

    @Schema(description = "三方订单号 交易hash")
    private String payTxId;

    @Schema(description = "汇率")
    private String exchangeRate;

    @Schema(description = "实际到账金额")
    private String tradeCurrencyAmount;

    @Schema(description = "方式手续费")
    private String wayFeeAmount;

    @Schema(description = "结算手续费")
    private String settlementFeeAmount;

    @Schema(description = "实际到账币种")
    private String coinCode;

}
