package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.commission.AgentCommissionReviewRecordPO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AgentCommissionReviewReportRepository extends BaseMapper<AgentCommissionReviewRecordPO> {

}
