package com.cloud.baowang.report.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportGameApi;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryCenterReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryCenterVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQuerySiteReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQuerySiteVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueTypeReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueTypeVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueVO;
import com.cloud.baowang.report.service.ReportGameService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ReportGameApiImpl implements ReportGameApi {
    private final ReportGameService reportGameService;

    @Override
    public ResponseVO<Page<ReportGameQueryCenterVO>> centerPageList(ReportGameQueryCenterReqVO vo) {
        return ResponseVO.success(reportGameService.centerPageList(vo));
    }

    @Override
    public ResponseVO<Page<ReportGameQuerySiteVO>> sitePageList(ReportGameQuerySiteReqVO vo) {
        return ResponseVO.success(reportGameService.sitePageList(vo));
    }

    @Override
    public ResponseVO<Page<ReportGameQueryVenueTypeVO>> venueTypePageList(ReportGameQueryVenueTypeReqVO vo) {
        return ResponseVO.success(reportGameService.venueTypePageList(vo));
    }

    @Override
    public ResponseVO<Page<ReportGameQueryVenueVO>> venuePageList(ReportGameQueryVenueReqVO vo) {
        return ResponseVO.success(reportGameService.venuePageList(vo));
    }

    @Override
    public ResponseVO<Long> centerPageListCount(ReportGameQueryCenterReqVO dto) {
        return ResponseVO.success(reportGameService.centerPageListCount(dto));
    }

    @Override
    public ResponseVO<Long> sitePageListCount(ReportGameQuerySiteReqVO dto) {
        return ResponseVO.success(reportGameService.sitePageListCount(dto));
    }

    @Override
    public ResponseVO<Long> venueTypePageListCount(ReportGameQueryVenueTypeReqVO dto) {
        return ResponseVO.success(reportGameService.venueTypePageListCount(dto));
    }

    @Override
    public ResponseVO<Long> venuePageListCount(ReportGameQueryVenueReqVO dto) {
        return ResponseVO.success(reportGameService.venuePageListCount(dto));
    }
}
