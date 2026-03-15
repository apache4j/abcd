package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.report.po.ReportAgentStaticDayPO;
import com.cloud.baowang.report.po.ReportTopAgentStaticDayPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商务总代报表 Mapper 接口
 *
 * @author ford
 * @since 2024-11-02
 */
@Mapper
public interface ReportTopAgentStaticDayRepository extends BaseMapper<ReportTopAgentStaticDayPO> {

    List<ReportTopAgentStaticDayPO> selectTotal(@Param(Constants.WRAPPER) LambdaQueryWrapper<ReportTopAgentStaticDayPO> ew);

    //团队总计
    ReportTopAgentStaticDayPO selectTeamNumTotal(@Param(Constants.WRAPPER) LambdaQueryWrapper<ReportTopAgentStaticDayPO> ew);

    Page<ReportTopAgentStaticDayPO> selectGroupPage(Page<ReportTopAgentStaticDayPO> page, @Param(Constants.WRAPPER)LambdaQueryWrapper<ReportTopAgentStaticDayPO> lambdaQueryWrapper);

    //团队分页
    Page<ReportTopAgentStaticDayPO> selectTeamNumGroupPage(Page<ReportTopAgentStaticDayPO> page, @Param(Constants.WRAPPER)LambdaQueryWrapper<ReportTopAgentStaticDayPO> lambdaQueryWrapper);

}
