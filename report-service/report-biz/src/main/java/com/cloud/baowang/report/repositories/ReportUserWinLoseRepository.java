package com.cloud.baowang.report.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.winloss.AgentUserWinLossParam;
import com.cloud.baowang.agent.api.vo.winloss.AgentWInLossInfoVO;
import com.cloud.baowang.report.api.vo.AgentUserWinLossDetailsPageVO;
import com.cloud.baowang.report.api.vo.GetBetNumberByAgentIdVO;
import com.cloud.baowang.report.api.vo.MemberWinLossDetailVO;
import com.cloud.baowang.report.api.vo.UserWinLoseBetUserVO;
import com.cloud.baowang.report.api.vo.agent.*;
import com.cloud.baowang.report.api.vo.site.GetWinLoseStatisticsBySiteCodeVO;
import com.cloud.baowang.report.api.vo.site.SiteFeesVO;
import com.cloud.baowang.report.api.vo.user.ReportUserBetsVO;
import com.cloud.baowang.report.api.vo.userwinlose.*;
import com.cloud.baowang.report.po.ReportUserWinLosePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 会员每日盈亏 Mapper 接口
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Mapper
public interface ReportUserWinLoseRepository extends BaseMapper<ReportUserWinLosePO> {

    void saveData(@Param("po") ReportUserWinLosePO po);

    void updateSettlement(@Param("id") String id,
                          @Param("validBetAmount") BigDecimal validBetAmount,
                          @Param("betWinLose") BigDecimal betWinLose,
                          @Param("betAmountSettlement") BigDecimal betAmountSettlement,
                          @Param("isAddBetNum") Integer isAddBetNum,
                          @Param("orderStatus") Integer orderStatus,
                          @Param("lastValidBetAmount") BigDecimal lastValidBetAmount,
                          @Param("lastBetWinLose") BigDecimal lastBetWinLose,
                          @Param("currentTime") Long currentTime);

    void updateManualUp(@Param("id") String id,
                        @Param("upCode") Integer upCode,
                        @Param("upAmount") BigDecimal upAmount,
                        @Param("currentTime") Long currentTime);

    void updateManualDown(@Param("id") String id,
                          @Param("downCode") Integer downCode,
                          @Param("downAmount") BigDecimal downAmount,
                          @Param("currentTime") Long currentTime);

    void updatePlateManualUp(@Param("id") String id,
                        @Param("upCode") Integer upCode,
                        @Param("upAmount") BigDecimal upAmount,
                        @Param("currentTime") Long currentTime);


    void updatePlateManualDown(@Param("id") String id,
                          @Param("downCode") Integer downCode,
                          @Param("downAmount") BigDecimal downAmount,
                          @Param("currentTime") Long currentTime);

    void updateActivityByPlatForm(@Param("id") String id,
                                  @Param("activityAmount") BigDecimal activityAmount,
                                  @Param("currentTime") Long currentTime);

    void updateActivityByMainCurrency(@Param("id") String id,
                                      @Param("activityAmount") BigDecimal activityAmount,
                                      @Param("currentTime") Long currentTime);

    void updateRebate(@Param("id") String id,
                      @Param("rebateAmount") BigDecimal rebateAmount,
                      @Param("currentTime") Long currentTime);

    void updateRebateMainCurrency(@Param("id") String id,
                      @Param("rebateAmount") BigDecimal rebateAmount,
                      @Param("currentTime") Long currentTime);

    void updateAlreadyAmount(@Param("id") String id,
                             @Param("alreadyUseAmount") BigDecimal alreadyUseAmount,
                             @Param("currentTime") Long currentTime);

    void updateVipBenefit(@Param("id") String id,
                          @Param("vipBenefitAmount") BigDecimal vipBenefitAmount,
                          @Param("currentTime") Long currentTime);




    void updateVipBenefitMainCurrency(@Param("id") String id,
                                      @Param("vipBenefitAmount") BigDecimal vipBenefitAmount,
                                      @Param("currentTime") Long currentTime);

    void updateOtherUserWinLose(@Param("ids") List<String> ids,

                                @Param("currentTime") Long currentTime);

    void updateBet(@Param("id") String id,
                   @Param("betAmount") BigDecimal betAmount,
                   @Param("currentTime") Long currentTime);


    List<AgentWInLossInfoVO> queryAgentWinLossInfo(@Param("vo") AgentUserWinLossParam param);

    List<HashMap> getAgentUserRecordByUserWinLoss(@Param("vo") ReportAgentUserTeamParam param);

    ReportUserBetsVO getUserBetsInfo(@Param("userAccount") String userAccount, @Param("siteCode") String siteCode);

    List<ReportAgentSubLineResVO> getUserWinLoseByAgent(@Param("vo") ReportAgentSubLineReqVO reqVO);

    List<Map<String, String>> queryActiveDirectBetList(@Param("start") Long startTime, @Param("end") Long endTime,
                                                       @Param("activeBet") BigDecimal activeBetAmount, @Param("allDownAgentUser") List<String> allDownAgentUser);

    List<Map<String, String>> queryActiveDirectDepositList(@Param("start") Long startTime, @Param("end") Long endTime,
                                                           @Param("activeDeposit") BigDecimal activeDepositAmount, @Param("allDownAgentUser") List<String> allDownAgentUser);

