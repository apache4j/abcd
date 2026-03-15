package com.cloud.baowang.account.api;

import com.cloud.baowang.account.api.api.AccountPlayApi;
import com.cloud.baowang.account.api.enums.AccountBalanceStatusEnums;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.po.UserCoinPO;
import com.cloud.baowang.account.po.UserCoinRecordPO;
import com.cloud.baowang.account.service.account.AccountTransfer;
import com.cloud.baowang.account.service.plat.UserCoinApi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author ford
 * @Date 2025-10-11
 */
@RestController
@AllArgsConstructor
@Slf4j
public class AccountPlayApiImpl implements AccountPlayApi {

    private final UserCoinApi userCoinApi;

    private final AccountTransfer accountTransfer;

    @Override
    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountCoinResultVO userBetCoin(AccountUserCoinAddReqVO reqVO) {
        String userId = reqVO.getUserId();
        AccountCoinResultVO accountCoinResultVO = new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        UserCoinPO userCoinPO = userCoinApi.getUserCoin(userId);
        //支出类型且不为提款，校验可用余额
        if (AccountBalanceTypeEnum.EXPENSES.getCode().equals(reqVO.getBalanceType())) {
            if(null == userCoinPO){
                accountCoinResultVO.setResult(false);
                accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.WALLET_NOT_EXIST);
                log.info("账变失败会员id{},支出类型，钱包信息不存在",userId);
                return accountCoinResultVO;
            }
            if(reqVO.getCoinValue().compareTo(userCoinPO.getAvailableAmount()) > 0 ){
                accountCoinResultVO.setResult(false);
                accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.INSUFFICIENT_BALANCE);
                log.info("账变失败会员id{},可用余额{},小于账变金额{}",userId,userCoinPO.getAvailableAmount(),reqVO.getCoinValue());
                return accountCoinResultVO;
            }
        }
        //获取订单账变记录
        List<UserCoinRecordPO> recordList =  userCoinApi.getCoinRecordList(reqVO.getInnerOrderNo(),reqVO.getBalanceType(),reqVO.getCoinType());
        if(!recordList.isEmpty()){
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.REPEAT_TRANSACTIONS);
            log.info("订单编号为{}的订单已添加账变", reqVO.getInnerOrderNo());
            return accountCoinResultVO;
        }
        reqVO.setCoinNum(CommonConstant.business_one);
        BigDecimal walletAmount = null == userCoinPO?BigDecimal.ZERO:userCoinPO.getAvailableAmount();
        if (AccountBalanceTypeEnum.FREEZE.getCode().equals(reqVO.getBalanceType()) && !AccountCoinTypeEnums.GAME_BET_FREEZD.getCode().equals(reqVO.getAccountCoinType())){
            walletAmount = null == userCoinPO?BigDecimal.ZERO:userCoinPO.getFreezeAmount();
        }

        accountCoinResultVO = userCoinApi.userCoinAdd(reqVO,userCoinPO);
        if(accountCoinResultVO.getResult()){
            singleTransfer(reqVO,walletAmount);
        }

        return accountCoinResultVO;
    }

    @Override
