package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordParam;
import com.cloud.baowang.agent.po.AgentLoginRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgentLoginRecordRepository extends BaseMapper<AgentLoginRecordPO> {

    Page<AgentLoginRecordPageVO> queryAgentLoginRecordPage(Page<AgentLoginRecordPageVO> page,
                                                           @Param("vo") AgentLoginRecordParam param,
                                                           @Param("siteCode") String siteCode);
    Long queryAgentLoginRecordCount(@Param("vo") AgentLoginRecordParam param,
                                                           @Param("siteCode") String siteCode);
}
