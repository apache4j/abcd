package com.cloud.baowang.agent.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.repositories.AgentCoinRecordRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代理账变记录 服务实现类
 * </p>
 *
 * @author qiqi
 * @since 2023-10-13
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentCoinRecordService extends ServiceImpl<AgentCoinRecordRepository, AgentCoinRecordPO> {

    private final AgentCoinRecordRepository agentCoinRecordRepository;

    private final RiskApi riskApi;



    public void addAgentCoinRecord(AgentCoinAddVO vo, AgentInfoVO agentInfoVO,
                                   BigDecimal coinFrom, BigDecimal coinTo){
        LambdaQueryWrapper<AgentCoinRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentCoinRecordPO::getOrderNo,vo.getOrderNo());
        lqw.eq(AgentCoinRecordPO::getBusinessCoinType,vo.getBalanceType());
        List<AgentCoinRecordPO> userCoinRecordPOList = this.baseMapper.selectList(lqw);
        if(!userCoinRecordPOList.isEmpty()){
            log.info("订单编号为{}的订单已添加账变",vo.getOrderNo());
        }
        //账变记录
        AgentCoinRecordPO agentCoinRecordPO = new AgentCoinRecordPO();
        agentCoinRecordPO.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(agentInfoVO.getSiteCode()));
        agentCoinRecordPO.setAgentId(agentInfoVO.getAgentId());
        agentCoinRecordPO.setAgentAccount(agentInfoVO.getAgentAccount());
        agentCoinRecordPO.setSiteCode(agentInfoVO.getSiteCode());
        agentCoinRecordPO.setAgentName(agentInfoVO.getName());
        agentCoinRecordPO.setParentId(agentInfoVO.getParentId());
        agentCoinRecordPO.setPath(agentInfoVO.getPath());
        agentCoinRecordPO.setLevel(agentInfoVO.getLevel());
        agentCoinRecordPO.setRiskControlLevelId(agentInfoVO.getRiskLevelId());
        agentCoinRecordPO.setAccountStatus(agentInfoVO.getStatus());
        agentCoinRecordPO.setWalletType(vo.getAgentWalletType());
        agentCoinRecordPO.setBusinessCoinType(vo.getBusinessCoinType());
        agentCoinRecordPO.setCoinType(vo.getCoinType());
        agentCoinRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentCoinRecordPO.setCustomerCoinType(vo.getCustomerCoinType());
        agentCoinRecordPO.setBalanceType(vo.getBalanceType());
        agentCoinRecordPO.setOrderNo(vo.getOrderNo());
        agentCoinRecordPO.setCoinFrom(coinFrom);
        agentCoinRecordPO.setCoinTo(coinTo);
        agentCoinRecordPO.setCoinAmount(vo.getCoinValue());
        agentCoinRecordPO.setCoinAmount(vo.getCoinValue());
        agentCoinRecordPO.setCreatedTime(System.currentTimeMillis());
        if(null != vo.getCoinTime()){
            agentCoinRecordPO.setCreatedTime(vo.getCoinTime());
        }
        agentCoinRecordPO.setRemark(vo.getRemark());
        this.baseMapper.insert(agentCoinRecordPO);
    }

    public Page<AgentCoinRecordVO> listAgentCoinRecordPage(AgentCoinRecordRequestVO vo) {
        Page<AgentCoinRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentCoinRecordPO> lqw  = buildLqw(vo);
        Page<AgentCoinRecordPO>  agentCoinRecordPOPage = this.baseMapper.selectPage(page,lqw);
        Page<AgentCoinRecordVO> agentCoinRecordVOPage = new Page<>();
        BeanUtils.copyProperties(agentCoinRecordPOPage,agentCoinRecordVOPage);
        List<AgentCoinRecordVO> recordVOList= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(agentCoinRecordPOPage.getRecords())){
            List<String> riskLevelIds=agentCoinRecordPOPage.getRecords().stream().filter(o-> org.springframework.util.StringUtils.hasText(o.getRiskControlLevelId())).map(o->o.getRiskControlLevelId()).toList();
            Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = riskApi.getByIds(riskLevelIds);
            List<AgentCoinRecordVO> agentCoinRecordVOList = ConvertUtil.entityListToModelList(agentCoinRecordPOPage.getRecords(),AgentCoinRecordVO.class);
            recordVOList= agentCoinRecordVOList.stream().map(record -> {
                if(null != record.getCreatedTime()){
                    record.setCreatedTimeStr(DateUtils.formatDateByZoneId(record.getCreatedTime(), DatePattern.NORM_DATETIME_PATTERN,vo.getTimeZone()));
                }
                if(riskLevelDetailsVOMap!=null){
                    RiskLevelDetailsVO riskLevelDetailsVO = riskLevelDetailsVOMap.get(record.getRiskControlLevelId());
                    if(riskLevelDetailsVO!=null){
                        record.setRiskControlLevel(riskLevelDetailsVO.getRiskControlLevel());
                    }
                }
                return record;
            }).toList();
        }
        agentCoinRecordVOPage.setRecords(recordVOList);
        return agentCoinRecordVOPage;
    }

    public AgentCoinRecordVO sumAllAgentCoinRecord(AgentCoinRecordRequestVO vo) {
        LambdaQueryWrapper<AgentCoinRecordPO> lqw  = buildLqw(vo);
        return this.baseMapper.sumAllAgentCoinRecord(lqw);
    }

    public Long agentCoinRecordPageListCount(AgentCoinRecordRequestVO vo) {
        LambdaQueryWrapper<AgentCoinRecordPO> lqw  = buildLqw(vo);
        return this.baseMapper.selectCount(lqw);
    }

    public LambdaQueryWrapper buildLqw(AgentCoinRecordRequestVO vo){
        LambdaQueryWrapper<AgentCoinRecordPO> lqw  = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(vo.getSiteCode()),AgentCoinRecordPO::getSiteCode,vo.getSiteCode());
        lqw.ge(null != vo.getDateTimeBegin(),AgentCoinRecordPO::getCreatedTime,vo.getDateTimeBegin());
        lqw.lt(null != vo.getDateTimeEnd(),AgentCoinRecordPO::getCreatedTime,vo.getDateTimeEnd());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()),AgentCoinRecordPO::getOrderNo,vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getWalletType()),AgentCoinRecordPO::getWalletType,vo.getWalletType());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentAccount()),AgentCoinRecordPO::getAgentAccount,vo.getAgentAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentName()),AgentCoinRecordPO::getAgentName,vo.getAgentName());
        lqw.eq(StringUtils.isNotBlank(vo.getAccountStatus()),AgentCoinRecordPO::getAccountStatus,vo.getAccountStatus());
        lqw.eq(StringUtils.isNotBlank(vo.getRiskLevel()),AgentCoinRecordPO::getRiskControlLevelId,vo.getRiskLevel());
        lqw.ge(null != vo.getCoinAmountMin(),AgentCoinRecordPO::getCoinAmount,vo.getCoinAmountMin());
        lqw.le(null != vo.getCoinAmountMax(),AgentCoinRecordPO::getCoinAmount,vo.getCoinAmountMax());
        if (!CollectionUtils.isEmpty(vo.getBusinessCoinTypeList())) {
            lqw.in(AgentCoinRecordPO::getBusinessCoinType, vo.getBusinessCoinTypeList());
        } else {
            lqw.eq(StringUtils.isNotBlank(vo.getBusinessCoinType()), AgentCoinRecordPO::getBusinessCoinType, vo.getBusinessCoinType());
        }
        if (!CollectionUtils.isEmpty(vo.getCoinTypeList())) {
            lqw.in(AgentCoinRecordPO::getCoinType, vo.getCoinTypeList());
        } else {
            lqw.eq(StringUtils.isNotBlank(vo.getCoinType()), AgentCoinRecordPO::getCoinType, vo.getCoinType());
        }
        lqw.eq(StringUtils.isNotBlank(vo.getBalanceType()),AgentCoinRecordPO::getBalanceType,vo.getBalanceType());
        lqw.orderByDesc(AgentCoinRecordPO::getCreatedTime);

        return lqw;

    }
}