//    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
//    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Transactional(rollbackFor = Exception.class)
    public AccountCoinResultVO userGamePayout(AccountUserCoinAddReqVO reqVO) {
        String userId = reqVO.getUserId();
        AccountCoinResultVO accountCoinResultVO = new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        UserCoinPO userCoinPO = userCoinApi.getUserCoin(userId);
        //获取订单账变记录
        List<UserCoinRecordPO> recordList =  userCoinApi.getCoinRecordList(reqVO.getInnerOrderNo(),reqVO.getBalanceType(),reqVO.getCoinType());
        if(!recordList.isEmpty()){
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.REPEAT_TRANSACTIONS);
            log.info("订单编号为{}的订单已添加账变", reqVO.getInnerOrderNo());
            return accountCoinResultVO;
        }
        reqVO.setCoinNum(CommonConstant.business_one);
        BigDecimal walletAmount = null == userCoinPO?BigDecimal.ZERO:userCoinPO.getAvailableAmount();

        //如果账变金额为0，不添加账变，只插入账务记录
        if(reqVO.getCoinValue().compareTo(BigDecimal.ZERO) >0){
            accountCoinResultVO =  userCoinApi.userCoinAdd(reqVO,userCoinPO);
        }
        if(accountCoinResultVO.getResult()){
            singleTransfer(reqVO,walletAmount);
        }

        return accountCoinResultVO;
    }

    @Override
    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountCoinResultVO userRecalculateGamePayout(AccountUserCoinAddReqVO reqVO) {
        String userId = reqVO.getUserId();
        AccountCoinResultVO accountCoinResultVO = new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        UserCoinPO userCoinPO = userCoinApi.getUserCoin(userId);
        //支出 检查钱包是否存在,二次结算不校验余额 可为负数
        if (AccountBalanceTypeEnum.EXPENSES.getCode().equals(reqVO.getBalanceType())) {
            if(null == userCoinPO){
                accountCoinResultVO.setResult(false);
                accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.WALLET_NOT_EXIST);
                log.info("账变失败会员id{},支出类型，钱包信息不存在",userId);
                return accountCoinResultVO;
            }
        }
        //获取订单账变记录
        List<UserCoinRecordPO> recordList =  userCoinApi.getCoinRecordList(reqVO.getInnerOrderNo(),reqVO.getBalanceType(),reqVO.getCoinType());
        reqVO.setCoinNum(recordList.size()+1);
        BigDecimal walletAmount = null == userCoinPO?BigDecimal.ZERO:userCoinPO.getAvailableAmount();
        accountCoinResultVO =  userCoinApi.userCoinAdd(reqVO,userCoinPO);
        if(accountCoinResultVO.getResult()){
            singleTransfer(reqVO,walletAmount);
        }

        return accountCoinResultVO;
    }

    @Override
    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountCoinResultVO userBetCancelCoin(AccountUserCoinAddReqVO reqVO) {
        String userId = reqVO.getUserId();
        AccountCoinResultVO accountCoinResultVO = new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        UserCoinPO userCoinPO = userCoinApi.getUserCoin(userId);
        //获取订单账变记录
        List<UserCoinRecordPO> recordList =  userCoinApi.getCoinRecordList(reqVO.getInnerOrderNo(),reqVO.getBalanceType(),reqVO.getCoinType());
        if(!recordList.isEmpty()){
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.REPEAT_TRANSACTIONS);
            log.info("订单编号为{}的订单已添加账变", reqVO.getInnerOrderNo());
            return accountCoinResultVO;
        }
        reqVO.setCoinNum(CommonConstant.business_one);
        //如果账变金额为0，不添加账变，只插入账务记录
        accountCoinResultVO =  userCoinApi.userCoinAdd(reqVO,userCoinPO);
        if(accountCoinResultVO.getResult()){
            BigDecimal walletAmount = BigDecimal.ZERO;
            if(null != userCoinPO){
                walletAmount = userCoinPO.getAvailableAmount();
            }
            singleTransfer(reqVO,walletAmount);
        }
        return accountCoinResultVO;
    }

    public void singleTransfer(AccountUserCoinAddReqVO reqVO,BigDecimal walletAmount){
        //账务系统变更
        AccountBusinessUserReqVO accountUserReqVO = new AccountBusinessUserReqVO();
        accountUserReqVO.setAccountName(reqVO.getUserAccount());
        accountUserReqVO.setSourceAccountNo(reqVO.getUserId());
        accountUserReqVO.setSiteCode(reqVO.getSiteCode());
        accountUserReqVO.setCode(reqVO.getAccountCoinType());
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
        accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getFreezeFlag()));
        accountTransfer.gameTransfer(accountUserReqVO);
    }
}
