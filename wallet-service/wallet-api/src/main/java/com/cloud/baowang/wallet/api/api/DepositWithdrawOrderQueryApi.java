package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.DepositWithdrawOrderQueryResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteDepositWithdrawOrderQueryApi", value = ApiConstants.NAME)
@Tag(name = "RPC 充值提款订单查询 服务")
public interface DepositWithdrawOrderQueryApi {

    String PREFIX = ApiConstants.PREFIX + "/depositWithdrawOrderQuery/api/";

    @Operation(summary = "订单号查询 订单归属（会员/代理） 订单类型（存款/取款）")
    @PostMapping(value = PREFIX + "queryOrderByOrderNo")
    DepositWithdrawOrderQueryResponseVO queryOrderByOrderNo(@RequestParam("orderNo") String orderNo) ;



}
