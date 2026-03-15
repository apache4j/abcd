package com.cloud.baowang.play.api.venue;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayGameClassInfoApi;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.po.SiteVenuePO;
import com.cloud.baowang.play.service.*;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: sheldon
 * @Date: 3/23/24 1:23 下午
 */
@RestController
@AllArgsConstructor
@Service
@Slf4j
public class GameClassInfoApiImpl implements PlayGameClassInfoApi {

    private final GameOneClassInfoService gameClassInfoService;

    private final GameTwoClassInfoService gameTwoClassInfoService;

    private final SiteVenueService siteVenueService;

    private final VenueInfoService venueInfoService;

    private final GameOneFloatConfigService gameOneFloatConfigService;


    @Override
    public ResponseVO<Boolean> initGameOneClassInfo(String siteCode) {
        return ResponseVO.success(gameClassInfoService.initGameOneClassInfo(siteCode));
    }

    @Override
    public ResponseVO<Boolean> addGameOneClassInfo(GameClassInfoAddRequest requestVO) {
        return ResponseVO.success(gameClassInfoService.addGameOneClassInfo(requestVO));
    }

    public ResponseVO<Page<GameOneClassInfoVO>> getGameOneClassInfoPage(GameClassInfoRequestVO requestVO) {
        return ResponseVO.success(gameClassInfoService.getGameOneClassInfoPage(requestVO));
    }

    public ResponseVO<List<GameOneClassInfoVO>> getGameOneClassInfoList(GameSortRequestVO requestVO) {
        List<GameOneClassInfoVO> list = gameClassInfoService.getAllGameOneClassInfoList();
        if (CollectionUtil.isEmpty(list)) {
            return ResponseVO.success(list);
        }
        return ResponseVO.success(gameClassInfoService.getSiteToSetGameOneSort(list, requestVO));
    }

