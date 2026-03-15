package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.user.api.api.ads.AdsManageApi;
import com.cloud.baowang.user.api.vo.ads.UserRechargeEventVO;
import com.cloud.baowang.wallet.api.vo.recharge.UserRechargeReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackDepositParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 废弃
 */
@Slf4j
@Service
@AllArgsConstructor
public class RechargeAdsService {
    private final AdsManageApi adsManageApi;

    @Async
    public void onRechargeAdsArrive(UserInfoVO userInfoVO, CallbackDepositParamVO orderNoVO){
        String value = RedisUtil.getValue(orderNoVO.getOrderNo());
        if (StringUtils.isEmpty(value)){
            return;
        }
        RedisUtil.deleteKey(value);
        log.info(" onRechargeAdsArrive : userInfoVO :"+userInfoVO);
        UserRechargeEventVO eventVO = BeanUtil.copyProperties(userInfoVO, UserRechargeEventVO.class);
        log.info(" onRechargeAdsArrive : eventVO :"+eventVO);
        eventVO.setOrderNo(orderNoVO.getOrderNo());
        eventVO.setAmount(orderNoVO.getAmount());
        eventVO.setEventId(value);
        eventVO.setReqIp(CurrReqUtils.getReqIp());
        eventVO.setDeviceType(CurrReqUtils.getReqDeviceType());
        adsManageApi.onRechargeAdsEventArrive(eventVO);
    }
}
