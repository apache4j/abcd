package com.cloud.baowang.report.api;

import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.ActiveByAgentVO;
import com.cloud.baowang.report.api.vo.ReportAgentActiveVO;
import com.cloud.baowang.report.api.vo.GetBetNumberByAgentIdVO;
import com.cloud.baowang.report.api.vo.agent.*;
import com.cloud.baowang.report.api.vo.user.ReportUserBetsVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountParamVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountReportVO;
import com.cloud.baowang.report.service.ReportUserWinLoseService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class ReportUserWinLoseApiImpl implements ReportUserWinLoseApi {

    private final ReportUserWinLoseService reportUserWinLoseService;

    @Override
    public ReportAgentTeamVO getTeamOrderInfo(ReportAgentUserWinLossVO vo) {
        return reportUserWinLoseService.getNewTeamOrderInfo(vo);
    }



    @Override
    public ReportUserBetsVO getUserBetsInfo(String userAccount, String siteCode) {
        return reportUserWinLoseService.getUserBetsInfo(userAccount, siteCode);
    }

    @Override
    public List<ReportAgentSubLineResVO> getUserWinLoseByAgent(ReportAgentSubLineReqVO reqVO) {
        return reportUserWinLoseService.getUserWinLoseByAgent(reqVO);
    }

    @Override
    public ActiveByAgentVO getActiveInfoByAgent(ReportAgentUserTeamParam param) {
        return reportUserWinLoseService.getActiveInfoByAgent(param);
    }


    @Override
    public List<GetBetNumberByAgentIdVO> getBetNumberByAgentId(String siteCode, Long start, Long end, String agentId, String userId) {
        return reportUserWinLoseService.getBetNumberByAgentId(siteCode, start, end, agentId, userId);
    }

    @Override
    public ReportAgentActiveVO getAgentActiveInfo(ReportAgentUserTeamParam param) {
        return reportUserWinLoseService.getAgentActiveInfo(param);
    }

    @Override
    public List<ReportAgentWinLoseVO> getUserWinLossByAgentIds(ReportAgentWinLossParamVO paramVO) {
        return reportUserWinLoseService.getUserWinLossByAgentIds(paramVO);
    }


    @Override
    public void addReportWinLoseRecord(ReportRecalculateVO reportRecalculateVO) {
        reportUserWinLoseService.addReportWinLoseRecord(reportRecalculateVO);
    }

    @Override
    public List<UserWinLossAmountReportVO> queryUserOrderAmountByAgent(UserWinLossAmountParamVO vo) {
        return reportUserWinLoseService.queryUserOrderAmountByAgent(vo);
    }

    @Override
    public List<DailyWinLoseResponseVO> dailyWinLoseCurrency(DailyWinLoseVO vo) {
        return reportUserWinLoseService.dailyWinLoseCurrency(vo);
    }


}
