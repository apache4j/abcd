package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ford
 * @Date 2025-10-14
 */
@Data
@Schema( description = "代理额度转账请求对象")
public class AccountAgentQuotaTransferVO {

    @Schema( description ="佣金钱包账变对象")
    private AccountAgentCoinAddReqVO agentCommissionCoinReqVO;

    @Schema( description ="额度钱包账变对象")
    private AccountAgentCoinAddReqVO agentQuotaCoinReqVO;

}
