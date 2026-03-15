package com.cloud.baowang.activity.service.base.activity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.ActivityDailyEnum;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.ActivityConfigVO;
import com.cloud.baowang.activity.api.vo.ActivityDailyCompetitionDetailRespVO;
import com.cloud.baowang.activity.api.vo.ActivityDailyCompetitionDetailVO;
import com.cloud.baowang.activity.api.vo.ActivityDailyCompetitionRespVO;
import com.cloud.baowang.activity.api.vo.ActivityDailyCompetitionVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameTriggerVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameVO;
import com.cloud.baowang.activity.api.vo.ActivityRankingDailyVO;
import com.cloud.baowang.activity.api.vo.SiteActivityDailyCompetitionDetail;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.UserBaseReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityDailyCompetitionPO;
import com.cloud.baowang.activity.po.SiteActivityDailyRankingPO;
import com.cloud.baowang.activity.po.SiteActivityDailyRecordPO;
import com.cloud.baowang.activity.service.SiteActivityDailyCompetitionService;
import com.cloud.baowang.activity.service.SiteActivityDailyRankingService;
import com.cloud.baowang.activity.service.SiteActivityDailyRecordService;
import com.cloud.baowang.activity.service.SiteActivityDailyRobotService;
import com.cloud.baowang.activity.service.SiteActivityDetailService;
import com.cloud.baowang.activity.service.SiteActivityOrderRecordService;
import com.cloud.baowang.activity.service.base.ActivityActionContext;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserTopReqVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.SiteUserAvatarConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 流水排行榜-活动实现
 */
@Service
@AllArgsConstructor
@Slf4j
public class ActivityDailyCompetitionImpl implements ActivityBaseInterface<ActivityDailyCompetitionRespVO> {


    private final SiteActivityDailyCompetitionService siteActivityDailyCompetitionService;

    private final SiteActivityDailyRankingService siteActivityDailyRankingService;

    private final SiteActivityOrderRecordService siteActivityOrderRecordService;

    private final SiteActivityBaseService siteActivityBaseService;

    private final ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;

    private final SiteActivityDetailService siteActivityDetailService;

    private final SiteActivityDailyRecordService siteActivityDailyRecordService;

    private final PlayVenueInfoApi venueInfoApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final ApplicationContext applicationContext;

    private final UserInfoApi userInfoApi;

    private final SiteUserAvatarConfigApi siteUserAvatarConfigApi;

