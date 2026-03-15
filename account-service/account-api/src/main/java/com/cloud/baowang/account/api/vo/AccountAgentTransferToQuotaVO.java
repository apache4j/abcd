package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ford
 * @Date 2025-10-14
 */
@Data
@Schema( description = "代理转账下级额度请求对象")
public class AccountAgentTransferToQuotaVO {

    @Schema( description ="转账额度钱包账变对象")
    private AccountAgentCoinAddReqVO agentQuotaCoinReqVO;

    @Schema( description ="转账下级额度钱包账变对象")
    private AccountAgentCoinAddReqVO agentSubordinatesQuotaCoinReqVO;

}
