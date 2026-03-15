package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.commission.AgentCommissionExpectReportPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 代理佣金预期表 Mapper 接口
 * </p>
 *
 * @author fangfei
 * @since 2023-10-10
 */
@Mapper
public interface AgentCommissionExpectReportRepository extends BaseMapper<AgentCommissionExpectReportPO> {
    AgentCommissionExpectReportPO getLatestCommissionExpectReport(@Param("agentId") String agentId);
}
