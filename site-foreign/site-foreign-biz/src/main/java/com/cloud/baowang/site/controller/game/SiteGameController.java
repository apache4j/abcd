package com.cloud.baowang.site.controller.game;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayGameClassInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.system.api.vo.site.SiteGameVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/22/24 1:14 下午
 */


@Tag(name = "游戏配置菜单-游戏管理页面接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/venue_info/api")
public class SiteGameController {

    private final GameInfoApi gameInfoApi;

    private final PlayGameClassInfoApi playGameClassInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    @PostMapping("/gameInfoPage")
    @Operation(summary = "游戏信息-分页")
    public ResponseVO<Page<SiteGameInfoVO>> adminGameInfoPage(@RequestBody GameInfoRequestVO requestVO) {
        if (!StringUtils.isBlank(requestVO.getGameId())) {
            requestVO.setGameNumberId(requestVO.getGameId());
            requestVO.setGameId(null);
        }
        return gameInfoApi.newSiteGameInfoPage(requestVO);
    }


    @PostMapping("/newSiteGameInfoPage")
    @Operation(summary = "游戏信息-分页")
    public ResponseVO<Page<SiteGameInfoVO>> newSiteGameInfoPage(@RequestBody GameInfoRequestVO requestVO) {
        if (!StringUtils.isBlank(requestVO.getGameId())) {
            requestVO.setGameNumberId(requestVO.getGameId());
            requestVO.setGameId(null);
        }
        return gameInfoApi.newSiteGameInfoPage(requestVO);
    }

    @PostMapping("/gameInfoList")
    @Operation(summary = "游戏信息-列表")
    public ResponseVO<List<SiteGameInfoVO>> gameInfoList(@RequestBody GameInfoRequestVO requestVO) {
        return gameInfoApi.siteGameInfoList(requestVO);
    }

    @Operation(summary = "二级分类页面游戏信息-列表")
    @PostMapping("/gameInfoListByTwoId")
    ResponseVO<TwoGameInfoListVO> gameInfoListByTwoId(@Valid @RequestBody GameInfoDelVO request) {
        return playGameClassInfoApi.getGameInfoByTwoId(request);
    }

    @Operation(summary = "新增二级分类游戏列表-游戏币种列表")
    @PostMapping("/gameInfoListByOneId")
    ResponseVO<List<TwoCurrencyGameInfoListVO>> gameInfoListByOneId() {
        return playGameClassInfoApi.gameInfoListByOneId();
    }

    @PostMapping("/upGameInfo")
    @Operation(summary = "游戏信息列表-修改")
    public ResponseVO<Boolean> upGameInfo(@RequestBody GameInfoAddOrUpdateRequest requestVO) {
        return gameInfoApi.siteUpGameInfo(requestVO);
    }

    @PostMapping("/getHotRemTypeList")
    @Operation(summary = "热门推荐类型列表")
    public ResponseVO<List<HotRemTypeVO>> getHotRemTypeList() {
        return gameInfoApi.getHotRemTypeList();
    }

    @PostMapping("/getHotGameInfoList")
    @Operation(summary = "热门游戏推荐列表")
    public ResponseVO<List<GameInfoHotVO>> getHotGameInfoList(@Valid @RequestBody HotRemTypeReqVO reqVO) {
        return gameInfoApi.getHotGameInfoList(reqVO);
    }

    @PostMapping("/setSortHotGame")
    @Operation(summary = "设置热门推荐排序")
    public ResponseVO<Boolean> setSortHotGame(@Valid @RequestBody UpHotRemTypeReqVO reqVO) {
        return gameInfoApi.upHotGameInfoList(reqVO);
    }

    @PostMapping("/getGameOneInfoList")
    @Operation(summary = "一级分类排序下拉框")
    public ResponseVO<List<GameOneClassInfoVO>> getGameOneInfoList() {
        return gameInfoApi.getGameOneInfoList();
    }


    @PostMapping("/getGameOneInfoALLList")
    @Operation(summary = "所有一级分类排序下拉框")
    public ResponseVO<List<GameOneClassInfoVO>> getGameOneInfoALLList() {
        return gameInfoApi.getAllGameOneClassInfoList();
    }


    @PostMapping("/getGameInfoSortByGameOneId")
    @Operation(summary = "查询一级分类游戏排序游戏列表")
    public ResponseVO<List<GameInfoHotVO>> getGameInfoSortByGameOneId(@Valid @RequestBody HotRemTypeReqVO reqVO) {
        return gameInfoApi.getGameInfoSortByGameOneId(reqVO);
    }

    @Operation(summary = "游戏信息-状态修改")
    @PostMapping("/upGameInfoStatus")
    public ResponseVO<Boolean> upSiteGameInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO request) {
        return gameInfoApi.upSiteGameInfoStatus(request);
    }

    @Operation(summary = "批量-游戏信息-状态修改")
    @PostMapping("/upSiteGameInfoStatusBatch")
    public ResponseVO<Boolean> upSiteGameInfoStatusBatch(@Valid @RequestBody BatchGameClassStatusRequestUpVO request) {
        return gameInfoApi.upSiteGameInfoStatusBatch(request);
    }


    @Operation(summary = "同步总控游戏维护信息")
    @PostMapping("/upSynAdminGameInfoStatus")
    public ResponseVO<Boolean> upSynAdminGameInfoStatus(@Valid @RequestBody GameClassStatusRequestUpVO venueInfoAddVO) {
        return gameInfoApi.upSynAdminGameInfoStatus(venueInfoAddVO);
    }


}
