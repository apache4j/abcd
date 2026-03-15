package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ford
 * @Date 2025-10-14
 */
@Data
@Schema( description = "代理转账下级佣金请求对象")
public class AccountAgentTransferToCommissionVO {

    @Schema( description ="转账佣金钱包账变对象")
    private AccountAgentCoinAddReqVO agentCommissionCoinReqVO;

    @Schema( description ="转账下级佣金钱包账变对象")
    private AccountAgentCoinAddReqVO agentSubordinatesCommissionCoinReqVO;

}
