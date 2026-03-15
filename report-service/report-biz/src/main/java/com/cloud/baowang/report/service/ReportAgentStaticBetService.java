package com.cloud.baowang.report.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsPageVO;
import com.cloud.baowang.report.po.ReportAgentStaticBetPO;
import com.cloud.baowang.report.repositories.ReportAgentStaticBetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @Desciption: 代理每日投注会员service
 * @Author: Ford
 * @Date: 2024/11/5 11:23
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class ReportAgentStaticBetService extends ServiceImpl<ReportAgentStaticBetRepository, ReportAgentStaticBetPO> {


    /**
     * 代理统计报表
     * @param reportAgentStaticBetPO 代理信息
     * @param userSet 会员列表
     */
    public void saveData(ReportAgentStaticBetPO reportAgentStaticBetPO, Set<String> userSet) {
        for(String userAccount:userSet){
            LambdaQueryWrapper<ReportAgentStaticBetPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getAgentId,reportAgentStaticBetPO.getAgentId());
            lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getSiteCode,reportAgentStaticBetPO.getSiteCode());
            lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getCurrencyCode,reportAgentStaticBetPO.getCurrencyCode());
            lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getDayStr,reportAgentStaticBetPO.getDayStr());
            lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getReportType,reportAgentStaticBetPO.getReportType());
            lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getUserAccount,userAccount);
            long countNum=this.baseMapper.selectCount(lambdaQueryWrapper);
            if(countNum<=0){
                reportAgentStaticBetPO.setId(IdUtil.getSnowflakeNextIdStr());
                reportAgentStaticBetPO.setUserAccount(userAccount);
                reportAgentStaticBetPO.setCreatedTime(System.currentTimeMillis());
                this.baseMapper.insert(reportAgentStaticBetPO);
            }
        }
    }


    /**
     * 统计投注人数
     * @param reportAgentStaticsPageVO 查询条件
     * @return 投注人数和
     */
    public Long staticBetUserNum(ReportAgentStaticsPageVO reportAgentStaticsPageVO, List<AgentInfoVO> agentInfoVO) {
        LambdaQueryWrapper<ReportAgentStaticBetPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getSiteCode,reportAgentStaticsPageVO.getSiteCode());
        lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getReportType,reportAgentStaticsPageVO.getReportType());
        if(!CollectionUtils.isEmpty(reportAgentStaticsPageVO.getAgentIds())){
            lambdaQueryWrapper.in(ReportAgentStaticBetPO::getAgentId,reportAgentStaticsPageVO.getAgentIds());
        }
        if(reportAgentStaticsPageVO.getStartStaticDay()!=null){
            lambdaQueryWrapper.ge(ReportAgentStaticBetPO::getDayMillis,reportAgentStaticsPageVO.getStartStaticDay());
        }
        if(reportAgentStaticsPageVO.getEndStaticDay()!=null){
            lambdaQueryWrapper.le(ReportAgentStaticBetPO::getDayMillis,reportAgentStaticsPageVO.getEndStaticDay());
        }
        if (!CollectionUtils.isEmpty(agentInfoVO)) {
            List<String> agentIds = agentInfoVO.stream().map(AgentInfoVO::getAgentId).toList();
            lambdaQueryWrapper.in(ReportAgentStaticBetPO::getAgentId, agentIds);
        }
        return this.baseMapper.staticBetUserNum(lambdaQueryWrapper);
    }


    /**
     * 代理报表重新统计 删除统计人数
     * @param vo 统计区间
     */
    public void delete(ReportAgentStaticsCondVO vo) {
        LambdaQueryWrapper<ReportAgentStaticBetPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getSiteCode,vo.getSiteCode());
        lambdaQueryWrapper.eq(ReportAgentStaticBetPO::getReportType,vo.getReportType());
        if(vo.getStartDayMillis()!=null){
            lambdaQueryWrapper.ge(ReportAgentStaticBetPO::getDayMillis,vo.getStartDayMillis());
        }
        if(vo.getEndDayMillis()!=null){
            lambdaQueryWrapper.le(ReportAgentStaticBetPO::getDayMillis,vo.getEndDayMillis());
        }
        this.baseMapper.delete(lambdaQueryWrapper);
    }
}
