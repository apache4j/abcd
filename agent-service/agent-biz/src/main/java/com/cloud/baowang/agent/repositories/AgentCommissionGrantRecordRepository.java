package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.commission.CommissionGranRecordReqVO;
import com.cloud.baowang.agent.po.commission.AgentCommissionGrantRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: fangfei
 * @createTime: 2024/11/08 19:48
 * @description:
 */
@Mapper
public interface AgentCommissionGrantRecordRepository extends BaseMapper<AgentCommissionGrantRecordPO> {
    Integer getGrantRecordPageCount(@Param("vo") CommissionGranRecordReqVO vo);
}
