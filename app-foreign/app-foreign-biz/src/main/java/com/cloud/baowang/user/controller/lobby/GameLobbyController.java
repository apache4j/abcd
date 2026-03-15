package com.cloud.baowang.user.controller.lobby;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.vo.LobbyLabelActivitySwitchResVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayLobbyGameApi;
import com.cloud.baowang.play.api.vo.game.SportLobbyEvents;
import com.cloud.baowang.play.api.vo.lobby.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/29/24 5:21 下午
 */

@Tag(name = "游戏大厅接口列表")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/game_lobby/api")
public class GameLobbyController {

    private final PlayLobbyGameApi playLobbyGameApi;

    private final ActivityBaseApi activityBaseApi;


    @Operation(summary = "轮播图")
    @PostMapping("/queryBannerList")
    public ResponseVO<List<LobbyBannerResVO>> queryBannerList(@Valid @RequestBody LobbyBannerReqVO reqVO) {
        return playLobbyGameApi.queryBannerList(reqVO);
    }

    @Operation(summary = "未登陆-轮播图")
    @PostMapping("/queryUnBannerList")
    public ResponseVO<LobbyUnBannerResVO> queryUnBannerList() {
        return playLobbyGameApi.queryUnBannerList();
    }

    @Operation(summary = "侧边栏")
    @PostMapping("/queryLobbyLabelList")
    public ResponseVO<List<LobbyGameOneVO>> queryLobbyLabelList() {
        return playLobbyGameApi.queryLobbyLabelList();
    }

    @Operation(summary = "一级分类的奖池")
    @PostMapping("/queryGameOnePrizePool")
    public ResponseVO<BigDecimal> queryGameOnePrizePool(@Valid @RequestBody LobbyBannerReqVO reqVO) {
        return playLobbyGameApi.queryGameOnePrizePool(reqVO.getGameOneClassId());
    }

    @Operation(summary = "活动入口开关")
    @PostMapping("/queryLobbyLabelActivitySwitch")
    public ResponseVO<LobbyLabelActivitySwitchResVO> queryLobbyLabelActivitySwitch() {
        return activityBaseApi.queryActivityListSwitch(CurrReqUtils.getSiteCode());
    }

    @Operation(summary = "查二级分类详情")
    @PostMapping("/queryGameInfoByOneClassId")
    public ResponseVO<List<LobbyGameTwoDetailVO>> queryGameInfoByOneClassId(@Valid @RequestBody LobbyQueryTwoDetailRequestVO requestVO) {
        return playLobbyGameApi.queryGameInfoByOneClassId(requestVO);
    }

    @Operation(summary = "获取体育推荐赛事")
    @PostMapping("/querySportEventsRecommend")
    public ResponseVO<List<LobbySportEventsVO>> querySportEventsRecommend() {
        return playLobbyGameApi.querySportEventsRecommend();
    }

    @Operation(summary = "获取游戏大厅热门游戏")
    @PostMapping("/queryLobbyTopGame")
    public ResponseVO<List<LobbyTopGameVO>> queryLobbyTopGame() {
        long start = System.currentTimeMillis();
        ResponseVO<List<LobbyTopGameVO>> responseVO = playLobbyGameApi.queryLobbyTopGame();
        long end = System.currentTimeMillis();
        log.info("执行时间:{}", end - start);
        return responseVO;
    }

    @Operation(summary = "皮肤4-根据二级分类查询游戏详情数据-分页")
    @PostMapping("/queryGameSkin4ByTwoBy")
    public ResponseVO<Skin4LobbyGameByTwoListVO> queryGameSkin4ByTwoId(@Valid @RequestBody Sin4LobbyGameRequestVO requestVO) {
        return playLobbyGameApi.queryGameSkin4ByTwoId(requestVO);
    }


    @Operation(summary = "查询游戏详情数据-分页")
    @PostMapping("/queryGameInfoDetail")
    public ResponseVO<Page<LobbyGameInfoVO>> queryGameInfoDetail(@RequestBody LobbyGameDetailRequestVO requestVO) {
        //只传了热门标签 没有传一二级分类代表是查询首页热门游戏
        if(ObjectUtil.isNotEmpty(requestVO.getLabel()) && requestVO.getLabel() == 1 &&
                ObjectUtil.isEmpty(requestVO.getGameOneId()) && ObjectUtil.isEmpty(requestVO.getGameTwoId())){
            return playLobbyGameApi.getGameInfoHomeHotSort(requestVO);
        }else if(ObjectUtil.isEmpty(requestVO.getLabel()) && ObjectUtil.isNotEmpty(requestVO.getGameTwoId())){
            return playLobbyGameApi.getGameInfoByTwoId(requestVO);//传了二级分类没有热门标签
        }else if(ObjectUtil.isNotEmpty(requestVO.getLabel()) && ObjectUtil.isNotEmpty(requestVO.getGameOneId())){
            return playLobbyGameApi.getGameInfoHotByOneId(requestVO);//传了一级分类跟热门标签
        }else if(ObjectUtil.isNotEmpty(requestVO.getLabel()) && ObjectUtil.isNotEmpty(requestVO.getGameTwoId())){
            return playLobbyGameApi.getGameInfoHotByOneId(requestVO);//传了二级分类跟热门标签
        }
        return null;
    }

    @Operation(summary = "游戏ID查询游戏详情数据")
    @PostMapping("/queryGameInfoByGameId")
    public ResponseVO<LobbyGameInfoVO> queryGameInfoByGameId(@Valid @RequestBody LobbyGameInfoByCodeRequestVO requestVO) {
        return playLobbyGameApi.queryGameInfoByGameId(requestVO);
    }


    @Operation(summary = "模糊查询多语言-游戏数据")
    @PostMapping("/queryGameInfoByName")
    public ResponseVO<Page<LobbyGameInfoVO>> queryGameInfoByName(@Valid @RequestBody LobbyGameNameRequestVO requestVO) {
        LobbyGameDetailRequestVO lobbyGameDetailRequest = LobbyGameDetailRequestVO.builder().build();
        BeanUtils.copyProperties(requestVO, lobbyGameDetailRequest);
        lobbyGameDetailRequest.setGameNameCode(requestVO.getGameName());
        return playLobbyGameApi.queryGameInfoByName(lobbyGameDetailRequest);
    }


    @Operation(summary = "获取站点赞助商列表")
    @PostMapping("/queryPartnerList")
    public ResponseVO<List<LobbyPaymentVendorVO>> queryPartnerList() {
        return playLobbyGameApi.queryPartnerList();
    }


    @Operation(summary = "获取支付商列表")
    @PostMapping("/queryPaymentVendorList")
    public ResponseVO<List<LobbyPaymentVendorVO>> queryPaymentVendorList() {
        return playLobbyGameApi.queryPaymentVendorList();
    }


    @Operation(summary = "获取指定球类的前50个体育联赛")
    @PostMapping("/getLobbySiteEvents")
    public ResponseVO<List<String>> getLobbySiteEvents(@Valid @RequestBody SportLobbyEvents sportLobbyEvents) {
        return playLobbyGameApi.getLobbySiteEvents(sportLobbyEvents);
    }


}
