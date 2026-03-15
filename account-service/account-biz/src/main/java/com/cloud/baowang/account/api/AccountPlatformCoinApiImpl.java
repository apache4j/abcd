package com.cloud.baowang.account.api;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.api.AccountPlatformCoinApi;
import com.cloud.baowang.account.api.enums.AccountPlatformWalletEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.account.api.enums.SourceAccountTypeEnums;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.po.UserPlatformCoinPO;
import com.cloud.baowang.account.service.account.AccountTransfer;
import com.cloud.baowang.account.service.plat.UserPlatformCoinApi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;

@RestController
@AllArgsConstructor
@Slf4j
public class AccountPlatformCoinApiImpl implements AccountPlatformCoinApi {

    private final UserPlatformCoinApi userPlatformCoinApi;

    private final AccountTransfer accountTransfer;


    @Override
    @DistributedLock(name = RedisKeyTransUtil.ADD_PLATFORM_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountCoinResultVO platformCoinAdd(AccountUserPlatformCoinAddReqVO reqVO) {
        reqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        log.info("账务系统余额账变开始,参数{}", JSONObject.toJSONString(reqVO));

        UserPlatformCoinPO userPlatformCoinPO = userPlatformCoinApi.getPlatformCoinByUserId(reqVO.getUserId());
        BigDecimal walletAmount = null == userPlatformCoinPO?BigDecimal.ZERO:userPlatformCoinPO.getAvailableAmount();
        AccountCoinResultVO accountCoinResultVO = userPlatformCoinApi.platformCoinAdd(reqVO,userPlatformCoinPO);
        if(accountCoinResultVO.getResult()){

            singleTransfer(reqVO,walletAmount);
        }

        return accountCoinResultVO;
    }

    public void singleTransfer(AccountUserPlatformCoinAddReqVO reqVO, BigDecimal walletAmount){
        //账务系统变更
        AccountBusinessUserReqVO accountUserReqVO = new AccountBusinessUserReqVO();
        accountUserReqVO.setAccountName(reqVO.getUserAccount());
        accountUserReqVO.setSourceAccountNo(reqVO.getUserId());
        accountUserReqVO.setSiteCode(reqVO.getSiteCode());
        accountUserReqVO.setCode(reqVO.getCoinType());
        accountUserReqVO.setUserType(SourceAccountTypeEnums.PLATFORM.getType());
        accountUserReqVO.setBalanceType(reqVO.getBalanceType());
        accountUserReqVO.setCurrencyCode(reqVO.getCurrencyCode());
        accountUserReqVO.setAccountStatus(reqVO.getAccountStatus());
        accountUserReqVO.setInnerOrderNo(reqVO.getInnerOrderNo());
        accountUserReqVO.setThirdOrderNo(reqVO.getThirdOrderNo());
        accountUserReqVO.setToThridCode(reqVO.getToThirdCode());
        accountUserReqVO.setWalletAmount(walletAmount);
        accountUserReqVO.setCoinTime(reqVO.getCoinTime());
        accountUserReqVO.setCoinValue(reqVO.getCoinValue());
        accountUserReqVO.setFinalRate(reqVO.getFinalRate());
        if ((AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(
                reqVO.getBusinessCoinType())||
                AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode().equals(
                        reqVO.getBusinessCoinType())) && Objects.nonNull(reqVO.getActivityFlag())){
            accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getActivityFlag()));
        }
        accountTransfer.singleTransfer(accountUserReqVO);
    }
}
