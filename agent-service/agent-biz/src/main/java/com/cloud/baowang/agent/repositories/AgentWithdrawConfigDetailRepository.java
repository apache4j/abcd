package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentWithdrawConfigDetailPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgentWithdrawConfigDetailRepository extends BaseMapper<AgentWithdrawConfigDetailPO> {
    Integer deleteByConfigId(@Param("configId") String configId);

}
