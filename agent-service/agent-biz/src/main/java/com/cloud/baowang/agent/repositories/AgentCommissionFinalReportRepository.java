package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReportReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.CommissionDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionGroupReqVO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 代理最终佣金信息表 Mapper 接口
 * </p>
 *
 * @author fangfei
 * @since 2023-10-10
 */
@Mapper
public interface AgentCommissionFinalReportRepository extends BaseMapper<AgentCommissionFinalReportPO> {
    AgentCommissionFinalReportPO getLatestReport(@Param("agentId") String agentId);
    AgentCommissionFinalReportPO getLatestReportByTime(@Param("vo") FrontCommissionGroupReqVO vo);
    List<CommissionDetailVO> getFinalReportListGroupAgentId(@Param("vo") FrontCommissionGroupReqVO vo);
    List<CommissionDetailVO> getStatisticsByAgentId(@Param("vo") FrontCommissionGroupReqVO vo);
    List<AgentCommissionFinalReportPO> getCommissionFinalList(@Param("vo") AgentCommissionReportReqVO reqVO);
}
