package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentCommissionPlanPO;
import com.cloud.baowang.agent.po.AgentCommissionPlanTurnoverPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author remo
 */
@Mapper
public interface AgentCommissionPlanTurnoverRepository extends BaseMapper<AgentCommissionPlanTurnoverPO> {

}
