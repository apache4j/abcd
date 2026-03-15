/**
 * @(#)AgentLabelRepositry.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentLabelPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/10/12
 */
@Mapper
public interface AgentLabelRepository extends BaseMapper<AgentLabelPO> {
}
