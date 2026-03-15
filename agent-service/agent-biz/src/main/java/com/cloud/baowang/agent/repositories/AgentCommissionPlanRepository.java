package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentCommissionPlanPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: fangfei
 * @createTime: 2024/09/20 14:34
 * @description:
 */
@Mapper
public interface AgentCommissionPlanRepository  extends BaseMapper<AgentCommissionPlanPO> {
    Integer deleteById(@Param("id")String id);
}
