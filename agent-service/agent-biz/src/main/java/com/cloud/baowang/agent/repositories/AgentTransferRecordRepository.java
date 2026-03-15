package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferPageRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordParam;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogPageVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogReqVO;
import com.cloud.baowang.agent.po.AgentTransferRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 20/10/23 8:17 PM
 * @Version : 1.0
 */
@Mapper
public interface AgentTransferRecordRepository extends BaseMapper<AgentTransferRecordPO> {
    Page<AgentTransferPageRecordVO> queryAgentTransferRecord(Page<AgentTransferRecordPO> page,
                                                             @Param("vo") AgentTransferRecordParam param);

    Page<AgentTransferRecordPageVO> queryAgentTransferRecordPage(Page<AgentTransferRecordPO> page,
                                                                 @Param("vo") AgentTransferRecordPageParam param);




    BigDecimal getProxyTransferSalaryTotal(@Param("subordType") String subordType,
                                           @Param("beginTime") Long beginTime,
                                           @Param("endTime") Long endTime);

    List<String> getProxyTransferSalaryTimeCount(@Param("subordType") String subordType,
                                                 @Param("beginTime") Long beginTime,
                                                 @Param("endTime") Long endTime);


    BigDecimal queryTotalTransferRecord(@Param("vo") AgentTransferRecordPageParam param);

    Page<AgentDistributeLogPageVO> distributeLog(Page<AgentTransferPageRecordVO> objectPage, @Param("vo") AgentDistributeLogReqVO vo);

    Long siteQueryAgentTransferRecordCount(@Param("vo")AgentTransferRecordPageParam vo);
}
