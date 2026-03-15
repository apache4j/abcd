package com.cloud.baowang.play.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.play.api.vo.third.GameInfoPullReqVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.vo.GameNameVO;
import com.cloud.baowang.play.vo.ThirdGameInfoVO;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ThirdPullGameInfoService {

    private Map<String, GameService> pullGameTaskMap;
    private final VenueInfoService venueInfoService;
    private final GameInfoService gameInfoService;
    private final I18nApi i18nApi;

    public void gameInfoPullTask(GameInfoPullReqVO gameInfoPullReqVO) {
        // 平台
        List<String> venues = Lists.newArrayList();
        String param = gameInfoPullReqVO.getParam();
        if (StringUtils.isNotEmpty(param)) {
            String[] venueCodes = param.split(CommonConstant.COMMA);
            venues.addAll(List.of(venueCodes));
        } else {
            venues.addAll(VenueEnum.getVenueCodeList());
        }
        List<VenueInfoPO> list = venueInfoService.list(Wrappers.<VenueInfoPO>lambdaQuery().in(VenueInfoPO::getVenueCode, venues));
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // 平台去重
        Map<Object, Boolean> map = new HashMap<>();
        list = list.stream().filter(i -> map.putIfAbsent(i.getVenueCode(), Boolean.TRUE) == null).collect(Collectors.toList());
        // 游戏信息处理
        for (VenueInfoPO venueInfoPO : list) {
            String venueCode = venueInfoPO.getVenueCode();
            // 检查已存在的游戏
            log.info("当前平台{}",ServiceType.GAME_THIRD_API_SERVICE + venueCode);
            GameService gameService = pullGameTaskMap.get(ServiceType.GAME_THIRD_API_SERVICE + venueCode);
            if(gameService == null){
                log.info("服务{}不存在",ServiceType.GAME_THIRD_API_SERVICE + venueCode);
                continue;
            }
            VenueInfoVO venueInfoVO = BeanUtil.toBean(venueInfoPO, VenueInfoVO.class);
            List<ThirdGameInfoVO> gameInfoVOS = gameService.gameInfo(venueInfoVO);
            if (CollectionUtil.isEmpty(gameInfoVOS)) {
                continue;
            }
            List<String> gameCodes = gameInfoVOS.stream().map(ThirdGameInfoVO::getGameCode).distinct().collect(Collectors.toList());
            List<GameInfoPO> existGame = gameInfoService.list(Wrappers.<GameInfoPO>lambdaQuery().select(GameInfoPO::getAccessParameters).eq(GameInfoPO::getVenueCode, venueCode).in(GameInfoPO::getAccessParameters, gameCodes));
            List<String> existGameCode = existGame.stream().map(GameInfoPO::getAccessParameters).distinct().toList();
            gameInfoVOS.removeIf(e -> existGameCode.contains(e.getGameCode()));
            if (CollectionUtil.isEmpty(gameInfoVOS)) {
                continue;
            }
            // 最大gameId
            int maxGameId = getMaxGameId(venueCode);
            Map<String, List<I18nMsgFrontVO>> i18nMap = Maps.newHashMap();
            List<GameInfoPO> gameInfoPOS = Lists.newArrayList();
            // 组装新增游戏信息
            for (ThirdGameInfoVO gameInfoVO : gameInfoVOS) {
                GameInfoPO gameInfoPO = new GameInfoPO();
                gameInfoPO.setStatus(StatusEnum.CLOSE.getCode());
                gameInfoPO.setAccessParameters(gameInfoVO.getGameCode());
                gameInfoPO.setIsRebate(0);
                gameInfoPO.setGameId(String.format("%05d", ++maxGameId));
                gameInfoPO.setVenueCode(venueInfoPO.getVenueCode());
                gameInfoPO.setVenueId(venueInfoPO.getId());
                gameInfoPO.setVenueName(venueInfoPO.getVenueName());
                gameInfoPO.setVenueType(venueInfoPO.getVenueType());
                String gameNameI18Code = I18MsgKeyEnum.GAME_NAME.getCode() + CommonConstant.UNDERLINE + gameInfoPO.getGameId();
                String gameDescI18Code = I18MsgKeyEnum.GAME_DESC.getCode() + CommonConstant.UNDERLINE + gameInfoPO.getGameId();
                String iconI18Code = I18MsgKeyEnum.GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameInfoPO.getGameId();
                gameInfoPO.setGameI18nCode(gameNameI18Code);
                gameInfoPO.setGameDescI18nCode(gameDescI18Code);
                gameInfoPO.setIconI18nCode(iconI18Code);
                gameInfoPO.setCreator("system");
                gameInfoPO.setUpdater("system");
                gameInfoPO.setSupportDevice("1,2,3,4,5");

                List<GameNameVO> gameName = gameInfoVO.getGameName();
                List<I18nMsgFrontVO> msgFrontVOS = Lists.newArrayList();
                for (GameNameVO gameNameVO : gameName) {
                    if (gameNameVO.getLang().equals(LanguageEnum.ZH_CN.getLang())) {
                        gameInfoPO.setGameName(gameNameVO.getGameName());
                    }
                    I18nMsgFrontVO i18nMsgFrontVO = new I18nMsgFrontVO();
                    i18nMsgFrontVO.setMessageKey(gameNameI18Code);
                    i18nMsgFrontVO.setLanguage(gameNameVO.getLang());
                    i18nMsgFrontVO.setMessage(gameNameVO.getGameName());
                    msgFrontVOS.add(i18nMsgFrontVO);
                }
                I18nMsgBindUtil.bind(i18nMap, gameNameI18Code, msgFrontVOS);
                gameInfoPOS.add(gameInfoPO);
            }

            if (CollectionUtil.isNotEmpty(gameInfoPOS)) {
                gameInfoService.saveBatch(gameInfoPOS);
            }
            log.info("i18nMap:{}",JSONObject.toJSONString(i18nMap));
            if (CollectionUtil.isNotEmpty(i18nMap)) {
                i18nApi.insert(i18nMap);
            }
        }

    }

    private int getMaxGameId(String venueCode) {
        QueryWrapper<GameInfoPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(" max(game_id) as gameId ");
        GameInfoPO gameInfoId = gameInfoService.getOne(queryWrapper);

        int maxGameId = 0;
        if (ObjectUtil.isNotEmpty(gameInfoId)) {
            maxGameId = Integer.parseInt(gameInfoId.getGameId());
        }
        return maxGameId;
    }
}
