package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userCoin.VirtualCurrencyPayCallbackVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackDepositParamVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackWithdrawParamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remotePayCallbackApi", value = ApiConstants.NAME)
@Tag(name = "RPC 充值提款支付回调 服务")
public interface PayCallbackApi {

    String PREFIX = ApiConstants.PREFIX + "/payCallback/api/";

    @Operation(summary = "虚拟币支付充值回调")
    @PostMapping(value = PREFIX + "virtualCurrencyDepositCallback")
    Boolean virtualCurrencyDepositCallback(@RequestBody VirtualCurrencyPayCallbackVO vo) ;

    @Operation(summary = "提现回调")
    @PostMapping(value = PREFIX + "withdrawCallback")
    boolean withdrawCallback(@RequestBody CallbackWithdrawParamVO callbackWithdrawParamVO);


    @Operation(summary = "三方平台-支付付回调", description = "三方平台-支付回调")
    @PostMapping(value = PREFIX+"userDepositCallback")
    Boolean userDepositCallback(@RequestBody CallbackDepositParamVO callbackDepositParamVO);


}
