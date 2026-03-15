package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentCommissionLadderPO;
import com.cloud.baowang.agent.po.commission.AgentVenueRatePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: fangfei
 * @createTime: 2024/11/05 14:34
 * @description:
 */
@Mapper
public interface AgentVenueRateRepository extends BaseMapper<AgentVenueRatePO> {
}
