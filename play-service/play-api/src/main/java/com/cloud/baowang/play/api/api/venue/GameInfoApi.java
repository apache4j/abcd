package com.cloud.baowang.play.api.api.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.venue.*;
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
@Tag(name = "游戏管理接口")
@FeignClient(contextId = "playServiceGameInfoAPi", value = ApiConstants.NAME)
public interface GameInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/game_info/api/";


    @Operation(summary = "游戏管理-分页")
    @PostMapping(PREFIX + "newSiteGameInfoPage")
    ResponseVO<Page<SiteGameInfoVO>> newSiteGameInfoPage(@RequestBody GameInfoRequestVO requestVO);



    @Operation(summary = "游戏管理-列表")
    @PostMapping(PREFIX + "siteGameInfoList")
    ResponseVO<List<SiteGameInfoVO>> siteGameInfoList(@RequestBody GameInfoRequestVO requestVO);

    @Operation(summary = "有配置一二级分类的游戏管理-列表")
    @PostMapping(PREFIX + "getConfigSiteGameInfoList")
    ResponseVO<List<SiteGameInfoVO>> getConfigSiteGameInfoList(@RequestBody GameInfoRequestVO vo);



    @Operation(summary = "总台游戏管理-分页")
    @PostMapping(PREFIX + "adminGameInfoPage")
    ResponseVO<Page<GameInfoVO>> adminGameInfoPage(@RequestBody GameInfoRequestVO requestVO);

    @Operation(summary = "总台游戏管理-列表")
    @PostMapping(PREFIX + "adminGameInfoList")
    ResponseVO<List<GameInfoVO>> adminGameInfoList(@RequestBody GameInfoRequestVO requestVO);

    @Operation(summary = "游戏管理-修改")
    @PostMapping(PREFIX + "upGameInfo")
    ResponseVO<Boolean> upGameInfo(@RequestBody GameInfoAddOrUpdateVO requestVO);

    @Operation(summary = "总台-游戏管理-状态修改")
    @PostMapping(PREFIX + "upAdminGameInfoStatus")
    ResponseVO<Boolean> upAdminGameInfoStatus(@RequestBody GameClassStatusRequestUpVO requestVO);

    @Operation(summary = "总台-游戏管理-状态修改-批量")
    @PostMapping(PREFIX + "upAdminGameInfoStatusBatch")
    ResponseVO<Boolean> upAdminGameInfoStatusBatch(@RequestBody BatchGameClassStatusRequestUpVO requestVO);



    @Operation(summary = "场馆关联-游戏管理-修改")
    @PostMapping(PREFIX + "siteUpGameInfo")
    ResponseVO<Boolean> siteUpGameInfo(@RequestBody GameInfoAddOrUpdateRequest requestVO);


    @Operation(summary = "游戏管理-新增")
    @PostMapping(PREFIX + "addGameInfo")
    ResponseVO<Boolean> addGameInfo(@RequestBody GameInfoAddOrUpdateVO requestVO);

    @Operation(summary = "查询游戏授权")
    @PostMapping(PREFIX + "queryGameAuthorize")
    ResponseVO<SiteGameResponseVO> queryGameAuthorize(@RequestBody SiteGameRequestVO siteGameRequestVO);

    @Operation(summary = "体育推荐-分页")
    @PostMapping(PREFIX + "sportRecommendPage")
    ResponseVO<Page<SportRecommendVO>> sportRecommendPage(@RequestBody SportRecommendRequestVO requestVO);

    @Operation(summary = "置顶体育推荐")
    @PostMapping(PREFIX + "setPinEvents")
    ResponseVO<Boolean> setPinEvents(@RequestBody UpSportRecommendRequestVO upSportRecommendRequestVO);

    @Operation(summary = "取消置顶体育推荐")
    @PostMapping(PREFIX + "cancelPinEvents")
    ResponseVO<Boolean> cancelPinEvents(@RequestBody UpSportRecommendRequestVO upSportRecommendRequestVO);


    @Operation(summary = "获取SH游戏CODE下拉框配置")
    @PostMapping(PREFIX + "getGameCodeList")
    ResponseVO<List<ShDeskInfoVO>> getGameCodeList(@RequestParam("venueCode") String venueCode);


    @PostMapping("getByIds")
    @Operation(summary = "批量获取总台游戏配置信息")
    ResponseVO<List<GameInfoVO>> getByIds(@RequestBody List<String> gameIds);

    @PostMapping("getHotRemTypeList")
    @Operation(summary = "批量获取总台游戏配置信息")
    ResponseVO<List<HotRemTypeVO>> getHotRemTypeList();

    @PostMapping("getHotGameInfoList")
    @Operation(summary = "热门推荐游戏排序")
    ResponseVO<List<GameInfoHotVO>> getHotGameInfoList(@RequestBody HotRemTypeReqVO hotRemTypeReqVO);

    @PostMapping("upHotGameInfoList")
    @Operation(summary = "批量获取总台游戏配置信息")
    ResponseVO<Boolean> upHotGameInfoList(@RequestBody UpHotRemTypeReqVO reqVO);

    @PostMapping("getGameOneInfoList")
    @Operation(summary = "首页一级分类排序下拉框")
    ResponseVO<List<GameOneClassInfoVO>> getGameOneInfoList();


    @PostMapping("getAllGameOneClassInfoList")
    @Operation(summary = "首页一级分类排序下拉框")
    ResponseVO<List<GameOneClassInfoVO>> getAllGameOneClassInfoList();


    @PostMapping("getGameInfoSortByGameOneId")
    @Operation(summary = "查询一级分类游戏排序游戏列表")
    ResponseVO<List<GameInfoHotVO>> getGameInfoSortByGameOneId(@RequestBody HotRemTypeReqVO hotRemTypeReqVO);


    @Operation(summary = "站点-游戏管理-状态修改")
    @PostMapping(PREFIX + "upSiteGameInfoStatus")
    ResponseVO<Boolean> upSiteGameInfoStatus(@RequestBody GameClassStatusRequestUpVO requestVO);


    @Operation(summary = "总台-游戏管理-状态修改-批量")
    @PostMapping(PREFIX + "upSiteGameInfoStatusBatch")
    ResponseVO<Boolean> upSiteGameInfoStatusBatch(@RequestBody BatchGameClassStatusRequestUpVO requestVO);

    @Operation(summary = "站点-同步总控游戏维护信息")
    @PostMapping(PREFIX + "upSynAdminGameInfoStatus")
    ResponseVO<Boolean> upSynAdminGameInfoStatus(@RequestBody GameClassStatusRequestUpVO requestVO);


    @Operation(summary = "批量新增币种回显")
    @PostMapping(PREFIX + "getAddGameCurrencyInfo")
    ResponseVO<UpGameInfoCurrencyInfoVO> getAddGameCurrencyInfo(@RequestBody AddGameCurrencyInfoVO requestVO);

    @Operation(summary = "批量新增币种回显")
    @PostMapping(PREFIX + "addGameCurrencyInfo")
    ResponseVO<Boolean> addGameCurrencyInfo(@RequestBody AddGameCurrencyInfoVO requestVO);

    @Operation(summary = "批量删除币种回显")
    @PostMapping(PREFIX + "delGameCurrencyInfo")
    ResponseVO<Boolean> delGameCurrencyInfo(@RequestBody AddGameCurrencyInfoVO requestVO);

    @Operation(summary = "获取游戏开启/关闭状态")
    @PostMapping(PREFIX + "getGameInfoByCode")
    ResponseVO<GameInfoVO> getGameInfoByCode(@RequestBody GameInfoValidRequestVO requestVO);

}
