package com.cloud.baowang.account.service.plat.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.account.api.enums.AccountBalanceStatusEnums;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountFreezeFlagEnum;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.po.UserCoinPO;
import com.cloud.baowang.account.po.UserCoinRecordPO;
import com.cloud.baowang.account.repositories.UserCoinRecordRepository;
import com.cloud.baowang.account.repositories.UserCoinRepository;
import com.cloud.baowang.account.service.plat.UserCoinApi;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor

public class UserCoinApiImpl implements UserCoinApi {

    private static final Logger logger = LoggerFactory.getLogger(UserCoinApiImpl.class);

    private final UserCoinRecordRepository userCoinRecordRepository;
    private final UserCoinRepository userCoinRepository;



    @Override

    public AccountCoinResultVO userCoinAdd(AccountUserCoinAddReqVO accountUserCoinAddReqVO,UserCoinPO userCoinPO) {
        AccountCoinResultVO accountCoinResultVO=new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        accountCoinResultVO.setCoinAfterBalance(BigDecimal.ZERO);
        accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.SUCCESS);
        if (accountUserCoinAddReqVO.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("订单编号为{}的订单账变金额小于0", accountUserCoinAddReqVO.getInnerOrderNo());
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.AMOUNT_LESS_ZERO);
            return accountCoinResultVO;
        }
        Long coinRecordTime = System.currentTimeMillis();
        if(null != accountUserCoinAddReqVO.getCoinTime()){
            coinRecordTime = accountUserCoinAddReqVO.getCoinTime();
        }

        BigDecimal coinValue = accountUserCoinAddReqVO.getCoinValue().setScale(4, RoundingMode.DOWN);
        //账变记录
        UserCoinRecordPO userCoinRecordPO = new UserCoinRecordPO();
        userCoinRecordPO.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(accountUserCoinAddReqVO.getSiteCode()));
        userCoinRecordPO.setSiteCode(accountUserCoinAddReqVO.getSiteCode());
        userCoinRecordPO.setCurrency(accountUserCoinAddReqVO.getCurrencyCode());
        userCoinRecordPO.setUserName(accountUserCoinAddReqVO.getUserName());
        userCoinRecordPO.setUserAccount(accountUserCoinAddReqVO.getUserAccount());
        userCoinRecordPO.setUserId(accountUserCoinAddReqVO.getUserId());
        userCoinRecordPO.setVipGradeCode(accountUserCoinAddReqVO.getVipGradeCode());
        userCoinRecordPO.setUserLabelId(accountUserCoinAddReqVO.getUserLabelId());
        userCoinRecordPO.setVipRank(accountUserCoinAddReqVO.getVipRank());
        userCoinRecordPO.setAccountStatus(String.valueOf(accountUserCoinAddReqVO.getAccountStatus()));
        userCoinRecordPO.setAccountType(accountUserCoinAddReqVO.getAccountType());
        userCoinRecordPO.setRiskControlLevelId(accountUserCoinAddReqVO.getRiskLevelId());
        userCoinRecordPO.setRiskControlLevel(accountUserCoinAddReqVO.getRiskLevel());
        userCoinRecordPO.setAgentId(accountUserCoinAddReqVO.getAgentId());
        userCoinRecordPO.setAgentName(accountUserCoinAddReqVO.getAgentAccount());
        userCoinRecordPO.setBusinessCoinType(accountUserCoinAddReqVO.getBusinessCoinType());
        userCoinRecordPO.setCoinType(accountUserCoinAddReqVO.getCoinType());
        userCoinRecordPO.setCustomerCoinType(accountUserCoinAddReqVO.getCustomerCoinType());
        userCoinRecordPO.setBalanceType(accountUserCoinAddReqVO.getBalanceType());
        userCoinRecordPO.setOrderNo(accountUserCoinAddReqVO.getInnerOrderNo());
        userCoinRecordPO.setCoinValue(coinValue);
        userCoinRecordPO.setCoinNum(accountUserCoinAddReqVO.getCoinNum());
        userCoinRecordPO.setCreatedTime(coinRecordTime);
        userCoinRecordPO.setRemark(accountUserCoinAddReqVO.getRemark());
        userCoinRecordPO.setDescInfo(accountUserCoinAddReqVO.getDescInfo());
        userCoinRecordPO.setExId1(accountUserCoinAddReqVO.getThirdOrderNo());
        if (null == userCoinPO) {
            userCoinRecordPO.setCoinFrom(BigDecimal.ZERO);
            userCoinRecordPO.setCoinTo(coinValue);
            userCoinRecordPO.setCoinAmount(coinValue);
            userCoinPO = new UserCoinPO();
            userCoinPO.setSiteCode(accountUserCoinAddReqVO.getSiteCode());
            userCoinPO.setUserAccount(accountUserCoinAddReqVO.getUserAccount());
            userCoinPO.setUserId(accountUserCoinAddReqVO.getUserId());
            userCoinPO.setCurrency(accountUserCoinAddReqVO.getCurrencyCode());
            userCoinPO.setTotalAmount(coinValue);
            userCoinPO.setFreezeAmount(BigDecimal.ZERO);
            userCoinPO.setAvailableAmount(coinValue);
            userCoinPO.setIsBringInVenue("1");
            userCoinPO.setCreator(accountUserCoinAddReqVO.getUserId());
            userCoinPO.setCreatedTime(coinRecordTime);
            userCoinPO.setUpdatedTime(coinRecordTime);
            userCoinRepository.insert(userCoinPO);
            accountCoinResultVO.setCoinAfterBalance(userCoinPO.getAvailableAmount());
            accountCoinResultVO.setCoinRecordTime(coinRecordTime);
        } else {
            userCoinRecordPO.setCoinFrom(userCoinPO.getAvailableAmount());
            userCoinRecordPO.setCoinAmount(coinValue);
            if (AccountBalanceTypeEnum.INCOME.getCode().equals(accountUserCoinAddReqVO.getBalanceType())) {
                userCoinPO.setTotalAmount(userCoinPO.getTotalAmount().add(coinValue));
                userCoinPO.setAvailableAmount(userCoinPO.getAvailableAmount().add(coinValue));
            } else if (AccountBalanceTypeEnum.EXPENSES.getCode().equals(accountUserCoinAddReqVO.getBalanceType())) {
                if (AccountFreezeFlagEnum.UNFREEZE.getCode().equals(accountUserCoinAddReqVO.getFreezeFlag())) {
                    //取款申请时已冻结金额，可用余额已扣除，所以这里只扣除总金额和冻结金额
                    userCoinPO.setTotalAmount(userCoinPO.getTotalAmount().subtract(coinValue));
                    userCoinPO.setFreezeAmount(userCoinPO.getFreezeAmount().subtract(coinValue));
                } else {
                    BigDecimal totalAmount = userCoinPO.getTotalAmount().subtract(coinValue);
                    BigDecimal availableAmount = userCoinPO.getAvailableAmount().subtract(coinValue);
                    userCoinPO.setTotalAmount(totalAmount);
                    userCoinPO.setAvailableAmount(availableAmount);
                }

            } else if (AccountBalanceTypeEnum.FREEZE.getCode().equals(accountUserCoinAddReqVO.getBalanceType())) {
                userCoinPO.setAvailableAmount(userCoinPO.getAvailableAmount().subtract(coinValue));
                userCoinPO.setFreezeAmount(userCoinPO.getFreezeAmount().add(coinValue));
            } else if (AccountBalanceTypeEnum.UN_FREEZE.getCode().equals(accountUserCoinAddReqVO.getBalanceType())) {
                userCoinPO.setAvailableAmount(userCoinPO.getAvailableAmount().add(coinValue));
                userCoinPO.setFreezeAmount(userCoinPO.getFreezeAmount().subtract(coinValue));
            }

            userCoinRecordPO.setCoinTo(userCoinPO.getAvailableAmount());
            userCoinRepository.updateById(userCoinPO);
            accountCoinResultVO.setCoinAfterBalance(userCoinPO.getAvailableAmount());
            accountCoinResultVO.setCoinBeforeBalance(userCoinRecordPO.getCoinFrom());
            accountCoinResultVO.setCoinRecordTime(coinRecordTime);
        }
        userCoinPO.setUpdatedTime(System.currentTimeMillis());
        userCoinRecordRepository.insert(userCoinRecordPO);
        accountCoinResultVO.setId(userCoinRecordPO.getId());
        logger.info("会员{}账变金额{}成功,订单编号{}", accountUserCoinAddReqVO.getUserId(), coinValue, accountUserCoinAddReqVO.getInnerOrderNo());

        return accountCoinResultVO;
    }

    @Override
    public UserCoinPO getUserCoin(String userId) {
        return this.userCoinRepository.selectOne(new LambdaQueryWrapper<UserCoinPO>().eq(UserCoinPO::getUserId,userId).last("FOR UPDATE"));
    }


    @Override
    public List<UserCoinRecordPO> getCoinRecordList(String innerOrderNo, String balanceType, String coinType) {

        LambdaQueryWrapper<UserCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(UserCoinRecordPO::getOrderNo, innerOrderNo);
        ucrlqw.eq(UserCoinRecordPO::getCoinType, coinType);
        ucrlqw.eq(UserCoinRecordPO::getBalanceType, balanceType);
        List<UserCoinRecordPO> userCoinRecordPOList = userCoinRecordRepository.selectList(ucrlqw);

        return userCoinRecordPOList;
    }

}
