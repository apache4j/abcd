package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserDepositSumVO;
import com.cloud.baowang.wallet.api.vo.agent.GetDepositStatisticsByAgentIdVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountReqVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelDataReportRespVO;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawAllRecordVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelDataReportRespVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.risk.RiskWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.site.GetAllArriveAmountBySiteCodeResponseVO;
import com.cloud.baowang.wallet.api.vo.site.GetDepositStatisticsBySiteCodeVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordResponseVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiqi
 */
@Mapper
public interface UserDepositWithdrawalRepository extends BaseMapper<UserDepositWithdrawalPO> {

    Page<UserDepositRecordResponseVO> getUserDepositRecordPage(Page<UserDepositRecordResponseVO> page, @Param("vo") UserDepositRecordPageVO vo);

    List<UserDepositRecordResponseVO> getAllUserDepositRecordPage(@Param("vo") UserDepositRecordPageVO vo);

    Long getUserDepositRecordPageCount(@Param("vo") UserDepositRecordPageVO vo);

    /**
     * 提款记录列表
     *
     * @param page
     * @param vo
     * @return
     */
    Page<UserDepositWithdrawalPO> withdrawalRecordPageList(Page<UserDepositWithdrawalPO> page, @Param("vo") UserWithdrawalRecordRequestVO vo);

    /**
     * 提款记录总计列表
     *
     * @param vo
     * @return
     */
    List<UserWithdrawRecordVO> withdrawalRecordTotalList(@Param("vo") UserWithdrawalRecordRequestVO vo);

    /**
     * 提款记录条数统计
     *
     * @param vo
     * @return
     */
    Long withdrawalRecordPageCount(@Param("vo") UserWithdrawalRecordRequestVO vo);

    /**
     * 提款审核列表
     *
     * @param page
     * @param vo
     * @return
     */
    Page<UserWithdrawReviewPageResVO> withdrawReviewPage(@Param("page") Page<UserWithdrawReviewPageReqVO> page, @Param("vo") UserWithdrawReviewPageReqVO vo);


    Page<FinanceManualConfirmVO> manualConfirmMemberWithdrawPage(Page<FinanceManualConfirmVO> page, @Param("vo") FinanceManualConfirmQueryVO requestVO);

    Page<FinanceManualConfirmRecordVO> manualConfirmMemberWithdrawRecPage(Page<FinanceManualConfirmRecordVO> page, @Param("vo") FinanceManualConfirmRecordQueryVO requestVO);

    /**
     * 提款审核记录
     *
     * @param page
     * @param vo
     * @return
     */
    Page<UserWithdrawReviewRecordVO> withdrawalReviewRecordPageList(Page<UserWithdrawReviewRecordVO> page, @Param("vo") UserWithdrawReviewRecordPageReqVO vo);

    /**
     * 提款审核记录条数统计
     *
     * @param vo
     * @return
     */
    Long withdrawalReviewRecordPageCount(@Param("vo") UserWithdrawReviewRecordPageReqVO vo);

    Long manualConfirmMemberWithdrawRecCount(@Param("vo") FinanceManualConfirmRecordQueryVO vo);

    List<WalletAgentSubLineResVO> getUserFundsListByAgent(@Param("vo") WalletAgentSubLineReqVO reqVO);

    //  List<AgentSubLineResVO> getManualAmountGroupAgent(AgentSubLineReqVO reqVO);

    /**
     * @param start
     * @param end
     * @param agentId
     * @param type
     * @param dbZone  '-05:00' 格式
     * @return
     */
    List<GetDepositStatisticsByAgentIdVO> getDepositStatisticsByAgentId(@Param("siteCode") String siteCode,
                                                                        @Param("start") Long start,
                                                                        @Param("end") Long end,
                                                                        @Param("agentId") String agentId,
                                                                        @Param("type") Integer type,
                                                                        @Param("dbZone") String dbZone,
                                                                        @Param("currencyCode") String currencyCode
    );

