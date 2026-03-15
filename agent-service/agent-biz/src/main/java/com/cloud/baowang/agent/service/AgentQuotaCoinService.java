package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinBalanceTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.po.AgentQuotaCoinPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentQuotaCoinRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
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
 * 代理额度钱包 服务类
 * </p>
 *
 * @author qiqi
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentQuotaCoinService extends ServiceImpl<AgentQuotaCoinRepository, AgentQuotaCoinPO> {

    private final AgentCoinAddService agentCoinAddService;


    public boolean addQuotaCoin(AgentCoinAddVO vo) {
        if (vo.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
           return false;
        }
        try {
                return agentCoinAddService.agentQuotaCoinAdd(vo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return false;
        }
    }


    public AgentCoinBalanceVO getQuotaCoinBalanceSite(String agentAccount, String siteCode) {
        LambdaQueryWrapper<AgentQuotaCoinPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentQuotaCoinPO::getAgentAccount, agentAccount);
        lqw.eq(AgentQuotaCoinPO::getSiteCode, siteCode);
        AgentQuotaCoinPO agentQuotaCoinPO = this.baseMapper.selectOne(lqw);
        AgentCoinBalanceVO agentCoinBalanceVO = new AgentCoinBalanceVO();
        if (null != agentQuotaCoinPO) {
            BeanUtils.copyProperties(agentQuotaCoinPO, agentCoinBalanceVO);
        } else {
            agentCoinBalanceVO.setAgentAccount(agentAccount);
            agentCoinBalanceVO.setTotalAmount(BigDecimal.ZERO);
            agentCoinBalanceVO.setFreezeAmount(BigDecimal.ZERO);
            agentCoinBalanceVO.setAvailableAmount(BigDecimal.ZERO);
        }
        return agentCoinBalanceVO;
    }


}
