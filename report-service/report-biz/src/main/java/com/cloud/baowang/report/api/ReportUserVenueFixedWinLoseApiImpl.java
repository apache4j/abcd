package com.cloud.baowang.report.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserWinLossReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.report.service.ReportUserVenueFixedWinLoseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ReportUserVenueFixedWinLoseApiImpl implements ReportUserVenueFixedWinLoseApi {
    private final ReportUserVenueFixedWinLoseService reportUserVenueFixedWinLoseService;

    @Override
    public ReportUserVenueBetsTopVO queryUserWinLossInfo(ReportUserWinLossReqVO userWinLossReqVO) {
       return reportUserVenueFixedWinLoseService.queryUserWinLossInfo(userWinLossReqVO);
    }

    @Override
    public List<String> queryVenueDayCurrency(ReportUserTopReqVO vo) {
        return reportUserVenueFixedWinLoseService.queryVenueDayCurrency(vo);
    }

    @Override
    public Page<ReportUserVenueBetsTopVO> queryUserIdsByVenueDayAmount(ReportUserTopReqVO vo) {
        return reportUserVenueFixedWinLoseService.queryUserIdsByVenueDayAmount(vo);
    }

    @Override
    public List<ReportUserVenueBetsTopVO> queryUserBetsTopPlatBetAmount(ReportUserTopReqVO userTopReqVO) {
        return reportUserVenueFixedWinLoseService.queryUserBetsTopPlatBetAmount(userTopReqVO);
    }

    @Override
    public ReportUserVenueBetsTopVO queryUserBetsPlatBetAmountTotal(ReportUserTopReqVO userTopReqVO) {
        return reportUserVenueFixedWinLoseService.queryUserBetsPlatBetAmountTotal(userTopReqVO);
    }

    @Override
    public ReportUserVenueBetsTopVO queryVenueBetsPlatBetAmountTotal(ReportUserTopReqVO userTopReqVO) {
        return reportUserVenueFixedWinLoseService.queryVenueBetsPlatBetAmountTotal(userTopReqVO);
    }

    @Override
    public Page<ReportUserVenueBetsTopVO> queryUserBetsTop(ReportUserTopReqVO userTopReqVO) {
        return reportUserVenueFixedWinLoseService.queryUserBetsTop(userTopReqVO);
    }
}
