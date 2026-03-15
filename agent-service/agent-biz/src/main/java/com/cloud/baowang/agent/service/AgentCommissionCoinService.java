package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinBalanceTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.po.AgentCommissionCoinPO;
import com.cloud.baowang.agent.repositories.AgentCommissionCoinRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 代理佣金钱包 服务类
 * </p>
 *
 * @author qiqi
 * @since 2023-10-13
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentCommissionCoinService extends ServiceImpl<AgentCommissionCoinRepository, AgentCommissionCoinPO> {

    private final AgentCoinAddService agentCoinAddService;


    public boolean addCommissionCoin(AgentCoinAddVO vo) {
        if (vo.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("佣金账变金额{}小于0",vo.getCoinValue());
            return false;
        }
        try {
                return agentCoinAddService.agentCommissionCoinAdd(vo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return false;
        }

    }


    public AgentCoinBalanceVO getCommissionCoinBalanceSite(String agentAccount, String siteCode) {
        LambdaQueryWrapper<AgentCommissionCoinPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentCommissionCoinPO::getAgentAccount, agentAccount);
        lqw.eq(AgentCommissionCoinPO::getSiteCode, siteCode);
        AgentCommissionCoinPO agentCommissionCoinPO = this.baseMapper.selectOne(lqw);
        AgentCoinBalanceVO agentCoinBalanceVO = new AgentCoinBalanceVO();
        if (null != agentCommissionCoinPO) {
            BeanUtils.copyProperties(agentCommissionCoinPO, agentCoinBalanceVO);
        } else {
            agentCoinBalanceVO.setAgentAccount(agentAccount);
            agentCoinBalanceVO.setTotalAmount(BigDecimal.ZERO);
            agentCoinBalanceVO.setFreezeAmount(BigDecimal.ZERO);
            agentCoinBalanceVO.setAvailableAmount(BigDecimal.ZERO);
        }
        return agentCoinBalanceVO;
    }
    public AgentCoinBalanceVO getCommissionCoinBalanceAgentId(String agentId) {
        LambdaQueryWrapper<AgentCommissionCoinPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentCommissionCoinPO::getAgentId, agentId);
        AgentCommissionCoinPO agentCommissionCoinPO = this.baseMapper.selectOne(lqw);
        AgentCoinBalanceVO agentCoinBalanceVO = new AgentCoinBalanceVO();
        if (null != agentCommissionCoinPO) {
            BeanUtils.copyProperties(agentCommissionCoinPO, agentCoinBalanceVO);
        } else {
            agentCoinBalanceVO.setAgentId(agentId);
            agentCoinBalanceVO.setTotalAmount(BigDecimal.ZERO);
            agentCoinBalanceVO.setFreezeAmount(BigDecimal.ZERO);
            agentCoinBalanceVO.setAvailableAmount(BigDecimal.ZERO);
        }
        return agentCoinBalanceVO;
    }

}
