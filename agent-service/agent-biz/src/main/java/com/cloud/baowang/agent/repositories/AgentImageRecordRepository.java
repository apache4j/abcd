package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentImageRecordPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图片管理
 */
@Mapper
public interface AgentImageRecordRepository extends BaseMapper<AgentImageRecordPO> {

}