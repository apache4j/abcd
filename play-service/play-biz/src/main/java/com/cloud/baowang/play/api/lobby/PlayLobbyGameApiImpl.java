package com.cloud.baowang.play.api.lobby;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayLobbyGameApi;
import com.cloud.baowang.play.api.vo.game.SportLobbyEvents;
import com.cloud.baowang.play.api.vo.lobby.*;
import com.cloud.baowang.play.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Author: sheldon
 * @Date: 3/23/24 1:23 下午
 */
@RestController
@AllArgsConstructor
@Service
@Slf4j
public class PlayLobbyGameApiImpl implements PlayLobbyGameApi {

    private final PlayLobbyGameService playLobbyGameService;

    private final SportEventsService sportEventsService;

    @Override
    public ResponseVO<List<LobbyBannerResVO>> queryBannerList(LobbyBannerReqVO reqVO) {
        return ResponseVO.success(playLobbyGameService.queryBannerList(reqVO));
    }

    @Override
    public ResponseVO<LobbyUnBannerResVO> queryUnBannerList() {
        return ResponseVO.success(playLobbyGameService.queryUnBannerList());
    }

    @Override
    public ResponseVO<List<LobbyGameOneVO>> queryLobbyLabelList() {
        return ResponseVO.success(playLobbyGameService.newQueryLobbyLabelList());
    }

    @Override
    public ResponseVO<BigDecimal> queryGameOnePrizePool(String id) {
        return ResponseVO.success(playLobbyGameService.queryGameOnePrizePool(id));
    }

    @Override
    public ResponseVO<List<LobbyGameTwoDetailVO>> queryGameInfoByOneClassId(LobbyQueryTwoDetailRequestVO requestVO) {
        return ResponseVO.success(playLobbyGameService.newQueryGameInfoByOneClassId(requestVO));
    }


    @Override
    public ResponseVO<Page<LobbyGameInfoVO>> getGameInfoHomeHotSort(PageVO pageVO) {
        return ResponseVO.success(playLobbyGameService.getGameInfoHomeHotSort(pageVO));
    }

    @Override
    public ResponseVO<Page<LobbyGameInfoVO>> getGameInfoByTwoId(LobbyGameDetailRequestVO lobbyGameDetailRequestVO) {
        return ResponseVO.success(playLobbyGameService.getGameInfoByTwoId(lobbyGameDetailRequestVO));
    }

    @Override
    public ResponseVO<Page<LobbyGameInfoVO>> getGameInfoHotByOneId(LobbyGameDetailRequestVO lobbyGameDetailRequestVO) {
        return ResponseVO.success(playLobbyGameService.getGameInfoHotByOneId(lobbyGameDetailRequestVO));
    }

    @Override
    public ResponseVO<Skin4LobbyGameByTwoListVO> queryGameSkin4ByTwoId(Sin4LobbyGameRequestVO requestVO) {
        return ResponseVO.success(playLobbyGameService.queryGameSkin4ByTwoId(requestVO));
    }

    @Override
    public ResponseVO<Page<LobbyGameInfoVO>> queryGameInfoByName(LobbyGameDetailRequestVO lobbyGameDetailRequestVO) {
        return ResponseVO.success(playLobbyGameService.newQueryGameInfoByName(lobbyGameDetailRequestVO));
    }

    @Override
    public ResponseVO<Boolean> collection(LobbyGameCollectionRequestVO requestVO) {
        return ResponseVO.success(playLobbyGameService.userGameCollection(requestVO));
    }

    @Override
    public ResponseVO<Page<LobbyGameInfoVO>> queryCollection(Sin4LobbyGameRequestVO pageVO) {
        return ResponseVO.success(playLobbyGameService.newQueryCollection(pageVO));
    }

    @Override
    public ResponseVO<List<LobbySportEventsVO>> querySportEventsRecommend() {
        return ResponseVO.success(sportEventsService.querySportEventsRecommend());
    }

    @Override
    public ResponseVO<List<LobbyTopGameVO>> queryLobbyTopGame() {
        return ResponseVO.success(playLobbyGameService.newQueryLobbyTopGame());
    }

    @Override
    public ResponseVO<List<LobbyPaymentVendorVO>> queryPartnerList() {
        return ResponseVO.success(playLobbyGameService.queryPartnerList());
    }

    @Override
    public ResponseVO<List<LobbyPaymentVendorVO>> queryPaymentVendorList() {
        return ResponseVO.success(playLobbyGameService.queryPaymentVendorList());
    }

    @Override
    public ResponseVO<LobbyGameInfoVO> queryGameInfoByGameId(LobbyGameInfoByCodeRequestVO requestVO) {
        return ResponseVO.success(playLobbyGameService.queryNewGameInfoByGameId(requestVO));
    }

    @Override
    public ResponseVO<List<String>> getLobbySiteEvents(SportLobbyEvents sportLobbyEvents) {
        return ResponseVO.success(playLobbyGameService.getLobbySiteEvents(sportLobbyEvents));
    }
}
