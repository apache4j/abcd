package com.cloud.baowang.agent.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReportVO;
import com.cloud.baowang.agent.po.AgentInfoRelationPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionVenueReportPO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Mapper
public interface AgentCommissionVenueRepository extends BaseMapper<AgentCommissionVenueReportPO> {

    List<AgentCommissionReportVO> getTeamCommissionReport(@Param("siteCode") String siteCode,
                                                          @Param("childNodes") Set<String> childNodes,
                                                          @Param("startTime") Long startTime,
                                                          @Param("endTime") Long endTime);

    Long selectSubAgents(@Param("agentId") String agentId,@Param("siteCode") String siteCode,
                         @Param("startTime") Long startTime,@Param("endTime") Long endTime);

    Long selectDirUsers(@Param("agentId") String agentId,@Param("siteCode") String siteCode,
                        @Param("startTime") Long startTime,@Param("endTime") Long endTime);

    BigDecimal selectTeamCommission(@Param("agentId") String agentId,@Param("siteCode") String siteCode,
                                    @Param("startTime") Long startTime,@Param("endTime") Long endTime);

    AgentCommissionVenueReportPO selectDirUserCommission(@Param("agentId") String agentId,@Param("siteCode") String siteCode,
                                       @Param("startTime") Long startTime,@Param("endTime") Long endTime);

    BigDecimal selectOtherLevelCommission(@Param("agentId") String agentId,@Param("siteCode") String siteCode,
                                          @Param("startTime") Long startTime,@Param("endTime") Long endTime);
}