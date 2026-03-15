package com.cloud.baowang.wallet.api.vo.userCoinManualDown;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ManualDownRecordVO {
   @Schema(description = "会员账号")
    private String userAccount;

   @Schema(description = "调整金额")
    private BigDecimal adjustAmount;


    private Integer number;

   @Schema(description = "是否大金额")
    private String isBigMoney;

   @Schema(description = "代理账号")
    private String agentAccount;

}
