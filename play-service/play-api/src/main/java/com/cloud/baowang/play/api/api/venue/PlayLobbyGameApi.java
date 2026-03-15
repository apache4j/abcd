package com.cloud.baowang.play.api.api.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.game.SportLobbyEvents;
import com.cloud.baowang.play.api.vo.lobby.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * play-service服务
 */
@Tag(name = "客户端游戏大厅接口")
@FeignClient(contextId = "playLobbyFeiGameApi", value = ApiConstants.NAME)
public interface PlayLobbyGameApi {

    String PREFIX = ApiConstants.PREFIX + "/lobby_game/api/";

    @Operation(summary = "轮播图")
    @PostMapping(PREFIX + "queryBannerList")
    ResponseVO<List<LobbyBannerResVO>> queryBannerList(@RequestBody LobbyBannerReqVO lobbyBannerReqVO);

    @Operation(summary = "未登陆-轮播图")
    @PostMapping(PREFIX + "queryUnBannerList")
    ResponseVO<LobbyUnBannerResVO> queryUnBannerList();

    @Operation(summary = "获取游戏大厅标签列表")
    @PostMapping(PREFIX + "queryLobbyLabelList")
    ResponseVO<List<LobbyGameOneVO>> queryLobbyLabelList();

    @Operation(summary = "一级分类的奖池")
    @PostMapping(PREFIX + "queryGameOnePrizePool")
    ResponseVO<BigDecimal> queryGameOnePrizePool(@RequestParam("id") String id);

    @Operation(summary = "根据游戏一级标签查询出游戏:该接口只针对赌场类型,当点击赌场或者赌场的大厅标签时渲染页面的接口")
    @PostMapping(PREFIX + "queryGameInfoByOneClassId")
    ResponseVO<List<LobbyGameTwoDetailVO>> queryGameInfoByOneClassId(@RequestBody LobbyQueryTwoDetailRequestVO requestVO);

    @Operation(summary = "首页热门-游戏数据")
    @PostMapping(PREFIX + "getGameInfoHomeHotSort")
    ResponseVO<Page<LobbyGameInfoVO>> getGameInfoHomeHotSort(@RequestBody PageVO pageVO);


    @Operation(summary = "首页-二级分类查询游戏")
    @PostMapping(PREFIX + "getGameInfoByTwoId")
    ResponseVO<Page<LobbyGameInfoVO>> getGameInfoByTwoId(@RequestBody LobbyGameDetailRequestVO lobbyGameDetailRequestVO);

    @Operation(summary = "首页-一级分类热门游戏")
    @PostMapping(PREFIX + "getGameInfoHotByOneId")
    ResponseVO<Page<LobbyGameInfoVO>> getGameInfoHotByOneId(@RequestBody LobbyGameDetailRequestVO lobbyGameDetailRequestVO);

    @Operation(summary = "首页-一级分类热门游戏")
    @PostMapping(PREFIX + "queryGameSkin4ByTwoId")
    ResponseVO<Skin4LobbyGameByTwoListVO> queryGameSkin4ByTwoId(@RequestBody Sin4LobbyGameRequestVO requestVO);


    @Operation(summary = "查询游戏数据-名称查询")
    @PostMapping(PREFIX + "queryGameInfoByName")
    ResponseVO<Page<LobbyGameInfoVO>> queryGameInfoByName(@RequestBody LobbyGameDetailRequestVO lobbyGameDetailRequestVO);



    @Operation(summary = "新增游戏收藏")
    @PostMapping(PREFIX + "collection")
    ResponseVO<Boolean> collection(@RequestBody LobbyGameCollectionRequestVO requestVO);


    @Operation(summary = "查询收藏游戏")
    @PostMapping(PREFIX + "queryCollection")
    ResponseVO<Page<LobbyGameInfoVO>> queryCollection(@RequestBody Sin4LobbyGameRequestVO pageVO);

    @Operation(summary = "获取体育推荐赛事")
    @PostMapping(PREFIX + "querySportEventsRecommend")
    ResponseVO<List<LobbySportEventsVO>> querySportEventsRecommend();

    @Operation(summary = "获取游戏大厅热门游戏")
    @PostMapping(PREFIX + "queryLobbyTopGame")
    ResponseVO<List<LobbyTopGameVO>> queryLobbyTopGame();

    @Operation(summary = "获取站点赞助商列表")
    @PostMapping(PREFIX + "queryPartnerList")
    ResponseVO<List<LobbyPaymentVendorVO>> queryPartnerList();

    @Operation(summary = "获取游戏大厅-支付商")
    @PostMapping(PREFIX + "queryPaymentVendorList")
    ResponseVO<List<LobbyPaymentVendorVO>> queryPaymentVendorList();


    @Operation(summary = "查询游戏数据")
    @PostMapping(PREFIX + "queryGameInfoByGameId")
    ResponseVO<LobbyGameInfoVO> queryGameInfoByGameId(@RequestBody LobbyGameInfoByCodeRequestVO requestVO);

    @Operation(summary = "获取前50个体育联赛")
    @PostMapping(PREFIX + "getLobbySiteEvents")
    ResponseVO<List<String>> getLobbySiteEvents(@RequestBody SportLobbyEvents events);


}
