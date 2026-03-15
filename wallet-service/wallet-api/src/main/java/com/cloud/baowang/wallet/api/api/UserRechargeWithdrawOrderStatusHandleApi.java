package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.VirtualCurrencyRechargeOmissionsReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserRechargeWithdrawOrderStatusHandleApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员存款取款订单状态处理 服务")
public interface UserRechargeWithdrawOrderStatusHandleApi {

    String PREFIX = ApiConstants.PREFIX + "/userRechargeWithdrawOrderStatusHandle/api/";


    /**
     * 会员充值订单处理任务
     * @return
     */
    @Operation(summary = "会员充值订单处理任务", description = "会员充值订单处理任务")
    @PostMapping(value = PREFIX+"rechargeOrderHandle")
    ResponseVO rechargeOrderHandle();


    /**
     * 会员取款订单处理任务
     * @return
     */
    @Operation(summary = "会员取款订单处理任务", description = "会员取款订单处理任务")
    @PostMapping(value = PREFIX+"withdrawOrderHandle")
    ResponseVO withdrawOrderHandle();

    /**
     * 会员虚拟币订单拉取前半小时订单，查看是否有遗漏
     * @param startTime
     */
    @Operation(summary = "会员取款订单处理任务", description = "会员取款订单处理任务")
    @PostMapping(value = PREFIX+"virtualCurrencyRechargeOmissionsHandle")
    ResponseVO virtualCurrencyRechargeOmissionsHandle(@RequestBody VirtualCurrencyRechargeOmissionsReqVO vo);
}
