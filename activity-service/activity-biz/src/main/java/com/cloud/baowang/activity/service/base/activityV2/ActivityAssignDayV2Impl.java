package com.cloud.baowang.activity.service.base.activityV2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.SiteActivityOrderRecordPO;
import com.cloud.baowang.activity.po.v2.SiteActivityAssignDayV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityEventRecordV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityOrderRecordV2PO;
import com.cloud.baowang.activity.service.ActivityGameService;
import com.cloud.baowang.activity.service.ActivityTypingAmountService;
import com.cloud.baowang.activity.service.v2.SiteActivityAssignDayV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityEventRecordV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.activity.utils.DataUtils;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.vo.ReportUserRechargeRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 指定存款日期-活动
 */
@Service
@Slf4j
public class ActivityAssignDayV2Impl implements ActivityBaseV2Interface<ActivityAssignDayRespVO> {

    @Resource
    private SiteActivityAssignDayV2Service siteActivityAssignDayV2Service;
    @Resource
    private SiteApi siteApi;
    @Resource
    private ReportUserRechargeApi reportUserRechargeApi;
    @Lazy
    @Resource
    private SiteActivityBaseV2Service siteActivityBaseV2Service;
    @Resource
    private SiteActivityEventRecordV2Service siteActivityEventRecordV2Service;
    @Resource
    private SiteActivityOrderRecordV2Service siteActivityOrderRecordV2Service;
    @Resource
    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    @Resource
    private ActivityParticipateV2Api activityParticipateV2Api;
    @Resource
    private SystemDictConfigApi systemDictConfigApi;

    @Resource
    private ActivityGameService gameService;

    @Resource
    private ActivityTypingAmountService activityTypingAmountService;


    @Override
    public ActivityTemplateV2Enum getActivity() {
        return ActivityTemplateV2Enum.ASSIGN_DAY_V2;
    }

