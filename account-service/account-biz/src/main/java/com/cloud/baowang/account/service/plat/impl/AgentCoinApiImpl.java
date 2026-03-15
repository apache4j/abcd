package com.cloud.baowang.account.service.plat.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum;
import com.cloud.baowang.account.api.enums.AccountBalanceStatusEnums;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.po.AgentCoinRecordPO;
import com.cloud.baowang.account.po.AgentCommissionCoinPO;
import com.cloud.baowang.account.po.AgentQuotaCoinPO;
import com.cloud.baowang.account.repositories.AgentCoinRecordRepository;
import com.cloud.baowang.account.repositories.AgentCommissionCoinRepository;
import com.cloud.baowang.account.repositories.AgentQuotaCoinRepository;
import com.cloud.baowang.account.service.plat.AgentCoinAPi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AgentCoinApiImpl implements AgentCoinAPi {

    private final AgentCoinRecordRepository agentCoinRecordRepository;

    private final AgentCommissionCoinRepository commissionCoinRepository;

    private final AgentQuotaCoinRepository quotaCoinRepository;

    @Override
    public AccountCoinResultVO agentCommissionCoinAdd(AccountAgentCoinAddReqVO vo,AgentCommissionCoinPO agentCommissionCoinPO) {
        AccountCoinResultVO accountCoinResultVO=new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        accountCoinResultVO.setCoinAfterBalance(BigDecimal.ZERO);
        accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.SUCCESS);
        if (vo.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("佣金账变金额{}小于0",vo.getCoinValue());
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.AMOUNT_LESS_ZERO);
            return accountCoinResultVO;
        }
        LambdaQueryWrapper<AgentCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(AgentCoinRecordPO::getOrderNo, vo.getInnerOrderNo());
        ucrlqw.eq(AgentCoinRecordPO::getBalanceType, vo.getBalanceType());
        ucrlqw.eq(AgentCoinRecordPO::getSiteCode,vo.getSiteCode());
        ucrlqw.eq(AgentCoinRecordPO::getWalletType, AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        ucrlqw.last("FOR UPDATE");
        List<AgentCoinRecordPO> agentCoinRecordPOList = agentCoinRecordRepository.selectList(ucrlqw);
        if (!agentCoinRecordPOList.isEmpty()) {
            log.info("佣金账变订单编号为{}的订单已添加账变", vo.getInnerOrderNo());
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.REPEAT_TRANSACTIONS);
            return accountCoinResultVO;
        }

        BigDecimal coinFrom = BigDecimal.ZERO, coinTo = BigDecimal.ZERO;
        if (null == agentCommissionCoinPO) {
            if (!AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                log.info("账变失败代理{},无钱包信息，不能进行当前操作{}", vo.getAgentAccount());
                accountCoinResultVO.setResult(false);
                accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.WALLET_NOT_EXIST);
                return accountCoinResultVO;
            }
            coinTo = vo.getCoinValue();
            agentCommissionCoinPO = new AgentCommissionCoinPO();
            agentCommissionCoinPO.setAgentAccount(vo.getAgentAccount());
            agentCommissionCoinPO.setAgentId(vo.getAgentId());
            agentCommissionCoinPO.setSiteCode(vo.getSiteCode());
            agentCommissionCoinPO.setAgentName(vo.getAgentName());
            agentCommissionCoinPO.setParentId(vo.getParentId());
            agentCommissionCoinPO.setPath(vo.getPath());
            agentCommissionCoinPO.setLevel(vo.getLevel());
            agentCommissionCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            agentCommissionCoinPO.setTotalAmount(vo.getCoinValue());
            agentCommissionCoinPO.setFreezeAmount(BigDecimal.ZERO);
            agentCommissionCoinPO.setAvailableAmount(vo.getCoinValue());
            agentCommissionCoinPO.setCreatedTime(System.currentTimeMillis());
            agentCommissionCoinPO.setUpdatedTime(System.currentTimeMillis());
            this.commissionCoinRepository.insert(agentCommissionCoinPO);
        } else {
            coinFrom = agentCommissionCoinPO.getAvailableAmount();
            BigDecimal coinValue = vo.getCoinValue();
            if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode().equals(vo.getBalanceType())) {
                if (!CommonConstant.business_one.equals(vo.getFreezeFlag()) &&
                        agentCommissionCoinPO.getAvailableAmount().compareTo(vo.getCoinValue()) < 0) {
                    log.info("账变失败代理{},可用余额{},小于账变金额{}",vo.getAgentAccount()
                            ,agentCommissionCoinPO.getAvailableAmount(),vo.getCoinValue());
                    accountCoinResultVO.setResult(false);
                    accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.INSUFFICIENT_BALANCE);
                    return accountCoinResultVO;
                }
            }
            if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                agentCommissionCoinPO.setTotalAmount(agentCommissionCoinPO.getTotalAmount().add(coinValue));
                agentCommissionCoinPO.setAvailableAmount(agentCommissionCoinPO.getAvailableAmount().add(coinValue));
            } else if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode().equals(vo.getBalanceType())) {
                if (CommonConstant.business_one.equals(vo.getFreezeFlag())) {
                    //取款申请时已冻结金额，可用余额已扣除，所以这里只扣除总金额和冻结金额
                    agentCommissionCoinPO.setTotalAmount(agentCommissionCoinPO.getTotalAmount().subtract(coinValue));
                    agentCommissionCoinPO.setFreezeAmount(agentCommissionCoinPO.getFreezeAmount().subtract(coinValue));
                } else {
                    BigDecimal totalAmount = agentCommissionCoinPO.getTotalAmount().subtract(coinValue);
                    BigDecimal availableAmount = agentCommissionCoinPO.getAvailableAmount().subtract(coinValue);
                    agentCommissionCoinPO.setTotalAmount(totalAmount);
                    agentCommissionCoinPO.setAvailableAmount(availableAmount);
                }

            } else if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.FREEZE.getCode().equals(vo.getBalanceType())) {
                agentCommissionCoinPO.setAvailableAmount(agentCommissionCoinPO.getAvailableAmount().subtract(coinValue));
                agentCommissionCoinPO.setFreezeAmount(agentCommissionCoinPO.getFreezeAmount().add(coinValue));
            } else if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.UN_FREEZE.getCode().equals(vo.getBalanceType())) {
                agentCommissionCoinPO.setAvailableAmount(agentCommissionCoinPO.getAvailableAmount().add(coinValue));
                agentCommissionCoinPO.setFreezeAmount(agentCommissionCoinPO.getFreezeAmount().subtract(coinValue));
            }
            coinTo = agentCommissionCoinPO.getAvailableAmount();
            agentCommissionCoinPO.setUpdatedTime(System.currentTimeMillis());
            agentCommissionCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            this.commissionCoinRepository.updateById(agentCommissionCoinPO);
        }
        //插入账变记录
        addAgentCoinRecord(vo, coinFrom, coinTo);
        accountCoinResultVO.setCoinBeforeBalance(coinFrom);
        accountCoinResultVO.setCoinBeforeBalance(coinTo);
        return accountCoinResultVO;
    }
    public AgentCommissionCoinPO getCommissionCoinAgentId(String agentId) {
        LambdaQueryWrapper<AgentCommissionCoinPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentCommissionCoinPO::getAgentId, agentId);
        AgentCommissionCoinPO agentCommissionCoinPO = this.commissionCoinRepository.selectOne(lqw);
        return agentCommissionCoinPO;
    }

    public AgentQuotaCoinPO getQuotaCoinAgentId(String agentId) {
        LambdaQueryWrapper<AgentQuotaCoinPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentQuotaCoinPO::getAgentId, agentId);
        AgentQuotaCoinPO agentQuotaCoinPO = this.quotaCoinRepository.selectOne(lqw);
        return agentQuotaCoinPO;
    }

    public AccountCoinResultVO agentQuotaCoinAdd(AccountAgentCoinAddReqVO vo,AgentQuotaCoinPO agentQuotaCoinPO) {
        AccountCoinResultVO accountCoinResultVO=new AccountCoinResultVO();
        accountCoinResultVO.setResult(true);
        accountCoinResultVO.setCoinAfterBalance(BigDecimal.ZERO);
        accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.SUCCESS);

        if (vo.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("佣金账变金额{}小于0",vo.getCoinValue());
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.AMOUNT_LESS_ZERO);
            return accountCoinResultVO;
        }
        LambdaQueryWrapper<AgentCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(AgentCoinRecordPO::getOrderNo, vo.getInnerOrderNo());
        ucrlqw.eq(AgentCoinRecordPO::getSiteCode,vo.getSiteCode());
        ucrlqw.eq(AgentCoinRecordPO::getBalanceType, vo.getBalanceType());
        ucrlqw.eq(AgentCoinRecordPO::getWalletType, AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        ucrlqw.last("FOR UPDATE");
        List<AgentCoinRecordPO> agentCoinRecordPOList = agentCoinRecordRepository.selectList(ucrlqw);
        if (!agentCoinRecordPOList.isEmpty()) {
            log.info("额度账变订单编号为{}的订单已添加账变", vo.getInnerOrderNo());
            accountCoinResultVO.setResult(false);
            accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.REPEAT_TRANSACTIONS);
            return accountCoinResultVO;
        }
        BigDecimal coinFrom = BigDecimal.ZERO, coinTo = BigDecimal.ZERO;
        if (null == agentQuotaCoinPO) {
            if (!AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                accountCoinResultVO.setResult(false);
                accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.WALLET_NOT_EXIST);
                return accountCoinResultVO;
            }
            coinTo = vo.getCoinValue();
            agentQuotaCoinPO = new AgentQuotaCoinPO();
            agentQuotaCoinPO.setAgentId(vo.getAgentId());
            agentQuotaCoinPO.setAgentAccount(vo.getAgentAccount());
            agentQuotaCoinPO.setSiteCode(vo.getSiteCode());
            agentQuotaCoinPO.setAgentName(vo.getAgentName());
            agentQuotaCoinPO.setParentId(vo.getParentId());
            agentQuotaCoinPO.setPath(vo.getPath());
            agentQuotaCoinPO.setLevel(vo.getLevel());
            agentQuotaCoinPO.setCurrency(vo.getCurrency());
            agentQuotaCoinPO.setTotalAmount(vo.getCoinValue());
            agentQuotaCoinPO.setFreezeAmount(BigDecimal.ZERO);
            agentQuotaCoinPO.setAvailableAmount(vo.getCoinValue());
            agentQuotaCoinPO.setCreatedTime(System.currentTimeMillis());
            agentQuotaCoinPO.setUpdatedTime(System.currentTimeMillis());
            this.quotaCoinRepository.insert(agentQuotaCoinPO);
        } else {
            if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode().equals(vo.getBalanceType())) {
                if (!CommonConstant.business_one.equals(vo.getFreezeFlag()) && agentQuotaCoinPO.getAvailableAmount().compareTo(vo.getCoinValue()) < 0) {
                    log.info("账变失败代理{},可用余额{},小于账变金额{}",vo.getAgentAccount(),agentQuotaCoinPO.getAvailableAmount(),vo.getCoinValue());
                    accountCoinResultVO.setResult(false);
                    accountCoinResultVO.setResultStatus(AccountBalanceStatusEnums.INSUFFICIENT_BALANCE);
                    return accountCoinResultVO;
                }
            }
            coinFrom = agentQuotaCoinPO.getAvailableAmount();
            BigDecimal coinValue = vo.getCoinValue();
            if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                agentQuotaCoinPO.setTotalAmount(agentQuotaCoinPO.getTotalAmount().add(coinValue));
                agentQuotaCoinPO.setAvailableAmount(agentQuotaCoinPO.getAvailableAmount().add(coinValue));
            } else if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode().equals(vo.getBalanceType())) {
                if (CommonConstant.business_one.equals(vo.getFreezeFlag())) {
                    //取款申请时已冻结金额，可用余额已扣除，所以这里只扣除总金额和冻结金额
                    agentQuotaCoinPO.setTotalAmount(agentQuotaCoinPO.getTotalAmount().subtract(coinValue));
                    agentQuotaCoinPO.setFreezeAmount(agentQuotaCoinPO.getFreezeAmount().subtract(coinValue));
                } else {
                    BigDecimal totalAmount = agentQuotaCoinPO.getTotalAmount().subtract(coinValue);
                    BigDecimal availableAmount = agentQuotaCoinPO.getAvailableAmount().subtract(coinValue);
                    agentQuotaCoinPO.setTotalAmount(totalAmount);
                    agentQuotaCoinPO.setAvailableAmount(availableAmount);
                }

            } else if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.FREEZE.getCode().equals(vo.getBalanceType())) {
                agentQuotaCoinPO.setAvailableAmount(agentQuotaCoinPO.getAvailableAmount().subtract(coinValue));
                agentQuotaCoinPO.setFreezeAmount(agentQuotaCoinPO.getFreezeAmount().add(coinValue));
            } else if (AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.UN_FREEZE.getCode().equals(vo.getBalanceType())) {
                agentQuotaCoinPO.setAvailableAmount(agentQuotaCoinPO.getAvailableAmount().add(coinValue));
                agentQuotaCoinPO.setFreezeAmount(agentQuotaCoinPO.getFreezeAmount().subtract(coinValue));
            }


            coinTo = agentQuotaCoinPO.getAvailableAmount();
            agentQuotaCoinPO.setUpdatedTime(System.currentTimeMillis());
            agentQuotaCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);

            accountCoinResultVO.setCoinBeforeBalance(coinFrom);
            accountCoinResultVO.setCoinBeforeBalance(coinTo);
            this.quotaCoinRepository.updateById(agentQuotaCoinPO);
        }
        //插入账变记录
        addAgentCoinRecord(vo, coinFrom, coinTo);
        return accountCoinResultVO;
    }



    private void addAgentCoinRecord(AccountAgentCoinAddReqVO vo,
                                   BigDecimal coinFrom, BigDecimal coinTo){
        LambdaQueryWrapper<AgentCoinRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentCoinRecordPO::getOrderNo,vo.getInnerOrderNo());
        lqw.eq(AgentCoinRecordPO::getBusinessCoinType,vo.getBalanceType());
        List<AgentCoinRecordPO> userCoinRecordPOList = this.agentCoinRecordRepository.selectList(lqw);
        if(!userCoinRecordPOList.isEmpty()){
            log.info("订单编号为{}的订单已添加账变",vo.getInnerOrderNo());
        }
        //账变记录
        AgentCoinRecordPO agentCoinRecordPO = new AgentCoinRecordPO();
        agentCoinRecordPO.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(vo.getSiteCode()));
        agentCoinRecordPO.setAgentId(vo.getAgentId());
        agentCoinRecordPO.setAgentAccount(vo.getAgentAccount());
        agentCoinRecordPO.setSiteCode(vo.getSiteCode());
        agentCoinRecordPO.setAgentName(vo.getAgentName());
        agentCoinRecordPO.setParentId(vo.getParentId());
        agentCoinRecordPO.setPath(vo.getPath());
        agentCoinRecordPO.setLevel(vo.getLevel());
        agentCoinRecordPO.setRiskControlLevelId(vo.getRiskLevelId());
        agentCoinRecordPO.setAccountStatus(vo.getStatus());
        agentCoinRecordPO.setWalletType(vo.getAgentWalletType());
        agentCoinRecordPO.setBusinessCoinType(vo.getBusinessCoinType());
        agentCoinRecordPO.setCoinType(vo.getCoinType());
        agentCoinRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentCoinRecordPO.setCustomerCoinType(vo.getCustomerCoinType());
        agentCoinRecordPO.setBalanceType(vo.getBalanceType());
        agentCoinRecordPO.setOrderNo(vo.getInnerOrderNo());
        agentCoinRecordPO.setCoinFrom(coinFrom);
        agentCoinRecordPO.setCoinTo(coinTo);
        agentCoinRecordPO.setCoinAmount(vo.getCoinValue());
        agentCoinRecordPO.setCoinAmount(vo.getCoinValue());
        agentCoinRecordPO.setCreatedTime(System.currentTimeMillis());
        if(null != vo.getCoinTime()){
            agentCoinRecordPO.setCreatedTime(vo.getCoinTime());
        }
        agentCoinRecordPO.setRemark(vo.getRemark());
        this.agentCoinRecordRepository.insert(agentCoinRecordPO);
    }
}
