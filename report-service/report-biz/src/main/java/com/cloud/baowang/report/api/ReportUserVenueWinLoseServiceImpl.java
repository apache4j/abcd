package com.cloud.baowang.report.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserVenueStaticsVO;
import com.cloud.baowang.user.api.vo.user.request.UserTopReqVO;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.PlatformVenueRequestVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueWinLossVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.site.SiteDataUserWinLossResVo;
import com.cloud.baowang.report.api.vo.site.SiteReportUserVenueStaticsVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueTopVO;
import com.cloud.baowang.report.api.vo.user.ReportUserWinLossRebateParamVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.api.vo.venuewinlose.*;
import com.cloud.baowang.report.service.ReportUserVenueWinLoseService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/06 16:15
 * @description: 会员每日场馆盈亏
 */
@RestController
@Validated
@AllArgsConstructor
public class ReportUserVenueWinLoseServiceImpl implements ReportUserVenueWinLoseApi {

    private final ReportUserVenueWinLoseService reportUserVenueWinLoseService;

    @Override
    public ResponseVO<ReportVenueWinLossResVO> pageList(ReportVenueWinLossPageReqVO vo) {
        return ResponseVO.success(reportUserVenueWinLoseService.pageList(vo));
    }

    @Override
    public Long getTotalCount(ReportVenueWinLossPageReqVO vo) {
        return reportUserVenueWinLoseService.getTotalCount(vo);
    }

    @Override
    public ResponseVO<Page<VenueWinLossInfoResVO>> info(ReportVenueWinLossPageReqVO vo) {
        return ResponseVO.success(reportUserVenueWinLoseService.info(vo));
    }


    @Override
    public ResponseVO<Page<ReportUserVenueTopVO>> topPlatformVenue(PlatformVenueRequestVO vo) {
        return ResponseVO.success(reportUserVenueWinLoseService.topPlatformVenue(vo));
    }

    @Override
    public Page<ReportUserVenueBetsTopVO> queryUserBetsTop(ReportUserTopReqVO userTopReqVO) {
        return reportUserVenueWinLoseService.queryUserBetsTop(userTopReqVO);
    }

    @Override
    public List<ReportAgentVenueWinLossVO> queryAgentVenueWinLoss(ReportAgentWinLossParamVO vo) {
        return reportUserVenueWinLoseService.queryAgentVenueWinLoss(vo);
    }
    public ResponseVO<List<ReportVenueWinLossAgentVO>> queryByTimeAndAgent(ReportVenueWinLossAgentReqVO vo) {
        return ResponseVO.success(reportUserVenueWinLoseService.queryByTimeAndAgent(vo));
    }

    @Override
    public ResponseVO<Integer> queryByTimeAndSiteCode(SiteDataUserWinLossResVo vo) {
        return ResponseVO.success(reportUserVenueWinLoseService.queryByTimeAndSiteCode(vo));
    }

    @Override
    public ResponseVO<Boolean> recalculate(ReportRecalculateVO vo) {
        return ResponseVO.success(reportUserVenueWinLoseService.recalculate(vo));
    }

    @Override
    public List<ReportAgentVenueStaticsVO> getUserVenueAmountByAgentIds(ReportAgentWinLossParamVO vo) {
        return reportUserVenueWinLoseService.getUserVenueAmountByAgentIds(vo);
    }

    @Override
    public List<ReportAgentVenueStaticsVO> getUserVenueAmountGroupByAgent(ReportAgentWinLossParamVO vo) {
        return reportUserVenueWinLoseService.getUserVenueAmountGroupByAgent(vo);
    }

    @Override
    public List<ReportAgentVenueStaticsVO> queryVenueAmountByDay(ReportAgentWinLossParamVO vo) {
        return reportUserVenueWinLoseService.queryVenueAmountByDay(vo);
    }

    @Override
    public Page<ReportUserVenueStaticsVO> getUserVenueBetInfo(ReportUserWinLossRebateParamVO vo) {
//        return reportUserVenueWinLoseService.getUserVenueBetInfo(vo);
        return new Page<>(vo.getPageNumber(),vo.getPageSize());
    }

    @Override
    public List<SiteReportUserVenueStaticsVO> getUserBetNumByDay(ReportUserWinLossRebateParamVO vo){


        return reportUserVenueWinLoseService.getUserBetNumByDay(vo);
    }

    @Override
    public List<SiteReportUserVenueStaticsVO> getDailyCurrencyAmount(ReportUserWinLossRebateParamVO vo){


        return reportUserVenueWinLoseService.getDailyCurrencyAmount(vo);
    }


}
