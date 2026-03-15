package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentMerchantPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AgentMerchantRepository extends BaseMapper<AgentMerchantPO> {
}
