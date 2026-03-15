package com.cloud.baowang.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.api.PayRechargeWithdrawApi;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.service.vendor.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/10/09 19:25
 * @description:
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class PayRechargeWithdrawApiImpl implements PayRechargeWithdrawApi {
    private final PaymentService paymentService;

    @Override
    public ResponseVO<PaymentResponseVO> payment(PaymentVO paymentVO) {
        return paymentService.payment(paymentVO);
    }

    @Override
    public WithdrawalResponseVO withdrawal(WithdrawalVO withdrawalVO) {
        return paymentService.withdrawal(withdrawalVO);
    }

    @Override
    public PayOrderResponseVO queryPayOrder(OrderQueryVO orderQueryVO) {
        return paymentService.queryPayOrder(orderQueryVO);
    }

    @Override
    public WithdrawalResponseVO queryWithdrawalOrder(OrderQueryVO orderQueryVO) {
        return paymentService.queryWithdrawalOrder(orderQueryVO);
    }
}
