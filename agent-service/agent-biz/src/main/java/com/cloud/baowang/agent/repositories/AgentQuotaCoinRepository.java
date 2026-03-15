package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentQuotaCoinPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 代理额度钱包 Mapper 接口
 * </p>
 *
 * @author qiqi
 */
@Mapper
public interface AgentQuotaCoinRepository extends BaseMapper<AgentQuotaCoinPO> {

}
