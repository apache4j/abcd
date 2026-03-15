package com.cloud.baowang.service.vendor;

import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;

public interface BasePayService {
    /**
     * 存款
     */
    PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo);

    /**
     * 提款
     */
    WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo);

    /**
     * 查询支付订单
     */
    PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo);

    /**
     * 查询代付订单
     */
    WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO,String orderNo);
}
