package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.cloud.baowang.report.po.ReportAgentStaticBetPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理每日投注会员 Mapper 接口
 *
 * @author ford
 * @since 2024-11-02
 */
@Mapper
public interface ReportAgentStaticBetRepository extends BaseMapper<ReportAgentStaticBetPO> {

    Long staticBetUserNum(@Param(Constants.WRAPPER)  LambdaQueryWrapper<ReportAgentStaticBetPO> ew);
}
