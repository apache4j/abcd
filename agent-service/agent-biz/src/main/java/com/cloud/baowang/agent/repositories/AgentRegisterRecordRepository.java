package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordParam;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordVO;
import com.cloud.baowang.agent.po.AgentRegisterInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgentRegisterRecordRepository extends BaseMapper<AgentRegisterInfoPO> {
    Page<AgentRegisterRecordVO> queryAgentRegisterRecord(Page<AgentRegisterInfoPO> page,
                                                         @Param("vo") AgentRegisterRecordParam param);
}
