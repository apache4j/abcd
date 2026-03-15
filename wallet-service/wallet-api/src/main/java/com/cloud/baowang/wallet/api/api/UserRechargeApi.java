package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserRechargeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员存款 服务")
public interface UserRechargeApi {

    String PREFIX = ApiConstants.PREFIX + "/userRecharge/api/";

    @Operation(summary = "处理中订单")
    @PostMapping(value = PREFIX + "processingOrder")
    HandledDepositOrderVO processingOrder(@RequestParam("userId")String userId);

    @Operation(summary = "获取充值订单详情")
    @PostMapping(value = PREFIX + "depositOrderDetail")
    ResponseVO<UserDepositOrderDetailVO> depositOrderDetail(@RequestBody OrderNoVO orderNoVO);

    @Operation(summary = "上传凭证")
    @PostMapping(value = PREFIX + "uploadVoucher")
    ResponseVO<Integer> uploadVoucher(@RequestBody DepositOrderFileVO depositOrderFileVO);

    @Operation(summary = "撤销充值订单")
    @PostMapping(value = PREFIX + "cancelDepositOrder")
    ResponseVO<Integer> cancelDepositOrder(@RequestBody OrderNoVO orderNoVO);


    @Operation(summary = "会员充值")
    @PostMapping(value = PREFIX + "userRecharge")
    ResponseVO<OrderNoVO> userRecharge(@RequestBody UserRechargeReqVO userRechargeReqVo);

    @Operation(summary = "催单")
    @PostMapping(value = PREFIX + "urgeOrder")
    void urgeOrder(@RequestBody OrderNoVO vo);

    /**
     * 获取充值提款配置
     * @param rechargeConfigRequestVO
     * @return
     */
    @Operation(summary = "充值提款配置")
    @PostMapping(value = PREFIX + "getRechargeConfig")
    ResponseVO<RechargeConfigVO> getRechargeConfig(@RequestBody RechargeConfigRequestVO rechargeConfigRequestVO);


}
