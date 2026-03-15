package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.SiteGamePO;
import com.cloud.baowang.play.repositories.GameInfoRepository;
import com.cloud.baowang.wallet.api.api.UserActivityTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.activity.UserActivityTypingAmountResp;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class GameInfoService extends ServiceImpl<GameInfoRepository, GameInfoPO> {

    private final GameInfoRepository gameInfoRepository;

    private final SiteGameService siteGameService;

    private final UserActivityTypingAmountApi userActivityTypingAmountApi;

    public List<GameInfoPO> getGameInfoList() {
        List<GameInfoPO> setList = RedisUtil.getValue(RedisConstants.KEY_GAME_INFO_LIST);

        if (CollectionUtil.isNotEmpty(setList)) {
            return new ArrayList<>(setList);
        }

        List<GameInfoPO> list = gameInfoRepository.selectList(null);
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        RedisUtil.setValue(RedisConstants.KEY_GAME_INFO_LIST, list, 10L, TimeUnit.MINUTES);
        return list;
    }


    //不走缓存只能管理后台调用
    public List<GameInfoPO> getDBSiteGameInfoList(String siteCode) {
        List<GameInfoPO> resultList = Lists.newArrayList();

        List<GameInfoPO> allList = gameInfoRepository.selectList(null);
        Map<String, GameInfoPO> gameMap = allList.stream().collect(Collectors.toMap(GameInfoPO::getId, Function.identity()));

        List<SiteGamePO> siteGameList = siteGameService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteGamePO.class).eq(SiteGamePO::getSiteCode, siteCode));

        siteGameList.forEach(x -> {
            GameInfoPO gameInfoPO = gameMap.get(x.getGameInfoId());
            //状态按照站点的游戏配置状态,游戏配置信息按总台的
            if (ObjectUtil.isNotEmpty(gameInfoPO)) {
                gameInfoPO.setStatus(x.getStatus());
                gameInfoPO.setMaintenanceStartTime(x.getMaintenanceStartTime());
                gameInfoPO.setMaintenanceEndTime(x.getMaintenanceEndTime());
                gameInfoPO.setRemark(x.getRemark());
                resultList.add(gameInfoPO);
            }
        });
        return resultList;
    }



    /**
     * 根据站点获取到站点到游戏详情信息
     */
    public List<GameInfoPO> getSiteGameInfoList(String siteCode) {
        List<GameInfoPO> resultList = Lists.newArrayList();

        List<GameInfoPO> allList = getGameInfoList();

        Map<String, GameInfoPO> gameMap = allList.stream().collect(Collectors.toMap(GameInfoPO::getId, Function.identity()));

        List<SiteGamePO> siteGameList = siteGameService.getSiteGameIdsBySiteCodeList(siteCode);

        siteGameList.forEach(x -> {
            GameInfoPO gameInfoPO = gameMap.get(x.getGameInfoId());
            //状态按照站点的游戏配置状态,游戏配置信息按总台的
            if (ObjectUtil.isNotEmpty(gameInfoPO)) {
                gameInfoPO.setStatus(x.getStatus());
                gameInfoPO.setMaintenanceStartTime(x.getMaintenanceStartTime());
                gameInfoPO.setMaintenanceEndTime(x.getMaintenanceEndTime());
                gameInfoPO.setRemark(x.getRemark());
                resultList.add(gameInfoPO);
            }
        });
        return resultList;
    }


    /**
     * 根据场馆查询游戏,不分站点
     *
     * @param venueCode 场馆
     * @return 游戏
     */
    public List<GameInfoPO> queryGameByVenueCode(String venueCode) {
        if (ObjectUtil.isEmpty(venueCode)) {
            return Lists.newArrayList();
        }
        List<GameInfoPO> list = getGameInfoList();

        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.stream()
                .filter(item -> venueCode.equals(item.getVenueCode()))
                .toList();
    }


    /**
     * 查询当前站点的gameCode与venueCode
     */
    public GameInfoPO getGameInfoByCode(String gameCode, String venueCode) {
        return getGameInfoByCode(CurrReqUtils.getSiteCode(), gameCode, venueCode);
    }

    /**
     * 查询当前站点的gameCode与venueCode
     */
    public GameInfoPO getGameInfoByCode(String siteCode, String gameCode, String venueCode) {

        if (ObjectUtil.isEmpty(gameCode) || ObjectUtil.isEmpty(venueCode)) {
            return null;
        }
        List<GameInfoPO> list = getSiteGameInfoList(siteCode);

        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        list = list.stream()
                .filter(item -> gameCode.equals(item.getAccessParameters()) && venueCode.equals(item.getVenueCode()))
                .toList();

        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }


    public List<GameInfoVO> queryGameAccessParamListByVenueCode(List<String> list) {
        List<GameInfoPO> gameInfoPOList = getGameInfoList();
        if (CollectionUtil.isEmpty(gameInfoPOList)) {
            return Lists.newArrayList();
        }

        gameInfoPOList = gameInfoPOList.stream().filter(x -> list.contains(x.getVenueCode())).toList();
        if (CollectionUtil.isEmpty(gameInfoPOList)) {
            return Lists.newArrayList();
        }

        return gameInfoPOList.stream().map(x -> {
            GameInfoVO vo = GameInfoVO.builder().build();
            BeanUtils.copyProperties(x, vo);
            return vo;
        }).toList();
    }

    public BigDecimal checkJoinActivity(CheckActivityVO checkActivityVO) {
        BigDecimal amount = BigDecimal.ZERO;
        if (StringUtils.isNotEmpty(checkActivityVO.getUserId()) && StringUtils.isNotEmpty(checkActivityVO.getVenueCode())) {
            UserInfoVO infoVO = UserInfoVO.builder().build();
            infoVO.setUserId(checkActivityVO.getUserId());
            infoVO.setSiteCode(checkActivityVO.getSiteCode());
            UserActivityTypingAmountResp userActivityTypingLimit = userActivityTypingAmountApi.getUserActivityTypingLimit(ConvertUtil.entityToModel(infoVO, WalletUserInfoVO.class));
            //Integer code = Objects.nonNull(VenueEnum.nameOfCode(checkActivityVO.getVenueCode()).getType()) ? VenueEnum.nameOfCode(checkActivityVO.getVenueCode()).getType().getCode() : null;
            VenueEnum venueEnum = VenueEnum.nameOfCode(checkActivityVO.getVenueCode());
            Integer code = null;
            if (venueEnum != null && venueEnum.getType() != null) {
                code = venueEnum.getType().getCode();
            }
            String limitCode = Objects.isNull(code) ? "" : code.toString();
            if (Objects.nonNull(userActivityTypingLimit) && Objects.nonNull(userActivityTypingLimit.getTypingAmount())
                    && userActivityTypingLimit.getTypingAmount().compareTo(BigDecimal.ZERO) == 1
                    && !userActivityTypingLimit.getLimitGameType().equals(limitCode)
            ) {
                return userActivityTypingLimit.getTypingAmount();
            }
        }
        return amount;
    }

}
