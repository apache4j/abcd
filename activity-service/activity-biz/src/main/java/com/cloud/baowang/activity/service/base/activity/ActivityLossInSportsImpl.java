package com.cloud.baowang.activity.service.base.activity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityOrderRecordPO;
import com.cloud.baowang.activity.po.SiteActivityProfitRebatePO;
import com.cloud.baowang.activity.service.SiteActivityEventRecordService;
import com.cloud.baowang.activity.service.SiteActivityOrderRecordService;
import com.cloud.baowang.activity.service.SiteActivityProfitRebateService;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.user.api.vo.UserInfoResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserTopReqVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserInfoPageVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 体育负盈利-活动实现
 */
@Service
@AllArgsConstructor
@Slf4j
public class ActivityLossInSportsImpl implements ActivityBaseInterface<ActivityLossInSportsRespVO> {

    private final SiteActivityProfitRebateService siteActivityProfitRebateService;

    private final SiteActivityBaseService siteActivityBaseService;

    private final SiteActivityEventRecordService siteActivityEventRecordService;

    private final UserInfoApi userInfoApi;

    private final SiteActivityOrderRecordService siteActivityOrderRecordService;

    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;

    private final PlayVenueInfoApi venueInfoApi;

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SystemDictConfigApi systemDictConfigApi;

    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.LOSS_IN_SPORTS;
    }


    @Override
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {
        ActivityLossInSportsVO activity = JSONObject.parseObject(activityBaseVO, ActivityLossInSportsVO.class);
        if (ObjectUtil.isEmpty(activity)) {
            return false;
        }
        checkSecond(ActivityConfigVO.builder().activityLossInSportsVO(activity).build());

        boolean saveBool = siteActivityProfitRebateService.save(activity, baseId);

        if (!saveBool) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        return true;
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO, String baseId) {

        ActivityLossInSportsVO activity = JSONObject.parseObject(activityBaseVO, ActivityLossInSportsVO.class);

        if (ObjectUtil.isEmpty(activity)) {
            return false;
        }

        LambdaQueryWrapper<SiteActivityProfitRebatePO> wrapper = Wrappers.lambdaQuery(SiteActivityProfitRebatePO.class)
                .eq(SiteActivityProfitRebatePO::getActivityId, baseId);

        SiteActivityProfitRebatePO siteActivityProfitRebatePO = siteActivityProfitRebateService.getBaseMapper().selectOne(wrapper);

        if (ObjectUtil.isEmpty(siteActivityProfitRebatePO)) {
            return false;
        }

        if (siteActivityProfitRebateService.getBaseMapper().delete(wrapper) <= 0) {
            return false;
        }

        return saveActivityDetail(activityBaseVO, baseId);
    }


    @Override
    public ActivityLossInSportsRespVO getActivityByActivityId(String activityId,String siteCode) {
        return siteActivityProfitRebateService.getActivityByActivityId(activityId);
    }

    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getActivityLossInSportsVO());
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
        siteActivityOrderRecordService.awardExpire(siteVO, getActivity());
    }

    @Override
    public void awardActive(SiteVO siteVO,String param) {
        List<SiteActivityBasePO> siteActivityBaseList = siteActivityBaseService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.LOSS_IN_SPORTS.getType())
                .eq(SiteActivityBasePO::getSiteCode, siteVO.getSiteCode())
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode()));

        if (CollectionUtil.isEmpty(siteActivityBaseList)) {
            log.info("活动:{},未配置或者未开启", getActivity().getName());
            return;
        }

        for (SiteActivityBasePO base : siteActivityBaseList) {
            participationActivity(siteVO, base);
        }

    }


    /**
     * 负盈利活动执行
     *
     * @param siteVO     站点
     * @param basePO 活动ID
     */
    private void participationActivity(SiteVO siteVO, SiteActivityBasePO basePO) {
        ActivityLossInSportsRespVO config = getActivityByActivityId(basePO.getId(),siteVO.getSiteCode());
        BeanUtils.copyProperties(basePO, config);
        if (ObjectUtil.isEmpty(config)) {
            log.info("负盈利活动执行异常,未查到活动详情:{}", basePO.getId());
            return;
        }

        Integer participationMode = config.getParticipationMode();
        //自动参与
        if (ActivityParticipationModeEnum.AUTO.getCode().equals(participationMode)) {
            autoParticipationActivity(siteVO, config);
        }
    }

    /**
     * 自动-参与活动
     *
     * @param siteVO 站点
     * @param config 活动信息
     */
    private void autoParticipationActivity(SiteVO siteVO, ActivityLossInSportsRespVO config) {

        Long registerTime = null;
        //如果新注册用的类型,则取出这类用户
        if (ActivityUserTypeEnum.NEW_REG_USER.getCode().equals(config.getUserType())) {
            //注册多少天的会员,则以当前时间为基准,以负数,往前推天数,获取到时间戳去查询 在这个时间戳之前注册的用户数据
            registerTime = TimeZoneUtils.getTimestampByDays(System.currentTimeMillis(), -config.getRegisterDay());
        }


        int pageSize = 100;
        int pageNumber = 1;
        boolean hasNext = true;
        while (hasNext) {
            UserInfoPageVO userVO = new UserInfoPageVO();
            userVO.setAccountType(List.of(config.getAccountType()));//账户类型 正式 测试
            userVO.setRegisterTimeEnd(registerTime);//在指定注册时间之前注册的用户
            userVO.setPageNumber(pageNumber);
            userVO.setPageSize(pageSize);
            ResponseVO<Page<UserInfoResponseVO>> data = userInfoApi.getPage(userVO);
            Page<UserInfoResponseVO> iPage =new Page<>();
            if (data.isOk()){
                iPage=data.getData();
            }
            List<UserInfoResponseVO> userAccountList = iPage.getRecords();
            List<String> userList = userAccountList.stream().map(UserInfoResponseVO::getUserId).toList();
            //过滤掉已经发放过的用户
            userList = siteActivityOrderRecordService.getActivityOrderRecordFilterUsersList(siteVO, userList, config.getCalculateType(), getActivity().getType());
            sendActivityOrder(siteVO, userList, config);
            // 判断是否还有下一页
            hasNext = iPage.hasNext();
            pageNumber++;
        }


    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO
     */
    @Override
    public void checkSecond(ActivityConfigVO activityConfigVO) {
        ActivityLossInSportsVO activityLossInSportsVO = activityConfigVO.getActivityLossInSportsVO();

        ActivityDiscountTypeEnum activityDiscountTypeEnum = ActivityDiscountTypeEnum.fromType(activityLossInSportsVO.getActivityDiscountType());
        if (ObjectUtil.isEmpty(activityLossInSportsVO.getActivityDiscountType()) ||
                activityDiscountTypeEnum == null) {
            log.info("{},活动详情参数,奖励方式,参数异常", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (ObjectUtil.isEmpty(activityLossInSportsVO.getVenueType())) {
            log.info("{},活动详情参数,场馆类型字段缺失", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (CollectionUtil.isEmpty(activityLossInSportsVO.getVenueCodeList())) {
            log.info("{},活动详情参数,场馆字段缺失", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (ObjectUtil.isEmpty(activityLossInSportsVO.getUserType()) || ActivityUserTypeEnum.fromCode(activityLossInSportsVO.getUserType()) == null) {
            log.info("{},活动详情参数,活动对象,参数异常", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (activityLossInSportsVO.getUserType().equals(ActivityUserTypeEnum.NEW_REG_USER.getCode()) &&
                (ObjectUtil.isEmpty(activityLossInSportsVO.getRegisterDay()) || activityLossInSportsVO.getRegisterDay() < 0)) {
            log.info("{},活动详情参数,新注册会员,注册天数参数异常", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        Integer calculateType = activityLossInSportsVO.getCalculateType();
        if (ObjectUtil.isEmpty(calculateType) || ActivityCalculateTypeEnum.fromCode(calculateType) == null) {
            log.info("{},活动详情参数,结算周期,参数异常", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        Integer distributionType = activityLossInSportsVO.getDistributionType();
        if (ObjectUtil.isEmpty(distributionType) || ActivityDistributionTypeEnum.fromCode(distributionType) == null) {
            log.info("{},活动详情参数,派发方式,参数异常", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        Integer participationMode = activityLossInSportsVO.getParticipationMode();
        if (ObjectUtil.isEmpty(participationMode) || ActivityParticipationModeEnum.fromCode(participationMode) == null) {
            log.info("{},活动详情参数,参与方式,参数异常", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (activityDiscountTypeEnum.getType().equals(ActivityDiscountTypeEnum.PERCENTAGE.getType())) {

            RechargePercentageVO percentageVO = activityLossInSportsVO.getPercentageVO();
            if (percentageVO == null) {
                log.info("{},活动详情参数,百分比的配置下,百分比类型对应条件值不可空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            BigDecimal minDeposit = percentageVO.getMinDeposit();
            if (minDeposit == null) {
                log.info("{},活动详情参数,百分比的配置下,最小存款金额不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if (minDeposit.compareTo(BigDecimal.ZERO) < 0) {
                log.info("{},活动详情参数,百分比的配置下,最小存款金额不能为负数", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            BigDecimal discountPct = percentageVO.getDiscountPct();
            if (discountPct == null) {
                log.info("{},活动详情参数,百分比的配置下,优惠百分比不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if (discountPct.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{},活动详情参数,百分比的配置下,优惠百分比不能为负数或0", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
            if (maxDailyBonus == null) {
                log.info("{},活动详情参数,百分比的配置下,单日最高赠送金额不能为空", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if (maxDailyBonus.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{},活动详情参数,百分比的配置下,单日最高赠送金额不能为负数或0", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

        }

        if (activityDiscountTypeEnum.getType().equals(ActivityDiscountTypeEnum.FIXED_AMOUNT.getType())) {
            List<SiteActivityProfitRebateDetail> activityDetail = activityLossInSportsVO.getActivityDetail();

            if (CollectionUtil.isEmpty(activityDetail)) {
                log.info("{},活动详情参数,固定金额:活动详情配置,参数异常", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            for (SiteActivityProfitRebateDetail detail : activityDetail) {
                if (ObjectUtil.isEmpty(detail.getStartAmount()) || ObjectUtil.isEmpty(detail.getEndAmount()) || ObjectUtil.isEmpty(detail.getRebateAmount())) {
                    log.info("{},活动详情参数,固定金额:活动详情配置,返回配置,参数异常", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (detail.getStartAmount().compareTo(BigDecimal.ZERO) < 0 || detail.getEndAmount().compareTo(BigDecimal.ZERO) < 0
                        || detail.getRebateAmount().compareTo(BigDecimal.ZERO) < 0) {
                    log.info("{},活动详情参数,固定金额:活动详情配置,返回配置,金额小于0,参数异常", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }


                if (detail.getStartAmount().compareTo(detail.getEndAmount()) > 0) {
                    log.info("{},活动详情参数,固定金额:活动详情配置,返回配置, 开始金额,大于结束金额,参数异常", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }
        }


        List<VenueInfoVO> allVenueList = venueInfoApi.venueInfoList().getData();

        Map<Integer, List<VenueInfoVO>> venueMap = allVenueList.stream().collect(Collectors.groupingBy(VenueInfoVO::getVenueType));

        List<VenueInfoVO> venueList = venueMap.get(activityLossInSportsVO.getVenueType());

        if (CollectionUtil.isEmpty(venueList)) {
            log.info("{},活动详情参数,场馆类型异常,该场馆类型下没查到场馆", getActivity().getName());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

//        List<String> venueCodeList = venueList.stream().map(VenueInfoVO::getVenueCode).toList();
//        for (String venueCode : activityLossInSportsVO.getVenueCodeList()) {
//            if (!venueCodeList.contains(venueCode)) {
//                log.info("{},活动详情参数,场馆异常:场馆类型:{},下没有:{},场馆", getActivity().getName(), activityLossInSportsVO.getVenueType(), venueCode);
//                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//            }
//        }
    }


    /**
     * @param siteVO                     站点
     * @param userList                   用户信息
     * @param activityLossInSportsRespVO 活动配置
     * @return
     */
    private List<ReportUserVenueBetsTopVO> getUserByCriteria(SiteVO siteVO, List<String> userList, ActivityLossInSportsRespVO activityLossInSportsRespVO) {
        if (CollectionUtil.isEmpty(userList)) {
            return List.of();
        }
        long nowTime = System.currentTimeMillis();
        Long startTime = null;
        Long endTime = null;

        //查询 昨天的开始时间到今天的00点
        if (activityLossInSportsRespVO.getCalculateType().equals(ActivityCalculateTypeEnum.DAY.getCode())) {
            long toDayTime = TimeZoneUtils.adjustTimestamp(nowTime, -1, siteVO.getTimezone());
            startTime = TimeZoneUtils.getStartOfDayInTimeZone(toDayTime, siteVO.getTimezone());
            endTime = TimeZoneUtils.getEndOfDayInTimeZone(toDayTime, siteVO.getTimezone());
        } else if (activityLossInSportsRespVO.getCalculateType().equals(ActivityCalculateTypeEnum.WEEK.getCode())) {//查询当前周第一天到当前周最后一天
            startTime = TimeZoneUtils.getStartOfWeekInTimeZone(nowTime, siteVO.getTimezone());
            endTime = TimeZoneUtils.getEndOfWeekInTimeZone(nowTime, siteVO.getTimezone());
        } else if (activityLossInSportsRespVO.getCalculateType().equals(ActivityCalculateTypeEnum.MONTH.getCode())) {//查询当月第一天到当月最后一天
            startTime = TimeZoneUtils.getStartOfMonthInTimeZone(nowTime, siteVO.getTimezone());
            endTime = TimeZoneUtils.getEndOfMonthInTimeZone(nowTime, siteVO.getTimezone());
        }

        ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO.builder()
                .userIdList(userList)
                .startTime(startTime)
                .endTime(endTime)
                .siteCode(siteVO.getSiteCode())
                .venueCodeList(activityLossInSportsRespVO.getVenueCodeList())
                .build();
        userTopReqVO.setPageNumber(1);
        userTopReqVO.setPageSize(1000);
        Page<ReportUserVenueBetsTopVO> page = reportUserVenueWinLoseApi.queryUserBetsTop(userTopReqVO);

        List<ReportUserVenueBetsTopVO> userVenueBetsTopVOS = page.getRecords();

        if (CollectionUtil.isEmpty(userVenueBetsTopVOS)) {
            log.info("{},手动派发流程,没有负数的输赢玩家", getActivity().getName());
            return List.of();
        }

        //该字段为负数，>= 0则代表赢钱,赢的忽略 输的才符合
        userVenueBetsTopVOS = userVenueBetsTopVOS.stream().filter(x -> x.getWinLossAmount().compareTo(BigDecimal.ZERO) <= 0).toList();
        if (CollectionUtil.isEmpty(userVenueBetsTopVOS)) {
            log.info("{},手动派发流程,过滤大于0的,没有负数的输赢玩家", getActivity().getName());
            return List.of();
        }

        return userVenueBetsTopVOS;

    }

    /**
     * @param userInfoVO                 用户信息
     * @param activityLossInSportsRespVO 活动配置
     * @param activityAmount             赠送的彩金
     * @param activityCurrencyCode       发放的礼金币种
     * @return 打码量
     */
    private BigDecimal getActivityOrderRunningWater(UserInfoVO userInfoVO, ActivityLossInSportsRespVO activityLossInSportsRespVO,
                                                    BigDecimal activityAmount, String activityCurrencyCode) {

        BigDecimal runningWaterMultiple = activityLossInSportsRespVO.getWashRatio();

        if (runningWaterMultiple == null || runningWaterMultiple.compareTo(BigDecimal.ZERO) <= 0) {
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
        BigDecimal typingAmount = runningWaterMultiple.multiply(activityAmount);

        if (typingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("打码量计算有误:{},不增加打码量", getActivity().getName());
            return BigDecimal.ZERO;
        }
        return typingAmount;
    }


    /**
     * 校验是否满足活动条件
     *
     * @param siteVO   站点
     * @param userList 用户
     * @param base     配置
     * @return 满足条件
     */
    public List<ActivitySendMqVO> validateActivityConditionsList(SiteVO siteVO, List<String> userList, ActivityBaseRespVO base) {
        ActivityLossInSportsRespVO activityLossInSportsRespVO = (ActivityLossInSportsRespVO) base;
        List<ReportUserVenueBetsTopVO> userVenueBetsTopVOS = getUserByCriteria(siteVO, userList, activityLossInSportsRespVO);
        if (CollectionUtil.isEmpty(userVenueBetsTopVOS)) {
            return Lists.newArrayList();
        }

        String siteCode = siteVO.getSiteCode();

        List<ActivitySendMqVO> list = Lists.newArrayList();

        Map<String, BigDecimal> curencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if (CollectionUtil.isEmpty(curencyRateMap)) {
            log.info("负盈利:没获取到站点的币种汇率:{}", siteVO);
            return Lists.newArrayList();
        }

        List<UserInfoVO> userInfoVOList = userInfoApi.getByUserIds(userList, siteVO.getSiteCode());

        if (CollectionUtil.isEmpty(userInfoVOList)) {
            log.info("负盈利:没获取到用户信息:{}", siteVO);
            return Lists.newArrayList();
        }

        Double hourTime = Double.parseDouble("72");
        SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_BENEFIT_EXPIRATION_TIME.getCode(), siteCode).getData();
        if(ObjectUtil.isNotEmpty(systemDictConfigRespVO) && Integer.parseInt(systemDictConfigRespVO.getConfigParam()) > 0){
            hourTime = Double.parseDouble(systemDictConfigRespVO.getConfigParam());
        }



        Map<String, UserInfoVO> userInfoMap = userInfoVOList.stream().collect(Collectors.toMap(UserInfoVO::getUserId, UserInfoVO -> UserInfoVO));

        //固定金额配置
        List<SiteActivityProfitRebateDetail> activityDetail = activityLossInSportsRespVO.getActivityDetail();

        String toDayTime = String.valueOf(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), siteVO.getTimezone()));
        for (ReportUserVenueBetsTopVO betsTop : userVenueBetsTopVOS) {

            String orderNo = OrderNoUtils.genOrderNo(betsTop.getUserId(),
                    ActivityTemplateEnum.LOSS_IN_SPORTS.getSerialNo(), toDayTime);

            //奖负数 转成 正式对比
            BigDecimal winLossAmount = betsTop.getWinLossAmount().abs();

            //用户的法币
            UserInfoVO userInfoVO = userInfoMap.get(betsTop.getUserId());

            if (ObjectUtil.isEmpty(userInfoVO)) {
                log.info("{},用户信息未获取到:{}", getActivity().getName(), userInfoVO);
                continue;
            }
            String userCurrencyCode = userInfoVO.getMainCurrency();
            BigDecimal rate = curencyRateMap.get(userCurrencyCode);
            if (ObjectUtil.isEmpty(rate)) {
                log.info("{},用户币种汇率未取到,rate:{}", getActivity().getName(), rate);
                continue;
            }

            //法币转平台币
            BigDecimal platAmount = AmountUtils.divide(winLossAmount, rate);
            if (platAmount == null || platAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{},用户:{},币种:{},汇率转平台币失败:{}", getActivity().getName(), betsTop.getUserId(), userCurrencyCode, platAmount);
                continue;
            }

            //百分比
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(activityLossInSportsRespVO.getActivityDiscountType())) {

                //百分比配置
                RechargePercentageVO percentageVO = activityLossInSportsRespVO.getPercentageVO();

                //最小亏损金额
                BigDecimal minDeposit = percentageVO.getMinDeposit();

                //亏损百分比
                BigDecimal discountPct = percentageVO.getDiscountPct();

                //返还上限
                BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();

                //亏损金额 * 百分比 / 100
                BigDecimal activityAmount = platAmount.multiply(discountPct).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                //亏损的金额未达到配置的标准不处理
                if (platAmount.compareTo(minDeposit) < 0) {
                    log.info("{},用户:{},币种:{},亏损的法币金额:{},转成平台:{},最小配置:{},不满足",
                            getActivity().getName(), betsTop.getUserId(), userCurrencyCode, winLossAmount, platAmount, minDeposit);
                    continue;
                }

                //如果计算的金额超出最高阀值 则按照最高的配置金额
                if (activityAmount.compareTo(maxDailyBonus) >= 0) {
                    activityAmount = maxDailyBonus;
                }


                //防止赠送金额的结果是0或者空
                if (ObjectUtil.isEmpty(activityAmount) || activityAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("{},用户:{} 计算结果是0", getActivity().getName(), betsTop.getUserId());
                    continue;
                }

                //平台币转法币
                BigDecimal currencyAmount = AmountUtils.multiply(activityAmount, rate);

                log.info("活动名称: " + getActivity().getName() +
                        ", 用户: " + betsTop.getUserId() +
                        ", 币种: " + userCurrencyCode +
                        ", 亏损的法币金额: " + winLossAmount +
                        ", 转成平台币: " + platAmount +
                        ", 最小配置: " + minDeposit +
                        ", 亏损百分比: " + discountPct +
                        ", 上限: " + maxDailyBonus +
                        ", 礼金计算方式= " + platAmount + " * " + discountPct + " / 100 = " + activityAmount +
                        ", 在转成法币=: " + currencyAmount);


                //平台币转法币-结果是0或者空
                if (ObjectUtil.isEmpty(currencyAmount) || currencyAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("{},用户:{} ,平台币金额:{},平台币转法币,失败", getActivity().getName(), betsTop.getUserId(), activityAmount);
                    continue;
                }

                //打码
                BigDecimal runningWater = getActivityOrderRunningWater(userInfoVO, activityLossInSportsRespVO, currencyAmount, userInfoVO.getMainCurrency());


                ActivitySendMqVO activitySendMqVO = ActivitySendMqVO.builder()
                        .orderNo(orderNo)
                        .siteCode(siteCode)
                        .activityTemplate(getActivity().getType())
                        .userId(betsTop.getUserId())
                        .currencyCode(userInfoVO.getMainCurrency())
                        .runningWater(runningWater)
                        .runningWaterMultiple(activityLossInSportsRespVO.getWashRatio())
                        .activityAmount(currencyAmount)
                        .distributionType(activityLossInSportsRespVO.getDistributionType())
                        .activityId(activityLossInSportsRespVO.getActivityId())
                        .receiveStartTime(System.currentTimeMillis())
                        .receiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime))
                        .build();
                list.add(activitySendMqVO);
            }


            //固定金额 平台币
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(activityLossInSportsRespVO.getActivityDiscountType())) {

                //倒序拍,从大往前匹配
                activityDetail = activityDetail.stream().sorted(Comparator.comparing(SiteActivityProfitRebateDetail::getEndAmount).reversed()).toList();
                for (SiteActivityProfitRebateDetail detail : activityDetail) {

                    BigDecimal startAmount = detail.getStartAmount();

                    BigDecimal rebateAmount = detail.getRebateAmount();

                    //打码
                    BigDecimal runningWater = getActivityOrderRunningWater(userInfoVO, activityLossInSportsRespVO, rebateAmount, CommonConstant.PLAT_CURRENCY_CODE);


                    //因为这个匹配是从 大往小开始匹配，所以当你的值大于配置的开始金额,则取这个配置
                    if (platAmount.compareTo(startAmount) >= 0) {

                        log.info("活动名称: " + getActivity().getName() +
                                ", 用户: " + betsTop.getUserId() +
                                ", 币种: " + userCurrencyCode +
                                ", 亏损的法币金额: " + winLossAmount +
                                ", 转成平台币: " + platAmount +
                                ", 固定金额条件配置: " + startAmount +
                                ", 赠送平台币: " + rebateAmount);


                        ActivitySendMqVO activitySendMqVO = ActivitySendMqVO.builder()
                                .orderNo(orderNo)
                                .siteCode(siteCode)
                                .activityTemplate(getActivity().getType())
                                .userId(betsTop.getUserId())
                                .activityId(activityLossInSportsRespVO.getActivityId())
                                .activityAmount(rebateAmount)
                                .runningWater(runningWater)
                                .runningWaterMultiple(activityLossInSportsRespVO.getWashRatio())
                                .distributionType(activityLossInSportsRespVO.getDistributionType())
                                .currencyCode(CommonConstant.PLAT_CURRENCY_CODE)//平台币
                                .receiveStartTime(System.currentTimeMillis())
                                .receiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime))
                                .build();
                        list.add(activitySendMqVO);
                    }
                }
            }
        }

        return list;
    }


    /**
     * 执行发放逻辑
     *
     * @param userList 用户列表
     * @param base     配置信息
     */
    public Boolean sendActivityOrder(SiteVO siteVO, List<String> userList, ActivityBaseRespVO base) {
        //校验活动条件是否满足
        List<ActivitySendMqVO> list = validateActivityConditionsList(siteVO, userList, base);
        if (CollectionUtil.isEmpty(list)) {
            return false;
        }


        //针对百分比的配置下需要发奖的用户,要给他们加上他们的法定币种
        if (CollectionUtil.isNotEmpty(list)) {
            ActivitySendListMqVO activitySendListMqVO = ActivitySendListMqVO.builder()
                    .list(list)
                    .build();
            KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
        }
        return true;
    }

    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        siteActivityProfitRebateService.getBaseMapper().delete(Wrappers.lambdaQuery(SiteActivityProfitRebatePO.class)
                .eq(SiteActivityProfitRebatePO::getActivityId, vo.getId()));
    }

    /**
     * 传入的 vo.id 字段实际上就是 发起启用状态后传入的ID，allValidBasePos = 全部的启用的数据，需要在全部启用的当中去寻找是否与 vo.id的重复
     *
     * @param vo
     * @param allValidBasePos
     */
    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBasePO> allValidBasePos) {
        if (CollectionUtils.isEmpty(allValidBasePos)) {
            log.info("{}:不存在已开启的,可以直接操作", getActivity().getName());
            return;
        }
        String activityId = vo.getId();
        ActivityLossInSportsRespVO activityLossInSportsRespVO = siteActivityProfitRebateService.getActivityByActivityId(activityId);

        Integer venueType = activityLossInSportsRespVO.getVenueType();

        List<String> activityIds = allValidBasePos.stream().map(SiteActivityBasePO::getId).toList();

        List<SiteActivityProfitRebatePO> list = siteActivityProfitRebateService.getBaseMapper()
                .selectList(Wrappers.lambdaQuery(SiteActivityProfitRebatePO.class)
                        .in(SiteActivityProfitRebatePO::getActivityId, activityIds));

        List<Integer> venueTypeList = list.stream().map(SiteActivityProfitRebatePO::getVenueType).toList();

        if (venueTypeList.contains(venueType)) {
            log.info("{}:存在相同配置已开启,此活动:{}无法开启", getActivity().getName(), vo.getId());
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
    }

    public ToActivityVO toActivity(String activityBase, UserBaseReqVO userBaseReqVO) {

        String userId = userBaseReqVO.getUserId();
        String timezone = userBaseReqVO.getTimezone();
        String siteCode = userBaseReqVO.getSiteCode();

        ActivityLossInSportsRespVO respVO = JSONObject.parseObject(activityBase, ActivityLossInSportsRespVO.class);
        String activityTemplate = respVO.getActivityTemplate();
        Integer calculateType = respVO.getCalculateType();


        //新注册会员的类型
        if (ActivityUserTypeEnum.NEW_REG_USER.getCode().equals(respVO.getUserType())) {
            //体育负盈利返利活动 注册日期限制条件
            if (ActivityTemplateEnum.LOSS_IN_SPORTS.getType().equals(respVO.getActivityTemplate())) {
                boolean profitRebate = getProfitRebate(activityBase, userId, timezone, siteCode);
                if (!profitRebate) {
                    return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
                }
            }
        }


        //true = 能参与
        boolean activityCondition = siteActivityEventRecordService.toActivityEventRecordCount(
                SiteActivityEventRecordQueryParam.builder()
                        .activityId(respVO.getActivityId())
                        .siteCode(siteCode)
                        .userId(userId)
                        .calculateType(calculateType)
                        .timezone(timezone)
                        .build()
        ) <= 0;

        Long startTime = null;
        Long endTime = null;
        if (ObjectUtil.isNotEmpty(calculateType)) {
            if (calculateType.equals(ActivityCalculateTypeEnum.DAY.getCode())) {
                startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);
                endTime = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timezone);
            } else if (calculateType.equals(ActivityCalculateTypeEnum.WEEK.getCode())) {
                startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timezone);
                endTime = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), timezone);
            } else if (calculateType.equals(ActivityCalculateTypeEnum.MONTH.getCode())) {
                startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timezone);
                endTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), timezone);
            }
        }

        //如果确定参与记录不存在的情况下,在查询下订单的记录，如果订单记录存在，也是不允许点击
        if (!activityCondition || siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityId, respVO.getActivityId())
                .eq(SiteActivityOrderRecordPO::getUserId, userId)
                .between(ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime), SiteActivityOrderRecordPO::getCreatedTime, startTime, endTime)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)) > 0) {
            log.info("负盈利,申请参与:{},活动,siteCoe:{}userId:{},被拒绝,重复参与", activityTemplate, siteCode, userId);
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
        }

        SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();

        //查询用户符合条件
        List<ActivitySendMqVO> userList = validateActivityConditionsList(siteVO, Lists.newArrayList(userId), respVO);
        if (CollectionUtil.isEmpty(userList)) {
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
        }

        return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).message(ResultCode.SUCCESS.getMessageCode()).build();
    }


    public ActivityConfigDetailVO getConfigDetail(String activityBase, ActivityConfigDetailVO detailVO,
                                                  String siteCode, String timezone, String userId) {
        ActivityLossInSportsRespVO lossInSportsRespVO = JSONObject.parseObject(activityBase, ActivityLossInSportsRespVO.class);
        String activityTemplate = lossInSportsRespVO.getActivityTemplate();
        //新注册会员的类型
       /* if (ActivityUserTypeEnum.NEW_REG_USER.getCode().equals(lossInSportsRespVO.getUserType())) {
            detailVO.setUserType(ActivityUserTypeEnum.NEW_REG_USER.getCode());
            //体育负盈利返利活动 注册日期限制条件
            if (ActivityTemplateEnum.LOSS_IN_SPORTS.getType().equals(lossInSportsRespVO.getActivityTemplate())) {
                boolean profitRebate = getProfitRebate(activityBase, userId, timezone, siteCode);
                if (!profitRebate) {
                    detailVO.setActivityCondition(false);
                    return detailVO;
                }
            }
        }*/
        //true = 能参与
        boolean activityCondition = siteActivityEventRecordService.toActivityEventRecordCount(
                SiteActivityEventRecordQueryParam.builder()
                        .activityId(lossInSportsRespVO.getActivityId())
                        .siteCode(siteCode)
                        .userId(userId)
                        .calculateType(lossInSportsRespVO.getCalculateType())
                        .timezone(timezone)
                        .build()
        ) <= 0;

        //如果确定参与记录不存在的情况下,在查询下订单的记录，如果订单记录存在，也是不允许点击
        if (!activityCondition || siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityId, lossInSportsRespVO.getActivityId())
                .eq(SiteActivityOrderRecordPO::getUserId, userId)
                .between(SiteActivityOrderRecordPO::getCreatedTime, DateUtils.getTodayStartTime(), DateUtils.getTodayEndTime())
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)) > 0) {
            log.info("申请参与:{},活动,siteCoe:{}userId:{},被拒绝,重复参与", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        /*SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();
        //判断用户是否符合条件
        List<ActivitySendMqVO> userVenueBetsTopVOS = validateActivityConditionsList(siteVO, Lists.newArrayList(userId), lossInSportsRespVO);
        if (CollectionUtil.isEmpty(userVenueBetsTopVOS)) {
            activityCondition = false;
        }
*/
        detailVO.setActivityCondition(activityCondition);

        return detailVO;
    }

    //负盈利返利 || 注册天数要求是否满足
    public boolean getProfitRebate(String baseRespVO, String userId, String timezone, String siteCode) {
        ActivityLossInSportsRespVO lossInSportsRespVO = JSONObject.parseObject(baseRespVO, ActivityLossInSportsRespVO.class);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

        LocalDateTime nowTime = TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), timezone);
        LocalDateTime registerTime = TimeZoneUtils.timeByTimeZone(userInfoVO.getRegisterTime(), timezone);
        long userRegisterDay = ChronoUnit.DAYS.between(registerTime, nowTime);
        if (userRegisterDay < lossInSportsRespVO.getRegisterDay()) {
            log.info("用户:{},siteCode:{},活动详情,不满足,用户注册天数:{},要求注册天数:{}",
                    userId, siteCode, userRegisterDay,
                    lossInSportsRespVO.getRegisterDay());
            return false;
        }
        return true;
    }

}
