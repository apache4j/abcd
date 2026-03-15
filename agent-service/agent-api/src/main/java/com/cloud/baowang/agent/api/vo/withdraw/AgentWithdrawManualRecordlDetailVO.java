package com.cloud.baowang.agent.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 本次提款详情
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@I18nClass
@Schema(title = "代理人工提款详情")
public class AgentWithdrawManualRecordlDetailVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "代理id")
    private String agentId;
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "提款金额")
    private BigDecimal applyAmount;

    /**
     * 交易币种金额
     */
    @Schema(description ="预计到账")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "账变金额")
    private BigDecimal arriveAmount;


    @Schema(description = "费率类型")
    private Integer feeType;

    private BigDecimal feeRate;


    @Schema(description = "提款类型id")
    private String depositWithdrawTypeId;

    @Schema(description = "提款类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.WITHDRAW_TYPE)
    private String depositWithdrawTypeCode;

    @Schema(description = "提款类型名称")
    private String depositWithdrawTypeCodeText;

    @Schema(description = "提款方式")
    private String depositWithdrawWayId;

    @Schema(description = "提款方式名称")
    @I18nField
    private String depositWithdrawWay;


    @Schema(description = "提款币种")
    private String currencyCode;


    @Schema(description = "出款时间")
    private Long updatedTime;

    @Schema(description = "出款人")
    private String updater;


    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(description = "订单状态名称")
    private String customerStatusText;


    /**
     * 存取款名字
     */
    @Schema(description = "名")
    private String depositWithdrawName;

    /**
     * 存取款姓
     */
    @Schema(description = "姓")
    private String depositWithdrawSurname;

    @Schema(description = "账号")
    private String depositWithdrawAddress;

    @Schema(description = "IFSC码(印度)")
    private String ifscCode;

    @Schema(description = "CPF")
    private String cpf;

    @Schema(description = "提款终端设备号")
    private String deviceNo;

    @Schema(description = "提款IP")
    private String applyIp;

    @Schema(description = "出款凭证KEY")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String fileKey;

    @Schema(description = "出款凭证完整RUL")
    private String fileKeyFileUrl;

}
