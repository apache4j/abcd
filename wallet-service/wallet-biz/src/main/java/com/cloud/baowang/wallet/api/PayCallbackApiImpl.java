package com.cloud.baowang.wallet.api;

import com.cloud.baowang.wallet.api.api.PayCallbackApi;
import com.cloud.baowang.wallet.api.vo.userCoin.VirtualCurrencyPayCallbackVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackDepositParamVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackWithdrawParamVO;
import com.cloud.baowang.wallet.service.UserDepositWithdrawCallbackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class PayCallbackApiImpl implements PayCallbackApi {

    private final UserDepositWithdrawCallbackService userDepositWithdrawCallbackService;



    @Override
    public Boolean virtualCurrencyDepositCallback(VirtualCurrencyPayCallbackVO vo) {
        return userDepositWithdrawCallbackService.virtualCurrencyDepositCallback(vo);
    }

    @Override
    public boolean withdrawCallback(CallbackWithdrawParamVO callbackWithdrawParamVO) {
        return userDepositWithdrawCallbackService.userWithdrawCallback(callbackWithdrawParamVO);
    }

    @Override
    public Boolean userDepositCallback(@RequestBody CallbackDepositParamVO callbackDepositParamVO){
        return userDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
    }
}
