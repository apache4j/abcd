package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentRebateConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/20 14:59
 * @description:
 */
@Mapper
public interface AgentRebateConfigRepository  extends BaseMapper<AgentRebateConfigPO> {
    Integer deleteByPlanId(@Param("planId")String planId);
    List<String> getPlanCodeListByCycle(@Param("settleCycle") Integer settleCycle, @Param("siteCode") String siteCode);
}
