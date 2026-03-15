package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionRateVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionImitateCalcVO;
import com.cloud.baowang.agent.po.AgentCommissionRatePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 代理佣金比例配置表 Mapper 接口
 * </p>
 *
 * @author fangfei
 * @since 2024-06-04
 */
@Mapper
public interface AgentCommissionRateRepository extends BaseMapper<AgentCommissionRatePO> {
    Integer deleteById(@Param("id")Long id);
    String getRateMaxLevel();
    AgentCommissionRateVO getAgentCommissionRate(@Param("vo") CommissionImitateCalcVO calcVO);
    List<AgentCommissionRateVO> commissionRateList();
}
