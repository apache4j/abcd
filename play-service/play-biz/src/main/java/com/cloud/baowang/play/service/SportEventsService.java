package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.play.api.enums.RecommendationStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.sb.SBSportTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.play.api.enums.SportRecommendStatusEnum;
import com.cloud.baowang.play.api.vo.lobby.LobbySportEventsVO;
import com.cloud.baowang.play.api.vo.third.SBA.SBAEventsInfo;
import com.cloud.baowang.play.api.vo.third.SBA.SBATeamInfo;
import com.cloud.baowang.play.api.vo.venue.SportRecommendRequestVO;
import com.cloud.baowang.play.api.vo.venue.SportRecommendVO;
import com.cloud.baowang.play.api.vo.venue.UpSportRecommendRequestVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.po.SiteSportEventsRecommendPO;
import com.cloud.baowang.play.po.SportEventsRecommendPO;
import com.cloud.baowang.play.repositories.SiteSportEventsRecommendRepository;
import com.cloud.baowang.play.repositories.SportEventsRecommendRepository;
import com.cloud.baowang.system.api.api.operations.IpAddressAreaCurrencyApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.operations.IpAdsWebReqVO;
import com.cloud.baowang.system.api.vo.operations.IpAdsWebResVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SportEventsService {

    private final SportEventsRecommendRepository sportEventsRecommendRepository;

    private final SiteSportEventsRecommendRepository siteSportEventsRecommendRepository;

    private final SportEventsRecommendService sportEventsRecommendService;

    private final SiteApi siteApi;

    private final VenueInfoService venueInfoService;

    private final UserInfoApi userInfoApi;

    private final IpAddressAreaCurrencyApi ipAddressAreaCurrencyApi;


//    private final SiteSportEventsRepository siteSportEventsRepository;


    /**
     * 获取用户币种
     */
    public String getCurrency() {

        String userId = CurrReqUtils.getOneId();
        //如果用户登录了直接返回用户币种
        if (ObjectUtil.isNotEmpty(userId)) {
            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            return userInfoVO.getMainCurrency();
        }

        String siteCode = CurrReqUtils.getSiteCode();
        String ip = CurrReqUtils.getReqIp();
//        IPResponse response = IpAddressUtils.queryIpRegion(ip);
        IPRespVO response = IpAPICoUtils.getIp(ip);
//        if (response != null) {
//            String key = CacheConstants.IP_ADDRESS_CURRENCY + ":" + siteCode + ":" + response.getCountryCode();
//            IpAdsWebResVO returnData = RedisUtil.getValue(key);
//            if (ObjectUtils.isNotEmpty(returnData)) {
//                return returnData.getCurrencyCode();
//            }
//        }

        IpAdsWebReqVO vo = new IpAdsWebReqVO();
        vo.setIp(ip);
        vo.setSiteCode(siteCode);
        if (ObjectUtils.isNotEmpty(response)) {
            vo.setAreaCode(response.getCountryCode());
        }

        log.info("查询未登录默认币种:{}",vo);

        ResponseVO<IpAdsWebResVO> ipAdsWebResVOResponseVO = ipAddressAreaCurrencyApi.queryWebCurrey(vo);
        if (!ipAdsWebResVOResponseVO.isOk()) {
            log.info("游戏大厅调用获取币种失败:{}", vo);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return ipAdsWebResVOResponseVO.getData().getCurrencyCode();
    }


    public Page<SportRecommendVO> getNewSportRecommendPage(SportRecommendRequestVO requestVO) {
        Page<SportEventsRecommendPO> page = PageConvertUtil.getMybatisPage(requestVO);
        requestVO.setEndTime(System.currentTimeMillis());
        IPage<SportRecommendVO> iPage = sportEventsRecommendRepository.querySportRecommendPage(page, requestVO, CurrReqUtils.getSiteCode());

        iPage.getRecords().forEach(x -> {
            x.setDateTime(x.getStartTime());
            x.setStatus(x.getStartTime() <= System.currentTimeMillis() ? SportRecommendStatusEnum.ALREADY_STARTED.getCode() : SportRecommendStatusEnum.NOT_STARTED.getCode());
        });
        return ConvertUtil.toConverPage(iPage);
    }

    public Page<SportRecommendVO> sportRecommendPage(SportRecommendRequestVO requestVO) {
        // 构建分页请求
        Page<SportEventsRecommendPO> page = PageConvertUtil.getMybatisPage(requestVO);

//        // 构建查询条件
//        LambdaQueryWrapper<SportEventsRecommendPO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(SportEventsRecommendPO::getSiteCode, CurrReqUtils.getSiteCode())
//                .eq(ObjectUtil.isNotEmpty(requestVO.getSportType()), SportEventsRecommendPO::getSportType, requestVO.getSportType())
//                .like(ObjectUtil.isNotEmpty(requestVO.getLeagueName()), SportEventsRecommendPO::getLeagueName, requestVO.getLeagueName())
//                .eq(ObjectUtil.isNotEmpty(requestVO.getEventsId()), SportEventsRecommendPO::getEventsId, requestVO.getEventsId())
//                .like(ObjectUtil.isNotEmpty(requestVO.getTeamName()), SportEventsRecommendPO::getTeamName, requestVO.getTeamName())
//                //开赛时间 大于 系统时间 = 未开赛
//                .ge(SportRecommendStatusEnum.NOT_STARTED.getCode().equals(requestVO.getStatus()), SportEventsRecommendPO::getDateTime, System.currentTimeMillis())
//                //开赛时间 小于等于 系统时间 = 已开赛
//                .le(SportRecommendStatusEnum.ALREADY_STARTED.getCode().equals(requestVO.getStatus()), SportEventsRecommendPO::getDateTime, System.currentTimeMillis())
//                //只查出增加两个小时后还大于当前系统时间的
//                .ge(SportEventsRecommendPO::getDateTime, TimeZoneUtils.addTime(System.currentTimeMillis(), 120, TimeUnit.MINUTES))
//                .orderByDesc(SportEventsRecommendPO::getStatus)
//                .orderByAsc(SportEventsRecommendPO::getDateTime);
//
//        // 执行分页查询
//        IPage<SportEventsRecommendPO> iPage = sportEventsRecommendRepository.selectPage(page, queryWrapper);
//
//
//
//        // 转换为VO对象
//        IPage<SportRecommendVO> result = iPage.convert(x -> {
//            SportRecommendVO vo = SportRecommendVO.builder().build();
//            BeanUtils.copyProperties(x, vo);
//            vo.setTeamName(x.getTeamName());
//            vo.setPinStatus(x.getStatus().equals(RecommendationStatusEnum.RECOMMENDED.getCode()));
//            vo.setStatus(x.getDateTime() <= System.currentTimeMillis() ? SportRecommendStatusEnum.ALREADY_STARTED.getCode() : SportRecommendStatusEnum.NOT_STARTED.getCode());
//            return vo;
//        });
//
//        // 排序处理
//        List<SportRecommendVO> sortedList = result.getRecords().stream()
//                .sorted(Comparator.comparing(SportRecommendVO::getSortTime, Comparator.nullsLast(Comparator.reverseOrder())))
//                .collect(Collectors.toList());
//
//        result.setRecords(sortedList);
//        return ConvertUtil.toConverPage(result);
        return null;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean setPinEvents(UpSportRecommendRequestVO upSportRecommendRequestVO) {
        return upEvents(upSportRecommendRequestVO.getId(), RecommendationStatusEnum.RECOMMENDED.getCode());
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean setNewPinEvents(UpSportRecommendRequestVO upSportRecommendRequestVO) {

        String id = upSportRecommendRequestVO.getId();

        String siteCode = CurrReqUtils.getSiteCode();

        SportEventsRecommendPO sportEventsRecommendPO = sportEventsRecommendRepository.selectById(id);

        if (ObjectUtil.isEmpty(sportEventsRecommendPO)) {
            return Boolean.FALSE;
        }

        SiteSportEventsRecommendPO recommendPO = SiteSportEventsRecommendPO
                .builder()
                .siteCode(siteCode)
                .venueCode(VenueEnum.SBA.getVenueCode())
                .sportRecommendId(id)
                .sportType(sportEventsRecommendPO.getSportType())
                .leagueId(sportEventsRecommendPO.getLeagueId())
                .eventsId(sportEventsRecommendPO.getEventsId())
                .build();
        boolean result = siteSportEventsRecommendRepository.insert(recommendPO) > 0;
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_SPORT_EVENTS_RECOMMEND_ALL));
        return result;
    }

    private Boolean upEvents(String id, Integer status) {
        Boolean result = sportEventsRecommendRepository.update(
                SportEventsRecommendPO.builder()
//                        .status(status)
                        .build(),
                Wrappers.lambdaQuery(SportEventsRecommendPO.class)
                        .eq(SportEventsRecommendPO::getId, id)) > 0;
        RedisUtil.deleteKeyByList(List.of(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_SPORT_EVENTS_RECOMMEND_ALL)));
        return result;
    }


    public Boolean cancelNewPinEvents(UpSportRecommendRequestVO upSportRecommendRequestVO) {
        boolean result = siteSportEventsRecommendRepository.delete(Wrappers.lambdaQuery(SiteSportEventsRecommendPO.class)
                .eq(SiteSportEventsRecommendPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(SiteSportEventsRecommendPO::getSportRecommendId, upSportRecommendRequestVO.getId())) > 0;
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_SPORT_EVENTS_RECOMMEND_ALL));
        return result;
    }

    public Boolean cancelPinEvents(UpSportRecommendRequestVO upSportRecommendRequestVO) {
        return upEvents(upSportRecommendRequestVO.getId(), RecommendationStatusEnum.NOT_RECOMMENDED.getCode());
    }

    /**
     * 删除已经过期结束的赛事
     */
    private void delEventsRecommend() {
        List<SportEventsRecommendPO> eventsRecommendPOList = sportEventsRecommendService.getBaseMapper().selectList(Wrappers.lambdaQuery(SportEventsRecommendPO.class)
                .le(SportEventsRecommendPO::getEndTime, System.currentTimeMillis()));


        if (CollectionUtil.isNotEmpty(eventsRecommendPOList)) {
            List<String> idList = eventsRecommendPOList.stream().map(SportEventsRecommendPO::getId).toList();
            sportEventsRecommendService.getBaseMapper().delete(Wrappers.lambdaQuery(SportEventsRecommendPO.class)
                    .le(SportEventsRecommendPO::getEndTime, System.currentTimeMillis()));
            siteSportEventsRecommendRepository.delete(
                    Wrappers.lambdaQuery(SiteSportEventsRecommendPO.class).in(SiteSportEventsRecommendPO::getSportRecommendId, idList)
            );
        }
        RedisUtil.deleteKeysByPattern(RedisConstants.getWildcardsKey(RedisConstants.KEY_QUERY_SPORT_EVENTS_RECOMMEND_ALL));
    }

    public void addEventsRecommend(List<SBAEventsInfo> infoList) {
        if (CollectionUtil.isEmpty(infoList)) {
            return;
        }


        // 提取事件ID列表
        List<String> eventIds = infoList.stream()
                .map(SBAEventsInfo::getEventId)
                .collect(Collectors.toList());

        delEventsRecommend();

        // 查询现有的推荐事件
        List<SportEventsRecommendPO> existingEvents = sportEventsRecommendRepository.selectList(
                Wrappers.lambdaQuery(SportEventsRecommendPO.class)
                        .in(SportEventsRecommendPO::getEventsId, eventIds));
        // 创建现有事件ID的映射
        Set<String> existingEventIds = existingEvents.stream()
                .map(SportEventsRecommendPO::getEventsId)
                .collect(Collectors.toSet());

        List<SportEventsRecommendPO> sportEventsRecommendPOList = Lists.newArrayList();

        // 处理每个新的事件信息
        infoList.forEach(info -> {
            if (existingEventIds.contains(info.getEventId())) {
                // 如果事件已经存在，则跳过
                return;
            }

            // 创建新的 SportEventsRecommendPO 对象
            SportEventsRecommendPO newEvent = new SportEventsRecommendPO();
            BeanUtils.copyProperties(info, newEvent);
            newEvent.setEventsId(info.getEventId());
            newEvent.setEventsCode(info.getEventCode());
            SBATeamInfo teamInfo = info.getTeamInfo();
            newEvent.setHomeName(teamInfo.getHomeName());
            newEvent.setHomeId(teamInfo.getHomeId());
            newEvent.setAwayId(teamInfo.getAwayId());
            newEvent.setAwayName(teamInfo.getAwayName());
            if (info.getGlobalShowTime() == null) {
                log.info("拉沙巴未来赛事异常,没有开赛时间字段:{}", info);
                return;
            }
            //开赛时间
            Long startTime = TimeZoneUtils.parseLocalDateTime4TimeZoneToTime(info.getGlobalShowTime(), TimeZoneUtils.UTC0);
            //结束时间=开赛时间+2小时
            Long dateTime = TimeZoneUtils.addTime(startTime, 120, TimeUnit.MINUTES);
            newEvent.setStartTime(startTime);
            newEvent.setEndTime(dateTime);
            newEvent.setTextInfo(JSON.toJSONString(info));
            newEvent.setTeamName(teamInfo.getHomeName() + " VS " + teamInfo.getAwayName());
            sportEventsRecommendPOList.add(newEvent);
        });
        saveBatchWithLimit(sportEventsRecommendPOList);
    }

    public void saveBatchWithLimit(List<SportEventsRecommendPO> sportEventsRecommendPOList) {
        // 每批次保存的条数
        int batchSize = 500;

        // 获取总数据的大小
        int totalSize = sportEventsRecommendPOList.size();

        // 计算总批次数
        int batchCount = (totalSize + batchSize - 1) / batchSize;

        // 分批次执行保存操作
        for (int i = 0; i < batchCount; i++) {
            // 计算当前批次的起始位置和结束位置
            int start = i * batchSize;
            int end = Math.min(start + batchSize, totalSize);

            // 提取当前批次的数据
            List<SportEventsRecommendPO> batchList = sportEventsRecommendPOList.subList(start, end);

            // 执行保存操作
            sportEventsRecommendService.saveBatch(batchList);
        }
    }


    /**
     * 获取推荐的体育赛事
     */
    public List<LobbySportEventsVO> querySportEventsRecommend() {
        try {
            VenueInfoVO siteVenueInfoByVenueCode = venueInfoService.getSiteVenueInfoByVenueCode(CurrReqUtils.getSiteCode(), VenueEnum.SBA.getVenueCode(), null);
            if (siteVenueInfoByVenueCode == null || !StatusEnum.OPEN.getCode().equals(siteVenueInfoByVenueCode.getStatus())) {
                return Lists.newArrayList();
            }

            //单场馆游戏 没有币种或者币种不支持不显示侧边栏
            if (CollectionUtil.isEmpty(siteVenueInfoByVenueCode.getCurrencyCodeList()) || !siteVenueInfoByVenueCode.getCurrencyCodeList().contains(getCurrency())) {
                return Lists.newArrayList();
            }

            return getSportEvents().stream().map(x -> {
                LobbySportEventsVO events = new LobbySportEventsVO();
                events.setEventsId(x.getEventsId());
                return events;
            }).toList();
        } catch (Exception e) {
            log.error("首页推荐赛事异常", e);
        }
        return Lists.newArrayList();
    }


    public List<SportRecommendVO> getSportEvents() {

        List<SportRecommendVO> resultList = Lists.newArrayList();

        //所有的体育赛事
        List<SportRecommendVO> allEvents = getSportAllEvents();


        if (CollectionUtil.isEmpty(allEvents)) {
            log.info("没有联赛数据");
            return Lists.newArrayList();
        }

        //判断是否有置顶的赛事
        boolean hasActive = allEvents.stream().anyMatch(SportRecommendVO::getPinStatus);

        //推荐赛事返回数量
        final int size = 20;

        //如果存在置顶的赛事
        if(hasActive){
            //置顶的赛事拿出来
            resultList = allEvents.stream().filter(SportRecommendVO::getPinStatus).collect(Collectors.toList());
            //置顶赛事有20个直接返回20个置顶赛事
            if(resultList.size() >= size){
                return resultList;
            }
        }

        //足球
        setAddSportTypeEvents(resultList, allEvents, SBSportTypeEnum.Sport_1.getId(), size);

        //篮球
        setAddSportTypeEvents(resultList, allEvents, SBSportTypeEnum.Sport_2.getId(), size);

        return resultList;
    }



    /**
     * 置顶的联赛不够20条的时候按照该方法补充
     * @param resultList 已有的置顶联赛
     * @param allEvents 所有联赛数据源
     * @param type 球类
     * @param size 返回的联赛数量
     */
    public void setAddSportTypeEvents(List<SportRecommendVO> resultList, List<SportRecommendVO> allEvents, Integer type, int size) {

        //当联赛达到20条则不做任何处理
        if(resultList.size() >= size){
            return;
        }

        int limit = size - resultList.size();

        //从所有的联赛中取出指定的球类列表
        List<SportRecommendVO> footballList = allEvents.stream().filter(x -> type.equals(Integer.valueOf(x.getSportType()))).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(footballList)) {

            // 已存在的联赛ID
            Set<String> existIds = resultList.stream()
                    .map(SportRecommendVO::getEventsId)
                    .collect(Collectors.toSet());

            //已有的联赛数据中过滤掉,防止出现重复联赛
            List<SportRecommendVO> filteredList = footballList.stream()
                    .filter(x -> !existIds.contains(x.getEventsId()))
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(filteredList)) {
                filteredList = filteredList.stream().limit(limit).collect(Collectors.toList());
                resultList.addAll(filteredList);
            }
        }
    }



    public List<SportRecommendVO> getSportAllEvents() {
        //所有体育赛事
        String sportKey = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_SPORT_EVENTS_RECOMMEND_ALL);
         List<SportRecommendVO> cachedEvents = RedisUtil.getValue(sportKey);

        if (CollectionUtil.isNotEmpty(cachedEvents)) {
            cachedEvents = cachedEvents.stream().filter(x -> {
                Long endTime = x.getEndTime();
                return endTime > System.currentTimeMillis();
            }).toList();
            if (CollectionUtil.isNotEmpty(cachedEvents)) {
                return cachedEvents;
            }
        }

        SportRecommendRequestVO vo = new SportRecommendRequestVO();
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setSportTypeList(List.of(SBSportTypeEnum.Sport_1.getId(), SBSportTypeEnum.Sport_2.getId()));
        Page<SportRecommendVO> sportRecommendPage = getNewSportRecommendPage(vo);
        cachedEvents = sportRecommendPage.getRecords();
        if (CollectionUtil.isNotEmpty(cachedEvents)) {
            RedisUtil.setValue(sportKey, cachedEvents, 5L, TimeUnit.MINUTES);
        }
        return cachedEvents;
    }


}
