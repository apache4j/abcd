package com.cloud.baowang.wallet.service;


import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.account.api.api.AccountUserApi;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.AccountUserCoinRequestMqVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class WalletUserCommonCoinService {

    private final AccountUserApi accountUserApi;


    private final UserCoinService userCoinService;


    /**
     * 会员普通账变
     * @param userCoinAddVO
     * @return
     */
    public CoinRecordResultVO userCommonCoinAdd(UserCoinAddVO userCoinAddVO){
        log.info("会员{}账变开始",userCoinAddVO.getUserId());

        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        log.info("会员{}账变调用开关值{}",userCoinAddVO.getUserId(),accountOpenFlag);
        CoinRecordResultVO coinRecordResultVO = null;
        //公共调用
        if(ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)){
            //全部老账变
            coinRecordResultVO = userCoinService.addCoin(userCoinAddVO);

        }else if(CommonConstant.business_one.equals(accountOpenFlag)){
            //财务流量推送
            coinRecordResultVO = userCoinService.addCoin(userCoinAddVO);
            AccountUserCoinAddReqVO accountUserCoinAddReqVO = ConvertUtil.entityToModel(userCoinAddVO,AccountUserCoinAddReqVO.class);
            setUserInfo(accountUserCoinAddReqVO,userCoinAddVO);
            AccountUserCoinRequestMqVO accountUserCoinRequestMqVO = ConvertUtil.entityToModel(accountUserCoinAddReqVO,AccountUserCoinRequestMqVO.class);
            KafkaUtil.send(TopicsConstants.ACCOUNT_USER_COIN_TOPIC,accountUserCoinRequestMqVO);
        }else if(CommonConstant.business_two.equals(accountOpenFlag)){
            //全部走新的账务接口
            AccountUserCoinAddReqVO accountUserCoinAddReqVO = ConvertUtil.entityToModel(userCoinAddVO,AccountUserCoinAddReqVO.class);
            setUserInfo(accountUserCoinAddReqVO,userCoinAddVO);
            AccountCoinResultVO accountCoinResultVO = null;
            if(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(accountUserCoinAddReqVO.getBusinessCoinType())){
                accountCoinResultVO = accountUserApi.userFreezeBalanceCoin(accountUserCoinAddReqVO);
            }else{
                accountCoinResultVO = accountUserApi.userBalanceCoin(accountUserCoinAddReqVO);
            }
            coinRecordResultVO = ConvertUtil.entityToModel(accountCoinResultVO,CoinRecordResultVO.class);
            UpdateBalanceStatusEnums updateBalanceStatusEnums =Enum.valueOf(UpdateBalanceStatusEnums.class,accountCoinResultVO.getResultStatus().name());
            coinRecordResultVO.setResultStatus(updateBalanceStatusEnums);
        }
        return coinRecordResultVO;
    }


    private void setUserInfo(AccountUserCoinAddReqVO reqVO,UserCoinAddVO userCoinAddVO){
        WalletUserInfoVO userInfoVO = userCoinAddVO.getUserInfoVO();
        reqVO.setSiteCode(userInfoVO.getSiteCode());
        reqVO.setUserId(userInfoVO.getUserId());
        reqVO.setUserAccount(userInfoVO.getUserAccount());
        reqVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
        reqVO.setCurrencyCode(userInfoVO.getMainCurrency());
        reqVO.setUserName(userInfoVO.getUserName());
        reqVO.setAccountStatus(userInfoVO.getAccountStatus());
        reqVO.setAccountType(userInfoVO.getAccountType());
        reqVO.setVipRank(userInfoVO.getVipRank());
        reqVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        reqVO.setInnerOrderNo(userCoinAddVO.getOrderNo());
        if(Objects.nonNull(userCoinAddVO.getThirdOrderNo())){
            reqVO.setThirdOrderNo(userCoinAddVO.getThirdOrderNo());
        }else{
            reqVO.setThirdOrderNo(userCoinAddVO.getOrderNo());
        }
        if (Objects.nonNull(userCoinAddVO.getToThridCode())){
            reqVO.setToThirdCode(userCoinAddVO.getToThridCode());
        }else{
            reqVO.setToThirdCode(userInfoVO.getSiteCode());
        }
        reqVO.setCoinTime(userCoinAddVO.getCoinTime());
    }



}
