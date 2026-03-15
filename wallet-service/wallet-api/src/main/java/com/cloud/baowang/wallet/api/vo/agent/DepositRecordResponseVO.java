package com.cloud.baowang.wallet.api.vo.agent;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "代理存款记录 ResponseVO")
@I18nClass
public class DepositRecordResponseVO {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "支付方式")
    @I18nField
    private String paymentMethod;

    @Schema(description = "币种代码")
    private String currencyCode;

    @Schema(description = "存款金额")
    private BigDecimal arriveAmount;

    @Schema(description = "赠送金额")
    private BigDecimal receiveAmount;

    @Schema(description = "存款时间")
    private Long updatedTime;

    @Schema(description = "状态 0处理中 1充值成功 2充值失败")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS)
    private String customerStatus;
    @Schema(description = "状态-Name")
    private String customerStatusText;
}
