package com.cloud.baowang.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawCondVO;
import com.cloud.baowang.report.po.ReportAgentDepositWithdrawUserPO;
import com.cloud.baowang.report.repositories.ReportAgentDepositWithdrawUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Desciption: 代理充提人员报表
 * @Author: Ford
 * @Date: 2024/11/11 17:44
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class ReportAgentDepositWithdrawUserService extends ServiceImpl<ReportAgentDepositWithdrawUserRepository, ReportAgentDepositWithdrawUserPO> {


    /**
     * 保存数据
     * @param reportAgentDepositWithdrawUserPO 代理代存用户信息
     */
    public void saveData(ReportAgentDepositWithdrawUserPO reportAgentDepositWithdrawUserPO) {
        LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO> lambdaQueryWrapper=new LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO>();
        lambdaQueryWrapper.eq(ReportAgentDepositWithdrawUserPO::getAgentId,reportAgentDepositWithdrawUserPO.getAgentId());
        lambdaQueryWrapper.eq(ReportAgentDepositWithdrawUserPO::getStaticType,reportAgentDepositWithdrawUserPO.getStaticType());
        lambdaQueryWrapper.eq(ReportAgentDepositWithdrawUserPO::getDayMillis,reportAgentDepositWithdrawUserPO.getDayMillis());
        lambdaQueryWrapper.eq(ReportAgentDepositWithdrawUserPO::getStaticUserId,reportAgentDepositWithdrawUserPO.getStaticUserId());
        ReportAgentDepositWithdrawUserPO reportAgentDepositWithdrawUserPODb=this.getBaseMapper().selectOne(lambdaQueryWrapper);
        if(reportAgentDepositWithdrawUserPODb==null){
            // 初始化数据
            reportAgentDepositWithdrawUserPO.setCreatedTime(System.currentTimeMillis());
            reportAgentDepositWithdrawUserPO.setUpdatedTime(System.currentTimeMillis());
            this.getBaseMapper().insert(reportAgentDepositWithdrawUserPO);
        }
    }

    public Long selectUserCount(LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO> lambdaQueryUserWrapper) {
       return this.getBaseMapper().selectUserCount(lambdaQueryUserWrapper);
    }

    public void delete(ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO) {
        LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO> lambdaQueryWrapper=new LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO>();
        if(reportAgentDepositWithdrawCondVO.getStartDayMillis()!=null){
            lambdaQueryWrapper.ge(ReportAgentDepositWithdrawUserPO::getDayMillis,reportAgentDepositWithdrawCondVO.getStartDayMillis());
        }
        if(reportAgentDepositWithdrawCondVO.getEndDayMillis()!=null){
            lambdaQueryWrapper.le(ReportAgentDepositWithdrawUserPO::getDayMillis,reportAgentDepositWithdrawCondVO.getEndDayMillis());
        }
        if(StringUtils.hasText(reportAgentDepositWithdrawCondVO.getSiteCode())){
            lambdaQueryWrapper.eq(ReportAgentDepositWithdrawUserPO::getSiteCode,reportAgentDepositWithdrawCondVO.getSiteCode());
        }
        this.getBaseMapper().delete(lambdaQueryWrapper);
    }
}
