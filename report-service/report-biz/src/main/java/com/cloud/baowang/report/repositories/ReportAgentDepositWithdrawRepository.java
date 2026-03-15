package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.cloud.baowang.report.po.ReportAgentDepositWithdrawPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理报表 Mapper 接口
 *
 * @author ford
 * @since 2024-11-02
 */
@Mapper
public interface ReportAgentDepositWithdrawRepository extends BaseMapper<ReportAgentDepositWithdrawPO> {

    ReportAgentDepositWithdrawPO selectTotal(@Param(Constants.WRAPPER) LambdaQueryWrapper<ReportAgentDepositWithdrawPO> ew);

    void addSumData(ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPO);
}
