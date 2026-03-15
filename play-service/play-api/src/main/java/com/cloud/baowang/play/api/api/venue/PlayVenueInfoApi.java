package com.cloud.baowang.play.api.api.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.api.vo.venue.siteDetail.SiteVenueQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * play-service服务
 */
@Tag(name = "游戏场馆接口")
@FeignClient(contextId = "playServiceVenueInfoAPi", value = ApiConstants.NAME)
public interface PlayVenueInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/venue_info/api/";

    //改为通过场馆code获取吧
    @PostMapping(PREFIX + "getSystemVenuesByIds")
    @Operation(summary = "根据id获取系统游戏场馆配置")
    ResponseVO<List<VenueInfoVO>> getSystemVenuesByIds(@RequestBody List<String> ids);

    @Operation(summary = "总控-游戏平台分页")
    @PostMapping(PREFIX + "adminVenueInfoPage")
    ResponseVO<Page<VenueInfoVO>> adminVenueInfoPage(@RequestBody VenueInfoRequestVO paramVO);


    @Operation(summary = "站点-游戏平台分页")
    @PostMapping(PREFIX + "siteVenueInfoPage")
    ResponseVO<Page<VenueInfoVO>> siteVenueInfoPage(@RequestBody VenueInfoRequestVO paramVO);

//    @Operation(summary = "站点后台-平台分页")
//    @PostMapping(PREFIX + "siteVenueInfoPage")
//    ResponseVO<Page<SiteVenueInfoVO>> siteVenueInfoPage(@RequestBody VenueInfoRequestVO paramVO);

    @Operation(summary = "游戏平台列表-有条件查询")
    @PostMapping(PREFIX + "venueInfoListByParam")
    ResponseVO<List<VenueInfoVO>> venueInfoListByParam(@RequestBody VenueInfoRequestVO paramVO);

    @Operation(summary = "游戏平台列表")
    @PostMapping(PREFIX + "siteVenueInfoList")
    ResponseVO<List<VenueInfoVO>> venueInfoList();

    @Operation(summary = "站点场馆列表名称")
    @PostMapping(PREFIX + "getSiteVenueNameMap")
    ResponseVO<Map<String, String>> getSiteVenueNameMap();

    @Operation(summary = "总控场馆列表名称")
    @PostMapping(PREFIX + "getAdminVenueNameMap")
    ResponseVO<Map<String, String>> getAdminVenueNameMap();


//    @Operation(summary = "新增游戏平台")
//    @PostMapping(PREFIX + "addVenueInfo")
//    ResponseVO<Boolean> addVenueInfo(@RequestBody VenueInfoAddVO venueInfoAddVO);

    @Operation(summary = "总控修改游戏平台")
    @PostMapping(PREFIX + "adminUpVenueInfo")
    ResponseVO<Boolean> adminUpVenueInfo(@RequestBody VenueInfoUpVO venueInfoUpVO);

    @Operation(summary = "修改游戏平台")
    @PostMapping(PREFIX + "siteUpVenueInfo")
    ResponseVO<Boolean> siteUpVenueInfo(@RequestBody SiteVenueInfoUpVO venueInfoUpVO);

    @Operation(summary = "修改游戏平台状态")
    @PostMapping(PREFIX + "upAdminVenueInfoStatus")
    ResponseVO<Boolean> upAdminVenueInfoStatus(@RequestBody GameClassStatusRequestUpVO venueInfoUpVO);

    @Operation(summary = "根据平台编码查询平台配置")
    @PostMapping(PREFIX + "venueInfoByVenueCode")
    ResponseVO<VenueInfoVO> venueInfoByVenueCode(@RequestParam(value = "venueCode") String venueCode,
                                                 @RequestParam(value = "currencyCode") String currencyCode);

    @Operation(summary = "根据场馆CODE查询场馆")
    @PostMapping(PREFIX + "venueInfoByCodeIds")
    ResponseVO<List<VenueInfoVO>> venueInfoByCodeIds(@RequestBody List<String> venueCodeIds);

    @Operation(summary = "站台关联场馆")
    @PostMapping(PREFIX + "addSiteVenue")
    ResponseVO<Boolean> addSiteVenue(@RequestParam("siteCode") String siteCode, @RequestBody List<SiteVenueVO> siteVenueVO,@RequestParam("siteName") String siteName);

    @Operation(summary = "站台关联场馆")
    @PostMapping(PREFIX + "queryVenueAuthorize")
    ResponseVO<SiteVenueResponseVO> queryVenueAuthorize(@RequestBody SiteVenueRequestVO siteVenueRequestVO);

    @Operation(summary = "获取当前站点下所有场馆以及对应游戏信息")
    @PostMapping(PREFIX + "querySiteVenueBySiteCode")
    List<SiteVenueQueryVO> querySiteVenueBySiteCode(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取所有场馆类型")
    @PostMapping(PREFIX + "queryVenueTypeAll")
    List<VenueTypeVO> queryVenueTypeAll();

    @Operation(summary = "获取所有场馆类型")
    @PostMapping(PREFIX + "venueMaintainClosed")
    ResponseVO<Boolean> venueMaintainClosed(@RequestParam("venueCode") String venueCode,@RequestParam("siteCode") String siteCode);

    @Operation(summary = "站点场馆权限")
    @PostMapping(PREFIX + "getSiteVenueIdsBySiteCodeAndByVenueCode")
    ResponseVO<Boolean> getSiteVenueIdsBySiteCodeAndByVenueCode(@RequestParam("siteCode") String siteCode,
                                                                @RequestParam("venueCode") String venueCode);


    @Operation(summary = "获取站点场馆信息")
    @PostMapping(PREFIX + "getSiteVenueInfoByVenueCode")
    ResponseVO<VenueInfoVO> getSiteVenueInfoByVenueCode(@RequestBody SiteVenueInfoCheckVO checkVO);


    @Operation(summary = "站点-修改场馆状态")
    @PostMapping(PREFIX + "upSiteVenueInfoStatus")
    ResponseVO<Boolean> upSiteVenueInfoStatus(@RequestBody GameClassStatusRequestUpVO venueInfoUpVO);

    @Operation(summary = "站点-同步总控场馆维护状态")
    @PostMapping(PREFIX + "upSynAdminVenueInfoStatus")
    ResponseVO<Boolean> upSynAdminVenueInfoStatus(@RequestBody GameClassStatusRequestUpVO venueInfoUpVO);

    @Operation(summary = "获取场馆list")
    @PostMapping(PREFIX + "getVenueInfoList")
    ResponseVO<List<VenueInfoVO>> getVenueInfoList(@RequestParam("venueCode") String venueCode);


    @Operation(summary = "获取一级分类的场馆配置")
    @PostMapping(PREFIX + "getGameOneVenueJoin")
    ResponseVO<GameOneClassVenueCurrencyVO> getGameOneVenueJoin(@RequestBody GameClassInfoDeleteVO req);

    @Operation(summary = "初始化同步总控场馆的配置信息到站点场馆配置信息")
    @PostMapping(PREFIX + "initVenueSiteConfig")
    ResponseVO<Void> initVenueSiteConfig();



}
