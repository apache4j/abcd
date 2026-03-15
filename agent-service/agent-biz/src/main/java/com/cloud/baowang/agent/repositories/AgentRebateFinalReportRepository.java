package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.commission.RebateDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.AgentPersonDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionGroupReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.RebateBetDetailVO;
import com.cloud.baowang.agent.po.commission.AgentRebateFinalReportPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/03 22:34
 * @description:
 */
@Mapper
public interface AgentRebateFinalReportRepository extends BaseMapper<AgentRebateFinalReportPO> {
    List<RebateBetDetailVO> getFinalReportListGroupAgentId(@Param("vo") FrontCommissionGroupReqVO vo);
    List<AgentPersonDetailVO> getPersonAmountGroupAgentId(@Param("vo") FrontCommissionGroupReqVO vo);
}
