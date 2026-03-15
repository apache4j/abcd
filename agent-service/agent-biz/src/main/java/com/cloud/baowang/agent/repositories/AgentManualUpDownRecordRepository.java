package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRecordRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRecordVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentManualUpDownRecordListVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAllUpCommissionByAgentAccountsVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpDownVO;
import com.cloud.baowang.agent.api.vo.manualup.UserManualUpReviewResponseVO;
import com.cloud.baowang.agent.po.AgentManualUpDownRecordPO;
import com.cloud.baowang.agent.api.vo.manualup.AgentUserRebateParam;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 代理人工加减额记录 Mapper 接口
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Mapper
public interface AgentManualUpDownRecordRepository extends BaseMapper<AgentManualUpDownRecordPO> {

    Page<AgentManualUpRecordResponseVO> getPage(Page<AgentManualUpRecordResponseVO> page, @Param("vo") AgentManualUpRecordPageVO vo);

    // 会员人工加额记录 总计
    AgentManualUpRecordResponseVO getTotalPage(@Param("vo") AgentManualUpRecordPageVO vo);

    Page<AgentManualUpReviewResponseVO> getUpReviewPage(
            Page<AgentManualUpReviewResponseVO> page,
            @Param("vo") AgentManualUpReviewPageVO vo,
            @Param("adminName") String adminName);

    Page<AgentGetRecordResponseVO> getRecordPage(Page<AgentGetRecordResponseVO> page, @Param("vo") AgentGetRecordPageVO vo);

    Long getTotalCount(@Param("vo") AgentGetRecordPageVO vo);

    BigDecimal getUserDiscountTotal(@Param("siteCode") String siteCode, @Param("adjustWay") Integer adjustWay,
                                    @Param("adjustType") Integer adjustType,
                                    @Param("orderStatus") Integer orderStatus,
                                    @Param("beginTime") Long beginTime,
                                    @Param("endTime") Long endTime);

    int getUserDiscountPersonCount(@Param("siteCode") String siteCode, @Param("adjustWay") Integer adjustWay,
                                   @Param("adjustType") Integer adjustType,
                                   @Param("orderStatus") Integer orderStatus,
                                   @Param("beginTime") Long beginTime,
                                   @Param("endTime") Long endTime);


    List<String> getUserDiscountPersonCountList(@Param("siteCode")String siteCode,@Param("beginTime") Long beginTime,
                                                @Param("endTime") Long endTime);


    int getUserDiscountPersonCount2(@Param("orderStatus") Integer orderStatus,
                                    @Param("beginTime") Long beginTime,
                                    @Param("endTime") Long endTime);


    Set<String> getUserDiscountPersonList(@Param("orderStatus") Integer orderStatus,
                                          @Param("beginTime") Long beginTime,
                                          @Param("endTime") Long endTime);


    List<String> getUserDiscountTimeCountList(@Param("adjustWay") Integer adjustWay,
                                              @Param("adjustType") Integer adjustType,
                                              @Param("orderStatus") Integer orderStatus,
                                              @Param("beginTime") Long beginTime,
                                              @Param("endTime") Long endTime);

    List<String> getUserWithdrawalBigTimeCount(@Param("adjustWay") Integer adjustWay,
                                               @Param("adjustType") Integer adjustType,
                                               @Param("orderStatus") Integer orderStatus,
                                               @Param("beginTime") Long beginTime,
                                               @Param("endTime") Long endTime);


    List<String> selectRedEnvelopeAward(@Param("firstMonthStart") Long firstMonthStart,
                                        @Param("firstMonthEnd") Long firstMonthEnd,
                                        @Param("thisMonthStart") Long thisMonthStart,
                                        @Param("thisMonthEnd") Long thisMonthEnd,
                                        @Param("limitAmount") BigDecimal limitAmount,
                                        @Param("type") String type,
                                        @Param("successCode") String code);

    Long getPageCount(@Param("vo") AgentManualUpRecordPageVO vo);

    List<UserManualUpReviewResponseVO> getAgentUserManualDownList(@Param("vo") AgentUserRebateParam param);

    AgentManualDownRecordVO sumAgentManualDown(@Param("vo") AgentManualDownRecordRequestVO vo);


    List<AgentManualUpDownVO> sumAgentManualUpDown(@Param("agentAccount") String agentAccount);

    List<AgentManualUpDownVO> sumToDayAgentManualUpDown(@Param("agentAccount") String agentAccount, @Param("min") Long min, @Param("max") Long max);


    BigDecimal getAllUpCommissionByAgentAccount(@Param("agentAccount") String agentAccount);

    List<GetAllUpCommissionByAgentAccountsVO> getAllUpCommissionByAgentAccounts(@Param("agentAccounts") List<String> agentAccounts);

    BigDecimal getAllDownCommissionByAgentAccount(@Param("agentAccount") String agentAccount);

    BigDecimal getAllUpRebateByAgentAccount(@Param("agentAccount") String agentAccount);

    BigDecimal getAllDownRebateByAgentAccount(@Param("agentAccount") String agentAccount);

    List<GetAgentManualUpDownRecordListVO> getAgentManualUpDownRecordList(@Param("siteCode")String siteCode,@Param("agentAccounts") List<String> agentAccounts);

    Page<AgentManualUpDownRecordPO> pageQuery(Page<AgentManualUpDownRecordPO> page, @Param("vo") AgentManualUpReviewPageVO vo);

    List<AgentManualUpDownVO> queryAgentDepositWithdraw(@Param("vo") AgentDepositWithdrawFeeVO feeVO);
}
