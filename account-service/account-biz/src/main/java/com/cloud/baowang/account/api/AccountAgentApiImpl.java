package com.cloud.baowang.account.api;

import com.cloud.baowang.account.api.api.AccountAgentApi;
import com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.SourceAccountTypeEnums;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.po.AgentCommissionCoinPO;
import com.cloud.baowang.account.po.AgentQuotaCoinPO;
import com.cloud.baowang.account.service.account.AccountTransfer;
import com.cloud.baowang.account.service.plat.AgentCoinAPi;
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
@Slf4j
@AllArgsConstructor
public class AccountAgentApiImpl implements AccountAgentApi {



    private final AgentCoinAPi agentCoinAPi;

    private final AccountTransfer accountTransfer;
    @Override
    @DistributedLock(name = RedisKeyTransUtil.ADD_AGENT_QUOTE_COIN_LOCK_KEY, unique = "#reqVO.agentId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Boolean agentQuotaCoin(AccountAgentCoinAddReqVO reqVO) {
        AgentQuotaCoinPO agentQuotaCoinPO = agentCoinAPi.getQuotaCoinAgentId(reqVO.getAgentId());
        BigDecimal walletAmount = null == agentQuotaCoinPO?BigDecimal.ZERO:agentQuotaCoinPO.getAvailableAmount();
        AccountCoinResultVO accountCoinResultVO  = agentCoinAPi.agentQuotaCoinAdd(reqVO,agentQuotaCoinPO);
        if(accountCoinResultVO.getResult()){

            singleTransfer(reqVO,walletAmount);
        }
        return accountCoinResultVO.getResult();
    }

    @Override
    @DistributedLock(name = RedisKeyTransUtil.ADD_AGENT_COMMISSION_COIN_LOCK_KEY, unique = "#reqVO.agentId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Boolean agentCommissionCoin(AccountAgentCoinAddReqVO reqVO) {
        AgentCommissionCoinPO agentCommissionCoinPO = agentCoinAPi.getCommissionCoinAgentId(reqVO.getAgentId());
        BigDecimal walletAmount = null == agentCommissionCoinPO?BigDecimal.ZERO:agentCommissionCoinPO.getAvailableAmount();
        if (AccountBalanceTypeEnum.FREEZE.getCode().equals(reqVO.getBalanceType())){
            walletAmount = null == agentCommissionCoinPO?BigDecimal.ZERO:agentCommissionCoinPO.getFreezeAmount();
        }
        AccountCoinResultVO accountCoinResultVO  = agentCoinAPi.agentCommissionCoinAdd(reqVO,agentCommissionCoinPO);

        if(accountCoinResultVO.getResult()){
            singleTransfer(reqVO,walletAmount);
        }
        return accountCoinResultVO.getResult();
    }




    private void singleTransfer(AccountAgentCoinAddReqVO reqVO, BigDecimal walletAmount){

        //账务系统变更
        AccountBusinessUserReqVO accountUserReqVO = new AccountBusinessUserReqVO();
        if (AccountBalanceTypeEnum.FREEZE.getCode().equals(reqVO.getBalanceType()) ||
                AccountBalanceTypeEnum.UN_FREEZE.getCode().equals(reqVO.getBalanceType())){
            accountUserReqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        }
        accountUserReqVO.setAccountName(reqVO.getAgentAccount());
        accountUserReqVO.setSourceAccountNo(reqVO.getAgentId());
        accountUserReqVO.setSiteCode(reqVO.getSiteCode());
        accountUserReqVO.setCode(reqVO.getCoinType());
        accountUserReqVO.setUserType(SourceAccountTypeEnums.AGENT.getType());
        accountUserReqVO.setBalanceType(reqVO.getBalanceType());
        accountUserReqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        accountUserReqVO.setAccountStatus(reqVO.getStatus());
        accountUserReqVO.setInnerOrderNo(reqVO.getInnerOrderNo());
        accountUserReqVO.setThirdOrderNo(reqVO.getThirdOrderNo());
        accountUserReqVO.setToThridCode(reqVO.getToThirdCode());
        accountUserReqVO.setWalletAmount(walletAmount);
        accountUserReqVO.setCoinTime(reqVO.getCoinTime());
        accountUserReqVO.setCoinValue(reqVO.getCoinValue());
        accountUserReqVO.setFinalRate(reqVO.getFinalRate());
        if (AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode().equals(
                reqVO.getBusinessCoinType()) && Objects.nonNull(reqVO.getFreezeFlag())){
            accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getFreezeFlag()));
        }
        if (AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_TRANSFER.getCode().equals(reqVO.getBusinessCoinType())
            || AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode().equals(reqVO.getBusinessCoinType())){
            accountUserReqVO.setBussinessFlag(reqVO.getAgentWalletType());
        }
        accountTransfer.singleTransfer(accountUserReqVO);
    }
}
