package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ford
 * @Date 2025-10-14
 */
@Data
@Schema( description = "代理佣金代存请求对象")
public class AccountAgentCommissionDepositSubordinatesVO {

    @Schema( description ="代存佣金钱包账变对象")
    private AccountAgentCoinAddReqVO agentCommissionCoinReqVO;

    @Schema( description ="转账下级会员钱包账变对象")
    private AccountUserCoinAddReqVO userCoinAddReqVO;

}
