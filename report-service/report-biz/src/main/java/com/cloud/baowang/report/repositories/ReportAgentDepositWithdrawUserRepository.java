package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.cloud.baowang.report.po.ReportAgentDepositWithdrawUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理存取报表用户 Mapper 接口
 *
 * @author ford
 * @since 2024-11-02
 */
@Mapper
public interface ReportAgentDepositWithdrawUserRepository extends BaseMapper<ReportAgentDepositWithdrawUserPO> {

    /**
     * 用户查询
     * @param ew
     * @return 去除重复用户数
     */
    Long selectUserCount(@Param(Constants.WRAPPER)LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO> ew);
}
