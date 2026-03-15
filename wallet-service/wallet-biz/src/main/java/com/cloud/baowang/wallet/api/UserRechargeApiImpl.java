package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserRechargeApi;
import com.cloud.baowang.wallet.api.vo.recharge.DepositOrderFileVO;
import com.cloud.baowang.wallet.api.vo.recharge.HandledDepositOrderVO;
import com.cloud.baowang.wallet.api.vo.recharge.OrderNoVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeConfigVO;
import com.cloud.baowang.wallet.api.vo.recharge.UserDepositOrderDetailVO;
import com.cloud.baowang.wallet.api.vo.recharge.UserRechargeReqVO;
import com.cloud.baowang.wallet.service.UserRechargeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@AllArgsConstructor
@RestController
public class UserRechargeApiImpl implements UserRechargeApi {

    private final UserRechargeService userRechargeService;






    @Override
    public HandledDepositOrderVO processingOrder(String userId) {
        return userRechargeService.processingOrder(userId);
    }

    @Override
    public ResponseVO<UserDepositOrderDetailVO> depositOrderDetail(OrderNoVO orderNoVO) {
        return ResponseVO.success(userRechargeService.depositOrderDetail(orderNoVO));
    }

    @Override
    public ResponseVO<Integer> uploadVoucher(DepositOrderFileVO depositOrderFileVO) {
        return ResponseVO.success(userRechargeService.uploadVoucher(depositOrderFileVO));
    }

    @Override
    public ResponseVO<Integer> cancelDepositOrder(OrderNoVO orderNoVO) {
        return ResponseVO.success(userRechargeService.cancelDepositOrder(orderNoVO));
    }


    @Override
    public ResponseVO<OrderNoVO> userRecharge(UserRechargeReqVO userRechargeReqVo) {
        return userRechargeService.userRecharge(userRechargeReqVo);

    }

    @Override
    public void urgeOrder(OrderNoVO vo) {
        userRechargeService.urgeOrder(vo);
    }

    @Override
    public ResponseVO<RechargeConfigVO> getRechargeConfig(RechargeConfigRequestVO vo) {

        return  userRechargeService.getRechargeConfig(vo);
    }


}
