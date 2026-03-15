package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "查询某代理下 某个会员的充值提款总额")
public class GetAllArriveAmountByAgentUserResponseVO {

    @Schema(title = "充值总额")
    private BigDecimal arriveAmount = BigDecimal.ZERO;

    @Schema(title = "提款总额")
    private BigDecimal applyAmount = BigDecimal.ZERO;
}
