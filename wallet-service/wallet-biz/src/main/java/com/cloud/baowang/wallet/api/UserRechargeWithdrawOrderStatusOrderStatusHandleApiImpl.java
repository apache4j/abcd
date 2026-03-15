package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserRechargeWithdrawOrderStatusHandleApi;
import com.cloud.baowang.wallet.api.vo.recharge.VirtualCurrencyRechargeOmissionsReqVO;
import com.cloud.baowang.wallet.service.UserRechargeWithdrawOrderStatusHandleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@AllArgsConstructor
@RestController
public class UserRechargeWithdrawOrderStatusOrderStatusHandleApiImpl implements UserRechargeWithdrawOrderStatusHandleApi {

    private final UserRechargeWithdrawOrderStatusHandleService userRechargeWithdrawOrderStatusHandleService;


    @Override
    public ResponseVO rechargeOrderHandle() {


        return userRechargeWithdrawOrderStatusHandleService.rechargeOrderHandle();
    }

    @Override
    public ResponseVO withdrawOrderHandle() {
        return userRechargeWithdrawOrderStatusHandleService.withdrawOrderHandle();
    }

    @Override
    public ResponseVO virtualCurrencyRechargeOmissionsHandle(VirtualCurrencyRechargeOmissionsReqVO vo) {
        return userRechargeWithdrawOrderStatusHandleService.virtualCurrencyRechargeOmissionsHandle(vo);
    }
}
