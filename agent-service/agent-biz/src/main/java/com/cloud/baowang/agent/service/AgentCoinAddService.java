package com.cloud.baowang.agent.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.api.enums.AgentCoinBalanceTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.po.AgentCommissionCoinPO;
import com.cloud.baowang.agent.po.AgentQuotaCoinPO;
import com.cloud.baowang.agent.repositories.AgentCommissionCoinRepository;
import com.cloud.baowang.agent.repositories.AgentQuotaCoinRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
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
public class AgentCoinAddService {

    private final PlatformTransactionManager transactionManager;

    private final AgentCommissionCoinRepository commissionCoinRepository;

    private final AgentCoinRecordService agentCoinRecordService;

    private final AgentQuotaCoinRepository quotaCoinRepository;


    @DistributedLock(name = RedisKeyTransUtil.ADD_AGENT_COMMISSION_COIN_LOCK_KEY, unique = "#vo.agentId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean agentCommissionCoinAdd(AgentCoinAddVO vo) {
        LambdaQueryWrapper<AgentCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(AgentCoinRecordPO::getOrderNo, vo.getOrderNo());
        ucrlqw.eq(AgentCoinRecordPO::getBalanceType, vo.getBalanceType());
        ucrlqw.eq(AgentCoinRecordPO::getSiteCode,vo.getSiteCode());
        ucrlqw.eq(AgentCoinRecordPO::getWalletType, AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        List<AgentCoinRecordPO> agentCoinRecordPOList = agentCoinRecordService.list(ucrlqw);
        if (!agentCoinRecordPOList.isEmpty()) {
            log.info("佣金账变订单编号为{}的订单已添加账变", vo.getOrderNo());
            return false;
        }
        AgentInfoVO agentInfoVO = vo.getAgentInfo();
        LambdaQueryWrapper<AgentCommissionCoinPO> lqw = new LambdaQueryWrapper();
        lqw.eq(AgentCommissionCoinPO::getAgentId, agentInfoVO.getAgentId());
        AgentCommissionCoinPO agentCommissionCoinPO = this.commissionCoinRepository.selectOne(lqw);
        BigDecimal coinFrom = BigDecimal.ZERO, coinTo = BigDecimal.ZERO;
        if (null == agentCommissionCoinPO) {
            if (!AgentCoinBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                return false;
            }
            coinTo = vo.getCoinValue();
            agentCommissionCoinPO = new AgentCommissionCoinPO();
            agentCommissionCoinPO.setAgentAccount(agentInfoVO.getAgentAccount());
            agentCommissionCoinPO.setAgentId(agentInfoVO.getAgentId());
            agentCommissionCoinPO.setSiteCode(agentInfoVO.getSiteCode());
            agentCommissionCoinPO.setAgentName(agentInfoVO.getName());
            agentCommissionCoinPO.setParentId(agentInfoVO.getParentId());
            agentCommissionCoinPO.setPath(agentInfoVO.getPath());
            agentCommissionCoinPO.setLevel(agentInfoVO.getLevel());
            agentCommissionCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            agentCommissionCoinPO.setTotalAmount(vo.getCoinValue());
            agentCommissionCoinPO.setFreezeAmount(BigDecimal.ZERO);
            agentCommissionCoinPO.setAvailableAmount(vo.getCoinValue());
            agentCommissionCoinPO.setCreator(agentInfoVO.getId());
            agentCommissionCoinPO.setCreatedTime(System.currentTimeMillis());
            agentCommissionCoinPO.setUpdatedTime(System.currentTimeMillis());
            this.commissionCoinRepository.insert(agentCommissionCoinPO);
        } else {
            coinFrom = agentCommissionCoinPO.getAvailableAmount();
            BigDecimal coinValue = vo.getCoinValue();
            if (AgentCoinBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                agentCommissionCoinPO.setTotalAmount(agentCommissionCoinPO.getTotalAmount().add(coinValue));
                agentCommissionCoinPO.setAvailableAmount(agentCommissionCoinPO.getAvailableAmount().add(coinValue));
            } else if (AgentCoinBalanceTypeEnum.EXPENSES.getCode().equals(vo.getBalanceType())) {
                if (CommonConstant.business_one.equals(vo.getWithdrawFlag())) {
                    //取款申请时已冻结金额，可用余额已扣除，所以这里只扣除总金额和冻结金额
                    agentCommissionCoinPO.setTotalAmount(agentCommissionCoinPO.getTotalAmount().subtract(coinValue));
                    agentCommissionCoinPO.setFreezeAmount(agentCommissionCoinPO.getFreezeAmount().subtract(coinValue));
                } else {
                    BigDecimal totalAmount = agentCommissionCoinPO.getTotalAmount().subtract(coinValue);
                    BigDecimal availableAmount = agentCommissionCoinPO.getAvailableAmount().subtract(coinValue);
                    agentCommissionCoinPO.setTotalAmount(totalAmount);
                    agentCommissionCoinPO.setAvailableAmount(availableAmount);
                }

            } else if (AgentCoinBalanceTypeEnum.FREEZE.getCode().equals(vo.getBalanceType())) {
                agentCommissionCoinPO.setAvailableAmount(agentCommissionCoinPO.getAvailableAmount().subtract(coinValue));
                agentCommissionCoinPO.setFreezeAmount(agentCommissionCoinPO.getFreezeAmount().add(coinValue));
            } else if (AgentCoinBalanceTypeEnum.UN_FREEZE.getCode().equals(vo.getBalanceType())) {
                agentCommissionCoinPO.setAvailableAmount(agentCommissionCoinPO.getAvailableAmount().add(coinValue));
                agentCommissionCoinPO.setFreezeAmount(agentCommissionCoinPO.getFreezeAmount().subtract(coinValue));
            }
            coinTo = agentCommissionCoinPO.getAvailableAmount();
            agentCommissionCoinPO.setUpdatedTime(System.currentTimeMillis());
            agentCommissionCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            this.commissionCoinRepository.updateById(agentCommissionCoinPO);
        }
        //插入账变记录
        agentCoinRecordService.addAgentCoinRecord(vo, agentInfoVO, coinFrom, coinTo);
        return true;
    }

    @DistributedLock(name = RedisKeyTransUtil.ADD_AGENT_QUOTE_COIN_LOCK_KEY, unique = "#vo.agentId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean agentQuotaCoinAdd(AgentCoinAddVO vo) {
        LambdaQueryWrapper<AgentCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(AgentCoinRecordPO::getOrderNo, vo.getOrderNo());
        ucrlqw.eq(AgentCoinRecordPO::getSiteCode,vo.getSiteCode());
        ucrlqw.eq(AgentCoinRecordPO::getBalanceType, vo.getBalanceType());
        ucrlqw.eq(AgentCoinRecordPO::getWalletType, AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        List<AgentCoinRecordPO> agentCoinRecordPOList = agentCoinRecordService.list(ucrlqw);
        if (!agentCoinRecordPOList.isEmpty()) {
            log.info("额度账变订单编号为{}的订单已添加账变", vo.getOrderNo());
            return false;
        }
        AgentInfoVO agentInfoVO= vo.getAgentInfo();
        LambdaQueryWrapper<AgentQuotaCoinPO> lqw = new LambdaQueryWrapper();
        lqw.eq(AgentQuotaCoinPO::getAgentId, agentInfoVO.getAgentId());
        AgentQuotaCoinPO agentQuotaCoinPO = this.quotaCoinRepository.selectOne(lqw);
        BigDecimal coinFrom = BigDecimal.ZERO, coinTo = BigDecimal.ZERO;
        if (null == agentQuotaCoinPO) {
            if (!AgentCoinBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                return false;
            }
            coinTo = vo.getCoinValue();
            agentQuotaCoinPO = new AgentQuotaCoinPO();
            agentQuotaCoinPO.setAgentId(agentInfoVO.getAgentId());
            agentQuotaCoinPO.setAgentAccount(agentInfoVO.getAgentAccount());
            agentQuotaCoinPO.setSiteCode(agentInfoVO.getSiteCode());
            agentQuotaCoinPO.setAgentName(agentInfoVO.getName());
            agentQuotaCoinPO.setParentId(agentInfoVO.getParentId());
            agentQuotaCoinPO.setPath(agentInfoVO.getPath());
            agentQuotaCoinPO.setLevel(agentInfoVO.getLevel());
            agentQuotaCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            agentQuotaCoinPO.setTotalAmount(vo.getCoinValue());
            agentQuotaCoinPO.setFreezeAmount(BigDecimal.ZERO);
            agentQuotaCoinPO.setAvailableAmount(vo.getCoinValue());
            agentQuotaCoinPO.setCreator(agentInfoVO.getId());
            agentQuotaCoinPO.setCreatedTime(System.currentTimeMillis());
            agentQuotaCoinPO.setUpdatedTime(System.currentTimeMillis());
            this.quotaCoinRepository.insert(agentQuotaCoinPO);
        } else {
            coinFrom = agentQuotaCoinPO.getAvailableAmount();
            BigDecimal coinValue = vo.getCoinValue();
            if (AgentCoinBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType())) {
                agentQuotaCoinPO.setTotalAmount(agentQuotaCoinPO.getTotalAmount().add(coinValue));
                agentQuotaCoinPO.setAvailableAmount(agentQuotaCoinPO.getAvailableAmount().add(coinValue));
            } else if (AgentCoinBalanceTypeEnum.EXPENSES.getCode().equals(vo.getBalanceType())) {
                if (CommonConstant.business_one.equals(vo.getWithdrawFlag())) {
                    //取款申请时已冻结金额，可用余额已扣除，所以这里只扣除总金额和冻结金额
                    agentQuotaCoinPO.setTotalAmount(agentQuotaCoinPO.getTotalAmount().subtract(coinValue));
                    agentQuotaCoinPO.setFreezeAmount(agentQuotaCoinPO.getFreezeAmount().subtract(coinValue));
                } else {
                    BigDecimal totalAmount = agentQuotaCoinPO.getTotalAmount().subtract(coinValue);
                    BigDecimal availableAmount = agentQuotaCoinPO.getAvailableAmount().subtract(coinValue);
                    agentQuotaCoinPO.setTotalAmount(totalAmount);
                    agentQuotaCoinPO.setAvailableAmount(availableAmount);
                }

            } else if (AgentCoinBalanceTypeEnum.FREEZE.getCode().equals(vo.getBalanceType())) {
                agentQuotaCoinPO.setAvailableAmount(agentQuotaCoinPO.getAvailableAmount().subtract(coinValue));
                agentQuotaCoinPO.setFreezeAmount(agentQuotaCoinPO.getFreezeAmount().add(coinValue));
            } else if (AgentCoinBalanceTypeEnum.UN_FREEZE.getCode().equals(vo.getBalanceType())) {
                agentQuotaCoinPO.setAvailableAmount(agentQuotaCoinPO.getAvailableAmount().add(coinValue));
                agentQuotaCoinPO.setFreezeAmount(agentQuotaCoinPO.getFreezeAmount().subtract(coinValue));
            }
            coinTo = agentQuotaCoinPO.getAvailableAmount();
            agentQuotaCoinPO.setUpdatedTime(System.currentTimeMillis());
            agentQuotaCoinPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            this.quotaCoinRepository.updateById(agentQuotaCoinPO);
        }
        //插入账变记录
        agentCoinRecordService.addAgentCoinRecord(vo, agentInfoVO, coinFrom, coinTo);
        return true;
    }

}