    List<GetDepositStatisticsBySiteCodeVO> getDepositStatisticsBySiteCode(@Param("siteCode") String siteCode,
                                                                          @Param("start") Long start,
                                                                          @Param("end") Long end,
                                                                          @Param("type") Integer type,
                                                                          @Param("dbZone") String dbZone,
                                                                          @Param("currencyCode") String currencyCode);

    List<GetDepositStatisticsBySiteCodeVO> getDepositWithdrawnUserCountBySiteCode(@Param("siteCode") String siteCode, @Param("start") Long start,
                                                                          @Param("end") Long end,
                                                                          @Param("type") Integer type,
                                                                          @Param("dbZone") String dbZone,
                                                                          @Param("currencyCode") String currencyCode
    );

    GetAllArriveAmountByAgentUserResponseVO getAllArriveAmountByAgentUser(@Param("siteCode") String siteCode,
                                                                          @Param("agentAccount") String agentAccount,
                                                                          @Param("userAccount") String userAccount,
                                                                          @Param("startTime") Long startTime,
                                                                          @Param("endTime") Long endTime,
                                                                          @Param("type") Integer type);

    List<GetAllArriveAmountBySiteCodeResponseVO> getAllArriveAmountBySiteCode(@Param("siteCode") String siteCode,
                                                                              @Param("start") Long start,
                                                                              @Param("end") Long end);

    List<GetAllArriveAmountByAgentIdVO> getAllArriveAmountByAgentId(@Param("siteCode") String siteCode,
                                                                    @Param("agentAccount") String agentAccount,
                                                                    @Param("startTime") Long startTime,
                                                                    @Param("endTime") Long endTime);

    List<GetAllWithdrawAmountByAgentIdVO> getAllWithdrawAmountByAgentId(@Param("siteCode") String siteCode,
                                                                        @Param("agentAccount") String agentAccount,
                                                                        @Param("startTime") Long startTime,
                                                                        @Param("endTime") Long endTime);

    /**
     * 获取最后一次取款成功的订单
     *
     * @param userId
     * @return
     */
    UserDepositWithdrawalPO selectLastSuccessOrder(@Param("userId") String userId, @Param("withdrawWayId") String withdrawWayId);

    /**
     * 获取最后一笔充值订单
     *
     * @param userId
     * @return
     */
    UserDepositWithdrawalPO selectLastRechargeOrder(@Param("userId") String userId);


    /**
     * 充值报表
     *
     * @param siteCode             站点
     * @param rechargeWithdrawCode 代码
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @return
     */
    List<UserDepositWithdrawalPO> getUserDepositRecordMonthReport(@Param("siteCode") String siteCode,
                                                                  @Param("rechargeWithdrawCode") String rechargeWithdrawCode,
                                                                  @Param("startTime") Long startTime,
                                                                  @Param("endTime") Long endTime);


    /**
     * 非虚拟币充值表
     *
     * @param siteCode  站点
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    List<UserDepositWithdrawalPO> getUserNormalDepositRecordMonthReport(@Param("siteCode") String siteCode,
                                                                        @Param("rechargeWithdrawCode") String rechargeWithdrawCode,
                                                                        @Param("startTime") Long startTime,
                                                                        @Param("endTime") Long endTime);


    /**
     * 提现记录统计
     *
     * @param siteCode  站点
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    List<UserDepositWithdrawalPO> getUserWithdrawRecordMonthReport(@Param("siteCode") String siteCode,
                                                                   @Param("startTime") Long startTime,
                                                                   @Param("endTime") Long endTime);

    /**
     * 充值渠道报表统计
     *
     * @param page
     * @param depositChannelStaticReportReqVO
     * @return
     */
    Page<DepositChannelDataReportRespVO> staticDepositChannelReport(@Param("page") Page<DepositChannelDataReportRespVO> page, @Param("dataReportReqVO") DepositChannelStaticReportReqVO depositChannelStaticReportReqVO);

    /**
     * 充值渠道报表统计 ALL
     *
     * @param depositChannelStaticReportReqVO
     * @return
     */
    DepositChannelDataReportRespVO staticAllDepositChannelReport(@Param("dataReportReqVO") DepositChannelStaticReportReqVO depositChannelStaticReportReqVO);

