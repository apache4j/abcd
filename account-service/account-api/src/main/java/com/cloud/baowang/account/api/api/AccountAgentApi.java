package com.cloud.baowang.account.api.api;

import com.cloud.baowang.account.api.constant.ApiConstants;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ford
 * @date 2025-10-11
 */
@FeignClient(contextId = "accountAgentApi", value = ApiConstants.NAME)
@Tag(name = "账务模块-代理-接口")
public interface AccountAgentApi {
    /**
     *  1代理额度余额账变
     *  2代理佣金余额账变
     */
    String PREFIX = ApiConstants.PREFIX + "/accountAgentCoin/api/";

    @PostMapping(value = PREFIX + "agentQuotaCoin")
    @Operation(summary = "代理额度余额账变")
    Boolean agentQuotaCoin(@RequestBody AccountAgentCoinAddReqVO accountAgentCoinAddReqVO) ;


    @PostMapping(value = PREFIX + "agentCommissionCoin")
    @Operation(summary = "代理佣金余额账变")
    Boolean agentCommissionCoin(@RequestBody AccountAgentCoinAddReqVO accountAgentCoinAddReqVO) ;



}
