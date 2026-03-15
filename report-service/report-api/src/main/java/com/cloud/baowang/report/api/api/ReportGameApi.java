package com.cloud.baowang.report.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryCenterReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryCenterVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQuerySiteReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQuerySiteVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueTypeReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueTypeVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteReportGameApi", value = ApiConstants.NAME)
@Tag(name = "RPC 游戏报表")
public interface ReportGameApi {

    String PREFIX = ApiConstants.PREFIX + "/remoteReportGameApi/api";

    @Operation(summary = "游戏报表-总台")
    @PostMapping(value = PREFIX + "/center/pageList")
    ResponseVO<Page<ReportGameQueryCenterVO>> centerPageList(@RequestBody ReportGameQueryCenterReqVO vo);

    @Operation(summary = "游戏报表-站点")
    @PostMapping(value = PREFIX + "/site/pageList")
    ResponseVO<Page<ReportGameQuerySiteVO>> sitePageList(@RequestBody ReportGameQuerySiteReqVO vo);

    @Operation(summary = "游戏报表-场馆类型")
    @PostMapping(value = PREFIX + "/venue-type/pageList")
    ResponseVO<Page<ReportGameQueryVenueTypeVO>> venueTypePageList(@RequestBody ReportGameQueryVenueTypeReqVO vo);

    @Operation(summary = "游戏报表-场馆")
    @PostMapping(value = PREFIX + "/venue/pageList")
    ResponseVO<Page<ReportGameQueryVenueVO>> venuePageList(@RequestBody ReportGameQueryVenueReqVO vo);

    @Operation(summary = "游戏报表-总台总条数")
    @PostMapping(value = PREFIX + "/center/pageCount")
    ResponseVO<Long> centerPageListCount(@RequestBody ReportGameQueryCenterReqVO dto);

    @Operation(summary = "游戏报表-总台总条数")
    @PostMapping(value = PREFIX + "/site/pageCount")
    ResponseVO<Long> sitePageListCount(@RequestBody ReportGameQuerySiteReqVO dto);

    @Operation(summary = "游戏报表-总台总条数")
    @PostMapping(value = PREFIX + "/venue-type/pageCount")
    ResponseVO<Long> venueTypePageListCount(@RequestBody ReportGameQueryVenueTypeReqVO dto);

    @Operation(summary = "游戏报表-总台总条数")
    @PostMapping(value = PREFIX + "/venue/pageCount")
    ResponseVO<Long> venuePageListCount(@RequestBody ReportGameQueryVenueReqVO dto);
}
