package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentVirtualCurrencyPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 代理虚拟币地址信息 Mapper 接口
 * </p>
 *
 * @author qiqi
 * @since 2023-10-11
 */
@Mapper
public interface AgentVirtualCurrencyRepository extends BaseMapper<AgentVirtualCurrencyPO> {

}
