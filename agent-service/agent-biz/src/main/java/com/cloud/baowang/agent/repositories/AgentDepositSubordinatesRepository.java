package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogPageVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSiteRecordPageVO;
import com.cloud.baowang.agent.api.vo.user.AgentComprehensiveReportVO;
import com.cloud.baowang.agent.po.AgentDepositSubordinatesPO;
import com.cloud.baowang.agent.api.vo.user.AgentStoredMemberVO;
import com.cloud.baowang.user.api.vo.user.request.ComprehensiveReportVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 代理代存记录 Mapper 接口
 * </p>
 *
 * @author qiqi
 * @since 2023-10-24
 */
@Mapper
public interface AgentDepositSubordinatesRepository extends BaseMapper<AgentDepositSubordinatesPO> {

    /**
     * 查询代理代存金额总和
     * @param vo
     * @return
     */
    AgentDepositSubordinatesPO queryDepositOfSubordinatesAmountTotal(@Param("vo") AgentDepositSiteRecordPageVO vo);

    /**
     * 总记录数
     * @param vo
     * @return
     */
    Long depositOfSubordinatesRecordExportCount(@Param("vo") AgentDepositSiteRecordPageVO vo);


    BigDecimal getProxyStoreSalaryTotal(@Param("subordType") String subordType,
                                        @Param("beginTime") Long beginTime,
                                        @Param("endTime") Long endTime);

    List<String> getProxyStoreSalaryTimeCount(@Param("subordType") String subordType,
                                              @Param("beginTime") Long beginTime,
                                              @Param("endTime") Long endTime);



    BigDecimal getProxyAndUserDepositTotal(@Param("beginTime") Long beginTime,
                                           @Param("endTime") Long endTime);



    List<String> getProxyAndUserDepositCount(@Param("beginTime") Long beginTime,
                                             @Param("endTime") Long endTime);


    Page<AgentDistributeLogPageVO> distributeLog(Page<AgentDepositOfSubordinatesResVO> page, @Param("vo") AgentDistributeLogReqVO vo);


    List<WalletAgentSubLineResVO> depositSubordinatesByAgentList(@Param("vo") WalletAgentSubLineReqVO reqVO);


    List<AgentStoredMemberVO> getAgentDepositSum(@Param("vo") AgentComprehensiveReportVO vo);
}
