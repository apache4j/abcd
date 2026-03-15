package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentInfoRelationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理上下级关系 Mapper 接口
 *
 * @author ford
 * @since 2025-08-27
 */
@Mapper
public interface AgentInfoRelationRepository extends BaseMapper<AgentInfoRelationPO> {


    int insertSelfIfAbsent(@Param("id") String id,
                           @Param("siteCode") String siteCode,
                           @Param("nodeId") String nodeId,
                           @Param("operator") String operator,
                           @Param("now") Long now);

    /**
     * 基于 parent，把 parent 的所有祖先链复制给 newAgent（distance + 1）
     */
//    int insertByParent(@Param("siteCode") String siteCode,
//                       @Param("parentNodeId") String parentNodeId,
//                       @Param("childNodeId") String childNodeId,
//                       @Param("operator") String operator,
//                       @Param("now") Long now);
}