    @Override
    public ResponseVO<List<GameOneClassInfoVO>> getSortGameOneClassInfoList(GameSortRequestVO requestVO) {
        List<GameOneClassInfoVO> list = gameClassInfoService.getAllGameOneClassInfoList();
        if (CollectionUtil.isEmpty(list)) {
            return ResponseVO.success(list);
        }

        List<SiteVenuePO> siteVenuePOList = siteVenueService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteVenuePO.class)
                .eq(SiteVenuePO::getSiteCode, CurrReqUtils.getSiteCode()));

        List<String> siteVenueCodeList = siteVenuePOList.stream().map(SiteVenuePO::getVenueCode).toList();

        List<VenueInfoVO> venueInfoVOList = venueInfoService.getSiteVenueInfoList();

        Map<String,VenueInfoVO> venueMap = venueInfoVOList.stream().collect(Collectors.toMap(VenueInfoVO::getVenueCode, Function.identity()));

        List<GameOneClassInfoVO> result = Lists.newArrayList();
        for (GameOneClassInfoVO item : list){
            String model = item.getModel();

            if (GameOneModelEnum.SIGN_VENUE.getCode().equals(model)) {
                List<GameOneClassVenueInfoVO> lobbySignVenueCode = item.getLobbySignVenueCode();

                if(CollectionUtil.isEmpty(lobbySignVenueCode)){
                    continue;
                }

                List<GameOneClassVenueInfoVO> newLobbySignVenueCode = Lists.newArrayList();

                for (GameOneClassVenueInfoVO venueInfoVO  : lobbySignVenueCode) {
                    //如果是单场馆一级分类 则必须这个站有这个场馆的权限
                    if (!siteVenueCodeList.contains(venueInfoVO.getVenueCode())) {
                        continue;
                    }

                    VenueInfoVO vo = venueMap.get(venueInfoVO.getVenueCode());//必须有场馆配置
                    if (vo == null) {
                        continue;
                    }

                    if (CollectionUtil.isEmpty(vo.getCurrencyCodeList()) || !vo.getCurrencyCodeList().contains(requestVO.getCurrencyCode())) {//场馆必须有这个币种权限
                        continue;
                    }

                    //单场馆必须配置了这个币种
                    if(!venueInfoVO.getCurrencyCode().equals(requestVO.getCurrencyCode())){
                        continue;
                    }
                    newLobbySignVenueCode.add(venueInfoVO);
                }

                if(CollectionUtil.isEmpty(newLobbySignVenueCode)){
                    continue;
                }
                item.setLobbySignVenueCode(newLobbySignVenueCode);
                result.add(item);
                continue;
            }



            result.add(item);
        }

        return ResponseVO.success(gameClassInfoService.getSiteToSetGameOneSort(result, requestVO));
    }

    @Override
    public ResponseVO<List<GameOneClassInfoVO>> getAllGameOneClassInfoList() {
        return ResponseVO.success(gameClassInfoService.getAllGameOneClassInfoList());
    }

    public ResponseVO<Boolean> setSortGameOneClassInfo(GameClassInfoSetSortListVO requestVO) {
        return ResponseVO.success(gameClassInfoService.setSortGameOneClassInfo(requestVO));
    }

    public ResponseVO<Boolean> upGameOneClassInfo(GameOneClassInfoUpVO requestVO) {
        return ResponseVO.success(gameClassInfoService.upGameOneClassInfo(requestVO));
    }

    @Override
    public ResponseVO<Boolean> upGameOneClassInfoStatus(GameClassStatusRequestUpVO request) {
        return ResponseVO.success(gameClassInfoService.upGameOneClassInfoStatus(request));
    }

    public ResponseVO<Boolean> delGameOneClassInfo(String id) {
        return ResponseVO.success(gameClassInfoService.delGameOneClassInfoById(id));
    }

    public ResponseVO<Boolean> addGameTwoClassInfo(GameTwoClassAddVO requestVO) {
        return ResponseVO.success(gameTwoClassInfoService.addAdminGameTwoClassInfo(requestVO));
    }

    public ResponseVO<Boolean> setSortGameTwoClassInfo(GameTwoSortReqVO requestVO) {
        return ResponseVO.success(gameTwoClassInfoService.setAdminSortGameTwoClassInfo(requestVO));
    }


    public ResponseVO<Page<GameTwoClassInfoVO>> getGameTwoClassInfoPage(GameClassTwoRequestVO requestVO) {
        return ResponseVO.success(gameTwoClassInfoService.getGameTwoClassInfoPage(requestVO));
    }


    public ResponseVO<List<GameTwoClassInfoVO>> getGameTwoClassInfoList(GameClassTwoRequestVO requestVO) {
        requestVO.setStatus(StatusEnum.OPEN.getCode());
        return ResponseVO.success(gameTwoClassInfoService.getGameTwoClassInfoList(requestVO));
    }

    public ResponseVO<Boolean> upGameTwoClassInfo(GameTwoClassUpVO requestVO) {
        return ResponseVO.success(gameTwoClassInfoService.updateGameTwoClassInfo(requestVO));
    }

    @Override
    public ResponseVO<Boolean> upGameTwoClassInfoStatus(GameClassStatusRequestUpVO requestVO) {
        return ResponseVO.success(gameTwoClassInfoService.updateAdminGameTwoClassInfoStatus(requestVO));
    }

    public ResponseVO<Boolean> delGameTwoClassInfoById(String id) {
        return ResponseVO.success(gameTwoClassInfoService.deleteAdminGameTwoClassInfoById(id));
    }

    @Override
    public ResponseVO<TwoGameInfoListVO> getGameInfoByTwoId(GameInfoDelVO request) {
        return ResponseVO.success(gameTwoClassInfoService.getNewAdminGameInfoByTwoId(request));
    }

    @Override
    public ResponseVO<List<TwoCurrencyGameInfoListVO>> gameInfoListByOneId() {
        return ResponseVO.success(gameTwoClassInfoService.gameInfoListByOneId());
    }

    @Override
    public ResponseVO<Boolean> addFloatConfig(GameOneFloatConfigAddReqVO requestVO) {
        return ResponseVO.success(gameOneFloatConfigService.addFloatConfig(requestVO));
    }

    @Override
    public ResponseVO<List<GameOneFloatConfigByGameOneIdVO>> getFloatConfigByGameOneId(String gameOneId) {
        return ResponseVO.success(gameOneFloatConfigService.getFloatConfigByGameOneId(gameOneId));
    }

    @Override
    public ResponseVO<Page<GameOneFloatConfigVO>> getFloatConfigPage(GameOneFloatConfigAddReqVO requestVO) {
        return ResponseVO.success(gameOneFloatConfigService.getFloatConfigPage(requestVO));
    }

    @Override
    public ResponseVO<Boolean> upFloatConfigStatus(GameClassStatusRequestUpVO requestVO) {
        return ResponseVO.success(gameOneFloatConfigService.upFloatConfigStatus(requestVO));
    }

    @Override
    public ResponseVO<Boolean> upFloatConfig(GameOneFloatConfigUpReqVO requestVO) {
        return ResponseVO.success(gameOneFloatConfigService.upFloatConfig(requestVO));
    }

    @Override
    public ResponseVO<Boolean> deFloatConfig(String id) {
        return ResponseVO.success(gameOneFloatConfigService.deFloatConfig(id));
    }
}
