package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.cloud.baowang.report.po.ReportAgentMerchantStaticDayPO;
import com.cloud.baowang.report.po.ReportAgentStaticDayPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商务报表 Mapper 接口
 *
 * @author ford
 * @since 2024-11-02
 */
@Mapper
public interface ReportAgentMerchantStaticDayRepository extends BaseMapper<ReportAgentMerchantStaticDayPO> {

    List<ReportAgentMerchantStaticDayPO> selectTotal(@Param(Constants.WRAPPER) LambdaQueryWrapper<ReportAgentMerchantStaticDayPO> ew);
}
