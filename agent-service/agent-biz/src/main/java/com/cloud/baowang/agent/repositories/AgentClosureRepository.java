package com.cloud.baowang.agent.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentClosurePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgentClosureRepository extends BaseMapper<AgentClosurePO> {

    /**
     * 插入 自己，如果已存在则忽略
     */
    int insertSelfIfAbsent(@Param("nodeId") String nodeId);

    /**
     * 基于 parent，插入所有父节点
     */
    int insertByParent(@Param("parentUserId") String parentUserId,
                       @Param("newUserId") String newUserId);
}