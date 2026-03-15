package com.cloud.baowang.api;

import com.cloud.baowang.agent.api.api.AgentPayCallbackApi;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.pay.api.api.VirtualCurrencyPayApi;
import com.cloud.baowang.pay.api.vo.HotWalletAddressRequestVO;
import com.cloud.baowang.pay.api.vo.HotWalletAddressResponseVO;
import com.cloud.baowang.pay.api.vo.OrderDateTimeQueryVO;
import com.cloud.baowang.service.VirtualCurrencyPayService;
import com.cloud.baowang.wallet.api.api.PayCallbackApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class VirtualCurrencyPayApiImpl implements VirtualCurrencyPayApi {

    private final PayCallbackApi payCallbackApi;

    private final VirtualCurrencyPayService virtualCurrencyPayService;

    private final AgentPayCallbackApi agentPayCallbackApi;

    @Override
    public ResponseVO<HotWalletAddressResponseVO> createHotWalletAddress(HotWalletAddressRequestVO hotWalletAddressRequestVO) {
        return virtualCurrencyPayService.createHotWalletAddress(hotWalletAddressRequestVO);
    }

    @Override
    public ResponseVO<List<TradeNotifyVo>> queryByTime(OrderDateTimeQueryVO vo) {
        return ResponseVO.success(virtualCurrencyPayService.queryByTime(vo));
    }
}
