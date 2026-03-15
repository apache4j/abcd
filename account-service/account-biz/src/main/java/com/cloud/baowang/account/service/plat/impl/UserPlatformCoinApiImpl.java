package com.cloud.baowang.account.service.plat.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.account.api.enums.AccountBalanceStatusEnums;
import com.cloud.baowang.account.api.enums.AccountPlatformCoinBalanceTypeEnum;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.po.UserCoinPO;
import com.cloud.baowang.account.po.UserPlatformCoinPO;
import com.cloud.baowang.account.po.UserPlatformCoinRecordPO;
import com.cloud.baowang.account.repositories.UserPlatformCoinRecordRepository;
import com.cloud.baowang.account.repositories.UserPlatformCoinRepository;
import com.cloud.baowang.account.service.plat.UserPlatformCoinApi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor

public class UserPlatformCoinApiImpl implements UserPlatformCoinApi {

    private static final Logger logger = LoggerFactory.getLogger(UserPlatformCoinApiImpl.class);
    private final UserPlatformCoinRepository userPlatformCoinRepository;
    private final UserPlatformCoinRecordRepository userPlatformCoinRecordRepository;


    @Override
    public AccountCoinResultVO platformCoinAdd(AccountUserPlatformCoinAddReqVO userPlatformCoinAddVO,UserPlatformCoinPO userPlatformCoinPO) {

        AccountCoinResultVO accountCoinResultVO=new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        accountCoinResultVO.setCoinAfterBalance(BigDecimal.ZERO);
        accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.SUCCESS);
        if (userPlatformCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("订单编号为{}的订单账变金额小于0", userPlatformCoinAddVO.getInnerOrderNo());
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.AMOUNT_LESS_ZERO);
            return accountCoinResultVO;
        }
        LambdaQueryWrapper<UserPlatformCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(UserPlatformCoinRecordPO::getOrderNo, userPlatformCoinAddVO.getInnerOrderNo());
        ucrlqw.eq(UserPlatformCoinRecordPO::getBalanceType, userPlatformCoinAddVO.getBalanceType());
        List<UserPlatformCoinRecordPO> userCoinRecordPOList = userPlatformCoinRecordRepository.selectList(ucrlqw);
        if (!userCoinRecordPOList.isEmpty()) {
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.REPEAT_TRANSACTIONS);
            log.info("订单编号为{}的订单已添加平台币账变", userPlatformCoinAddVO.getInnerOrderNo());
            return accountCoinResultVO;
        }
        accountCoinResultVO.setCoinRecordTime(userPlatformCoinAddVO.getCoinTime());

        //账变记录
        UserPlatformCoinRecordPO userPlatformCoinRecordPO = new UserPlatformCoinRecordPO();
        userPlatformCoinRecordPO.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(userPlatformCoinAddVO.getSiteCode()));
        userPlatformCoinRecordPO.setSiteCode(userPlatformCoinAddVO.getSiteCode());
        userPlatformCoinRecordPO.setUserName(userPlatformCoinAddVO.getUserName());
        userPlatformCoinRecordPO.setUserAccount(userPlatformCoinAddVO.getUserAccount());
        userPlatformCoinRecordPO.setUserId(userPlatformCoinAddVO.getUserId());
        userPlatformCoinRecordPO.setAccountStatus(String.valueOf(userPlatformCoinAddVO.getAccountStatus()));
        userPlatformCoinRecordPO.setAccountType(userPlatformCoinAddVO.getAccountType());
        userPlatformCoinRecordPO.setUserLabelId(userPlatformCoinAddVO.getUserLabelId());
        userPlatformCoinRecordPO.setVipGradeCode(userPlatformCoinAddVO.getVipGradeCode());
        userPlatformCoinRecordPO.setVipRank(userPlatformCoinAddVO.getVipRank());
        userPlatformCoinRecordPO.setRiskControlLevelId(userPlatformCoinAddVO.getRiskLevelId());
        userPlatformCoinRecordPO.setAgentId(userPlatformCoinAddVO.getAgentId());
        userPlatformCoinRecordPO.setAgentName(userPlatformCoinAddVO.getAgentAccount());
        userPlatformCoinRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userPlatformCoinRecordPO.setBusinessCoinType(userPlatformCoinAddVO.getBusinessCoinType());
        userPlatformCoinRecordPO.setCoinType(userPlatformCoinAddVO.getCoinType());
        userPlatformCoinRecordPO.setCustomerCoinType(userPlatformCoinAddVO.getCustomerCoinType());
        userPlatformCoinRecordPO.setBalanceType(userPlatformCoinAddVO.getBalanceType());
        userPlatformCoinRecordPO.setOrderNo(userPlatformCoinAddVO.getInnerOrderNo());
        userPlatformCoinRecordPO.setCoinValue(userPlatformCoinAddVO.getCoinValue());
        userPlatformCoinRecordPO.setCreatedTime(System.currentTimeMillis());
        userPlatformCoinRecordPO.setRemark(userPlatformCoinAddVO.getRemark());

        if (null == userPlatformCoinPO) {
            if (!AccountPlatformCoinBalanceTypeEnum.INCOME.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                log.info("账变失败会员{},无钱包信息，不能进行当前操作{}", userPlatformCoinAddVO.getUserId()
                        , userPlatformCoinAddVO.getBalanceType());
                accountCoinResultVO.setResult(false);
                accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.WALLET_NOT_EXIST);
                return accountCoinResultVO;
            }
            userPlatformCoinRecordPO.setCoinFrom(BigDecimal.ZERO);
            userPlatformCoinRecordPO.setCoinTo(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinRecordPO.setCoinAmount(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinPO = new UserPlatformCoinPO();
            userPlatformCoinPO.setSiteCode(userPlatformCoinAddVO.getSiteCode());
            userPlatformCoinPO.setUserAccount(userPlatformCoinAddVO.getUserAccount());
            userPlatformCoinPO.setUserId(userPlatformCoinAddVO.getUserId());
            userPlatformCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            userPlatformCoinPO.setTotalAmount(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinPO.setFreezeAmount(BigDecimal.ZERO);
            userPlatformCoinPO.setAvailableAmount(userPlatformCoinAddVO.getCoinValue());
            userPlatformCoinPO.setCreatedTime(System.currentTimeMillis());
            userPlatformCoinPO.setUpdatedTime(System.currentTimeMillis());
            userPlatformCoinRepository.insert(userPlatformCoinPO);
            accountCoinResultVO.setCoinAfterBalance(userPlatformCoinPO.getAvailableAmount());
        } else {
            if (AccountPlatformCoinBalanceTypeEnum.EXPENSES.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                if (userPlatformCoinPO.getAvailableAmount().compareTo(userPlatformCoinAddVO.getCoinValue()) < 0) {
                    log.info("账变失败会员{},可用余额{},小于账变金额{}",userPlatformCoinAddVO.getUserId()
                            ,userPlatformCoinPO.getAvailableAmount(),userPlatformCoinAddVO.getCoinValue());
                    accountCoinResultVO.setResult(false);
                    accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.INSUFFICIENT_BALANCE);
                    return accountCoinResultVO;
                }
            }
            userPlatformCoinRecordPO.setCoinFrom(userPlatformCoinPO.getAvailableAmount());
            userPlatformCoinRecordPO.setCoinAmount(userPlatformCoinAddVO.getCoinValue());
            BigDecimal coinValue = userPlatformCoinAddVO.getCoinValue();
            if (AccountPlatformCoinBalanceTypeEnum.INCOME.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                userPlatformCoinPO.setTotalAmount(userPlatformCoinPO.getTotalAmount().add(coinValue));
                userPlatformCoinPO.setAvailableAmount(userPlatformCoinPO.getAvailableAmount().add(coinValue));
            } else if (AccountPlatformCoinBalanceTypeEnum.EXPENSES.getCode().equals(userPlatformCoinAddVO.getBalanceType())) {
                BigDecimal totalAmount = userPlatformCoinPO.getTotalAmount().subtract(coinValue);
                BigDecimal availableAmount = userPlatformCoinPO.getAvailableAmount().subtract(coinValue);
                userPlatformCoinPO.setTotalAmount(totalAmount);
                userPlatformCoinPO.setAvailableAmount(availableAmount);
            }

            userPlatformCoinRecordPO.setCoinTo(userPlatformCoinPO.getAvailableAmount());
            userPlatformCoinRepository.updateById(userPlatformCoinPO);


        }
        accountCoinResultVO.setCoinAfterBalance(userPlatformCoinPO.getAvailableAmount());
        accountCoinResultVO.setCoinBeforeBalance(userPlatformCoinRecordPO.getCoinFrom());

        userPlatformCoinPO.setUpdatedTime(System.currentTimeMillis());
        userPlatformCoinRecordRepository.insert(userPlatformCoinRecordPO);
        log.info("会员{}平台币账变金额{}成功,订单编号{}", userPlatformCoinAddVO.getUserId(), userPlatformCoinAddVO.getCoinValue(), userPlatformCoinAddVO.getInnerOrderNo());
        return accountCoinResultVO;
    }

    @Override
    public UserPlatformCoinPO getPlatformCoinByUserId(String userId) {
        return this.userPlatformCoinRepository.selectOne(new LambdaQueryWrapper<UserPlatformCoinPO>().eq(UserPlatformCoinPO::getUserId,userId).last("FOR UPDATE"));
    }
}
