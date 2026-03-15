package com.cloud.baowang.account.api;

import com.cloud.baowang.account.api.api.AccountActivityApi;
import com.cloud.baowang.account.api.enums.AccountBalanceStatusEnums;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.account.api.enums.SourceAccountTypeEnums;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.po.UserCoinPO;
import com.cloud.baowang.account.po.UserCoinRecordPO;
import com.cloud.baowang.account.service.account.AccountTransfer;
import com.cloud.baowang.account.service.plat.UserCoinApi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @Author ford
 * @Date 2025-10-11
 */
@RestController
@Slf4j
@AllArgsConstructor
public class AccountActivityApiImpl implements AccountActivityApi {

    private final UserCoinApi userCoinApi;

    private final AccountTransfer accountTransfer;
    @Override
    public AccountCoinResultVO userActivityCoin(AccountUserCoinAddReqVO reqVO) {
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
        accountCoinResultVO =  userCoinApi.userCoinAdd(reqVO,userCoinPO);
        if(accountCoinResultVO.getResult()){

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
        accountUserReqVO.setCode(reqVO.getCoinType());
        accountUserReqVO.setUserType(SourceAccountTypeEnums.MEMBER.getType());
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
        if (AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(
                reqVO.getBusinessCoinType())){
            accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getFreezeFlag()));
        }
        if ((AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(
                reqVO.getBusinessCoinType())||
                AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode().equals(
                        reqVO.getBusinessCoinType())) && Objects.nonNull(reqVO.getActivityFlag())){
            accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getActivityFlag()));
        }

        accountTransfer.singleTransfer(accountUserReqVO);
    }
}