    @Override
    public boolean saveActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {

        if (ObjectUtil.isEmpty(((ActivityAssignDayV2VO) activityBaseVO).getVenueType())) {

            if (!((ActivityAssignDayV2VO) activityBaseVO).validate()) {
                log.info("新增活动:{},参数异常:{}", getActivity().getName(), activityBaseVO);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        activityBaseVO.setId(baseId);
        return siteActivityAssignDayV2Service.insert((ActivityAssignDayV2VO) activityBaseVO);
    }

    @Override
    public boolean upActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {
        if (ObjectUtil.isEmpty(((ActivityAssignDayV2VO) activityBaseVO).getVenueType())) {
            if (!((ActivityAssignDayV2VO) activityBaseVO).validate()) {
                log.info("修改活动:{},参数异常:{}", getActivity().getName(), activityBaseVO);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        activityBaseVO.setId(baseId);
        return siteActivityAssignDayV2Service.updateInfo((ActivityAssignDayV2VO) activityBaseVO);
    }


    /**
     * 指定存款日期活动详情。
     *
     * @return 详情
     */
    @Override
    public ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO, String siteCode) {

        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_CONFIG_V2, siteActivityBasePO.getId()));
        Object value = RedisUtil.getValue(key);
        if (value != null) {
            return JSON.parseObject(value.toString(), ActivityAssignDayV2RespVO.class);
        }
        ActivityAssignDayV2RespVO activityAssignDayRespVO = ActivityAssignDayV2RespVO.builder().build();
        SiteActivityAssignDayV2PO siteActivityAssignDayPO = siteActivityAssignDayV2Service.info(siteActivityBasePO.getId());
        BeanUtils.copyProperties(siteActivityAssignDayPO, activityAssignDayRespVO);
        activityAssignDayRespVO.setId(siteActivityBasePO.getId());

        activityAssignDayRespVO.setActivityAssignDayVenueVOS(JSON.parseArray(siteActivityAssignDayPO.getConditionVal(), ActivityAssignDayVenueV2VO.class));

        BeanUtils.copyProperties(siteActivityBasePO, activityAssignDayRespVO);
        RedisUtil.setValue(key, JSON.toJSONString(activityAssignDayRespVO), 5L, TimeUnit.MINUTES);

        return activityAssignDayRespVO;
    }


    @Override
    public ActivityBaseV2VO getActivityBody(ActivityConfigV2VO activityConfigVO) {
        return activityConfigVO.getActivityAssignDayVO();
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseV2Service.expiredActivity(siteVO, getActivity());
        siteActivityOrderRecordV2Service.awardExpire(siteVO, getActivity());
    }

    /**
     * 指定日存款定时任务
     */
    @Override
    public void awardActive(SiteVO siteVO, String param) {
        String timeZoneId = siteVO.getTimezone();
        String siteCode = siteVO.getSiteCode();
        if (!StringUtils.hasText(timeZoneId)) {
            log.info("[指定存款日活动]站点编号:{}尚未配置时区,无需处理", siteCode);
            return;
        }
        Long startTime = DateUtils.getYesTodayStartTime(timeZoneId);
        // todo wade 测试使用，不提交代码
        //startTime = 1753459200000L;
        String yesterdayStr = DateUtils.formatDateByZoneId(startTime, "yyyy-MM-dd", siteVO.getTimezone());
        // 查找活动配置
        List<ActivityBaseV2RespVO> list = findActivityAssignDay(siteCode, startTime, siteVO.getTimezone());

        if (CollUtil.isEmpty(list)) {
            log.info("[指定存款日活动]当前站点:{},日期:{}不存在指定存款日期有效活动配置,无需处理", siteCode, yesterdayStr);
            return;
        }
        ReportUserRechargeRequestVO reportUserRechargeRequestVO = new ReportUserRechargeRequestVO();
        reportUserRechargeRequestVO.setSiteCode(siteCode);
        reportUserRechargeRequestVO.setPageNumber(1);
        reportUserRechargeRequestVO.setPageSize(500);
        reportUserRechargeRequestVO.setDateStr(yesterdayStr);
        ResponseVO<Page<ReportUserRechargeResponseVO>> reportUserRechargeResp = reportUserRechargeApi.queryRechargeAmount(reportUserRechargeRequestVO);
        if (!reportUserRechargeResp.isOk()) {
            log.info("[指定存款日活动]根据条件:{},{}查询不到统计数据", siteCode, yesterdayStr);
            return;
        }
        Page<ReportUserRechargeResponseVO> rechargeResponseVOPage = reportUserRechargeResp.getData();
        long totalPage = rechargeResponseVOPage.getPages();
        log.info("[指定存款日活动]当前站点:{},总数据量:{},总页数:{}开始处理", siteCode, rechargeResponseVOPage.getRecords().size(), totalPage);
        for (int pageIndex = 1; pageIndex <= totalPage; pageIndex++) {
            reportUserRechargeRequestVO.setPageNumber(pageIndex);
            ResponseVO<Page<ReportUserRechargeResponseVO>> reportUserRechargeRespNew = reportUserRechargeApi.queryRechargeAmount(reportUserRechargeRequestVO);
            singleOneProcess(siteCode, reportUserRechargeRespNew.getData().getRecords(), yesterdayStr, list, timeZoneId, startTime);
            log.info("[指定存款日活动]当前站点:{},第:{}页处理完毕", siteCode, pageIndex);
        }
    }


    /**
     * 指定日期存款 活动
     * t免费旋转次数
     *
     * @param siteCode  站点编码
     * @param assignDay 指定日期时间戳
     * @return 指定日期存款
     */
    public List<ActivityBaseV2RespVO> findActivityAssignDay(String siteCode, Long assignDay, String timeZone) {
        List<SiteActivityBaseV2PO> siteActivityBasePOS = siteActivityBaseV2Service.selectBySiteAndTemplate(siteCode, ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType());

        List<ActivityBaseV2RespVO> list = new ArrayList<>();
        for (SiteActivityBaseV2PO siteActivityBasePO : siteActivityBasePOS) {
            Integer status = siteActivityBasePO.getStatus();
            Long forbidTime = siteActivityBasePO.getForbidTime();
            Long activityEndTime = siteActivityBasePO.getActivityEndTime();
            String assignDayStr = DateUtils.formatDateByZoneId(assignDay, "yyyy-MM-dd", timeZone);

            //增加删除跳过
            if (Objects.equals(siteActivityBasePO.getDeleteFlag(), EnableStatusEnum.DISABLE.getCode())) {
                log.info("指定日期存款符合条件的配置， 状态为删除的，跳过");
                continue;
            }

            //是否允许发放
            boolean permitSendFlag = false;
            if (Objects.equals(status, EnableStatusEnum.ENABLE.getCode())) {
                log.info("指定日期存款,指定日期:{},活动启用状态,允许发放奖励", assignDayStr);
                permitSendFlag = true;
            }
            //活动被禁用
            if (Objects.equals(status, EnableStatusEnum.DISABLE.getCode())) {
                //活动已过期被禁用
                if (activityEndTime != null && forbidTime != null && activityEndTime.compareTo(forbidTime) <= 0 && assignDay.compareTo(activityEndTime) < 0) {
                    log.info("指定日期存款,指定日期:{},活动正常过期,允许发放奖励", assignDayStr);
                    permitSendFlag = true;
                }
            }
            if (permitSendFlag) {
                ActivityBaseV2RespVO activityBaseV2RespVO = this.getActivityByActivityId(siteActivityBasePO, siteCode);
                BeanUtils.copyProperties(siteActivityBasePO, activityBaseV2RespVO);
                String weekDays = ((ActivityAssignDayV2RespVO) activityBaseV2RespVO).getWeekDays();
                int ownerWeekday = DateUtils.getWeekDay(assignDayStr);
                log.info("指定日期存款,指定日期:{},获取的星期几:{},配置星期几:{}", assignDayStr, ownerWeekday, weekDays);
                if (weekDays.contains(String.valueOf(ownerWeekday))) {
                    list.add(activityBaseV2RespVO);
                }
            }
        }
        return list;
    }


    /**
     *  NOTE 站点单次处理
     *  NOTE 指定日期存款活动配置，活动配置
     */
    public void singleOneProcess(String siteCode, List<ReportUserRechargeResponseVO> userRechargeVOList, String yesterdayStr, List<ActivityBaseV2RespVO> list, String timeZoneId, Long startTime) {

        //查询已经参与活动的用户
        //List<String> userIds = siteActivityEventRecordV2Service.permitSendRewardUserIds(siteCode, activityAssignDayRespVO.getId(), startTime);
        // 活动配置。 这个把固定与百分比，组成一个
        List<ActivitySendMqVO> activitySendMqVOS = Lists.newArrayList();
        //List<ActivityFreeGameVO> activityFreeGameVOS = Lists.newArrayList();
        Map<String, BigDecimal> finalRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_BENEFIT_EXPIRATION_TIME.getCode(), siteCode).getData();
        Double hourTime = Double.valueOf(systemDictConfigRespVO.getConfigParam());
        log.info("指定存款日:{},获取过期时间参数:{}", yesterdayStr, systemDictConfigRespVO);
        for (ReportUserRechargeResponseVO userRechargeVO : userRechargeVOList) {

            BigDecimal rechargeAmount = userRechargeVO.getRechargeAmount();
            if (rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("指定存款日:{}活动充值金额:{}不合法", yesterdayStr, rechargeAmount);
                continue;
            }
            //NOTE 获取一个开启的活动
            ActivityBaseV2RespVO activityAssignDayConfig = list.stream().filter(activityBaseV2RespVO -> ObjUtil.equals(activityBaseV2RespVO.getStatus(), StatusEnum.OPEN.getCode())).findFirst().orElse(null);

            //手动参与 判断是否已经申请参与活动
            boolean manualFlag = false;

            SiteActivityEventRecordV2PO byUserIdAndDay = siteActivityEventRecordV2Service.getByUserIdAndDay(siteCode, userRechargeVO.getUserId(), startTime, ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType());
            if (byUserIdAndDay != null) {
                activityAssignDayConfig = list.stream().filter(activityBaseV2RespVO -> ObjUtil.equals(activityBaseV2RespVO.getId(), byUserIdAndDay.getActivityId())).findFirst().orElse(null);
                manualFlag = true;
            }
            if (activityAssignDayConfig == null) {
                log.error("站点： {}， 指定存款日期:{} 活动配置不存在, 用户ID{}", siteCode, yesterdayStr,userRechargeVO.getUserId());
                continue;
            }

            ActivityAssignDayV2RespVO assignDayV2RespVO = (ActivityAssignDayV2RespVO) activityAssignDayConfig;

            Integer distributionType = assignDayV2RespVO.getDistributionType();
            Integer participationMode = assignDayV2RespVO.getParticipationMode();

            log.info("站点： {}，指定存款日:{}活动手动参与:{},自动参与:{}", siteCode, yesterdayStr, manualFlag, participationMode);
            //自动参与
            if (manualFlag || Objects.equals(ActivityParticipationModeEnum.AUTO.getCode(), participationMode)) {
                long toDayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZoneId);
                long toDayEndTime = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZoneId);

                if (siteActivityOrderRecordV2Service.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                        .eq(SiteActivityOrderRecordV2PO::getActivityTemplate, assignDayV2RespVO.getActivityTemplate())
                        .eq(SiteActivityOrderRecordV2PO::getUserId, userRechargeVO.getUserId())
                        .between(SiteActivityOrderRecordV2PO::getCreatedTime, toDayStartTime, toDayEndTime)
                        .eq(SiteActivityOrderRecordV2PO::getSiteCode, siteCode)) > 0) {
                    log.info("指定日期存款活动,siteCoe:{}userId:{} ,参与日期{},重复参与, 跳过", siteCode, userRechargeVO.getUserId(), yesterdayStr);
                    continue;
                }
                // 判断活动是否自动派发，如果是自动派发，则添加游戏大类是电子，如果没有电子，则随机一个游戏大类
                String venueType;
                if (ObjectUtil.isNotEmpty(assignDayV2RespVO.getVenueType())) {
                    UserInfoVO byUserId = UserInfoVO.builder().build();
                    BeanUtils.copyProperties(userRechargeVO, byUserId);
                    byUserId.setMainCurrency(userRechargeVO.getCurrency());
                    byUserId.setSiteCode(siteCode);
                    venueType = activityTypingAmountService.initUserActivityTypingAmountLimit(assignDayV2RespVO.getVenueType(), byUserId);
                } else {
                    venueType = "";
                }
                // 查询用户的申请游戏大类记录
                // 根据venueType 设置奖励金额与其他

