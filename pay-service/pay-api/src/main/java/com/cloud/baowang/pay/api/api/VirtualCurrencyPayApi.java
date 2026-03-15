package com.cloud.baowang.pay.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.pay.api.enums.ApiConstants;
import com.cloud.baowang.pay.api.vo.HotWalletAddressRequestVO;
import com.cloud.baowang.pay.api.vo.HotWalletAddressResponseVO;
import com.cloud.baowang.pay.api.vo.OrderDateTimeQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteVirtualCurrencyPay", value = ApiConstants.NAME)
@Tag(name = "RPC 虚拟币支付 服务")
public interface VirtualCurrencyPayApi {

    String PREFIX =  "callback/virtualCurrencyPay/api/";

    @Operation(summary = "创建热钱包地址")
    @PostMapping(value = PREFIX + "createHotWalletAddress")
    ResponseVO<HotWalletAddressResponseVO> createHotWalletAddress(@RequestBody HotWalletAddressRequestVO hotWalletAddressRequestVO);

    @Operation(summary = "查询订单-根据时间区间")
    @PostMapping(value = PREFIX + "queryByTime")
    ResponseVO<List<TradeNotifyVo>> queryByTime(@RequestBody OrderDateTimeQueryVO vo);
}
