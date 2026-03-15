package com.cloud.baowang.user.api.api.site;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.site.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @className: SiteStatistics
 * @author: wade
 * @description: 站点首页统计
 * @date: 12/8/24 10:05
 */
@FeignClient(contextId = "remoteSiteHomeStatisticsApi", value = ApiConstants.NAME)
@Tag(name = "站点首页统计信息服务")
public interface SiteHomeStatisticsApi {
    String PREFIX = ApiConstants.PREFIX + "/siteHomeStatistics/api";


    @Operation(summary = "用户数据概览")
    @PostMapping(value = PREFIX+"/userDataOverview")
    UserDataOverviewRespVo userDataOverview(@RequestBody UserDataOverviewResVo param);

    @Operation(summary = "用户数据概览")
    @PostMapping(value = PREFIX+"/agentDataOverview")
    AgentDataOverviewRespVo agentDataOverview(@RequestBody UserDataOverviewResVo param);

    @Operation(summary = "站点线图数据概览")
    @PostMapping(value = PREFIX+"/dataCompareGraph")
    ResponseVO<SiteDataCompareGraphVO> dataCompareGraph(@RequestBody SiteDataCompareGraphParam param);

    @Operation(summary = "站点待办")
    @PostMapping(value = PREFIX+"/getSiteTodo")
    ResponseVO<SiteTodoDataResVO> getSiteTodo(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "输赢概览")
    @PostMapping(value = PREFIX+"/siteDataWinLoss")
    ResponseVO<SiteDataWinLossResVO> siteDataWinLoss(@RequestBody UserDataOverviewResVo vo);

    @Operation(summary = "来路分析")
    @PostMapping(value = PREFIX + "/getIPTop10")
    ResponseVO<List<IPTop10ResVO>> getDomainNameRanking(@RequestBody IPTop10ReqVO vo);


    @Operation(summary = "流量显示-地图")
    @PostMapping(value = PREFIX + "/getVisitFromByIp")
    ResponseVO<List<IPTop10ResVO>> getVisitFromByIp(@RequestBody IPTop10ReqVO vo);
}
