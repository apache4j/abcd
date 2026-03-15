package com.cloud.baowang.wallet.api.vo.report;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "会员存提发送累计MQ请求对象")
@Builder
public class DepositWithdrawAllRecordVO {

    @Schema(description = "类型 1存款，2取款")
    private String type;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "账号类型")
    private String accountType;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "代理id")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(title = "币种")
    private String currency;


    @Schema(title = "充值提款金额")
    private BigDecimal amount;

    @Schema(title = "存取款方式ID")
    private String depositWithdrawWayId;

    @Schema(title = "客户手续费")
    private BigDecimal feeAmount;

    @Schema(title = "方式手续费")
    private BigDecimal wayFeeAmount;

    @Schema(title = "结算手续费")
    private BigDecimal settlementFeeAmount;

    @Schema(title = "大额存取款金额")
    private BigDecimal largeAmount;

    @Schema(title = "代理代存金额")
    private BigDecimal depositSubordinatesAmount;

    @Schema(title = "记录时间")
    private Long recordTime;

}
