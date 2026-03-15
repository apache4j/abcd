package com.cloud.baowang.wallet.service;


import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.account.api.api.AccountPlatformCoinApi;
import com.cloud.baowang.account.api.api.AccountPolymerizationApi;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserWtcToMainCurrencyVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.AccountPlatfromCoinRequestMqVO;
import com.cloud.baowang.common.kafka.vo.AccountUserCoinRequestMqVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class WalletUserCommonPlatformCoinService {


    private final UserPlatformCoinService userPlatformCoinService;

    private final UserCoinService userCoinService;

    private final AccountPolymerizationApi accountPolymerizationApi;

    private final AccountPlatformCoinApi accountPlatformCoinApi;

    /**
     * 平台币普通账变
     * @param userPlatformCoinAddVO
     * @return
     */
    public CoinRecordResultVO userCommonPlatformCoin(UserPlatformCoinAddVO userPlatformCoinAddVO){
        CoinRecordResultVO coinRecordResultVO = null;
        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        //公共调用
        if(ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)){
            //全部老账变
            coinRecordResultVO = userPlatformCoinService.addPlatformCoin(userPlatformCoinAddVO);

        }else if(CommonConstant.business_one.equals(accountOpenFlag)){
            //财务流量推送
            coinRecordResultVO = userPlatformCoinService.addPlatformCoin(userPlatformCoinAddVO);

            AccountPlatfromCoinRequestMqVO accountPlatfromCoinRequestMqVO = ConvertUtil.entityToModel(userPlatformCoinAddVO,AccountPlatfromCoinRequestMqVO.class);
            accountPlatfromCoinRequestMqVO.setInnerOrderNo(userPlatformCoinAddVO.getOrderNo());
            accountPlatfromCoinRequestMqVO.setThirdOrderNo(accountPlatfromCoinRequestMqVO.getInnerOrderNo());
            accountPlatfromCoinRequestMqVO.setToThirdCode(userPlatformCoinAddVO.getUserInfoVO().getSiteCode());

            KafkaUtil.send(TopicsConstants.ACCOUNT_USER_PLATFROM_COIN_TOPIC,accountPlatfromCoinRequestMqVO);
        }else if(CommonConstant.business_two.equals(accountOpenFlag)){
            //全部走新的账务接口
            AccountUserPlatformCoinAddReqVO accountUserPlatformCoinAddReqVO = ConvertUtil.entityToModel(userPlatformCoinAddVO,AccountUserPlatformCoinAddReqVO.class);
            setPlatformUserInfo(accountUserPlatformCoinAddReqVO,userPlatformCoinAddVO);
           AccountCoinResultVO accountCoinResultVO = accountPlatformCoinApi.platformCoinAdd(accountUserPlatformCoinAddReqVO);
            coinRecordResultVO = ConvertUtil.entityToModel(accountCoinResultVO,CoinRecordResultVO.class);
            UpdateBalanceStatusEnums updateBalanceStatusEnums =Enum.valueOf(UpdateBalanceStatusEnums.class,accountCoinResultVO.getResultStatus().name());
            coinRecordResultVO.setResultStatus(updateBalanceStatusEnums);
        }
        return coinRecordResultVO;
    }



    /**
     * 平台币兑换WTC->主货币
     * @param userPlatformCoinAddVO
     * @return
     */
    public CoinRecordResultVO wtcToMainCurrency(UserPlatformCoinAddVO userPlatformCoinAddVO,UserCoinAddVO userCoinAddVO){
        CoinRecordResultVO coinRecordResultVO = null;
        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        //公共调用
        if(ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)){
            //全部老账变
            coinRecordResultVO = userPlatformCoinService.addPlatformCoin(userPlatformCoinAddVO);
            coinRecordResultVO = userCoinService.addCoin(userCoinAddVO);

        }else if(CommonConstant.business_one.equals(accountOpenFlag)){
            //财务流量推送 平台币和代理一同推送
            coinRecordResultVO = userPlatformCoinService.addPlatformCoin(userPlatformCoinAddVO);
            AccountUserPlatformCoinAddReqVO accountUserPlatformCoinAddReqVO = ConvertUtil.entityToModel(userPlatformCoinAddVO,AccountUserPlatformCoinAddReqVO.class);
            setPlatformUserInfo(accountUserPlatformCoinAddReqVO,userPlatformCoinAddVO);
            AccountPlatfromCoinRequestMqVO accountPlatfromCoinRequestMqVO = ConvertUtil.entityToModel(userPlatformCoinAddVO,AccountPlatfromCoinRequestMqVO.class);
            KafkaUtil.send(TopicsConstants.ACCOUNT_USER_PLATFROM_COIN_TOPIC,accountPlatfromCoinRequestMqVO);

            coinRecordResultVO = userCoinService.addCoin(userCoinAddVO);
            AccountUserCoinAddReqVO accountUserCoinAddReqVO = ConvertUtil.entityToModel(userCoinAddVO,AccountUserCoinAddReqVO.class);
            setUserInfo(accountUserCoinAddReqVO,userCoinAddVO);
            AccountUserCoinRequestMqVO accountUserCoinRequestMqVO = ConvertUtil.entityToModel(userCoinAddVO,AccountUserCoinRequestMqVO.class);
            KafkaUtil.send(TopicsConstants.ACCOUNT_USER_COIN_TOPIC,accountUserCoinRequestMqVO);


        }else if(CommonConstant.business_two.equals(accountOpenFlag)){
            //全部走新的账务接口
            AccountUserPlatformCoinAddReqVO accountUserPlatformCoinAddReqVO = ConvertUtil.entityToModel(userPlatformCoinAddVO,AccountUserPlatformCoinAddReqVO.class);
            setPlatformUserInfo(accountUserPlatformCoinAddReqVO,userPlatformCoinAddVO);

            AccountUserCoinAddReqVO accountUserCoinAddReqVO = ConvertUtil.entityToModel(userCoinAddVO,AccountUserCoinAddReqVO.class);
            setUserInfo(accountUserCoinAddReqVO,userCoinAddVO);
            AccountUserWtcToMainCurrencyVO accountUserWtcToMainCurrencyVO = new AccountUserWtcToMainCurrencyVO();
            accountUserWtcToMainCurrencyVO.setUserPlatformCoinAddReqVO(accountUserPlatformCoinAddReqVO);
            accountUserWtcToMainCurrencyVO.setUserCoinAddReqVO(accountUserCoinAddReqVO);
            AccountCoinResultVO accountCoinResultVO = accountPolymerizationApi.WtcToMainCurrency(accountUserWtcToMainCurrencyVO);
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
        reqVO.setThirdOrderNo(userCoinAddVO.getOrderNo());
        reqVO.setToThirdCode(userInfoVO.getSiteCode());
        reqVO.setCoinTime(userCoinAddVO.getCoinTime());
    }

    private void setPlatformUserInfo(AccountUserPlatformCoinAddReqVO reqVO,UserPlatformCoinAddVO userPlatformCoinAddVO){
        WalletUserInfoVO userInfoVO = userPlatformCoinAddVO.getUserInfoVO();
        reqVO.setSiteCode(userInfoVO.getSiteCode());
        reqVO.setUserId(userInfoVO.getUserId());
        reqVO.setUserAccount(userInfoVO.getUserAccount());
        reqVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
        reqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO.setUserName(userInfoVO.getUserName());
        reqVO.setAccountStatus(userInfoVO.getAccountStatus());
        reqVO.setAccountType(userInfoVO.getAccountType());
        reqVO.setVipRank(userInfoVO.getVipRank());
        reqVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        reqVO.setInnerOrderNo(userPlatformCoinAddVO.getOrderNo());
        reqVO.setThirdOrderNo(userPlatformCoinAddVO.getOrderNo());
        reqVO.setToThirdCode(userInfoVO.getSiteCode());
        reqVO.setCoinTime(userPlatformCoinAddVO.getCoinTime());
    }
}