    private final SiteActivityDailyRobotService siteActivityDailyRobotService;

    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.DAILY_COMPETITION;
    }

    @Override
    public boolean saveActivityDetail(String base, String baseId) {
        ActivityDailyCompetitionVO activity = JSONObject.parseObject(base, ActivityDailyCompetitionVO.class);
        if (ObjectUtil.isEmpty(activity)) {
            return false;
        }

        checkSecond(ActivityConfigVO.builder().activityDailyCompetitionVO(activity).build());

        List<Integer> venueTypeList = activity.getList().stream().map(ActivityDailyCompetitionDetailVO::getVenueType).toList();

        int venueTypeSize = venueTypeList.size();

        venueTypeList = venueTypeList.stream().distinct().toList();

        int venueTypeDisSize = venueTypeList.size();

        if (venueTypeDisSize < venueTypeSize) {
            log.info("新增每日竞赛,多个重复场馆类型:{}", venueTypeList);
            throw new BaowangDefaultException(ResultCode.VENUE_TYPE_REPEAT);
        }

        Long venueTypeCount = siteActivityDailyCompetitionService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                .eq(SiteActivityDailyCompetitionPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(SiteActivityDailyCompetitionPO::getActivityId, baseId)
                .in(SiteActivityDailyCompetitionPO::getVenueType, venueTypeList));
        if (venueTypeCount > 0) {
            log.info("新增每日竞赛,重复场馆类型,:{}", venueTypeList);
            throw new BaowangDefaultException(ResultCode.VENUE_TYPE_REPEAT);
        }

        if (!siteActivityDailyCompetitionService.save(activity, baseId)) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return true;
    }


    private void validParamAmount(ActivityDailyCompetitionVO detail) {

        for (ActivityDailyCompetitionDetailVO tmp : detail.getList()) {
            List<SiteActivityDailyCompetitionDetail> activityDetail = tmp.getActivityDetail();
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(tmp.getActivityDiscountType())) {
                BigDecimal totalActivityAmountPer = activityDetail.stream()
                        .map(SiteActivityDailyCompetitionDetail::getActivityAmountPer)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                //百分比的奖励配置 不可以超过100
                if (totalActivityAmountPer.compareTo(BigDecimal.valueOf(100)) > 0) {
                    log.info("{},百分比配置超出100", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.ABNORMAL_BONUS_RATIO);
                }
            }

            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(tmp.getActivityDiscountType())) {
                BigDecimal totalActivityAmount = activityDetail.stream()
                        .map(SiteActivityDailyCompetitionDetail::getActivityAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                //固定金额配置超出初始奖池
                if (totalActivityAmount.compareTo(tmp.getInitAmount()) > 0) {
                    log.info("{},固定金额配置超出初始奖池", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.ABNORMAL_BONUS_RATIO);
                }
            }
        }


    }

    @Override
    public boolean upActivityDetail(String base, String baseId) {
        ActivityDailyCompetitionVO activity = JSONObject.parseObject(base, ActivityDailyCompetitionVO.class);
        if (ObjectUtil.isEmpty(activity)) {
            return false;
        }
        validParamAmount(activity);
        return siteActivityDailyCompetitionService.upActivityDetail(activity, baseId);
    }


    @Override
    public ActivityDailyCompetitionRespVO getActivityByActivityId(String activityId,String siteCode) {
        return siteActivityDailyCompetitionService.getActivityByActivityId(activityId, siteCode);
    }

    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getActivityDailyCompetitionVO());
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
        siteActivityOrderRecordService.awardExpire(siteVO, getActivity());
    }
    /**
     * 每日竞赛 活动
     *
     * @param siteCode 站点编码
     * @param assignDay 指定日期 时间戳
     * @return 每日竞赛
     */
    public ActivityDailyCompetitionRespVO findActivityDailyCompetition(String siteCode, Long assignDay,String timeZone) {
        List<SiteActivityBasePO> siteActivityBasePOS = siteActivityBaseService.selectBySiteAndTemplate(siteCode, ActivityTemplateEnum.DAILY_COMPETITION.getType());
        for (SiteActivityBasePO siteActivityBasePO : siteActivityBasePOS) {
            Integer status=siteActivityBasePO.getStatus();
            Long forbidTime=siteActivityBasePO.getForbidTime();
            Long activityEndTime=siteActivityBasePO.getActivityEndTime();
            String assignDayStr=DateUtils.formatDateByZoneId(assignDay, "yyyy-MM-dd", timeZone);
            //是否允许发放
            boolean permitSendFlag=false;
            if(Objects.equals(status, EnableStatusEnum.ENABLE.getCode())){
                log.info("每日竞赛,指定日期:{},活动启用状态,允许发放奖励",assignDayStr);
                permitSendFlag=true;
            }
            //活动被禁用
            if(Objects.equals(status, EnableStatusEnum.DISABLE.getCode())) {
                //活动已过期被禁用
                if (activityEndTime != null  && activityEndTime.compareTo(forbidTime) <= 0 && assignDay.compareTo(activityEndTime) < 0) {
                    log.info("每日竞赛,指定日期:{},活动正常过期,允许发放奖励", assignDayStr);
                    permitSendFlag = true;
                }
            }
            if(permitSendFlag){
                ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO = this.getActivityByActivityId(siteActivityBasePO.getId(),siteCode);
                BeanUtils.copyProperties(siteActivityBasePO, activityDailyCompetitionRespVO);
                return activityDailyCompetitionRespVO;
            }
        }
        return null;
    }

    @Override
    public void awardActive(SiteVO siteVO, String param) {
        String siteCode = siteVO.getSiteCode();
        String timezone = siteVO.getTimezone();
        log.info("每日竞赛,执行发奖:{},{}",siteCode,param);
        String lock = String.format(RedisConstants.SITE_DAILY_ROBOT_LOCK,siteCode);
        String lockCode = RedisUtil.acquireImmediate(lock, 600L);
        try {
            long currentTimeMillis = System.currentTimeMillis();
            Long yestDayStartTime = TimeZoneUtils.getStartOfYesterdayInTimeZone(currentTimeMillis, timezone);
            if (StringUtils.isNotBlank(param)) {
                yestDayStartTime = Long.parseLong(param);
            }

            //查询符合条件的每日竞赛配置
            ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO = findActivityDailyCompetition(siteCode,yestDayStartTime,siteVO.getTimezone());

            if (ObjectUtil.isEmpty(activityDailyCompetitionRespVO)) {
                log.info("{},执行失败,活动详情未查到", getActivity().getName());
                return;
            }

            //活动开始时间
            Long activityStartTime = activityDailyCompetitionRespVO.getActivityStartTime();
            if (ObjectUtil.isEmpty(activityStartTime)) {
                log.info("siteCode:{},{},执行失败,活动开始时间未查到", siteCode, getActivity().getName());
                return;
            }

            //获取出活动开始日的当天开始时间戳
            Long activityStartDayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(activityStartTime, siteVO.getTimezone());

            //获取出当天开始日的当天开始时间戳
            Long toDayStartDayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());


            //当天是活动开始日.不给予发放活动奖励
            if (activityStartDayStartTime.equals(toDayStartDayStartTime)) {
                log.info("{},当天是活动开始日.不给予发放活动奖励", getActivity().getName());
                return;
            }

            //获取站点的币种汇率
            Map<String, BigDecimal> finalRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);

            if (ObjectUtil.isEmpty(finalRateMap)) {
                log.info("{},获取站点的币种汇率失败,siteCode:{}", getActivity().getName(), siteCode);
                return;
            }

            List<ActivityDailyCompetitionDetailRespVO> list = activityDailyCompetitionRespVO.getList();

            //竞赛
            for (ActivityDailyCompetitionDetailRespVO venueDetail : list) {
                log.info("每日竞赛,siteCode:{},{},发放每日竞赛的活动奖励:venueCode:{}", siteCode, getActivity().getName(), venueDetail.getVenueCodeList());
                sendUserActivity(venueDetail, activityDailyCompetitionRespVO.getActivityNo(), activityDailyCompetitionRespVO.getWashRatio(), siteVO, yestDayStartTime);
            }

            //每日竞赛初始化机器人数据
            siteActivityDailyRobotService.initDailyRobot(siteVO.getSiteCode());
        } catch (Exception e) {
            log.error("每日竞赛异常", e);
        } finally {
            if (ObjectUtil.isNotEmpty(lockCode)) {
                boolean release = RedisUtil.release(lock, lockCode);
                log.info("每日竞赛:{},执行结束,删除锁:{}", lock, release);
            }
        }


    }

    /**
     * 发放每日竞赛的活动奖励
     *
     * @param activityDetail 活动详情配置
     * @param siteVO         站点
     */
    private void sendUserActivity(ActivityDailyCompetitionDetailRespVO activityDetail, String activityNo,
                                  BigDecimal washRatio, SiteVO siteVO, Long yestDayStartTime) {

        ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO
                .builder()
                .siteCode(siteVO.getSiteCode())
                .venueCodeList(activityDetail.getVenueCodeList())
                .dayMillis(yestDayStartTime)
                .build();
        List<ReportUserVenueBetsTopVO> activityUserList = reportUserVenueFixedWinLoseApi.queryUserBetsTopPlatBetAmount(userTopReqVO);

        if (CollectionUtil.isEmpty(activityUserList)) {
            log.info("发放每日竞赛的活动奖励,没有要发的真实用户,siteCode:{},venueCode:{},yestDayStartTime:{},venueCode:{}",
                    siteVO.getSiteCode(), activityDetail.getVenueCodeList(), yestDayStartTime, activityDetail.getVenueCodeList());
//            return;
        }

        //奖励配置
        List<SiteActivityDailyCompetitionDetail> activityConfigList = activityDetail.getActivityDetail();

        if (CollectionUtil.isEmpty(activityConfigList)) {
            log.info("发放每日竞赛的活动奖励,没配置彩金,siteCode:{},venueCode:{},yestDayStartTime:{}", siteVO.getSiteCode(), activityDetail.getVenueCodeList(), yestDayStartTime);
            return;
        }

        Map<Integer, SiteActivityDailyCompetitionDetail> activityConfigMap = activityConfigList.stream().
                collect(Collectors.toMap(SiteActivityDailyCompetitionDetail::getRanking, SiteActivityDailyCompetitionDetail -> SiteActivityDailyCompetitionDetail));

        //获取出奖励的配置 底池 + 场馆池
        BigDecimal totalAward = siteActivityDetailService.getTotalRewardsAmount(activityDetail, siteVO.getSiteCode(), yestDayStartTime, siteVO.getTimezone());

        //得到排行榜的数据 type , true=机器人,false=真实用户打码
        List<ActivityRankingDailyVO> rankingDailyList = siteActivityDetailService.updateRankings(activityDetail, activityUserList, siteVO.getSiteCode(), totalAward);

        if (CollectionUtil.isEmpty(rankingDailyList)) {
            log.info("发放每日竞赛的活动奖励,排行榜没有数据,siteCode:{},venueCode:{},yestDayStartTime:{}", siteVO.getSiteCode(), activityDetail.getVenueCodeList(), yestDayStartTime);
            return;
        }

        log.info("发放每日竞赛的活动奖励:竞赛用户列表前100名:{},venueCode:{}", activityDetail.getVenueCodeList(), JSON.toJSON(rankingDailyList));

        //取出前50条数据 第1名-第50名
        rankingDailyList = rankingDailyList.stream().sorted(Comparator.comparing(ActivityRankingDailyVO::getRanking)).limit(50).toList();

        //新增需要法奖的数据集合
        List<ActivitySendMqVO> sendList = Lists.newArrayList();


        //赠送免费旋转次数
        List<ActivityFreeGameVO> freeSpinList = Lists.newArrayList();

        //新增排行榜数据集合
        List<SiteActivityDailyRecordPO> recordList = Lists.newArrayList();

        String dateStr = Long.toString(yestDayStartTime);

        //将已经发放过的用户查出来.防止重复放
        List<SiteActivityDailyRecordPO> activityDailyRecord = siteActivityDailyRecordService.getBaseMapper().selectList(Wrappers
                .lambdaQuery(SiteActivityDailyRecordPO.class)
                .eq(SiteActivityDailyRecordPO::getDailyId, activityDetail.getId())
                .eq(SiteActivityDailyRecordPO::getSiteCode, siteVO.getSiteCode())
                .eq(SiteActivityDailyRecordPO::getDay, yestDayStartTime)
        );

        //机器人记录
        List<SiteActivityDailyRecordPO> robotRecordList = activityDailyRecord.stream()
                .filter(x -> x.getRole().equals(ActivityDailyEnum.ROBOT.getCode())).toList();
        Map<String, SiteActivityDailyRecordPO> robotRecordMap = robotRecordList.stream().collect(Collectors
                .toMap(SiteActivityDailyRecordPO::getUserId, SiteActivityDailyRecordPO -> SiteActivityDailyRecordPO));


        //真实用户记录
        List<SiteActivityDailyRecordPO> userRecordList = activityDailyRecord.stream()
                .filter(x -> x.getRole().equals(ActivityDailyEnum.REAL_USER.getCode())).toList();
        Map<String, SiteActivityDailyRecordPO> userRecordMap = userRecordList.stream().collect(Collectors
                .toMap(SiteActivityDailyRecordPO::getUserId, SiteActivityDailyRecordPO -> SiteActivityDailyRecordPO));


        List<String> userIds = rankingDailyList.stream()
                .filter(rankingDaily -> !rankingDaily.getType() && activityConfigMap.containsKey(rankingDaily.getRanking()))
                .map(ActivityRankingDailyVO::getUserId)
                .toList();


        List<UserInfoVO> userInfoVOList = userInfoApi.getUserInfoByUserIds(userIds);
        Map<String, UserInfoVO> userInfoVOMap = userInfoVOList.stream().collect(Collectors.toMap(UserInfoVO::getUserId, UserInfoVO -> UserInfoVO));


        for (ActivityRankingDailyVO rankingDaily : rankingDailyList) {

            //奖励币种
            String amountCurrencyCode = rankingDaily.getAwardCurrencyCode();

            //打码币种
            String currencyCode = rankingDaily.getCurrencyCode();

            String roleIcon = null;
            //第一名是机器人
            if (Objects.equals(rankingDaily.getRanking(), 1) && rankingDaily.getType()) {
                roleIcon = getTop1RoleUserIcon(siteVO.getSiteCode());
            }

            SiteActivityDailyRecordPO recordPO = SiteActivityDailyRecordPO
                    .builder()
                    .dailyId(activityDetail.getId())
                    .venueType(activityDetail.getVenueType())
                    .siteCode(siteVO.getSiteCode())
                    .userAccount(rankingDaily.getUserAccount())
                    .userId(rankingDaily.getUserId())
                    .role(rankingDaily.getType() ? ActivityDailyEnum.ROBOT.getCode() : ActivityDailyEnum.REAL_USER.getCode())
                    .ranking(rankingDaily.getRanking())
                    .awardAmount(rankingDaily.getAwardAmount())
                    .awardPercentage(rankingDaily.getActivityAmountPer())
                    .activityDiscountType(activityDetail.getActivityDiscountType())
                    .awardCurrency(amountCurrencyCode)//用户发奖励的币种
                    .currency(currencyCode)//投注币种
                    .day(yestDayStartTime)
                    .betAmount(rankingDaily.getBetAmount())
                    .roleIcon(roleIcon)
                    .build();


            //机器人
            if (rankingDaily.getType() && robotRecordMap.get(rankingDaily.getUserId()) == null) {
                recordList.add(recordPO);
            }

            //真实用户
            if (!rankingDaily.getType() && userRecordMap.get(rankingDaily.getUserId()) == null) {
                recordList.add(recordPO);
            }


            //机器人数据-不需要奖励
            if (rankingDaily.getType()) {
                continue;
            }

            //排名没有配置奖励,不发奖
            SiteActivityDailyCompetitionDetail siteActivityDailyCompetitionDetail = activityConfigMap.get(rankingDaily.getRanking());
            if (ObjectUtil.isEmpty(siteActivityDailyCompetitionDetail)) {
                continue;
            }

            UserInfoVO userInfoVO = userInfoVOMap.get(rankingDaily.getUserId());

            UserBaseReqVO baseReqVO = UserBaseReqVO.builder()
                    .userAccount(rankingDaily.getUserAccount())
                    .userId(rankingDaily.getUserId())
                    .activityId(activityDetail.getActivityId())
                    .siteCode(siteVO.getSiteCode())
                    .timezone(siteVO.getTimezone())
                    .applyFlag(false)
                    .build();

            ToActivityVO toActivityVO = null;
            ActivityActionContext activityActionContext = applicationContext.getBean(ActivityActionContext.class);
            try {
                toActivityVO = activityActionContext.checkToActivity(baseReqVO);
                log.info("发放每日竞赛的活动奖励,调用活动校验:venueCode:{},param:{},result:{}", activityDetail.getVenueCodeList(), baseReqVO, toActivityVO);
            } catch (Exception e) {
                log.info("发放每日竞赛的活动奖励,调用活动校验异常,不允许发放,param:{}", baseReqVO, e);
                continue;
            }
            if (toActivityVO == null) {
                log.info("发放每日竞赛的活动奖励,调用活动校验返回null,不允许发放,venueCode:{},param:{}", activityDetail.getVenueCodeList(), baseReqVO);
                continue;
            }

            if (toActivityVO.getStatus() != ResultCode.SUCCESS.getCode()) {
                log.info("发放每日竞赛的活动奖励,活动校验失败,不允许发放,venueCode:{},result:{},param:{}", activityDetail.getVenueCodeList(), toActivityVO, baseReqVO);
                continue;
            }

            //防止重复发放,先判断用户是否已经发放过
            if (userRecordMap.get(rankingDaily.getUserId()) == null) {
                BigDecimal runningWater = getActivityOrderRunningWater(userInfoVO, washRatio, rankingDaily.getAwardAmount(), amountCurrencyCode);
                String orderNo = OrderNoUtils.genOrderNo(rankingDaily.getUserId(), activityDetail.getComNo(), dateStr);
                ActivitySendMqVO sendMqVO = ActivitySendMqVO.builder()
                        .orderNo(orderNo)
                        .siteCode(siteVO.getSiteCode())
                        .userId(rankingDaily.getUserId())
                        .activityId(activityDetail.getActivityId())
                        .currencyCode(amountCurrencyCode)
                        .runningWater(runningWater)
                        .runningWaterMultiple(washRatio)
                        .activityTemplate(getActivity().getType())
                        .distributionType(ActivityDistributionTypeEnum.IMMEDIATE.getCode())
                        .activityAmount(rankingDaily.getAwardAmount())
                        .build();
                sendList.add(sendMqVO);

                if (siteActivityDailyCompetitionDetail.getFreeTimes() != null && siteActivityDailyCompetitionDetail.getFreeTimes() > 0) {
                    ActivityFreeGameVO freeGameVO = new ActivityFreeGameVO();
                    freeGameVO.setSiteCode(siteVO.getSiteCode());
                    freeGameVO.setOrderNo(orderNo);
                    freeGameVO.setActivityNo(activityNo);
                    freeGameVO.setUserId(rankingDaily.getUserId());
                    freeGameVO.setActivityTemplate(getActivity().getType());
                    freeGameVO.setActivityId(activityDetail.getActivityId());
                    freeGameVO.setActivityTemplateName(activityDetail.getActivityNameI18nCode());
                    freeGameVO.setAcquireNum(siteActivityDailyCompetitionDetail.getFreeTimes());
                    freeGameVO.setCurrencyCode(rankingDaily.getCurrencyCode());
                    freeGameVO.setAccessParameters(siteActivityDailyCompetitionDetail.getAccessParameters());
                    freeGameVO.setVenueCode(siteActivityDailyCompetitionDetail.getVenueCode());
                    freeGameVO.setBetLimitAmount(siteActivityDailyCompetitionDetail.getBetLimitAmount());
                    freeSpinList.add(freeGameVO);
                }
            }
        }

        if (CollectionUtil.isNotEmpty(recordList)) {
            Boolean saveBool = siteActivityDailyRecordService.saveBatch(recordList);
            log.info("新增排行榜记录:{},siteCode:{},saveBool:{},venueCode:{}", getActivity().getName(), siteVO.getSiteCode(), saveBool, activityDetail.getVenueCodeList());
            if (!saveBool) {
                return;
            }
        }

        if (CollectionUtil.isNotEmpty(sendList)) {
            log.info("发送活动礼金消息:{},siteCode:{},venueCode:{}", getActivity().getName(), siteVO.getSiteCode(), activityDetail.getVenueCodeList());
            KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, ActivitySendListMqVO.builder().list(sendList).build());
        }

        if (CollectionUtil.isNotEmpty(freeSpinList)) {
            log.info("发送活动免费旋转消息:{},siteCode:{},venueCode:{}", getActivity().getName(), siteVO.getSiteCode(), activityDetail.getVenueCodeList());
            // todo 添加三个参数
            KafkaUtil.send(TopicsConstants.FREE_GAME, ActivityFreeGameTriggerVO.builder().freeGameVOList(freeSpinList).build());
        }
    }


    private String getTop1RoleUserIcon(String siteCode) {
        try {
            //随机取一个头像
            SiteUserAvatarConfigRespVO siteUserAvatarConfigRespVO = siteUserAvatarConfigApi.getRandomUserAvatar(siteCode);
            if (ObjectUtil.isNotEmpty(siteUserAvatarConfigRespVO)) {
                return siteUserAvatarConfigRespVO.getAvatarImageUrl();
            }
        } catch (Exception e) {
            log.info("每日竞赛获取机器人第一名头像异常:", e);
        }
        return null;
    }


    /**
     * @param userInfoVO           用户信息
     * @param washRatio            活动配置
     * @param activityAmount       赠送的彩金
     * @param activityCurrencyCode 发放的礼金币种
     * @return 打码量
     */
    private BigDecimal getActivityOrderRunningWater(UserInfoVO userInfoVO, BigDecimal washRatio,
                                                    BigDecimal activityAmount, String activityCurrencyCode) {


        if (washRatio == null || washRatio.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("发放礼包,打码倍数为空,不需要增加打码,activity:{}", getActivity().getName());
            return BigDecimal.ZERO;
        }

        //打码量只能是法币 , 如果是平台币 则需要转
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(activityCurrencyCode)) {
            PlatCurrencyFromTransferVO transferVO = PlatCurrencyFromTransferVO
                    .builder()
                    .siteCode(userInfoVO.getSiteCode())
                    .sourceAmt(activityAmount)
                    .targetCurrencyCode(userInfoVO.getMainCurrency())
                    .build();
            //平台币转法币
            ResponseVO<SiteCurrencyConvertRespVO> siteCurrencyConvertRespVOResponseVO = siteCurrencyInfoApi.transferToMainCurrency(transferVO);
            if (!siteCurrencyConvertRespVOResponseVO.isOk()) {
                log.info("发送礼金,平台币转法币失败:activity:[{}]", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            activityAmount = siteCurrencyConvertRespVOResponseVO.getData().getTargetAmount();
            log.info("发放礼包,增加打码量:activity:{},user:{},转换前币种:{},金额:{},转化后币种:{},金额:{}",
                    getActivity().getName(), userInfoVO.getUserId(), CommonConstant.PLAT_CURRENCY_CODE,
                    activityAmount,
                    siteCurrencyConvertRespVOResponseVO.getData().getTargetCurrencyCode(), activityAmount);
        }

        //打码量 = 彩金 * 倍数
        BigDecimal typingAmount = washRatio.multiply(activityAmount);

        if (typingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("打码量计算有误:{},不增加打码量", getActivity().getName());
            return BigDecimal.ZERO;
        }
        return typingAmount;
    }


    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO
     */
    @Override
    public void checkSecond(ActivityConfigVO activityConfigVO) {
        ActivityDailyCompetitionVO activityDailyCompetitionVO = activityConfigVO.getActivityDailyCompetitionVO();

        if (ObjectUtil.isEmpty(activityDailyCompetitionVO) || CollectionUtil.isEmpty(activityDailyCompetitionVO.getList())) {
            log.info("{},缺少活动详情参数", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }


        List<VenueInfoVO> allVenueList = venueInfoApi.venueInfoList().getData();

        validParamAmount(activityDailyCompetitionVO);


        for (ActivityDailyCompetitionDetailVO tmp : activityDailyCompetitionVO.getList()) {
            if (ObjectUtil.isEmpty(tmp.getInitAmount())) {
                log.info("{},活动详情参数,初始化金额为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }


            if (ObjectUtil.isEmpty(tmp.getVenuePercentage()) || tmp.getVenuePercentage().compareTo(BigDecimal.ZERO) < 0) {
                log.info("{},活动详情参数,场馆总流水比例参数异常", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if (ObjectUtil.isEmpty(tmp.getActivityDiscountType()) || ObjectUtil.isEmpty(ActivityDiscountTypeEnum.fromType(tmp.getActivityDiscountType()))) {
                log.info("{},活动详情参数,奖励方式,参数异常", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            List<SiteActivityDailyCompetitionDetail> activityDetail = tmp.getActivityDetail();

            if (CollectionUtil.isEmpty(activityDetail)) {
                log.info("{},活动详情配置集合参数异常", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if (activityDetail.size() > 50) {
                log.info("{},活动详情配置集合参数异常,最少配置10条排行榜", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.INCORRECT_RANKING_SETTINGS);
            }

            //百分比总数
            BigDecimal activityAmountPerSum = BigDecimal.ZERO;

            for (SiteActivityDailyCompetitionDetail detail : activityDetail) {

                Integer freeTimes = detail.getFreeTimes();
                if (ObjectUtil.isEmpty(freeTimes) || freeTimes < 0) {
                    log.info("{},活动详情配置集合参数异常, 免费旋转次数 必须大于0", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }


                if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(tmp.getActivityDiscountType())) {

                    BigDecimal activityAmountPer = detail.getActivityAmountPer();
                    if (ObjectUtil.isEmpty(activityAmountPer) || activityAmountPer.compareTo(BigDecimal.ZERO) <= 0) {
                        log.info("{},活动详情配置集合参数异常,百分比类型, 彩金百分比 必须大于0", getActivity().getName());
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }

                    activityAmountPerSum = activityAmountPerSum.add(activityAmountPer);

                } else {
                    BigDecimal activityAmount = detail.getActivityAmount();
                    if (ObjectUtil.isEmpty(activityAmount) || activityAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        log.info("{},活动详情配置集合参数异常,固定金额类型, 彩金金额 必须大于0", getActivity().getName());
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                }
            }

            if (activityAmountPerSum.compareTo(BigDecimal.valueOf(100)) > 0) {
                log.info("{},活动详情配置集合参数异常,百分比类型 百分比超出 100", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PERCENT_PARAMETER_ABNORMALITY);
            }


            Map<Integer, List<VenueInfoVO>> venueMap = allVenueList.stream().collect(Collectors.groupingBy(VenueInfoVO::getVenueType));

            List<VenueInfoVO> venueList = venueMap.get(tmp.getVenueType());

            if (CollectionUtil.isEmpty(venueList)) {
                log.info("{},活动详情参数,场馆类型异常,该场馆类型下没查到场馆", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

//            List<String> venueCodeList = venueList.stream().map(VenueInfoVO::getVenueCode).toList();
//            for (String venueCode : tmp.getVenueCodeList()) {
//                if (!venueCodeList.contains(venueCode)) {
//                    log.info("{},活动详情参数,场馆异常:场馆类型:{},下没有:{},场馆", getActivity().getName(), tmp.getVenueType(), venueCode);
//                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//                }
//            }
        }


    }


    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        siteActivityDailyCompetitionService
                .getBaseMapper()
                .delete(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                        .eq(SiteActivityDailyCompetitionPO::getActivityId, vo.getId()));

        siteActivityDailyRankingService.getBaseMapper()
                .delete(Wrappers.lambdaQuery(SiteActivityDailyRankingPO.class)
                        .eq(SiteActivityDailyRankingPO::getActivityId, vo.getId()));

    }

    public ToActivityVO toActivity(String activityBase, UserBaseReqVO userBaseReqVO) {
        return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).build();
    }


}
