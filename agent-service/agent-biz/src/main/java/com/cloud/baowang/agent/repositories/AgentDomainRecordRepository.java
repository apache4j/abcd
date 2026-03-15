package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentDomainRecordPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 域名管理
 */
@Mapper
public interface AgentDomainRecordRepository extends BaseMapper<AgentDomainRecordPO> {

}
