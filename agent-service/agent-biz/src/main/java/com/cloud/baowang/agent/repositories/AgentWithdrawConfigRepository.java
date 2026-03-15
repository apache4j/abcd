package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentWithdrawConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理提款设置 Mapper 接口
 *
 * @author kimi
 * @since 2024-06-12
 */
@Mapper
public interface AgentWithdrawConfigRepository extends BaseMapper<AgentWithdrawConfigPO> {
    Integer deleteById(@Param("id")Long id);
}
