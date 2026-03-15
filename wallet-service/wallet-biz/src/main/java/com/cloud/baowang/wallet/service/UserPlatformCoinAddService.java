package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformCoinBalanceTypeEnum;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.wallet.po.UserPlatformCoinPO;
import com.cloud.baowang.wallet.po.UserPlatformCoinRecordPO;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRecordRepository;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserPlatformCoinAddService {

    private final UserPlatformCoinRecordRepository userPlatformCoinRecordRepository;

    private final UserPlatformCoinRepository userPlatformCoinRepository;

    private final PlatformTransactionManager transactionManager;

    @DistributedLock(name = RedisKeyTransUtil.ADD_PLATFORM_COIN_LOCK_KEY, unique = "#userPlatformCoinAddVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public CoinRecordResultVO userPlatformCoinAdd(UserPlatformCoinAddVO userPlatformCoinAddVO, CoinRecordResultVO coinRecordResultVO) {

        LambdaQueryWrapper<UserPlatformCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(UserPlatformCoinRecordPO::getOrderNo, userPlatformCoinAddVO.getOrderNo());
        ucrlqw.eq(UserPlatformCoinRecordPO::getBalanceType, userPlatformCoinAddVO.getBalanceType());
        List<UserPlatformCoinRecordPO> userCoinRecordPOList = userPlatformCoinRecordRepository.selectList(ucrlqw);
        if (!userCoinRecordPOList.isEmpty()) {
            coinRecordResultVO.setResult(false);
            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS);
            log.info("订单编号为{}的订单已添加平台币账变", userPlatformCoinAddVO.getOrderNo());
            return coinRecordResultVO;
        }
        coinRecordResultVO.setCoinRecordTime(userPlatformCoinAddVO.getCoinTime());
        WalletUserInfoVO userInfoVO = userPlatformCoinAddVO.getUserInfoVO();
        //账变记录
        UserPlatformCoinRecordPO userPlatformCoinRecordPO = new UserPlatformCoinRecordPO();
        userPlatformCoinRecordPO.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(userInfoVO.getSiteCode()));
        userPlatformCoinRecordPO.setSiteCode(userInfoVO.getSiteCode());
        userPlatformCoinRecordPO.setUserName(userInfoVO.getUserName());
        userPlatformCoinRecordPO.setUserAccount(userInfoVO.getUserAccount());
        userPlatformCoinRecordPO.setUserId(userInfoVO.getUserId());
        userPlatformCoinRecordPO.setAccountStatus(String.valueOf(userInfoVO.getAccountStatus()));
        userPlatformCoinRecordPO.setAccountType(userInfoVO.getAccountType());
        userPlatformCoinRecordPO.setUserLabelId(userInfoVO.getUserLabelId());
        userPlatformCoinRecordPO.setVipGradeCode(userInfoVO.getVipGradeCode());
        userPlatformCoinRecordPO.setVipRank(userInfoVO.getVipRank());
        userPlatformCoinRecordPO.setRiskControlLevelId(userInfoVO.getRiskLevelId());
        userPlatformCoinRecordPO.setAgentId(userInfoVO.getSuperAgentId());
        userPlatformCoinRecordPO.setAgentName(userInfoVO.getSuperAgentAccount());
        userPlatformCoinRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userPlatformCoinRecordPO.setBusinessCoinType(userPlatformCoinAddVO.getBusinessCoinType());
        userPlatformCoinRecordPO.setCoinType(userPlatformCoinAddVO.getCoinType());
        userPlatformCoinRecordPO.setCustomerCoinType(userPlatformCoinAddVO.getCustomerCoinType());
        userPlatformCoinRecordPO.setBalanceType(userPlatformCoinAddVO.getBalanceType());
        userPlatformCoinRecordPO.setOrderNo(userPlatformCoinAddVO.getOrderNo());
        userPlatformCoinRecordPO.setCoinValue(userPlatformCoinAddVO.getCoinValue());
        userPlatformCoinRecordPO.setCreatedTime(System.currentTimeMillis());
        userPlatformCoinRecordPO.setRemark(userPlatformCoinAddVO.getRemark());
        LambdaQueryWrapper<UserPlatformCoinPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserPlatformCoinPO::getUserId, userInfoVO.getUserId());
        UserPlatformCoinPO userPlatformCoinPO = userPlatformCoinRepository.selectOne(lqw);
        if (null == userPlatformCoinPO) {
            if (!PlatformCoinBalanceTypeEnum.INCOME.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                log.info("账变失败会员{},无钱包信息，不能进行当前操作{}", userPlatformCoinAddVO.getUserId()
                        , userPlatformCoinAddVO.getBalanceType());
                coinRecordResultVO.setResult(false);
                coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.WALLET_NOT_EXIST);
                return coinRecordResultVO;
            }
            userPlatformCoinRecordPO.setCoinFrom(BigDecimal.ZERO);
            userPlatformCoinRecordPO.setCoinTo(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinRecordPO.setCoinAmount(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinPO = new UserPlatformCoinPO();
            userPlatformCoinPO.setSiteCode(userInfoVO.getSiteCode());
            userPlatformCoinPO.setUserAccount(userInfoVO.getUserAccount());
            userPlatformCoinPO.setUserId(userInfoVO.getUserId());
            userPlatformCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            userPlatformCoinPO.setTotalAmount(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinPO.setFreezeAmount(BigDecimal.ZERO);
            userPlatformCoinPO.setAvailableAmount(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinPO.setCreator(userInfoVO.getId());
            userPlatformCoinPO.setCreatedTime(System.currentTimeMillis());
            userPlatformCoinPO.setUpdatedTime(System.currentTimeMillis());
            userPlatformCoinRepository.insert(userPlatformCoinPO);
            coinRecordResultVO.setCoinAfterBalance(userPlatformCoinPO.getAvailableAmount());
        } else {
            if (PlatformCoinBalanceTypeEnum.EXPENSES.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                if (userPlatformCoinPO.getAvailableAmount().compareTo(userPlatformCoinAddVO.getCoinValue()) < 0) {
                    log.info("账变失败会员{},可用余额{},小于账变金额{}",userPlatformCoinAddVO.getUserId()
                            ,userPlatformCoinPO.getAvailableAmount(),userPlatformCoinAddVO.getCoinValue());
                    coinRecordResultVO.setResult(false);
                    coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.INSUFFICIENT_BALANCE);
                    return coinRecordResultVO;
                }
            }
            userPlatformCoinRecordPO.setCoinFrom(userPlatformCoinPO.getAvailableAmount());
            userPlatformCoinRecordPO.setCoinAmount(userPlatformCoinAddVO.getCoinValue());
            BigDecimal coinValue = userPlatformCoinAddVO.getCoinValue();
            if (PlatformCoinBalanceTypeEnum.INCOME.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                userPlatformCoinPO.setTotalAmount(userPlatformCoinPO.getTotalAmount().add(coinValue));
                userPlatformCoinPO.setAvailableAmount(userPlatformCoinPO.getAvailableAmount().add(coinValue));
            } else if (PlatformCoinBalanceTypeEnum.EXPENSES.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                BigDecimal totalAmount = userPlatformCoinPO.getTotalAmount().subtract(coinValue);
                BigDecimal availableAmount = userPlatformCoinPO.getAvailableAmount().subtract(coinValue);
                userPlatformCoinPO.setTotalAmount(totalAmount);
                userPlatformCoinPO.setAvailableAmount(availableAmount);
            }

            userPlatformCoinRecordPO.setCoinTo(userPlatformCoinPO.getAvailableAmount());
            userPlatformCoinRepository.updateById(userPlatformCoinPO);
            coinRecordResultVO.setCoinAfterBalance(userPlatformCoinPO.getAvailableAmount());

        }

        userPlatformCoinPO.setUpdatedTime(System.currentTimeMillis());
        userPlatformCoinRecordRepository.insert(userPlatformCoinRecordPO);
        log.info("会员{}平台币账变金额{}成功,订单编号{}", userPlatformCoinAddVO.getUserId(), userPlatformCoinAddVO.getCoinValue(), userPlatformCoinAddVO.getOrderNo());
        return coinRecordResultVO;
    }
}
