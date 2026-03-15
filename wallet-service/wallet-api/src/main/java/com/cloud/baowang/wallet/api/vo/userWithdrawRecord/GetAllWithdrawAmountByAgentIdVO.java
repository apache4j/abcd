package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "查询某个代理的提款总额(按照会员分组) VO")
public class GetAllWithdrawAmountByAgentIdVO {

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "提款申请金额")
    private BigDecimal applyAmount;
}