    List<GetWinLoseStatisticsByAgentIdVO> getWinLoseStatisticsByAgentId(@Param("start") Long start,
                                                                        @Param("end") Long end,
                                                                        @Param("agentId") String agentId,
                                                                        @Param("dbZone") String dbZone,
                                                                        @Param("currencyCode") String currencyCode
    );

    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCode(@Param("start") Long start,
                                                                          @Param("end") Long end,
                                                                          @Param("siteCode") String siteCode,
                                                                          @Param("dbZone") String dbZone,
                                                                          @Param("currencyCode") String currencyCode
    );

    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCodeHour(@Param("start") Long start,
                                                                              @Param("end") Long end,
                                                                              @Param("siteCode") String siteCode,
                                                                              @Param("dbZone") String dbZone,
                                                                              @Param("currencyCode") String currencyCode
    );

    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCodeMonth(@Param("start") Long start,
                                                                               @Param("end") Long end,
                                                                               @Param("siteCode") String siteCode,
                                                                               @Param("dbZone") String dbZone,
                                                                               @Param("currencyCode") String currencyCode
    );

    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseAndProfitAndLossStatisticsBySiteCode(@Param("start") Long start,
                                                                                          @Param("end") Long end,
                                                                                          @Param("siteCode") String siteCode
    );


    List<GetBetNumberByAgentIdVO> getBetNumberByAgentId(@Param("siteCode") String siteCode,
                                                        @Param("start") Long start,
                                                        @Param("end") Long end,
                                                        @Param("agentId") String agentId,
                                                        @Param("userId") String userId);

    Page<UserWinLoseResponseVO> getPage(Page<UserWinLoseResponseVO> page, @Param("vo") UserWinLosePageVO vo);

    Long getTotalCount(@Param("vo") UserWinLosePageVO vo);

    // 会员盈亏 全部合计
    List<UserWinLoseResponseVO> getTotalPage(@Param("vo") UserWinLosePageVO vo);


    Long getPageCount(@Param("vo") UserWinLosePageVO vo);

    Page<DailyWinLoseResponseVO> dailyWinLosePage(Page<DailyWinLoseResponseVO> page, @Param("vo") DailyWinLosePageVO vo, @Param("siteCode") String siteCode);

    List<DailyWinLoseResponseVO> dailyWinLoseTotal(@Param("vo") DailyWinLosePageVO vo, @Param("siteCode") String siteCode);

    List<DailyWinLoseResponseVO> dailyWinLoseCurrency(@Param("vo") DailyWinLoseVO vo);

    Long dailyWinLosePageCount(@Param("vo") DailyWinLosePageVO vo, @Param("siteCode") String siteCode);


    Page<ClickUserAccountVO> clickUserAccount(Page<ClickUserAccountVO> page, @Param("vo") ClickUserAccountPageVO vo);

    List<ReportUserWinLosePO> queryValidBetAmountMaxByTime(@Param("firstDayMilli") Long firstDayMilli, @Param("lastDayMilli") Long lastDayMilli, @Param("siteCode") String siteCode);

    List<ReportUserWinLosePO> queryWinLoseAmountMaxByTime(@Param("firstDayMilli") Long firstDayMilli, @Param("lastDayMilli") Long lastDayMilli, @Param("siteCode") String siteCode);

    List<ReportAgentWinLoseVO> getUserWinLossByAgentIds(@Param("vo") ReportAgentWinLossParamVO paramVO);

    List<MemberWinLossDetailVO> allChangeMemWinLostDetail(@Param("vo") AgentUserWinLossDetailsPageVO vo);

    List<UserWinLoseAgentVO> queryByTimeAndAgent(@Param("vo") UserWinLoseAgentReqVO vo);

    List<UserWinLoseResponseVO> queryListByParam(@Param("vo") UserWinLoseAgentReqVO vo);

    List<MemberWinLossDetailVO> allMemWinLostDetail(@Param("vo") AgentUserWinLossDetailsPageVO vo);



    /**
     * 当期投注人数
     *
     * @param vo
     * @return
     */
    Long getBetUserNum(@Param("vo") UserWinLoseAgentReqVO vo);

    void updateMainAmount(@Param("id") String id,
                          @Param("alreadyUseAmount") BigDecimal alreadyUseAmount,
                          @Param("adjustAmount") BigDecimal adjustAmount,
                          @Param("currentTime") Long currentTime);


    void updatePlatAmount(@Param("id") String id,
                          @Param("vipAmount") BigDecimal vipAmount,
                          @Param("activityAmount") BigDecimal activityAmount,
                          @Param("currentTime") Long currentTime);


    List<UserWinLoseBetUserVO> getSiteBetUserList(@Param("startTime") Long startTime,
                                                  @Param("endTime") Long endTime,
                                                  @Param("siteCodeList") List<String> siteCodeList);

    /**
     *充提手续费
     */
    List<SiteFeesVO> getWayFeeAmountBySiteCode(Long start, Long end, String siteCode);


    /**
     * 会员盈亏统计
     * @param vo
     * @return
     */
    List<UserWinLossAmountReportVO> queryUserOrderAmountByAgent(@Param("vo") UserWinLossAmountParamVO vo);

    List<ReportUserBetAmountSumVO> getWinLoseStatisticsByAgentIds(@Param("vo") ReportAgentWinLossParamVO paramVO);

    List<ReportUserBetAmountSumVO> getUserOrderAmountByUserId(@Param("vo")ReportUserWinLossParamVO paramVO);
}
