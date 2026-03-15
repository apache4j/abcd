package com.cloud.baowang.play.api.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Author: sheldon
 * @Date: 3/25/24 9:16 上午
 */
@RestController
@AllArgsConstructor
@Service
@Slf4j
public class GameInfoApiImpl implements GameInfoApi {

    private final AdminGameInfoService adminGameInfoService;

    private final SportEventsService sportEventsService;

    private final SiteGameService siteGameService;

    private final GameOneClassInfoService gameOneClassInfoService;
    private final GameInfoService gameInfoService;


    @Override
    public ResponseVO<Page<SiteGameInfoVO>> newSiteGameInfoPage(GameInfoRequestVO requestVO) {
        return ResponseVO.success(adminGameInfoService.newSiteGameInfoPage(requestVO));
    }

    @Override
    public ResponseVO<List<SiteGameInfoVO>> siteGameInfoList(GameInfoRequestVO requestVO) {
        requestVO.setPageSize(-1);
//        return ResponseVO.success(adminGameInfoService.siteGameInfoPage(requestVO).getRecords());
        return ResponseVO.success(adminGameInfoService.newSiteGameInfoPage(requestVO).getRecords());
    }

    @Override
    public ResponseVO<List<SiteGameInfoVO>> getConfigSiteGameInfoList(GameInfoRequestVO vo) {
        return ResponseVO.success(adminGameInfoService.getConfigSiteGameInfoList(vo));
    }

    @Override
    public ResponseVO<Page<GameInfoVO>> adminGameInfoPage(GameInfoRequestVO requestVO) {
        return ResponseVO.success(adminGameInfoService.adminGameInfoPage(requestVO));
    }

    @Override
    public ResponseVO<List<GameInfoVO>> adminGameInfoList(GameInfoRequestVO requestVO) {
        requestVO.setPageSize(-1);
        return ResponseVO.success(adminGameInfoService.adminGameInfoPage(requestVO).getRecords());
    }

    @Override
    public ResponseVO<Boolean> upGameInfo(GameInfoAddOrUpdateVO requestVO) {
        Boolean result = adminGameInfoService.upGameInfo(requestVO);
        return ResponseVO.success(result);
    }

    public ResponseVO<Boolean> upAdminGameInfoStatus(GameClassStatusRequestUpVO requestVO) {
        return ResponseVO.success(adminGameInfoService.upAdminGameInfoStatus(requestVO));
    }

    @Override
    public ResponseVO<Boolean> upAdminGameInfoStatusBatch(BatchGameClassStatusRequestUpVO requestVO) {
//        return ResponseVO.success(adminGameInfoService.upAdminGameInfoStatusBatch(requestVO));
        return ResponseVO.success(adminGameInfoService.newUpAdminGameInfoStatusBatch(requestVO));
    }

    @Override
    public ResponseVO<Boolean> siteUpGameInfo(GameInfoAddOrUpdateRequest requestVO) {
        return ResponseVO.success(siteGameService.siteUpGameInfo(requestVO));
    }


    @Override
    public ResponseVO<Boolean> addGameInfo(GameInfoAddOrUpdateVO requestVO) {
        return ResponseVO.success(adminGameInfoService.addGameInfo(requestVO));
    }

    @Override
    public ResponseVO<SiteGameResponseVO> queryGameAuthorize(SiteGameRequestVO siteGameRequestVO) {
        return ResponseVO.success(adminGameInfoService.queryGameAuthorize(siteGameRequestVO));
    }

    @Override
    public ResponseVO<Page<SportRecommendVO>> sportRecommendPage(SportRecommendRequestVO requestVO) {
//        return ResponseVO.success(sportEventsService.sportRecommendPage(requestVO));
        return ResponseVO.success(sportEventsService.getNewSportRecommendPage(requestVO));
    }

    @Override
    public ResponseVO<Boolean> setPinEvents(UpSportRecommendRequestVO upSportRecommendRequestVO) {
//        return ResponseVO.success(sportEventsService.setPinEvents(upSportRecommendRequestVO));
        return ResponseVO.success(sportEventsService.setNewPinEvents(upSportRecommendRequestVO));
    }

    @Override
    public ResponseVO<Boolean> cancelPinEvents(UpSportRecommendRequestVO upSportRecommendRequestVO) {
//        return ResponseVO.success(sportEventsService.cancelPinEvents(upSportRecommendRequestVO));
        return ResponseVO.success(sportEventsService.cancelNewPinEvents(upSportRecommendRequestVO));
    }

