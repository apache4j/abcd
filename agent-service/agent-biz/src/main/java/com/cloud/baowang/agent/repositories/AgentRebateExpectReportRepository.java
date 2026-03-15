package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.commission.RebateDetailVO;
import com.cloud.baowang.agent.po.commission.AgentRebateExpectReportPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/03 22:34
 * @description:
 */
@Mapper
public interface AgentRebateExpectReportRepository extends BaseMapper<AgentRebateExpectReportPO> {
    List<RebateDetailVO> getLatestRebateDetail(@Param("agentId") String agentId);
}
