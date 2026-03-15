package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewRecordVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionReviewReq;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewVO;
import com.cloud.baowang.agent.po.commission.AgentCommissionReviewRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 佣金审核 Mapper 接口
 * </p>
 *
 * @author fangfei
 * @since 2023-10-10
 */
@Mapper
public interface AgentCommissionReviewRepository extends BaseMapper<AgentCommissionReviewRecordPO> {

    /**
     *
     * @param page
     * @param vo
     * @param adminName
     * @param auditPageIndex 0:发放审核页面
     * @return
     */
    Page<AgentCommissionReviewVO> getCommissionReviewPage(
            Page<AgentCommissionReviewVO> page,
            @Param("vo") CommissionReviewReq vo,
            @Param("adminName") String adminName,
            @Param("auditPageIndex")Integer auditPageIndex
            );


    /**
     * 1:审核记录页面
     * @param page
     * @param vo
     * @param adminName
     * @param auditPageIndex
     * @return
     */
    Page<AgentCommissionReviewRecordVO> getCommissionReviewRecordPage(
            Page<AgentCommissionReviewRecordVO> page,
            @Param("vo") CommissionReviewReq vo,
            @Param("adminName") String adminName,
            @Param("auditPageIndex")Integer auditPageIndex
    );

    BigDecimal getTotalCommissionByAgentId( @Param("agentId") String agentId);

    Integer getCommissionReviewCount(@Param("vo") CommissionReviewReq vo);

    void deleteByReportId(@Param("reportId") String reportId);
}
