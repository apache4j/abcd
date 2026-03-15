package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.vo.game.SportLobbyEvents;
import com.cloud.baowang.play.po.SiteEventsPO;
import com.cloud.baowang.play.repositories.SiteEventsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@AllArgsConstructor
public class SiteEventsService extends ServiceImpl<SiteEventsRepository, SiteEventsPO> {


    /**
     * 获取出体育联赛
     */
    public List<String> getLobbySiteEvents(SportLobbyEvents events) {
        Long sportType = events.getSportType();
        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_LOBBY_EVENTS_TOP, sportType, VenueEnum.SBA.getVenueCode()));
        String jsonStr = RedisUtil.getValue(key);
        if (StringUtils.isNotBlank(jsonStr)) {
            return Arrays.asList(jsonStr.split(","));
        }

        List<SiteEventsPO> siteEventsList = baseMapper.selectList(Wrappers.lambdaQuery(SiteEventsPO.class)
                .eq(SiteEventsPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(SiteEventsPO::getVenueCode, VenueEnum.SBA.getVenueCode())
                .eq(SiteEventsPO::getSportType, sportType)
                .select(SiteEventsPO::getLeagueId)
                .orderByAsc(SiteEventsPO::getSort)
                .last(" limit 50 "));


        List<String> leagueIdList = siteEventsList.stream().map(SiteEventsPO::getLeagueId).toList();
        if (CollectionUtil.isNotEmpty(leagueIdList)) {
            RedisUtil.setValue(key, String.join(",", leagueIdList), 10L, TimeUnit.MINUTES);
        }
        return leagueIdList;


    }


}
