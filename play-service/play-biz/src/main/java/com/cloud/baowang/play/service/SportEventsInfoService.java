package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.play.api.enums.sb.SBSportTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.vo.venue.GameClassInfoSetSortDetailVO;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoRequestVO;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoSortRequestVO;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoVO;
import com.cloud.baowang.play.po.SiteEventsPO;
import com.cloud.baowang.play.po.SportEventsInfoPO;
import com.cloud.baowang.play.repositories.SportEventsInfoRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class SportEventsInfoService extends ServiceImpl<SportEventsInfoRepository, SportEventsInfoPO> {


    private final SiteEventsService siteEventsService;


    public Page<SportEventsInfoVO> getSportEventsInfoPage(SportEventsInfoRequestVO requestVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        Page<SportEventsInfoVO> iPage = baseMapper.getSportEventsInfoPage(PageConvertUtil.getMybatisPage(requestVO), siteCode, requestVO);

        List<SportEventsInfoVO> list = iPage.getRecords();

        list.forEach(x -> {
            String siteEventsId = x.getSiteEventsId();
            x.setPinStatus(ObjectUtil.isNotEmpty(siteEventsId));
            x.setVenueCode(VenueEnum.SBA.getVenueCode());
            x.setVenueName(VenueEnum.SBA.getVenueName());
            x.setSportName(SBSportTypeEnum.nameOfId(x.getSportType()));
        });
        return iPage;
    }

    public List<SportEventsInfoVO> getSportEventsInfoSortList(SportEventsInfoRequestVO sportEventsInfoRequestVO) {
        return baseMapper.getSportEventsInfoSortList(CurrReqUtils.getSiteCode(), sportEventsInfoRequestVO);
    }

    public Boolean setSportEventsPinEvents(String id) {
        SportEventsInfoPO sportEventsInfoPO = baseMapper.selectById(id);

        if (ObjectUtil.isEmpty(sportEventsInfoPO)) {
            return Boolean.FALSE;
        }


        SiteEventsPO siteEventsPO = siteEventsService.getBaseMapper().selectOne(Wrappers.lambdaQuery(SiteEventsPO.class)
                .eq(SiteEventsPO::getEventsInfoId, id)
                .eq(SiteEventsPO::getSiteCode, CurrReqUtils.getSiteCode()));

        if (siteEventsPO != null) {
            return Boolean.TRUE;
        }

        Long count = siteEventsService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteEventsPO.class)
                .eq(SiteEventsPO::getSiteCode, CurrReqUtils.getSiteCode()));
        SiteEventsPO addSiteEventsPO = SiteEventsPO.builder()
                .eventsInfoId(id)
                .siteCode(CurrReqUtils.getSiteCode())
                .sort(count + 1)
                .leagueId(sportEventsInfoPO.getLeagueId())
                .venueCode(VenueEnum.SBA.getVenueCode())
                .sportType(sportEventsInfoPO.getSportType())
                .build();
        addSiteEventsPO.setUpdatedTime(System.currentTimeMillis());
        addSiteEventsPO.setCreatedTime(System.currentTimeMillis());
        addSiteEventsPO.setUpdater(CurrReqUtils.getAccount());
        addSiteEventsPO.setCreator(CurrReqUtils.getAccount());
        siteEventsService.getBaseMapper().insert(addSiteEventsPO);
        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_LOBBY_EVENTS_TOP, "*", VenueEnum.SBA.getVenueCode()));
        RedisUtil.deleteKeysByPattern(key);
        return Boolean.TRUE;
    }


    public Boolean cancelSportEventsPinEvents(String id) {
        siteEventsService.getBaseMapper().delete(Wrappers.lambdaQuery(SiteEventsPO.class)
                .eq(SiteEventsPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(SiteEventsPO::getEventsInfoId, id));
        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_LOBBY_EVENTS_TOP, "*", VenueEnum.SBA.getVenueCode()));
        RedisUtil.deleteKeysByPattern(key);
        return Boolean.TRUE;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean setSortEvents(SportEventsInfoSortRequestVO request) {
        String siteCode = CurrReqUtils.getSiteCode();


        List<String> ids = request.getList().stream().map(GameClassInfoSetSortDetailVO::getId).toList();


        List<SportEventsInfoPO> list = baseMapper.selectList(Wrappers.lambdaQuery(SportEventsInfoPO.class)
                .in(SportEventsInfoPO::getId, ids));

        if (CollectionUtil.isEmpty(list)) {
            return Boolean.FALSE;
        }


        //排序的时候传入的只会是同一种球类,所以将同一种球类删除重新排
        siteEventsService.getBaseMapper().delete(Wrappers.lambdaQuery(SiteEventsPO.class)
                .eq(SiteEventsPO::getSiteCode, siteCode)
                .eq(SiteEventsPO::getSportType, list.get(0).getSportType()));


        Map<String, SportEventsInfoPO> map = list.stream().collect(Collectors.toMap(SportEventsInfoPO::getId, Function.identity()));


        List<SiteEventsPO> add = Lists.newArrayList();
        for (GameClassInfoSetSortDetailVO item : request.getList()) {
            SportEventsInfoPO sportEventsInfoPO = map.get(item.getId());
            if (sportEventsInfoPO == null) {
                continue;
            }
            SiteEventsPO siteEventsPO = SiteEventsPO.builder()
                    .venueCode(VenueEnum.SBA.getVenueCode())
                    .eventsInfoId(item.getId())
                    .leagueId(sportEventsInfoPO.getLeagueId())
                    .sort(item.getSort().longValue())
                    .sportType(sportEventsInfoPO.getSportType())
                    .siteCode(siteCode)
                    .build();
            siteEventsPO.setUpdater(CurrReqUtils.getAccount());
            siteEventsPO.setUpdatedTime(System.currentTimeMillis());
            add.add(siteEventsPO);
        }

        Boolean result = siteEventsService.saveBatch(add);

        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_LOBBY_EVENTS_TOP, "*", VenueEnum.SBA.getVenueCode()));
        RedisUtil.deleteKeysByPattern(key);
        return result;
    }



}
