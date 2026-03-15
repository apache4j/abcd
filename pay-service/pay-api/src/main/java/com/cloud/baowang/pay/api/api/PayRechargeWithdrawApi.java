package com.cloud.baowang.pay.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.feign.constants.RpcConstants;
import com.cloud.baowang.pay.api.enums.ApiConstants;
import com.cloud.baowang.pay.api.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: fangfei
 * @createTime: 2024/10/09 19:19
 * @description:
 */
@FeignClient(contextId = "remotePayWithdraw", value = ApiConstants.NAME)
@Tag(name = "RPC 支付 服务")
public interface PayRechargeWithdrawApi {
    String PREFIX = RpcConstants.NACOS_API_PREFIX+"/payWithdrawAp/api/";


    /**
     * 三方充值接口
     */
    @Operation(summary = "三方充值接口")
    @PostMapping(value = PREFIX + "payment")
    ResponseVO<PaymentResponseVO> payment(@RequestBody PaymentVO paymentVO);

    /**
     * 三方提款接口
     */
    @Operation(summary = "三方提款接口")
    @PostMapping(value = PREFIX + "withdrawal")
    WithdrawalResponseVO withdrawal(@RequestBody WithdrawalVO withdrawalVO);

    /**
     * 充值订单查询接口
     */
    @Operation(summary = "充值订单查询接口")
    @PostMapping(value = PREFIX + "queryPayOrder")
    PayOrderResponseVO queryPayOrder(@RequestBody OrderQueryVO orderQueryVO);

    /**
     * 提款订单查询接口
     */
    @Operation(summary = "提款订单查询接口")
    @PostMapping(value = PREFIX + "queryWithdrawalOrder")
    WithdrawalResponseVO queryWithdrawalOrder(@RequestBody OrderQueryVO orderQueryVO);
}
