package com.cloud.baowang.wallet.api.vo.report;


import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "会员存提发送累计MQ请求对象")
@Builder
public class DepositWtihdrawMqSendVO {

    @Schema(title = "类型1存款 2取款")
    private String type;

    @Schema(title = "币种")
    private String currency;

    @Schema(title = "存取款方式ID")
    private String depositWithdrawWayId;

    @Schema(title = "充值提款金额")
    private BigDecimal amount;

    @Schema(title = "充值提款客户手续费")
    private BigDecimal feeAmount;

    @Schema(title = "充值提款 方式手续费")
    private BigDecimal wayFeeAmount;

    @Schema(title = "充值提款结算手续费")
    private BigDecimal settlementFeeAmount;

    @Schema(title = "大额存取款金额")
    private BigDecimal largeAmount;


    @Schema(title = "代理代存金额")
    private BigDecimal depositSubordinatesAmount;

    private String orderNo;

    private Long dateTime;

    private WalletUserInfoVO userInfoVO;
}