    /**
     * 客户客户端交易记录
     *
     * @return
     */
    Page<UserTradeRecordResponseVO> tradeRecordRechargeList(@Param("page") Page<UserTradeRecordResponseVO> page, @Param("vo") UserTradeRecordRequestVO vo);

    /**
     * 提现渠道报表统计
     *
     * @param page
     * @param withdrawChannelStaticReportReqVO
     * @return
     */
    Page<WithdrawChannelDataReportRespVO> staticWithDrawChannelReport(@Param("page") Page<WithdrawChannelDataReportRespVO> page, @Param("dataReportReqVO") WithdrawChannelStaticReportReqVO withdrawChannelStaticReportReqVO);

    /**
     * 提款渠道报表统计 ALL
     *
     * @param withdrawChannelStaticReportReqVO
     * @return
     */
    WithdrawChannelDataReportRespVO staticAllWithdrawChannelReport(@Param("dataReportReqVO") WithdrawChannelStaticReportReqVO withdrawChannelStaticReportReqVO);

    /**
     * 客户客户端交易记录存款
     *
     * @param page
     * @param vo
     * @return
     */
    Page<UserTradeRecordResponseVO> tradeRecordWithdrawList(@Param("page") Page<UserTradeRecordResponseVO> page, @Param("vo") UserTradeRecordRequestVO vo);

    List<AgentDepositWithResVO> queryAgentUserDepByWay(@Param("vo") AgentDepositWithdrawFeeVO vo);

    List<UserDepositSumVO> getUserDepositAmount(@Param("vo") AgentWinLossParamVO vo);


    List<AgentDepositWithFeeVO> queryAgentUserDepFee(@Param("vo") AgentDepositWithdrawFeeVO feeVO);

    List<DepositWithdrawAllRecordVO> getAllDepositWithdrawRecord(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("siteCodes") List<String> siteCodes);


    /**
     * 会员存取款 手续费 查询 按照更新时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param siteCode
     * @return
     */
    Page<DepositWithdrawAllRecordVO> findDepositWithdrawPage(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("siteCode")String siteCode);


    Page<UserDepositWithdrawalPO> listPage(@Param("page")Page<UserDepositWithdrawalPO> page, @Param("vo") UserDepositWithdrawPageReqVO vo);
    UserDepositRecordRespVO getUserDepositRecord(@Param("vo") UserDepositRecordPageVO vo);

    UserDepositRecordRespVO getWithDrawalRecord(@Param("vo") UserWithdrawalRecordRequestVO vo);

    List<UserDepositAmountVO> queryDepositAmountByUserIds(@Param("vo") UserDepositAmountReqVO vo);

    Page<RiskWithdrawRecordVO> getUserWithdrawalRecordsDuplicateList(@Param("page")Page<UserDepositWithdrawalPO> page, @Param("vo") UserWithdrawalRecordRequestVO vo);

    long getUserWithdrawalRecordsDuplicateCount(@Param("vo") UserWithdrawalRecordRequestVO vo);

    Page<RiskWithdrawRecordVO> withdrawalRiskRecordPageList(@Param("page")Page<UserDepositWithdrawalPO> page, @Param("vo") UserWithdrawalRecordRequestVO vo);

    long withdrawalRiskRecordPageCount(@Param("vo") UserWithdrawalRecordRequestVO vo);

    Page<UserWithdrawReviewAddressResponseVO> getAddressInfoList(@Param("page")Page<UserWithdrawReviewAddressResponseVO> page,@Param("vo") WithdrawReviewAddressReqVO vo);

    Page<UserManualDepositPageResVO> userManualDepositPage(@Param("page") Page<UserManualDepositPageResVO> page, @Param("vo") UserManualDepositPageReqVO vo);
    Long userManualDepositCount(@Param("vo") UserManualDepositPageReqVO vo);

    Page<UserManualDepositRecordPageResVO> userManualDepositRecordPage(@Param("page")Page<UserManualDepositRecordPageResVO> page,@Param("vo") UserManualDepositRecordPageReqVO vo);

    Long userManualDepositReviewRecordExportCount(@Param("vo") UserManualDepositRecordPageReqVO vo);
}
