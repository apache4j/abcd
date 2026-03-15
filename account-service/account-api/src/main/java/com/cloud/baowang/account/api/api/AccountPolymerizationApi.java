package com.cloud.baowang.account.api.api;


import com.cloud.baowang.account.api.constant.ApiConstants;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountAgentCommissionDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaTransferVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToCommissionVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToQuotaVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserWtcToMainCurrencyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiqi
 * @date 2025-10-20
 * 会员账户，平台币账户，代理账户 聚合账变处理
 */
@FeignClient(contextId = "accountPolymerizationApi", value = ApiConstants.NAME)
@Tag(name = "账务模块-聚合-接口")
public interface AccountPolymerizationApi {

    String PREFIX = ApiConstants.PREFIX + "/accountPolymerization/api/";
    /**
     * WTC兑换   平台币账户扣减   会员账户增加
     */
    @PostMapping(value = PREFIX + "WtcToMainCurrency")
    @Operation(summary = "WTC兑换")
    AccountCoinResultVO WtcToMainCurrency(@RequestBody AccountUserWtcToMainCurrencyVO accountUserWtcToMainCurrencyVO);

    /**
     * 代理额度转账 佣金钱包->额度钱包
     */
    @PostMapping(value = PREFIX + "agentQuotaTransfer")
    @Operation(summary = "代理额度转账 佣金钱包->额度钱包")
    Boolean agentQuotaTransfer(@RequestBody AccountAgentQuotaTransferVO accountAgentQuotaTransferVO);

    /**
     * 代理转账下级 额度钱包->额度钱包
     * @param agentTransferToQuotaVO 代理转账下级请求对象
     */
    @PostMapping(value = PREFIX + "agentTransferToQuota")
    @Operation(summary = "代理转账下级 额度钱包->额度钱包")
    Boolean agentTransferToQuota(@RequestBody AccountAgentTransferToQuotaVO agentTransferToQuotaVO);
    /**
     * 代理转账下级 佣金钱包->佣金钱包
     * @param agentTransferToCommissionVO 代理转账下级 佣金钱包
     */
    @PostMapping(value = PREFIX + "agentTransferToCommission")
    @Operation(summary = "代理转账下级 佣金钱包->佣金钱包")
    Boolean agentTransferToCommission(@RequestBody AccountAgentTransferToCommissionVO agentTransferToCommissionVO);
    /**
     * 代理额度钱包代会员存款
     * @param agentQuotaDepositSubordinatesVO 代理额度钱包代会员存款
     */
    @PostMapping(value = PREFIX + "agentQuotaDepositSubordinates")
    @Operation(summary = "代理额度钱包代会员存款")
    Boolean agentQuotaDepositSubordinates(@RequestBody AccountAgentQuotaDepositSubordinatesVO agentQuotaDepositSubordinatesVO);

    /**
     * 代理佣金钱包代会员存款
     * @param agentCommissionDepositSubordinatesVO
     */
    @PostMapping(value = PREFIX + "agentCommissionDepositSubordinates")
    @Operation(summary = "代理佣金钱包代会员存款")
    Boolean agentCommissionDepositSubordinates(@RequestBody AccountAgentCommissionDepositSubordinatesVO agentCommissionDepositSubordinatesVO);
}