                AssignDayCondV2VO activityAssignDayCondVO = rechargeAutoMatch(assignDayV2RespVO, userRechargeVO, finalRateMap, venueType);

                if (activityAssignDayCondVO.getAcquireNum() > 0 || activityAssignDayCondVO.getAcquireAmount().compareTo(BigDecimal.ZERO) > 0) {
                    String currencyCode = activityAssignDayCondVO.getCurrencyCode();

                    ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
                    String activityTemplateCode = ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType();
                    String orderNo = OrderNoUtils.genOrderNo(userRechargeVO.getUserId(), ActivityTemplateV2Enum.ASSIGN_DAY_V2.getSerialNo(), yesterdayStr);
                    activitySendMqVO.setOrderNo(orderNo);
                    activitySendMqVO.setSiteCode(userRechargeVO.getSiteCode());
                    activitySendMqVO.setUserId(userRechargeVO.getUserId());
                    activitySendMqVO.setCurrencyCode(currencyCode);
                    activitySendMqVO.setActivityTemplate(activityTemplateCode);
                    activitySendMqVO.setActivityId(assignDayV2RespVO.getId());
                    activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
                    activitySendMqVO.setReceiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime));
                    activitySendMqVO.setDistributionType(distributionType);
                    activitySendMqVO.setActivityAmount(activityAssignDayCondVO.getAcquireAmount());
                    activitySendMqVO.setSiteCode(userRechargeVO.getSiteCode());
                    activitySendMqVO.setActivityId(assignDayV2RespVO.getActivityId());
                    //指定盘口
                    activitySendMqVO.setHandicapMode(1);

                    // 打码倍数 打码流水
                    activitySendMqVO.setRunningWaterMultiple(activityAssignDayCondVO.getWashRatio());
                    activitySendMqVO.setRunningWater(activityAssignDayCondVO.getRequiredTurnover());
                    activitySendMqVO.setParticipationMode(assignDayV2RespVO.getParticipationMode());


                    /*ActivityFreeGameVO activityFreeGameVO = new ActivityFreeGameVO();
                    activityFreeGameVO.setOrderNo(orderNo);
                    activityFreeGameVO.setSiteCode(userRechargeVO.getSiteCode());
                    activityFreeGameVO.setUserId(userRechargeVO.getUserId());
                    activityFreeGameVO.setCurrencyCode(currencyCode);
                    activityFreeGameVO.setActivityId(assignDayV2RespVO.getId());
                    activityFreeGameVO.setActivityNo(assignDayV2RespVO.getActivityNo());
                    activityFreeGameVO.setActivityTemplate(activityTemplateCode);
                    activityFreeGameVO.setActivityTemplateName(ActivityTemplateV2Enum.parseNameByCode(activityTemplateCode));
                    activityFreeGameVO.setAcquireNum(activityAssignDayCondVO.getAcquireNum());

                    activityFreeGameVO.setWashRatio(activityAssignDayCondVO.getWashRatio());
                    activityFreeGameVO.setVenueCode(activityAssignDayCondVO.getVenueCode());
                    activityFreeGameVO.setAccessParameters(activityAssignDayCondVO.getAccessParameters());
                    activityFreeGameVO.setBetLimitAmount(activityAssignDayCondVO.getBetLimitAmount());*/

                    String activityId = String.valueOf(assignDayV2RespVO.getId());
                    String userId = userRechargeVO.getUserId();
                    UserBaseReqVO userBaseReqVO = new UserBaseReqVO();
                    userBaseReqVO.setActivityId(activityId);
                    userBaseReqVO.setUserId(userId);
                    userBaseReqVO.setSiteCode(siteCode);
                    userBaseReqVO.setTimezone(timeZoneId);
                    userBaseReqVO.setApplyFlag(false);
                    //获取开始日期
                    Long startDayTime = DateUtils.getStartDayMillis(startTime, timeZoneId);
                    userBaseReqVO.setDayStartTime(startDayTime);
                    userBaseReqVO.setDayEndTime(DateUtils.getEndDayByStartTime(startDayTime));
                    ResponseVO<ToActivityVO> responseVO = activityParticipateV2Api.checkToActivity(userBaseReqVO);
                    ToActivityVO toActivityVO = responseVO.getData();
                    if (toActivityVO!=null && ObjectUtil.equals(ResultCode.SUCCESS.getCode(),toActivityVO.getStatus()))   {
                        activitySendMqVOS.add(activitySendMqVO);
                        //activityFreeGameVOS.add(activityFreeGameVO);
                    } else {
                        log.info("站点:{},指定存款日期:{}不符合发放条件", siteCode, responseVO);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(activitySendMqVOS)) {
            ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
            activitySendListMqVO.setList(activitySendMqVOS);
            log.info("站点:{},指定存款日期开始发放条数:{}", siteCode, activitySendMqVOS.size());
            KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
        }
    }


    /**
     * 判断条件是否匹配
     *
     * @param activityAssignDayRespVO      活动配置
     * @param reportUserRechargeResponseVO 充值金额
     * @param finalRateMap                 汇率
     * @param venueType                    游戏大类
     * @return 指定日期存款活动条件
     */
    private AssignDayCondV2VO rechargeAutoMatch(ActivityAssignDayV2RespVO activityAssignDayRespVO,
                                                ReportUserRechargeResponseVO reportUserRechargeResponseVO,
                                                Map<String, BigDecimal> finalRateMap,
                                                String venueType) {
        String mainCurrency = reportUserRechargeResponseVO.getCurrency();
        BigDecimal rechargeAmount = reportUserRechargeResponseVO.getRechargeAmount();
        AssignDayCondV2VO depositConfigDTO = new AssignDayCondV2VO();

        List<ActivityAssignDayVenueV2VO> depositConfigDTOS = activityAssignDayRespVO.getActivityAssignDayVenueVOS();

        if (StrUtil.isNotEmpty(venueType)) {
            depositConfigDTOS = depositConfigDTOS.stream().filter(e -> e.getVenueType().equals(venueType)).toList();
        }
        ActivityAssignDayVenueV2VO activityAssignDayVenueV2VO = depositConfigDTOS.stream().findFirst().orElse(null);
        if (activityAssignDayVenueV2VO == null) {
            log.info("指定日活动配置异常, venueType：{},  depositConfigDTOS: {}", venueType, depositConfigDTOS);
        }

        BigDecimal washRatio = activityAssignDayVenueV2VO.getWashRatio();
        Integer discountType = activityAssignDayVenueV2VO.getDiscountType();

        BigDecimal transferRate = finalRateMap.get(mainCurrency);
        BigDecimal rechargeToPlatFormAmount = AmountUtils.divide(rechargeAmount, transferRate);
        log.info("指定日期存款,用户:{},充值金额转换为平台币:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount);
        String processCurrency;

        //按照主货币派发
        String platformOrFiatCurrency = activityAssignDayVenueV2VO.getPlatformOrFiatCurrency();
        boolean isPlatform = false;
        if ("1".equals(platformOrFiatCurrency)) {
            processCurrency = mainCurrency;
        } else {
            processCurrency = "WTC";
            isPlatform = true;
        }


        if (Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), discountType)) {

            List<AssignDayCondV2VO> percentCondVO = activityAssignDayVenueV2VO.getPercentCondVO();
            depositConfigDTO = percentCondVO.stream().filter(assignDayCondV2VO -> processCurrency.equalsIgnoreCase(assignDayCondV2VO.getCurrencyCode())).findFirst().orElse(null);

            if (depositConfigDTO == null) {
                log.info("指定日存款, depositConfigDTO配置为空, userID:{}, 充值金额:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount);
            }



            BigDecimal acquireAmountMainCurrency = BigDecimal.ZERO;
            BigDecimal acquireAmountPlatform = BigDecimal.ZERO;
            Integer acquireNum = 0;
            // 配置的是平台币金额,对比的时候也是平台币金额进行对比, 赠送的时候需要将奖励金额转化为主货币
            if (rechargeToPlatFormAmount.compareTo(depositConfigDTO.getMinDepositAmt()) >= 0) {

                //NOTE 本币金额
                acquireAmountMainCurrency = AmountUtils.multiplyPercent(rechargeAmount, depositConfigDTO.getDepositPercent());//平台币
                //NOTE 平台币金额
                acquireAmountPlatform = AmountUtils.divide(acquireAmountMainCurrency, transferRate);
                //NOTE 配置奖励最大金额
                BigDecimal acquireAmountMax = depositConfigDTO.getAcquireAmountMax();

                if (isPlatform) {
                    if (acquireAmountPlatform.compareTo(acquireAmountMax) > 0) {
                        //平台币转法币
                        acquireAmountPlatform = acquireAmountMax;
                    }
                    acquireAmountMainCurrency = AmountUtils.multiply(acquireAmountPlatform, transferRate);
                    depositConfigDTO.setAcquireAmount(acquireAmountPlatform);
                } else {
                    if (acquireAmountMainCurrency.compareTo(acquireAmountMax) > 0) {
                        //平台币转法币
                        acquireAmountMainCurrency = acquireAmountMax;
                    }
                    acquireAmountPlatform = AmountUtils.divide(acquireAmountMainCurrency, transferRate);
                    depositConfigDTO.setAcquireAmount(acquireAmountMainCurrency);
                }
                log.info("指定日期存款,配置为百分比,用户:{},充值金额转换为平台币:{},平台币奖励:{},奖励最大值平台币:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount, acquireAmountPlatform, acquireAmountMainCurrency);
                acquireNum = depositConfigDTO.getAcquireNum();
            }else {
                depositConfigDTO.setAcquireAmount(BigDecimal.ZERO);

            }
            //充本金的法币 + 彩金平台币(转法币)
            BigDecimal totalAmount = rechargeAmount.add(acquireAmountMainCurrency);
            BigDecimal requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
            if (requiredTurnover.compareTo(BigDecimal.ZERO) > 0) {
                requiredTurnover = requiredTurnover.subtract(rechargeAmount);
            }
            log.info("指定日期存款,配置为百分比,用户:{},充值金额转换为平台币:{},奖励金额:{}{},奖励次数:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount, acquireAmountMainCurrency, mainCurrency, acquireNum);
            depositConfigDTO.setWashRatio(washRatio);
            depositConfigDTO.setCurrencyCode(processCurrency);
            depositConfigDTO.setAcquireNum(acquireNum);

            depositConfigDTO.setRequiredTurnover(requiredTurnover);
            return depositConfigDTO;
        } else {

            BigDecimal acquireAmountMainCurrency = BigDecimal.ZERO;
            BigDecimal acquireAmountPlatform = BigDecimal.ZERO;
            Integer acquireNum = 0;
            ActivityAssignDayCondV2VO assignDayCondV2VO = activityAssignDayVenueV2VO.getFixCondVOList().stream().filter(condV2VO -> processCurrency.equalsIgnoreCase(condV2VO.getCurrencyCode())).findFirst().orElse(null);

            if (assignDayCondV2VO == null || CollUtil.isEmpty(assignDayCondV2VO.getAmount())) {
                log.info("指定日存款, assignDayCondV2VO 配置为空, userID:{}, 充值金额:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount);
            }
            for (AssignDayCondV2VO activityAssignDayCondVO : assignDayCondV2VO.getAmount()) {
                //判断一下是否满足某个区间
                BigDecimal minDeposit = activityAssignDayCondVO.getMinDepositAmt();
                BigDecimal maxDeposit = activityAssignDayCondVO.getMaxDepositAmt();
                //只要大于最大值 奖励金额取配置金额
                log.debug("指定日期存款,平台币金额:{},最小充值:{},最大充值:{},当前配置奖励金额:{}", rechargeToPlatFormAmount, minDeposit, maxDeposit, activityAssignDayCondVO.getAcquireAmount());

                if (isPlatform) {
                    if (rechargeToPlatFormAmount.compareTo(maxDeposit) >= 0) {
                        acquireAmountPlatform = activityAssignDayCondVO.getAcquireAmount();
                        acquireNum = activityAssignDayCondVO.getAcquireNum();
                    } else {
                        //需要大于最小值
                        if (rechargeToPlatFormAmount.compareTo(minDeposit) >= 0) {
                            acquireAmountPlatform = activityAssignDayCondVO.getAcquireAmount();
                            acquireNum = activityAssignDayCondVO.getAcquireNum();
                        }
                        break;
                    }
                } else {
                    if (rechargeAmount.compareTo(maxDeposit) >= 0) {
                        acquireAmountMainCurrency = activityAssignDayCondVO.getAcquireAmount();
                        acquireNum = activityAssignDayCondVO.getAcquireNum();
                    } else {
                        //需要大于最小值
                        if (rechargeAmount.compareTo(minDeposit) >= 0) {
                            acquireAmountMainCurrency = activityAssignDayCondVO.getAcquireAmount();
                            acquireNum = activityAssignDayCondVO.getAcquireNum();
                        }
                        break;
                    }
                }
            }
            //彩金法币金额
            if (isPlatform) {
                acquireAmountMainCurrency = AmountUtils.multiply(acquireAmountPlatform, transferRate);
                depositConfigDTO.setAcquireAmount(acquireAmountPlatform);
            } else {
                acquireAmountPlatform = AmountUtils.divide(acquireAmountMainCurrency, transferRate);
                depositConfigDTO.setAcquireAmount(acquireAmountMainCurrency);
            }
            log.info("指定日期存款,平台币金额:{},实际奖励金额:{}转换为法币金额:{}", rechargeToPlatFormAmount, acquireAmountMainCurrency, acquireAmountPlatform);


            //充本金的法币 + 彩金平台币(转法币)
            BigDecimal totalAmount = rechargeAmount.add(acquireAmountMainCurrency);
            BigDecimal requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
            if (requiredTurnover.compareTo(BigDecimal.ZERO) > 0) {
                requiredTurnover = requiredTurnover.subtract(rechargeAmount);
            }
            depositConfigDTO.setAcquireNum(acquireNum);

            depositConfigDTO.setRequiredTurnover(requiredTurnover);
            depositConfigDTO.setCurrencyCode(processCurrency);
            return depositConfigDTO;
        }

    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO
     */
    @Override
    public void checkSecond(ActivityConfigV2VO activityConfigVO) {
        ActivityAssignDayV2VO activityAssignDayVO = activityConfigVO.getActivityAssignDayVO();

        if (StrUtil.isEmpty(activityAssignDayVO.getVenueType())) {
            ActivityAssignDayVenueV2VO assignDayVenueV2VO = new ActivityAssignDayVenueV2VO();
            if (ObjectUtil.equals(0, activityAssignDayVO.getDiscountType())) {
                if (!activityAssignDayVO.percentCondVOProcess(activityAssignDayVO.getPercentCondVO())) {
                    log.info("{},指定存款V2无游戏大类, 百分比, 详细校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                assignDayVenueV2VO.setPercentCondVO(activityAssignDayVO.getPercentCondVO());
            } else {
                if (!activityAssignDayVO.fixCondVOListProcess(activityAssignDayVO.getFixCondVOList())) {
                    log.info("{},指定存款V2无游戏大类, 固定金额, 详细校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                assignDayVenueV2VO.setFixCondVOList(activityAssignDayVO.getFixCondVOList());
            }
            assignDayVenueV2VO.setWashRatio(activityAssignDayVO.getWashRatio());
            assignDayVenueV2VO.setDiscountType(activityAssignDayVO.getDiscountType());
            assignDayVenueV2VO.setPlatformOrFiatCurrency(activityAssignDayVO.getPlatformOrFiatCurrency());
            List<ActivityAssignDayVenueV2VO> tempList = new ArrayList<>();
            tempList.add(assignDayVenueV2VO);
            activityAssignDayVO.setActivityAssignDayVenueVOS(tempList);
        } else {
            List<ActivityAssignDayVenueV2VO> activityAssignDayVenueVOS = activityAssignDayVO.getActivityAssignDayVenueVOS();

            if (CollUtil.isEmpty(activityAssignDayVenueVOS)) {
                log.info("{},指定存款V2 详细校验失败", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            for (ActivityAssignDayVenueV2VO activityAssignDayVenueV2VO : activityAssignDayVenueVOS) {
                if (!activityAssignDayVenueV2VO.validate()) {
                    log.info("{},指定存款V2, 详细校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }

            // 判断是否配置了游戏大类
            if (ObjectUtil.isNotEmpty(activityAssignDayVO.getVenueType())) {
                List<GameSelectVO> gameSelect = gameService.getGameSelect(ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType(), activityConfigVO.getSiteCode());
                for (GameSelectVO vo : gameSelect) {
                    if (!DataUtils.checkStringSame(activityAssignDayVO.getVenueType(), vo.getVenueType())) {
                        log.info("{},指定存款日 详细校验失败,该活动配置游戏类型与启用的游戏类型不一致", getActivity().getName());
                        throw new BaowangDefaultException(ResultCode.ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH);
                    }
                }
            }
        }
    }


    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        siteActivityAssignDayV2Service.deleteByActivityId(vo.getId());
    }


    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBaseV2PO> allValidBasePos) {
        if (CollectionUtils.isEmpty(allValidBasePos)) {
            log.info("指定日期活动,不存在已开启的,可以直接操作");
            return;
        }
        String activityId = vo.getId();
        SiteActivityAssignDayV2PO currentActivityPO = this.siteActivityAssignDayV2Service.info(activityId);
        String weekDays = currentActivityPO.getWeekDays();
        List<String> originWeekDays = Arrays.stream(weekDays.split(",")).toList();
        List<String> activityIds = allValidBasePos.stream().map(SiteActivityBaseV2PO::getId).toList();
        List<SiteActivityAssignDayV2PO> siteActivityAssignDayPOS = this.siteActivityAssignDayV2Service.selectByActivityIds(activityIds);
        for (SiteActivityAssignDayV2PO siteActivityAssignDayPO : siteActivityAssignDayPOS) {
            List<String> compareWeekDays = Arrays.stream(siteActivityAssignDayPO.getWeekDays().split(",")).toList();
            boolean hasSameElement = Collections.disjoint(originWeekDays, compareWeekDays);
            //包含指定元素 返回false
            if (!hasSameElement) {
                log.info("指定日期存在相同配置已开启,此活动:{}无法开启", activityId);
                throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
            }
        }
    }


    public ToActivityVO toActivity(ActivityBaseV2RespVO activityBaseRespVO, UserBaseReqVO userBaseReqVO) {
        ActivityAssignDayV2RespVO respVO = (ActivityAssignDayV2RespVO) activityBaseRespVO;
        String activityTemplate = respVO.getActivityTemplate();
        Integer participationMode = respVO.getParticipationMode();

        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(respVO.getActivityTemplate())
                .siteCode(userBaseReqVO.getSiteCode())
                .userId(userBaseReqVO.getUserId())
                .build();

        //是否申请操作
        if (userBaseReqVO.isApplyFlag()) {

            //查询出当天的开始时间戳
            queryParam.setDay(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), userBaseReqVO.getTimezone()));
            //是否有参与记录
            boolean hasClick = siteActivityEventRecordV2Service.toActivityEventRecordCount(queryParam)> 0;
            // 人工点击参与
            if (hasClick) {
                log.info("指定日期存款 检查参与:{}活动,siteCoe:{},userId:{},已经参与,不能再参加", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
            }
        } else {

            //查询出当天的开始时间戳
            queryParam.setDay(TimeZoneUtils.getStartOfYesterdayInTimeZone(System.currentTimeMillis(), userBaseReqVO.getTimezone()));
            //是否有参与记录
            boolean hasClick = siteActivityEventRecordV2Service.toActivityEventRecordCount(queryParam) > 0;
            // job或mq触发
            if (!hasClick && ActivityParticipationModeEnum.MANUAL.getCode().equals(participationMode)) {
                log.info("指定日期存款  检查参与:{}活动,siteCoe:{},userId:{},尚未参与不能派发", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
            }
        }
        //是否发放过记录
        /*if (siteActivityOrderRecordV2Service.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordPO::getUserId, userId)
                .between(SiteActivityOrderRecordPO::getCreatedTime, toDayStartTime, toDayEndTime)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)) > 0) {
            log.info("指定日期存款  申请参与:{},活动,siteCoe:{}userId:{},被拒绝,重复参与", activityTemplate, siteCode, userId);
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
        }*/
        return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).build();
    }


    public ActivityConfigDetailVO getConfigDetail(ActivityBaseV2RespVO activityBase, ActivityConfigDetailVO detailVO, String siteCode, String timezone, String userId) {

        ActivityAssignDayV2RespVO activityAssignDayRespVO = new ActivityAssignDayV2RespVO();
        if (activityBase instanceof ActivityAssignDayV2RespVO) {
            activityAssignDayRespVO = (ActivityAssignDayV2RespVO) activityBase;
        }

        String activityTemplate = activityAssignDayRespVO.getActivityTemplate();
        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(activityTemplate)
                .siteCode(siteCode)
                .userId(userId)
                .timezone(timezone)
                .build();

        Long toDayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);
        //查询出当天的开始时间戳
        queryParam.setDay(toDayStartTime);
        boolean activityCondition = true;
        //true = 能参与
        boolean countEventFlag = siteActivityEventRecordV2Service.toActivityEventRecordCount(queryParam) > 0;
        if (countEventFlag) {
            log.info("指定日期存款,活动:{},siteCoe:{}userId:{},已经参与,不需要显示按钮", activityTemplate, siteCode, userId);
            activityCondition = false;
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        detailVO.setActivityCondition(activityCondition);
        return detailVO;
    }
}

