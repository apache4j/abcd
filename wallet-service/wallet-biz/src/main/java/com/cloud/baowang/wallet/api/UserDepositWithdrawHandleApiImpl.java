package com.cloud.baowang.wallet.api;


import com.cloud.baowang.wallet.api.api.UserDepositWithdrawHandleApi;
import com.cloud.baowang.wallet.api.vo.report.DepositWtihdrawMqSendVO;
import com.cloud.baowang.wallet.service.UserDepositWithdrawHandleService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class UserDepositWithdrawHandleApiImpl implements UserDepositWithdrawHandleApi {

    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;


    @Override
    public void rechargeMq(DepositWtihdrawMqSendVO vo) {
        userDepositWithdrawHandleService.rechargeMq( vo);
    }
}
