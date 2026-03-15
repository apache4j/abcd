package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.remark.AgentRemarkRecordVO;
import com.cloud.baowang.agent.po.AgentRemarkRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 代理备注表 Mapper 接口
 * </p>
 *
 * @author kimi
 * @since 2023-10-10
 */
@Mapper
public interface AgentRemarkRecordRepository extends BaseMapper<AgentRemarkRecordPO> {

    Page<AgentRemarkRecordVO> getAgentRemarkPage(Page<AgentRemarkRecordVO> page,
                                                 @Param("vo") AgentDetailParam param);
}
