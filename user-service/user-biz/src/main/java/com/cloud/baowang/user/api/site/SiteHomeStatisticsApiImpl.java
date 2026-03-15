package com.cloud.baowang.user.api.site;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.site.SiteHomeStatisticsApi;
import com.cloud.baowang.user.api.vip.UserVipFlowApiService;
import com.cloud.baowang.user.api.vo.site.*;
import com.cloud.baowang.user.service.SiteHomeStatisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteHomeStatisticsApiImpl implements SiteHomeStatisticsApi {

    private final SiteHomeStatisticsService siteHomeStatisticsService;
    private final UserVipFlowApiService userVipFlowApiService;


    @Override
    public UserDataOverviewRespVo userDataOverview(UserDataOverviewResVo param) {
        return siteHomeStatisticsService.userDataOverview(param);
    }

    @Override
    public AgentDataOverviewRespVo agentDataOverview(UserDataOverviewResVo param) {
        return siteHomeStatisticsService.agentDataOverview(param);
    }

    @Override
    public ResponseVO<SiteDataCompareGraphVO> dataCompareGraph(SiteDataCompareGraphParam param) {
        return siteHomeStatisticsService.dataCompareGraph(param);
    }

    @Override
    public ResponseVO<SiteTodoDataResVO> getSiteTodo(String siteCode) {
        return siteHomeStatisticsService.getSiteTodo(siteCode);
    }

    @Override
    public ResponseVO<SiteDataWinLossResVO> siteDataWinLoss(UserDataOverviewResVo vo) {
        return siteHomeStatisticsService.siteDataWinLoss(vo);
    }



    @Override
    public ResponseVO<List<IPTop10ResVO>> getDomainNameRanking(IPTop10ReqVO vo) {
        return siteHomeStatisticsService.getDomainNameRanking(vo);
    }

//    @Override
//    public ResponseVO<List<VisitFromResVO>> getVisitFrom(IPTop10ReqVO vo) {
//        return siteHomeStatisticsService.getVisitFrom(vo);
//    }

    @Override
    public ResponseVO<List<IPTop10ResVO>> getVisitFromByIp(IPTop10ReqVO vo) {
        return siteHomeStatisticsService.getVisitFromByIp(vo);
    }

}
