package com.cloud.baowang.report.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.PlatformVenueRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserVenueStaticsVO;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteReportUserVenueWinLoseApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 场馆盈亏报表")
public interface ReportUserVenueWinLoseApi {
    String PREFIX = ApiConstants.PREFIX + "/reportUserVenueWinLose/api/";

    @PostMapping(value = PREFIX + "pageList")
    @Operation(summary = "场馆盈亏分页查询")
    ResponseVO<ReportVenueWinLossResVO> pageList(@RequestBody ReportVenueWinLossPageReqVO vo);

    @PostMapping(value = PREFIX + "getTotalCount")
    @Operation(summary = "场馆盈亏导出总计")
    Long getTotalCount(@RequestBody ReportVenueWinLossPageReqVO vo);

    @PostMapping(value = PREFIX + "info")
    @Operation(summary = "场馆盈亏详情查询")
    ResponseVO<Page<VenueWinLossInfoResVO>> info(@RequestBody ReportVenueWinLossPageReqVO vo);


    @PostMapping(value = PREFIX + "topPlatformVenue")
    @Operation(summary = "会员平台统计")
    ResponseVO<Page<ReportUserVenueTopVO>> topPlatformVenue(@RequestBody PlatformVenueRequestVO requestVO );

    @PostMapping(value = PREFIX + "queryUserBetsTop")
    @Operation(summary = "会员流水排行榜统计")
    Page<ReportUserVenueBetsTopVO> queryUserBetsTop(@RequestBody ReportUserTopReqVO userTopReqVO);

    @PostMapping(value = PREFIX + "queryAgentVenueWinLoss")
    @Operation(summary = "统计代理场馆数据")
    List<ReportAgentVenueWinLossVO> queryAgentVenueWinLoss(@RequestBody ReportAgentWinLossParamVO vo);

    @PostMapping(value = PREFIX + "queryByTimeAndAgent")
    @Operation(summary = "查询代理下场馆费")
    ResponseVO<List<ReportVenueWinLossAgentVO>> queryByTimeAndAgent(@RequestBody ReportVenueWinLossAgentReqVO vo);

    @PostMapping(value = PREFIX + "queryByTimeAndSiteCode")
    @Operation(summary = "查询指定站点投注人数")
    ResponseVO<Integer> queryByTimeAndSiteCode(@RequestBody SiteDataUserWinLossResVo vo);

    @PostMapping(value = PREFIX + "recalculate")
    @Operation(summary = "场馆盈亏重算")
    ResponseVO<Boolean> recalculate(@RequestBody ReportRecalculateVO vo);

    @PostMapping(value = PREFIX + "getUserVenueAmountByAgentIds")
    @Operation(summary = "获取代理下统计数据")
    List<ReportAgentVenueStaticsVO> getUserVenueAmountByAgentIds(@RequestBody ReportAgentWinLossParamVO vo);

    @PostMapping(value = PREFIX + "getUserVenueAmountGroupByAgent")
    @Operation(summary = "分组统计代理数据")
    List<ReportAgentVenueStaticsVO> getUserVenueAmountGroupByAgent(@RequestBody ReportAgentWinLossParamVO vo);



    @PostMapping(value = PREFIX + "queryVenueAmountByDay")
    @Operation(summary = "按照日期查询场馆投注金额")
    List<ReportAgentVenueStaticsVO> queryVenueAmountByDay(@RequestBody ReportAgentWinLossParamVO vo);

    @PostMapping(value = PREFIX + "getUserVenueBetInfo")
    @Operation(summary = "按照日期查询场馆投注金额-废弃")
    Page<ReportUserVenueStaticsVO> getUserVenueBetInfo(@RequestBody ReportUserWinLossRebateParamVO vo);

    @PostMapping(value = PREFIX + "getUserBetNumByDay")
    @Operation(summary = "按照日期查询场馆投注人数")
    List<SiteReportUserVenueStaticsVO> getUserBetNumByDay(@RequestBody ReportUserWinLossRebateParamVO vo);

    @PostMapping(value = PREFIX + "getDailyCurrencyAmount")
    @Operation(summary = "按照日期查询场馆输赢和有效投注金额")
    List<SiteReportUserVenueStaticsVO> getDailyCurrencyAmount(@RequestBody ReportUserWinLossRebateParamVO vo);

}
