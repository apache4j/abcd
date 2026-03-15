package com.cloud.baowang.report.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserWinLossParamVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeUserRequestVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.*;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import com.cloud.baowang.report.po.ReportUserRechargeWithdrawPO;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawalVO;
import com.cloud.baowang.wallet.api.vo.report.user.UserInfoStatementVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportUserRechargeWithdrawRepository extends BaseMapper<ReportUserRechargeWithdrawPO> {

    /**
     * 根据时间区间查询会员充值
     *
     * @param page
     * @param vo
     * @return
     */
    Page<ReportUserRechargeResponseVO> queryRechargeAmount(@Param("page") Page<ReportUserRechargeResponseVO> page, @Param("vo") ReportUserRechargeRequestVO vo);

    /**
     * 根据userId查询会员累计充值额度
     *
     * @param vo
     * @return
     */
    ReportUserRechargeResponseVO queryRechargeAmountByUserId(@Param("vo") ReportUserRechargeUserRequestVO vo);

    /**
     * 查询时间范围内代理下会员存提总计
     *
     * @param vo
     * @return
     */
    List<ReportRechargeAgentVO> queryByTimeAndAgent(@Param("vo") ReportUserRechargeAgentReqVO vo);

    List<ReportUserRechargePayMethodAgentVO> queryPayMethodByTimeAndAgent(@Param("vo") ReportUserRechargePayMethodAgentReqVO vo);

    /**
     * 按照小时查询每一个会员的记录
     */
    List<DepositWithdrawalVO> getUserDepositWithdrawalPOList(@Param("vo") UserInfoStatementVO vo);

    List<ReportUserAmountVO> getUserDepAmountByAgentIds(@Param("vo") ReportAgentWinLossParamVO vo);
    List<ReportUserAmountVO> getUserDepAmountByUserId(@Param("vo") ReportUserWinLossParamVO vo);
    List<ReportUserAmountVO> getUserDepAmountByUserIds(@Param("vo") ReportUserWinLossParamVO vo);
    List<ReportUserAmountVO> getUserFeeAmountByType(@Param("vo") ReportAgentWinLossParamVO vo);
}
