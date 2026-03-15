package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityDailyCompetitionPO;
import com.cloud.baowang.activity.po.SiteActivityDailyRankingPO;
import com.cloud.baowang.activity.po.SiteActivityDailyRobotPO;
import com.cloud.baowang.activity.repositories.SiteActivityDailyCompetitionRepository;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.user.api.vo.user.request.UserTopReqVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Slf4j
public class SiteActivityDailyCompetitionService extends
        ServiceImpl<SiteActivityDailyCompetitionRepository, SiteActivityDailyCompetitionPO> {

    private final I18nApi i18nApi;

    private final SiteActivityDailyRankingService siteActivityDailyRankingService;

    private final SiteActivityDailyRobotService siteActivityDailyRobotService;

    private final SiteActivityBaseService siteActivityBaseService;

    private final SiteActivityDetailService siteActivityDetailService;

    private final SiteApi siteApi;

    private final ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;


    public ActivityDailyCompetitionRespVO getActivityByActivityId(String activityId, String siteCode) {
        List<SiteActivityDailyCompetitionPO> siteActivityDailyCompetitionPOList = baseMapper.selectList(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                .eq(SiteActivityDailyCompetitionPO::getActivityId, activityId)
                .eq(SiteActivityDailyCompetitionPO::getSiteCode, siteCode));
        ActivityDailyCompetitionRespVO respVO = ActivityDailyCompetitionRespVO.builder().build();

        if (CollectionUtil.isEmpty(siteActivityDailyCompetitionPOList)) {
            return respVO;
        }

        List<String> detailIds = siteActivityDailyCompetitionPOList.stream().map(SiteActivityDailyCompetitionPO::getId).toList();

        //排行榜配置信息
        List<SiteActivityDailyRankingPO> siteActivityDailyRankingList = siteActivityDailyRankingService.getBaseMapper()
                .selectList(Wrappers.lambdaQuery(SiteActivityDailyRankingPO.class)
                        .in(SiteActivityDailyRankingPO::getActivityDailyCompetitionId, detailIds));

        //key=详情配置ID ,v=排行榜配置信息
        Map<String, List<SiteActivityDailyRankingPO>> siteActivityDailyRankingMap = siteActivityDailyRankingList.stream()
                .collect(Collectors.groupingBy(SiteActivityDailyRankingPO::getActivityDailyCompetitionId));


        //机器人配置信息
        List<SiteActivityDailyRobotPO> siteActivityDailyRobotList = siteActivityDailyRobotService
                .getBaseMapper().selectList(Wrappers.lambdaQuery(SiteActivityDailyRobotPO.class)
                        .eq(SiteActivityDailyRobotPO::getActivityId, activityId)
                        .eq(SiteActivityDailyRobotPO::getSiteCode, siteCode));

        //key = 竞赛ID,value = 机器人
        Map<String, List<SiteActivityDailyRobotPO>> dailyRobotMap = siteActivityDailyRobotList.stream()
                .collect(Collectors.groupingBy(SiteActivityDailyRobotPO::getActivityDailyCompetitionId));


        List<ActivityDailyCompetitionDetailRespVO> list = Lists.newArrayList();
        for (SiteActivityDailyCompetitionPO tmp : siteActivityDailyCompetitionPOList) {
            //排行榜信息
            List<SiteActivityDailyRankingPO> rankingList = siteActivityDailyRankingMap.get(tmp.getId());
            ActivityDailyCompetitionDetailRespVO vo = ActivityDailyCompetitionDetailRespVO.builder().build();
            BeanUtils.copyProperties(tmp, vo);
            vo.setActivityNameI18nCode(tmp.getCompetitionI18nCode());
            vo.setActivityDetail(soruceToList(rankingList));
            vo.setVenueCodeList(Arrays.asList(tmp.getVenueCode().split(",")));

            List<SiteActivityDailyRobotPO> robotList = dailyRobotMap.get(tmp.getId());

            List<ActivityDailyRobotRespVO> robotRespVOS = Lists.newArrayList();
            if (CollectionUtil.isNotEmpty(robotList)) {
                robotRespVOS = robotList.stream().map(x -> {
                    ActivityDailyRobotRespVO resp = new ActivityDailyRobotRespVO();
                    resp.setUserAccount(x.getRobotAccount());
                    resp.setRobotId(x.getId());
                    resp.setPlatBetAmount(x.getRobotBetAmount());
                    resp.setType(true);
                    resp.setBetGrowthPct(x.getBetGrowthPct());
                    resp.setEdit(x.getEdit());
                    resp.setInitRobotBetAmount(x.getInitRobotBetAmount());
                    resp.setMaxRobotBetAmount(x.getMaxRobotBetAmount());
                    return resp;
                }).toList();
            }
            vo.setRobotList(robotRespVOS);

            list.add(vo);
        }
        respVO.setList(list);
        return respVO;
    }

    private List<SiteActivityDailyCompetitionDetail> soruceToList(List<SiteActivityDailyRankingPO> rankingList) {
        List<SiteActivityDailyCompetitionDetail> detailList = Lists.newArrayList();
        if(CollectionUtil.isEmpty(rankingList)){
            return detailList;
        }

        for (SiteActivityDailyRankingPO detail : rankingList) {
            String activityDetail = detail.getActivityDetail();
            if (ObjectUtil.isNotEmpty(activityDetail)) {
                SiteActivityDailyCompetitionDetail comRankDetail = JSONObject.parseObject(activityDetail, SiteActivityDailyCompetitionDetail.class);
                comRankDetail.setRanking(detail.getRanking());
                detailList.add(comRankDetail);
            }
        }

        return detailList;
    }

    //详情数据,集合转数据源
    private List<SiteActivityDailyRankingPO> listToSource(String baseId, String detailId, ActivityDailyCompetitionDetailVO tmp) {
        Integer activityDiscountType = tmp.getActivityDiscountType();

        List<SiteActivityDailyRankingPO> rankingList = Lists.newArrayList();

        for (SiteActivityDailyCompetitionDetail detail : tmp.getActivityDetail()) {

            Integer ranking = detail.getRanking();
            Integer freeTimes = detail.getFreeTimes();

            //百分比数据配置格式
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(activityDiscountType)) {

                BigDecimal activityAmountPer = detail.getActivityAmountPer();

                if (ObjectUtil.hasEmpty(ranking, activityAmountPer, freeTimes)) {
                    log.info("每日竞赛,百分比数据配置格式,参数详情异常");
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (freeTimes < 0) {
                    log.info("每日竞赛,百分比数据配置格式,免费旋转次数,异常");
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (activityAmountPer.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("每日竞赛,百分比数据配置格式,彩金百分比,异常");
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                SiteActivityDailyCompetitionDetail siteActivityDailyCompetitionDetail = SiteActivityDailyCompetitionDetail.builder()
                        .activityAmountPer(activityAmountPer)
                        .freeTimes(freeTimes)
                        .accessParameters(detail.getAccessParameters())
                        .betLimitAmount(detail.getBetLimitAmount())
                        .venueCode(detail.getVenueCode())
                        .build();

                SiteActivityDailyRankingPO rankingPO = SiteActivityDailyRankingPO
                        .builder()
                        .activityId(baseId)
                        .activityDailyCompetitionId(detailId)
                        .ranking(ranking)
                        .activityDetail(JSON.toJSONString(siteActivityDailyCompetitionDetail))
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build();
                rankingList.add(rankingPO);
            } else {
                BigDecimal activityAmount = detail.getActivityAmount();


                if (ObjectUtil.hasEmpty(ranking, activityAmount, freeTimes)) {
                    log.info("每日竞赛,固定金额数据配置格式,参数详情异常");
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (activityAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("每日竞赛,固定金额数据配置格式,彩金金额,异常");
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                SiteActivityDailyCompetitionDetail siteActivityDailyCompetitionDetail = SiteActivityDailyCompetitionDetail.builder()
                        .activityAmount(activityAmount)
                        .freeTimes(freeTimes)
                        .accessParameters(detail.getAccessParameters())
                        .betLimitAmount(detail.getBetLimitAmount())
                        .venueCode(detail.getVenueCode())
                        .build();

                SiteActivityDailyRankingPO rankingPO = SiteActivityDailyRankingPO
                        .builder()
                        .activityId(baseId)
                        .activityDailyCompetitionId(detailId)
                        .ranking(ranking)
                        .activityDetail(JSON.toJSONString(siteActivityDailyCompetitionDetail))
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build();
                rankingList.add(rankingPO);
            }
        }
        return rankingList;
    }

    private SiteActivityDailyCompetitionPO convertPO(ActivityDailyCompetitionDetailVO activity) {
        SiteActivityDailyCompetitionPO po = new SiteActivityDailyCompetitionPO();
        BeanUtils.copyProperties(activity, po);
//        po.setActivityDetail(listToSource(activity.getActivityDiscountType(), activity.getActivityDetail()));
        po.setSiteCode(CurrReqUtils.getSiteCode());
        String typeI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_DAIL_COMPETITION.getCode());
        po.setCompetitionI18nCode(typeI18nCode);
        po.setVenueCode(String.join(",", activity.getVenueCodeList()));
        return po;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean save(ActivityDailyCompetitionVO activity, String baseId) {
        int count = 0;

        List<SiteActivityDailyRankingPO> allRanking = Lists.newArrayList();
        for (ActivityDailyCompetitionDetailVO tmp : activity.getList()) {
            SiteActivityDailyCompetitionPO po = convertPO(tmp);
            po.setActivityId(baseId);

            ResponseVO<Boolean> responseVO = i18nApi.insert(I18nMsgBindUtil.bind(po.getCompetitionI18nCode(), tmp.getActivityNameI18nCodeList()));
            if (!responseVO.isOk() || !responseVO.getData()) {
                log.info("i18,新增异常");
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            po.setComNo(OrderUtil.getOrderNoNum("", 3));
            if (baseMapper.insert(po) > 0) {
                count++;
            }

            List<SiteActivityDailyRankingPO> rankingPOS = listToSource(baseId, po.getId(), tmp);
            allRanking.addAll(rankingPOS);

        }
        if (count < activity.getList().size()) {
            log.info("活动每日竞赛添加失败,新增条数不对");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        siteActivityDailyRankingService.saveBatch(allRanking);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean upActivityDetail(ActivityDailyCompetitionVO activity, String baseId) {
        String siteCode = CurrReqUtils.getSiteCode();

        baseMapper.delete(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                .eq(SiteActivityDailyCompetitionPO::getSiteCode, siteCode)
                .eq(SiteActivityDailyCompetitionPO::getActivityId, baseId));


        siteActivityDailyRankingService.getBaseMapper()
                .delete(Wrappers.lambdaQuery(SiteActivityDailyRankingPO.class)
                        .eq(SiteActivityDailyRankingPO::getSiteCode, siteCode)
                        .eq(SiteActivityDailyRankingPO::getActivityId, baseId));

//        siteActivityDailyRobotService.getBaseMapper()
//                .delete(Wrappers.lambdaQuery(SiteActivityDailyRobotPO.class)
//                        .eq(SiteActivityDailyRobotPO::getSiteCode, siteCode)
//                        .eq(SiteActivityDailyRobotPO::getActivityId, baseId));

        //删除这个活动下的所有竞赛缓存
//        String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, siteCode,
//                TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), baseId,
//                ":*");
//        RedisUtil.deleteKey(top100Key);


//        return save(activity, baseId);
        return updateInfo(activity, baseId);
    }

    private boolean updateInfo(ActivityDailyCompetitionVO activity, String baseId) {
        int count = 0;

        List<String> comList = activity.getList().stream().map(ActivityDailyCompetitionDetailVO::getComNo).filter(ObjectUtil::isNotEmpty).toList();
        Map<String, SiteActivityDailyCompetitionPO> competitionMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(comList)) {

            List<SiteActivityDailyCompetitionPO> competitionList = baseMapper.selectList(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                    .in(SiteActivityDailyCompetitionPO::getComNo, comList));

            competitionMap = competitionList.stream()
                    .collect(Collectors.toMap(SiteActivityDailyCompetitionPO::getComNo, SiteActivityDailyCompetitionPO -> SiteActivityDailyCompetitionPO));
        }

        if (CollectionUtil.isNotEmpty(activity.getList())) {
            List<Integer> venueTypeList = activity.getList().stream().map(ActivityDailyCompetitionDetailVO::getVenueType).toList();

            List<String> activityIds = activity.getList().stream().map(ActivityDailyCompetitionDetailVO::getActivityId).toList();
            if (CollectionUtil.isNotEmpty(activity.getList())) {
                Long venueTypeCount = baseMapper.selectCount(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                        .in(SiteActivityDailyCompetitionPO::getVenueType, venueTypeList)
                        .eq(SiteActivityDailyCompetitionPO::getSiteCode, CurrReqUtils.getSiteCode())
                        .in(CollectionUtil.isNotEmpty(activityIds), SiteActivityDailyCompetitionPO::getActivityId, activityIds));

                //场馆活动重复
                if(venueTypeCount > 0){
                    throw new BaowangDefaultException(ResultCode.ACTIVITY_VENUE_TYPE_REPEAT);
                }
            }

        }


        List<SiteActivityDailyRankingPO> allRanking = Lists.newArrayList();
        for (ActivityDailyCompetitionDetailVO tmp : activity.getList()) {
            SiteActivityDailyCompetitionPO po = convertPO(tmp);
            po.setActivityId(baseId);
            Map<String, List<I18nMsgFrontVO>> i18nMag = I18nMsgBindUtil.bind(po.getCompetitionI18nCode(), tmp.getActivityNameI18nCodeList());
            SiteActivityDailyCompetitionPO dailyCompetitionPO = competitionMap.get(tmp.getComNo());


            //修改
            if (ObjectUtil.isNotEmpty(tmp.getComNo()) && dailyCompetitionPO != null) {
                po.setId(dailyCompetitionPO.getId());
                ResponseVO<Boolean> responseVO = i18nApi.update(i18nMag);
                if (!responseVO.isOk() || !responseVO.getData()) {
                    log.info("i18,修改异常");
                    throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                }

                if (baseMapper.update(po, Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                        .eq(SiteActivityDailyCompetitionPO::getId, po.getId())) > 0) {
                    count++;
                }


                //新增
            } else {
                ResponseVO<Boolean> responseVO = i18nApi.insert(i18nMag);
                if (!responseVO.isOk() || !responseVO.getData()) {
                    log.info("i18,新增异常");
                    throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                }
                po.setComNo(OrderUtil.getOrderNoNum("", 3));
                if (baseMapper.insert(po) > 0) {
                    count++;
                }
            }
            List<SiteActivityDailyRankingPO> rankingPOS = listToSource(baseId, po.getId(), tmp);
            allRanking.addAll(rankingPOS);

        }
        if (count < activity.getList().size()) {
            log.info("活动每日竞赛修改失败,修改条数不对");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        siteActivityDailyRankingService.saveBatch(allRanking);

        //删除这个活动下的所有竞赛缓存
        String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, CurrReqUtils.getSiteCode(),
                TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), baseId,
                ":*");
        RedisUtil.deleteKey(top100Key);

        //每日竞赛活动
        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(),
                String.format(RedisConstants.ACTIVITY_CONFIG, baseId));
        RedisUtil.deleteKey(key);

        return true;
    }


    //后台管理系统 查看每日竞赛的排行榜
    public List<ActivityDailyRobotRespVO> queryDailyRobot(ActivityDailyRobotListReqVO reqVO, String siteCode) {

        SiteActivityBasePO siteActivityBasePO = siteActivityBaseService.getBaseMapper().selectOne(Wrappers
                .lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getId, reqVO.getActivityId())
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.DAILY_COMPETITION.getType()));
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            log.info("活动不存在");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO = getActivityByActivityId(reqVO.getActivityId(), siteCode);

        if (ObjectUtil.isEmpty(activityDailyCompetitionRespVO)) {
            log.info("活动竞赛,不存在");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        BeanUtils.copyProperties(siteActivityBasePO, activityDailyCompetitionRespVO);

        //查询今天的排行榜
        List<ActivityPartUserRankingDailyRespVO> dailyRespVOS = siteActivityDetailService.getToDayRoleDailyRecord(reqVO.getDetailId(), activityDailyCompetitionRespVO);

        //没有启用活动,不显示真实用户的记录
        if (!siteActivityBasePO.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            dailyRespVOS = dailyRespVOS.stream().filter(ActivityPartUserRankingDailyRespVO::getType).toList();
        }

        //只取出前10条
        dailyRespVOS = dailyRespVOS.stream()
                .sorted(Comparator.comparing(ActivityPartUserRankingDailyRespVO::getPlatBetAmount).reversed())
                .limit(50)
                .toList();

        List<SiteActivityDailyRobotPO> siteActivityDailyRobot = siteActivityDailyRobotService.getBaseMapper().selectList(null);

        Map<String,SiteActivityDailyRobotPO> siteActivityDailyRobotMap = siteActivityDailyRobot.stream()
                .collect(Collectors.toMap(SiteActivityDailyRobotPO::getId, Function.identity()));

        return dailyRespVOS.stream().map(x -> {
            ActivityDailyRobotRespVO dailyRespVO = new ActivityDailyRobotRespVO();
            BeanUtils.copyProperties(x, dailyRespVO);
            dailyRespVO.setCurrencyAmount(x.getBetAmount());
            dailyRespVO.setPlatBetAmount(BigDecimalUtils.formatFourKeep4Dec(x.getPlatBetAmount()));
//            SiteActivityDailyRobotPO siteActivityDailyRobotPO = siteActivityDailyRobotMap.get(x.getRobotId());
//            if(ObjectUtil.isNotEmpty(siteActivityDailyRobotPO) && x.getType()){
//                BigDecimal initRobotBetAmount = siteActivityDailyRobotPO.getInitRobotBetAmount();
//                BigDecimal maxRobotBetAmount = siteActivityDailyRobotPO.getMaxRobotBetAmount();
//                BigDecimal betGrowthPct = siteActivityDailyRobotPO.getBetGrowthPct();
//                Boolean edit = siteActivityDailyRobotPO.getEdit();
//                dailyRespVO.setInitRobotBetAmount(initRobotBetAmount);
//                dailyRespVO.setMaxRobotBetAmount(maxRobotBetAmount);
//                dailyRespVO.setBetGrowthPct(betGrowthPct);
//                dailyRespVO.setEdit(edit);
//            }
            return dailyRespVO;
        }).toList();
    }


    /**
     * 定时任务将每日的前100名用户同步到缓存
     */
    public void toSetActivityDailyTop100() {
        List<SiteActivityBasePO> list = siteActivityBaseService.getBaseMapper().selectList(Wrappers
                .lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.DAILY_COMPETITION.getType())
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode()));
        if (CollectionUtil.isEmpty(list)) {
            log.info("没有每日竞赛的配置,不进行计算前100用户");
            return;
        }

        Map<String, List<SiteActivityBasePO>> listMap = list.stream().collect(Collectors.groupingBy(SiteActivityBasePO::getSiteCode));

        ResponseVO<List<SiteVO>> siteAllList = siteApi.allSiteInfo();

        Map<String, SiteVO> siteMap = siteAllList.getData().stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        long currentTimeMillis = System.currentTimeMillis();

        //该循环为站点维度 ,一个站点一次
        for (Map.Entry<String, List<SiteActivityBasePO>> item : listMap.entrySet()) {
            String siteCode = item.getKey();
            List<SiteActivityBasePO> activityBaseList = item.getValue();

            if (CollectionUtil.isEmpty(activityBaseList)) {
                continue;
            }

            //站点下的每日竞赛活动
            for (SiteActivityBasePO siteBase : activityBaseList) {
                ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO = getActivityByActivityId(siteBase.getId(), siteCode);

                if (ObjectUtil.isEmpty(activityDailyCompetitionRespVO)) {
                    log.info("活动竞赛,不存在,不进行计算前100用户");
                    continue;
                }

                SiteVO siteVO = siteMap.get(siteBase.getSiteCode());
                String timezone = siteVO.getTimezone();
                String activityId = siteBase.getId();

                BeanUtils.copyProperties(siteBase, activityDailyCompetitionRespVO);

                //活动的竞赛
                List<ActivityDailyCompetitionDetailRespVO> activityDetailList = activityDailyCompetitionRespVO.getList();

                //每个活动下的竞赛
                for (ActivityDailyCompetitionDetailRespVO competitionPO : activityDetailList) {
                    List<String> venueCodeList = competitionPO.getVenueCodeList();

                    //获取出奖励的配置 底池 + 场馆池
                    BigDecimal totalAward = siteActivityDetailService.getTotalRewardsAmount(competitionPO, siteCode, System.currentTimeMillis(), timezone);

                    ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO
                            .builder()
                            .siteCode(siteCode)
                            .venueCodeList(venueCodeList)
                            .dayMillis(TimeZoneUtils.getStartOfDayInTimeZone(currentTimeMillis, timezone))
                            .build();
                    List<ReportUserVenueBetsTopVO> betTop100List = reportUserVenueFixedWinLoseApi.queryUserBetsTopPlatBetAmount(userTopReqVO);
                    List<ActivityRankingDailyVO> rankingsList = siteActivityDetailService.updateRankings(competitionPO, betTop100List, siteCode, totalAward);
                    String key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, siteCode,
                            TimeZoneUtils.getStartOfDayInTimeZone(currentTimeMillis, timezone), activityId, competitionPO.getId());
                    if (CollectionUtil.isNotEmpty(rankingsList)) {
                        RedisUtil.setList(key, rankingsList, 5L, TimeUnit.MINUTES);
                    }
                }
            }
        }
    }


}
