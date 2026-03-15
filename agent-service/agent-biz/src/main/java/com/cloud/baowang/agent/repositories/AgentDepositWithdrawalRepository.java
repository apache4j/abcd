package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.GetAgentDepositWithdrawalListVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordResponseVO;
import com.cloud.baowang.agent.api.vo.withdraw.ClientAgentWithdrawRecordRequestVO;
import com.cloud.baowang.agent.api.vo.withdraw.ClientAgentWithdrawRecordResponseVO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportRechargeAgentVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.AgentDepositWithFeeVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordPageReqVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiqi
 */
@Mapper
public interface AgentDepositWithdrawalRepository extends BaseMapper<AgentDepositWithdrawalPO> {

    Page<AgentWithdrawReviewRecordVO> withdrawalReviewRecordPageList(Page<AgentWithdrawReviewRecordVO> page, @Param("vo") AgentWithdrawReviewRecordPageReqVO vo);

    Long withdrawalReviewRecordPageCount(@Param("vo") UserWithdrawReviewRecordPageReqVO vo);

    Page<AgentWithdrawReviewPageResVO> withdrawReviewPage(Page<AgentWithdrawReviewPageReqVO> page, @Param("vo") AgentWithdrawReviewPageReqVO vo);

    /**
     * 代理存款记录 分页查询
     *
     * @param page
     * @param vo
     * @return
     */
    Page<AgentDepositRecordRes> queryAgentDepositByPage(Page<AgentDepositRecordRes> page, @Param("vo") AgentDepositRecordReq vo);

    /**
     * 计算导出数据总条数
     *
     * @param requestVO
     * @return
     */
    Long getDepositRecordExportCount(@Param("vo") AgentDepositRecordReq requestVO);


    List<GetAgentDepositWithdrawalListVO> getAgentDepositWithdrawalList(@Param("siteCode") String siteCode, @Param("agentAccounts") List<String> agentAccounts);

    AgentDepositWithdrawalPO selectLastRechargeOrder(@Param("siteCode") String siteCode, @Param("agentId") String agentId);

    AgentDepositWithdrawalPO selectLastRechargeOrderByAgentId(@Param("agentId") String agentId);

    AgentDepositWithdrawalPO selectLastSuccessOrder(@Param("agentId") String agentId,@Param("withdrawWayId") String withdrawWayId);

    Page<AgentDepositWithdrawalPO> getWithdrawRecordPage(Page<AgentDepositWithdrawalPO> page, @Param("vo") AgentWithdrawalRecordReqVO vo);
    List<AgentDepositWithdrawalPO> getWithdrawRecordList(@Param("vo") AgentWithdrawalRecordReqVO vo);

    List<AgentDepositWithdrawalPO> getWithdrawRecord(@Param("vo") AgentWithdrawalRecordReqVO vo);

    Long getWithdrawRecordTotal(@Param("vo") AgentWithdrawalRecordReqVO vo);

    Long withdrawalReviewRecordTotal(@Param("vo")AgentWithdrawReviewRecordPageReqVO vo);

    List<AgentDepositWithFeeVO> queryAgentUserDepFee(@Param("vo") AgentDepositWithdrawFeeVO feeVO);

    Page<ClientAgentRechargeRecordResponseVO> rechargeRecordList(@Param("page") Page<AgentDepositWithdrawalPO> page,@Param("vo") ClientAgentRechargeRecordRequestVO vo);

    Page<ClientAgentWithdrawRecordResponseVO> withdrawRecordList(@Param("page") Page<AgentDepositWithdrawalPO> page,@Param("vo") ClientAgentWithdrawRecordRequestVO vo);

    List<AgentDepositWithFeeVO> queryAgentUserDepFeeGroupType(@Param("vo") AgentDepositWithdrawFeeVO feeVO);

    List<ReportRechargeAgentVO> queryAgentDepositWithdrawFee(@Param("vo") AgentDepositWithdrawFeeVO feeVO);

    List<ReportRechargeAgentVO> queryAgentDepositWithdrawFeeByWay(@Param("vo") AgentDepositWithdrawFeeVO feeVO);

    List<AgentDepositWithdrawSumRespVO> queryAgentReportAmountGroupBy(@Param("vo") AgentDepositWithDrawSumReqVO vo);
    AgentDepositWithdrawSumRespVO queryAgentReportCountGroupBy(@Param("vo") AgentDepositWithDrawSumReqVO vo);

    Page<AgentWithdrawReviewAddressResponseVO> getAddressInfoList(@Param("page")Page<AgentWithdrawReviewAddressResponseVO> page, @Param("vo") AgentWithdrawReviewAddressReqVO vo);

    Page<AgentDepositReviewPageResVO> depositReviewPage(@Param("page") Page<AgentWithdrawReviewPageReqVO> page,  @Param("vo")  AgentDepositReviewPageReqVO vo);
    Long depositReviewCount(@Param("vo")  AgentDepositReviewPageReqVO vo);

    Page<AgentDepositReviewRecordPageResVO> depositReviewRecordPage(@Param("page") Page<AgentDepositReviewRecordPageResVO> page,@Param("vo") AgentDepositReviewRecordPageReqVO vo);

    Long agentManualDepositReviewRecordExportCount(@Param("vo")AgentDepositReviewRecordPageReqVO vo);
}
