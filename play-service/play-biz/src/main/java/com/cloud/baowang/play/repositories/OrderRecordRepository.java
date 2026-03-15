package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.UserVenueTopVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoVenueStatisticalReqVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoVenueStatisticalVO;
import com.cloud.baowang.play.api.vo.order.report.*;
import com.cloud.baowang.wallet.api.vo.userCoin.UserWithdrawRunningWaterVO;
import com.cloud.baowang.play.api.vo.agent.PlayAgentUserTeamParam;
import com.cloud.baowang.play.api.vo.agent.PlayAgentWinLossParamVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAdminResVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAggregationDTO;
import com.cloud.baowang.play.api.vo.order.client.ClientOrderTotalVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientReqVO;
import com.cloud.baowang.play.api.vo.user.PlayUserBetAmountSumVO;
import com.cloud.baowang.play.api.vo.user.PlayUserWinLossParamVO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.vo.AgentUserVenueListParam;
import com.cloud.baowang.wallet.api.vo.rebate.UserRebateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


@Mapper
public interface OrderRecordRepository extends BaseMapper<OrderRecordPO> {
    List<OrderRecordAggregationDTO> orderCountAndSumGroup(@Param("dto") OrderRecordAdminResVO dto);

    Long orderRecordCount(@Param("dto") OrderRecordAdminResVO vo);


    List<UserVenueTopVO> getAgentUserVenueLis(@Param("dto") AgentUserVenueListParam vo);

    List<HashMap> getAgentUserRecordByUserWinLoss(@Param("vo") PlayAgentUserTeamParam param);

    List<UserRebateVO> selectUserRebate(@Param("beginDate") long beginDate,
                                        @Param("endDate") long endDate,
                                        @Param("code") String code,
                                        @Param("venueType") Integer venueType,
                                        @Param("siteVOList") List<String> siteVOList);

    UserWithdrawRunningWaterVO getTotalAmountByUserId(@Param("userId") String userId, @Param("startTime") Long startTime, @Param("endTime")  Long endTime);

    List<OrderRecordAggregationDTO> getOrderCountAndSumGroupByGameId(@Param("dto") OrderRecordAdminResVO dto);

    List<AgentLowerLevelInfoVenueStatisticalVO> agentLowerLevelInfoVenueStatistical(@Param("vo") AgentLowerLevelInfoVenueStatisticalReqVO vo);

    BigDecimal classGameWinLossSum(@Param("userId") String userId, @Param("venueCode") String venueCode, @Param("thirdGameCode") String thirdGameCode);

    List<PlayUserBetAmountSumVO> getUserOrderAmountByAgent(@Param("vo") PlayAgentWinLossParamVO vo);

    ClientOrderTotalVO clientOrderTotal(@Param("vo") OrderRecordClientReqVO vo);

    Long getDistinctByTimeSiteCodeCurrencyCode(@Param("startTime") Long startTime,
                                               @Param("endTime") Long endTime,
                                               @Param("siteCode") String siteCode,
                                               @Param("currencyCode") String currencyCode);

    Page<VenueWinLoseRecalculateVO> venueWinLoseRecalculatePage(Page<OrderRecordPO> objectPage, @Param("vo") VenueWinLoseRecalculateReqVO vo);

    List<PlayUserBetAmountSumVO> getUserOrderAmountByUserId(@Param("vo") PlayUserWinLossParamVO vo);

    Integer getBetUserCount(@Param("vo") PlayAgentWinLossParamVO vo);

    Page<WinLoseRecalculateVO> winLoseRecalculatePage(Page<OrderRecordPO> objectPage, @Param("vo") WinLoseRecalculateReqVO vo);

    List<WinLoseRecalculateFeelSpinVO> winLoseRecalculateFreeSpinPage(@Param("vo") WinLoseRecalculateReqVO vo);


}
