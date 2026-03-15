package com.cloud.baowang.system.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.param.AgentParamConfigPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代理参数配置
 */
@Mapper
public interface AgentParamConfigRepository extends BaseMapper<AgentParamConfigPO> {

}
