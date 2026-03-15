package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ford
 * @Date 2025-10-14
 */
@Data
@Schema( description = "代理额度代存请求对象")
public class AccountAgentQuotaDepositSubordinatesVO {

    @Schema( description ="代存额度钱包账变对象")
    private AccountAgentCoinAddReqVO agentQuotaCoinReqVO;

    @Schema( description ="转账下级会员钱包账变对象")
    private AccountUserCoinAddReqVO userCoinAddReqVO;

}
