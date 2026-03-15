package com.cloud.baowang.service.vendor;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SpringBeanUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {


    public ResponseVO<PaymentResponseVO> payment(PaymentVO paymentVO) {
        String orderId = paymentVO.getOrderId();

        SystemRechargeChannelBaseVO channelBaseVO = ConvertUtil.entityToModel(paymentVO.getRechargeChannelVO(), SystemRechargeChannelBaseVO.class);
        if (channelBaseVO == null) {
            throw new BaowangDefaultException(ResultCode.CHANNEL_NOT_EXISTS);
        }

        BasePayService basePayService = (BasePayService) SpringBeanUtil.getBean(channelBaseVO.getChannelName());
        PaymentResponseVO paymentResponseVO = basePayService.creatPayOrder(channelBaseVO, paymentVO, orderId);
        if (ObjectUtil.isNotEmpty(paymentResponseVO)) {
            paymentResponseVO.setDepositTime(System.currentTimeMillis());
            return ResponseVO.success(paymentResponseVO);
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    public WithdrawalResponseVO withdrawal(WithdrawalVO withdrawalVO) {
        //查询符合条件的三方平台
        SystemWithdrawChannelResponseVO channelBaseVO = ConvertUtil.entityToModel(withdrawalVO.getWithdrawChannelVO(), SystemWithdrawChannelResponseVO.class);
        if (channelBaseVO == null) {
            throw new BaowangDefaultException(ResultCode.CHANNEL_NOT_EXISTS);
        }

        BasePayService basePayService = (BasePayService) SpringBeanUtil.getBean(channelBaseVO.getChannelName());
        WithdrawalResponseVO withdrawalResponseVO =  basePayService.creatPayoutOrder(channelBaseVO, withdrawalVO, withdrawalVO.getOrderNo());
        return withdrawalResponseVO;
    }

    public WithdrawalResponseVO queryWithdrawalOrder(OrderQueryVO orderQueryVO) {
        IdVO idVO=new IdVO();
        idVO.setId(orderQueryVO.getChannelId());
        SystemWithdrawChannelResponseVO baseVO = ConvertUtil.entityToModel(orderQueryVO.getWithdrawChannelVO(), SystemWithdrawChannelResponseVO.class);
        if(baseVO==null|| !StringUtils.hasText(baseVO.getChannelName())){
            log.info("queryWithdrawalOrder 根据id:{}未查询到渠道信息",idVO);
            return null;
        }
        BasePayService basePayService = (BasePayService) SpringBeanUtil.getBean(baseVO.getChannelName());
        baseVO.setThirdOrderNo(orderQueryVO.getThirdOrderNo());
        return basePayService.queryPayoutOrder(baseVO, orderQueryVO.getOrderNo());
    }

    public PayOrderResponseVO queryPayOrder(OrderQueryVO orderQueryVO) {
        IdVO idVO=new IdVO();
        idVO.setId(orderQueryVO.getChannelId());
        SystemRechargeChannelBaseVO baseVO = ConvertUtil.entityToModel(orderQueryVO.getRechargeChannelVO(), SystemRechargeChannelBaseVO.class);
        if(baseVO==null|| !StringUtils.hasText(baseVO.getChannelName())){
            log.info("queryPayOrder 根据id:{}未查询到渠道信息",idVO);
            return null;
        }
        baseVO.setThirdOrderNo(orderQueryVO.getThirdOrderNo());
        BasePayService basePayService = (BasePayService) SpringBeanUtil.getBean(baseVO.getChannelName());
        return basePayService.queryPayOrder(baseVO, orderQueryVO.getOrderNo());
    }
}