    @Override
    public ResponseVO<List<ShDeskInfoVO>> getGameCodeList(String venueCode) {
//        return ResponseVO.success(adminGameInfoService.getGameCodeList(venueCode));
        return ResponseVO.success(adminGameInfoService.getNewGameCodeList(venueCode));
    }

    @Override
    public ResponseVO<List<GameInfoVO>> getByIds(List<String> gameIds) {
        return adminGameInfoService.getByIds(gameIds);
    }

    @Override
    public ResponseVO<List<HotRemTypeVO>> getHotRemTypeList() {
        return ResponseVO.success(adminGameInfoService.getHotRemTypeList());
    }

    @Override
    public ResponseVO<List<GameInfoHotVO>> getHotGameInfoList(HotRemTypeReqVO hotRemTypeReqVO) {
        return ResponseVO.success(adminGameInfoService.getNewHotGameInfoList(hotRemTypeReqVO));
    }

    @Override
    public ResponseVO<Boolean> upHotGameInfoList(UpHotRemTypeReqVO reqVO) {
        return ResponseVO.success(adminGameInfoService.upNewHotGameInfoList(reqVO));
    }

    @Override
    public ResponseVO<List<GameOneClassInfoVO>> getGameOneInfoList() {
        List<GameOneClassInfoVO> list = gameOneClassInfoService.getAllGameOneClassInfoList();
        return ResponseVO.success(list.stream()
                .map(x -> {
                    if (x.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())
                            || x.getModel().equals(GameOneModelEnum.SBA.getCode())) {
                        return null;
                    }
                    GameOneClassInfoVO vo = GameOneClassInfoVO.builder().build();
                    BeanUtils.copyProperties(x, vo);
                    return vo;
                })
                .filter(Objects::nonNull)
                .toList());
    }

    @Override
    public ResponseVO<List<GameOneClassInfoVO>> getAllGameOneClassInfoList() {
        return ResponseVO.success(gameOneClassInfoService.getAllGameOneClassInfoList());
    }

    @Override
    public ResponseVO<List<GameInfoHotVO>> getGameInfoSortByGameOneId(HotRemTypeReqVO hotRemTypeReqVO) {
        return ResponseVO.success(adminGameInfoService.getNewGameInfoSortByGameOneId(hotRemTypeReqVO));
    }

    @Override
    public ResponseVO<Boolean> upSiteGameInfoStatus(GameClassStatusRequestUpVO requestVO) {
        return ResponseVO.success(siteGameService.upSiteGameInfoStatus(requestVO));
    }

    @Override
    public ResponseVO<Boolean> upSiteGameInfoStatusBatch(BatchGameClassStatusRequestUpVO requestVO) {
//        return ResponseVO.success(siteGameService.upGameInfoStatusBatch(requestVO));
        return ResponseVO.success(siteGameService.newUpGameInfoStatusBatch(requestVO));
    }

    @Override
    public ResponseVO<Boolean> upSynAdminGameInfoStatus(GameClassStatusRequestUpVO requestVO) {
        return ResponseVO.success(siteGameService.upSynAdminGameInfoStatus(requestVO));
    }

    @Override
    public ResponseVO<UpGameInfoCurrencyInfoVO> getAddGameCurrencyInfo(AddGameCurrencyInfoVO requestVO) {
        return ResponseVO.success(adminGameInfoService.getAddGameCurrencyInfo(requestVO));
    }

    @Override
    public ResponseVO<Boolean> addGameCurrencyInfo(AddGameCurrencyInfoVO requestVO) {
        return ResponseVO.success(adminGameInfoService.addGameCurrencyInfo(requestVO));
    }

    @Override
    public ResponseVO<Boolean> delGameCurrencyInfo(AddGameCurrencyInfoVO requestVO) {
        return ResponseVO.success(adminGameInfoService.delGameCurrencyInfo(requestVO));
    }

    @Override
    public ResponseVO<GameInfoVO> getGameInfoByCode(GameInfoValidRequestVO requestVO) {
        GameInfoPO gameInfoPO = gameInfoService.getGameInfoByCode(requestVO.getSiteCode(), requestVO.getGameId(), requestVO.getVenueCode());
        GameInfoVO result = GameInfoVO.builder().build();
        if (result == null) {
            return ResponseVO.fail(ResultCode.NO_HAVE_DATA);
        }
        BeanUtils.copyProperties(gameInfoPO,result);
        return ResponseVO.success(result);
    }


}
