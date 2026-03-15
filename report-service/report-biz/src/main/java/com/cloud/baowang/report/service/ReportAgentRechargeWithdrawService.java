package com.cloud.baowang.report.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.kafka.vo.AgentRechargeWithdrawMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.report.po.ReportAgentRechargeWithdrawMqMessagePO;
import com.cloud.baowang.report.po.ReportAgentRechargeWithdrawPO;
import com.cloud.baowang.report.repositories.ReportAgentRechargeMqMessageRepository;
import com.cloud.baowang.report.repositories.ReportAgentRechargeWithdrawRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class ReportAgentRechargeWithdrawService extends ServiceImpl<ReportAgentRechargeWithdrawRepository, ReportAgentRechargeWithdrawPO> {

    private final ReportAgentRechargeMqMessageRepository agentRechargeMqMessageRepository;
    private final ReportAgentRechargeWithdrawRepository reportAgentRechargeWithdrawRepository;


    @DistributedLock(name = RedisConstants.REWARD_SPIN_WHEEL_LOCK, unique = "#vo.agentId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public void addRechargeAmount(long start, String jsonStr, AgentRechargeWithdrawMqVO vo){
        ReportAgentRechargeWithdrawMqMessagePO reportAgentRechargeWithdrawMqMessagePO = new ReportAgentRechargeWithdrawMqMessagePO();
        reportAgentRechargeWithdrawMqMessagePO.setJsonStr(jsonStr);
        agentRechargeMqMessageRepository.insert(reportAgentRechargeWithdrawMqMessagePO);

        LambdaQueryWrapper<ReportAgentRechargeWithdrawPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportAgentRechargeWithdrawPO::getAgentId,vo.getAgentId());
        lqw.eq(ReportAgentRechargeWithdrawPO::getCurrency,vo.getCurrency());
        lqw.eq(ReportAgentRechargeWithdrawPO::getDayHourMillis,vo.getDayHourMillis());
        lqw.eq(ReportAgentRechargeWithdrawPO::getType,vo.getType());
        lqw.eq(StringUtils.isNotBlank(vo.getDepositWithdrawWayId()),ReportAgentRechargeWithdrawPO::getDepositWithdrawWayId,vo.getDepositWithdrawWayId());
        ReportAgentRechargeWithdrawPO reportAgentRechargeWithdrawPO = baseMapper.selectOne(lqw);

        if(null == reportAgentRechargeWithdrawPO){
            reportAgentRechargeWithdrawPO = ConvertUtil.entityToModel(vo, ReportAgentRechargeWithdrawPO.class);
            reportAgentRechargeWithdrawPO.setNums(CommonConstant.business_one);
            if(null != vo.getLargeAmount() && BigDecimal.ZERO.compareTo(vo.getLargeAmount()) < 0){
                reportAgentRechargeWithdrawPO.setLargeNums(CommonConstant.business_one);
            }
            baseMapper.insert(reportAgentRechargeWithdrawPO);
        }else{
            reportAgentRechargeWithdrawPO.setAmount(reportAgentRechargeWithdrawPO.getAmount().add(vo.getAmount()));
            reportAgentRechargeWithdrawPO.setFeeAmount(reportAgentRechargeWithdrawPO.getFeeAmount().add(vo.getFeeAmount()));
            reportAgentRechargeWithdrawPO.setWayFeeAmount(reportAgentRechargeWithdrawPO.getWayFeeAmount().add(vo.getWayFeeAmount()));
            reportAgentRechargeWithdrawPO.setSettlementFeeAmount(reportAgentRechargeWithdrawPO.getSettlementFeeAmount().add(vo.getSettlementFeeAmount()));
            reportAgentRechargeWithdrawPO.setNums(reportAgentRechargeWithdrawPO.getNums()+1);
            if(null != vo.getLargeAmount() && BigDecimal.ZERO.compareTo(vo.getLargeAmount()) < 0){
                reportAgentRechargeWithdrawPO.setLargeAmount(reportAgentRechargeWithdrawPO.getLargeAmount().add(vo.getLargeAmount()));
                reportAgentRechargeWithdrawPO.setLargeNums(reportAgentRechargeWithdrawPO.getLargeNums()+1);
            }
            baseMapper.updateById(reportAgentRechargeWithdrawPO);
        }
        log.info("代理累计存款-MQ队列-------------------------------执行success,耗时{}毫秒", System.currentTimeMillis() - start);


    }

}
