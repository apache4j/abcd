package com.cloud.baowang.activity.service.base.activity;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.SiteActivityAssignDayPO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityOrderRecordPO;
import com.cloud.baowang.activity.service.*;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.utils.DataUtils;
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
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.vo.ReportUserRechargeRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
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

/**
 * 指定存款日期-活动
 */
@Service
@Slf4j
public class ActivityAssignDayImpl implements ActivityBaseInterface<ActivityAssignDayRespVO> {

    @Resource
    private SiteActivityAssignDayService siteActivityAssignDayService;
    @Resource
    private SiteApi siteApi;
    @Resource
    private ReportUserRechargeApi reportUserRechargeApi;
    @Lazy
    @Resource
    private SiteActivityBaseService siteActivityBaseService;
    @Resource
    private SiteActivityEventRecordService siteActivityEventRecordService;
    @Resource
    private SiteActivityOrderRecordService siteActivityOrderRecordService;
    @Resource
    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    @Resource
    private ActivityParticipateApi activityParticipateApi;
    @Resource
    private SystemDictConfigApi systemDictConfigApi;

    @Resource
    private ActivityGameService gameService;

    @Resource
    private ActivityTypingAmountService activityTypingAmountService;


    @Override
    public ActivityTemplateEnum getActivity() {
        return ActivityTemplateEnum.ASSIGN_DAY;
    }

    @Override
    public boolean saveActivityDetail(String activityBaseVO, String baseId) {
        ActivityAssignDayVO activityAssignDayVO = JSONObject.parseObject(activityBaseVO, ActivityAssignDayVO.class);
        if (ObjectUtil.isEmpty(activityAssignDayVO.getVenueType())) {
            if (!activityAssignDayVO.validate()) {
                log.info("新增活动:{},参数异常:{}", getActivity().getName(), activityAssignDayVO);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        activityAssignDayVO.setId(baseId);
        return siteActivityAssignDayService.insert(activityAssignDayVO);
    }

    @Override
    public boolean upActivityDetail(String activityBaseVO, String baseId) {
        ActivityAssignDayVO activityAssignDayVO = JSONObject.parseObject(activityBaseVO, ActivityAssignDayVO.class);
        if (ObjectUtil.isEmpty(activityAssignDayVO.getVenueType())) {
            if (!activityAssignDayVO.validate()) {
                log.info("修改活动:{},参数异常:{}", getActivity().getName(), activityAssignDayVO);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        activityAssignDayVO.setId(baseId);
        return siteActivityAssignDayService.updateInfo(activityAssignDayVO);
    }


    /**
     * 指定存款日期活动详情。
     *
     * @param activityId 活动主键ID
     * @return 详情
     */
    @Override
    public ActivityAssignDayRespVO getActivityByActivityId(String activityId, String siteCode) {
        ActivityAssignDayRespVO activityAssignDayRespVO = ActivityAssignDayRespVO.builder().build();
        SiteActivityAssignDayPO siteActivityAssignDayPO = siteActivityAssignDayService.info(activityId);
        BeanUtils.copyProperties(siteActivityAssignDayPO, activityAssignDayRespVO);
        activityAssignDayRespVO.setId(activityId);

        // 不为空，设置游戏大类存款配置
        if (ObjectUtil.isNotEmpty(siteActivityAssignDayPO.getVenueType())) {
            activityAssignDayRespVO.setActivityAssignDayVenueVOS(JSON.parseArray(siteActivityAssignDayPO.getConditionVal(), ActivityAssignDayVenueVO.class));

        } else {
            // 通用配置
            if (Objects.equals(siteActivityAssignDayPO.getDiscountType(), ActivityDiscountTypeEnum.FIXED_AMOUNT.getType())) {
                activityAssignDayRespVO.setFixCondVOList(JSONArray.parseArray(siteActivityAssignDayPO.getConditionVal(), ActivityAssignDayCondVO.class));
            } else if (Objects.equals(siteActivityAssignDayPO.getDiscountType(), ActivityDiscountTypeEnum.PERCENTAGE.getType())) {
                activityAssignDayRespVO.setPercentCondVO(JSON.parseObject(siteActivityAssignDayPO.getConditionVal(), ActivityAssignDayCondVO.class));
            }
        }
        return activityAssignDayRespVO;
    }


    @Override
    public String getActivityBody(ActivityConfigVO activityConfigVO) {
        return JSON.toJSONString(activityConfigVO.getActivityAssignDayVO());
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseService.expiredActivity(siteVO, getActivity());
        siteActivityOrderRecordService.awardExpire(siteVO, getActivity());
    }

    /**
     * 指定日存款定时任务
     *
     * @param siteVO
     * @param param
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
        ActivityAssignDayRespVO activityAssignDayRespVO = findActivityAssignDay(siteCode, startTime, siteVO.getTimezone());
        if (activityAssignDayRespVO == null) {
            log.info("[指定存款日活动]当前站点:{},日期:{}不存在指定存款日期有效活动配置,无需处理", siteCode, yesterdayStr);
            return;
        }
        ReportUserRechargeRequestVO reportUserRechargeRequestVO = new ReportUserRechargeRequestVO();
        reportUserRechargeRequestVO.setSiteCode(activityAssignDayRespVO.getSiteCode());
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
            singleOneProcess(reportUserRechargeRespNew.getData().getRecords(), yesterdayStr, activityAssignDayRespVO, timeZoneId, startTime);
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
    public ActivityAssignDayRespVO findActivityAssignDay(String siteCode, Long assignDay, String timeZone) {
        List<SiteActivityBasePO> siteActivityBasePOS = siteActivityBaseService.selectBySiteAndTemplate(siteCode, ActivityTemplateEnum.ASSIGN_DAY.getType());
        for (SiteActivityBasePO siteActivityBasePO : siteActivityBasePOS) {
            Integer status = siteActivityBasePO.getStatus();
            Long forbidTime = siteActivityBasePO.getForbidTime();
            Long activityEndTime = siteActivityBasePO.getActivityEndTime();
            String assignDayStr = DateUtils.formatDateByZoneId(assignDay, "yyyy-MM-dd", timeZone);
            String forbidTimeStr = DateUtils.formatDateByZoneId(forbidTime, "yyyy-MM-dd", timeZone);
            //是否允许发放
            boolean permitSendFlag = false;
            if (Objects.equals(status, EnableStatusEnum.ENABLE.getCode())) {
                log.info("指定日期存款,指定日期:{},活动启用状态,允许发放奖励", assignDayStr);
                permitSendFlag = true;
            }
            //活动被禁用
            if (Objects.equals(status, EnableStatusEnum.DISABLE.getCode())) {
                //活动已过期被禁用
                if (activityEndTime != null && forbidTime!=null && activityEndTime.compareTo(forbidTime) <= 0 && assignDay.compareTo(activityEndTime) < 0) {
                    log.info("指定日期存款,指定日期:{},活动正常过期,允许发放奖励", assignDayStr);
                    permitSendFlag = true;
                }
            }
            if (permitSendFlag) {
                ActivityAssignDayRespVO activityAssignDayRespVO = this.getActivityByActivityId(siteActivityBasePO.getId(), siteCode);
                BeanUtils.copyProperties(siteActivityBasePO, activityAssignDayRespVO);
                String weekDays = activityAssignDayRespVO.getWeekDays();
                int ownerWeekday = DateUtils.getWeekDay(assignDayStr);
                log.info("指定日期存款,指定日期:{},获取的星期几:{},配置星期几:{}", assignDayStr, ownerWeekday, weekDays);
                if (weekDays.contains(String.valueOf(ownerWeekday))) {
                    return activityAssignDayRespVO;
                }
            }
        }
        return null;
    }


    /**
     * 单次处理
     *
     * @param reportUserRechargeResponseVOS 充值数据
     * @param autoDate                      日期 yyyy-MM-dd 格式
     * @param activityAssignDayRespVO       指定日期存款活动配置，活动配置
     */
    public void singleOneProcess(List<ReportUserRechargeResponseVO> reportUserRechargeResponseVOS, String autoDate, ActivityAssignDayRespVO activityAssignDayRespVO, String timeZoneId, Long startTime) {
        Integer distributionType = activityAssignDayRespVO.getDistributionType();
        Integer participationMode = activityAssignDayRespVO.getParticipationMode();
        Integer discountType = activityAssignDayRespVO.getDiscountType();
        String siteCode = activityAssignDayRespVO.getSiteCode();
        //查询已经参与活动的用户
        List<String> userIds = siteActivityEventRecordService.permitSendRewardUserIds(siteCode, activityAssignDayRespVO.getId());
        // 活动配置。 这个把固定与百分比，组成一个
        List<ActivityAssignDayCondVO> activityAssignDayCondVOList = activityAssignDayRespVO.getActivityAssignDayCondVOList();
        List<ActivitySendMqVO> activitySendMqVOS = Lists.newArrayList();
        List<ActivityFreeGameVO> activityFreeGameVOS = Lists.newArrayList();
        Map<String, BigDecimal> finalRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_BENEFIT_EXPIRATION_TIME.getCode(), siteCode).getData();
        Double hourTime = Double.valueOf(systemDictConfigRespVO.getConfigParam());
        log.info("指定存款日:{},获取过期时间参数:{}", autoDate, systemDictConfigRespVO);
        for (ReportUserRechargeResponseVO reportUserRechargeResponseVO : reportUserRechargeResponseVOS) {
            BigDecimal rechargeAmount = reportUserRechargeResponseVO.getRechargeAmount();
            if (rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("指定存款日:{}活动充值金额:{}不合法", autoDate, rechargeAmount);
                continue;
            }
            //手动参与 判断是否已经申请参与活动
            boolean manualFlag = false;
            if (Objects.equals(ActivityParticipationModeEnum.MANUAL.getCode(), participationMode)) {
                manualFlag = userIds.stream().anyMatch(o -> o.equals(reportUserRechargeResponseVO.getUserId()));
            }
            log.info("指定存款日:{}活动手动参与:{},自动参与:{}", autoDate, manualFlag, participationMode);
            //自动参与
            if (manualFlag || Objects.equals(ActivityParticipationModeEnum.AUTO.getCode(), participationMode)) {
                // 判断活动是否自动派发，如果是自动派发，则添加游戏大类是电子，如果没有电子，则随机一个游戏大类
                String venueType;
                if (ObjectUtil.isNotEmpty(activityAssignDayRespVO.getVenueType())) {
                    UserInfoVO byUserId = UserInfoVO.builder().build();
                    BeanUtils.copyProperties(reportUserRechargeResponseVO, byUserId);
                    byUserId.setMainCurrency(reportUserRechargeResponseVO.getCurrency());
                    byUserId.setSiteCode(siteCode);
                    venueType = activityTypingAmountService.initUserActivityTypingAmountLimit(activityAssignDayRespVO.getVenueType(), byUserId);

                } else {
                    venueType = "";
                }
                // 查询用户的申请游戏大类记录
                // 根据venueType 设置奖励金额与其他

                ActivityAssignDayCondVO activityAssignDayCondVO = rechargeAutoMatch(activityAssignDayRespVO,
                        reportUserRechargeResponseVO, activityAssignDayCondVOList, finalRateMap, venueType);
                log.info("指定存款日活动 用户:{}, 充值金额:{},满足条件:{},赠送金额:{}{},赠送数量:{},流水:{}", reportUserRechargeResponseVO.getUserId(),
                        rechargeAmount, discountType,
                        activityAssignDayCondVO.getAcquireAmount(),
                        activityAssignDayCondVO.getCurrencyCode(),
                        activityAssignDayCondVO.getAcquireNum(),
                        activityAssignDayCondVO.getRequiredTurnover()
                );
                if (activityAssignDayCondVO.getAcquireNum() > 0 || activityAssignDayCondVO.getAcquireAmount().compareTo(BigDecimal.ZERO) > 0) {
                    String currencyCode = activityAssignDayCondVO.getCurrencyCode();
                    ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
                    String activityTemplateCode = ActivityTemplateEnum.ASSIGN_DAY.getType();
                    String orderNo = OrderNoUtils.genOrderNo(reportUserRechargeResponseVO.getUserId(), ActivityTemplateEnum.ASSIGN_DAY.getSerialNo(), autoDate);
                    activitySendMqVO.setOrderNo(orderNo);
                    activitySendMqVO.setSiteCode(reportUserRechargeResponseVO.getSiteCode());
                    activitySendMqVO.setUserId(reportUserRechargeResponseVO.getUserId());
                    activitySendMqVO.setCurrencyCode(currencyCode);
                    activitySendMqVO.setActivityTemplate(activityTemplateCode);
                    activitySendMqVO.setActivityId(activityAssignDayRespVO.getId());
                    activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
                    activitySendMqVO.setReceiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime));
                    activitySendMqVO.setDistributionType(distributionType);
                    activitySendMqVO.setActivityAmount(activityAssignDayCondVO.getAcquireAmount());
                    activitySendMqVO.setSiteCode(reportUserRechargeResponseVO.getSiteCode());
                    activitySendMqVO.setActivityId(activityAssignDayRespVO.getActivityId());
                    // 打码倍数 打码流水
                    activitySendMqVO.setRunningWaterMultiple(activityAssignDayCondVO.getWashRatio());
                    activitySendMqVO.setRunningWater(activityAssignDayCondVO.getRequiredTurnover());
                    activitySendMqVO.setParticipationMode(activityAssignDayRespVO.getParticipationMode());


                    ActivityFreeGameVO activityFreeGameVO = new ActivityFreeGameVO();
                    activityFreeGameVO.setOrderNo(orderNo);
                    activityFreeGameVO.setSiteCode(reportUserRechargeResponseVO.getSiteCode());
                    activityFreeGameVO.setUserId(reportUserRechargeResponseVO.getUserId());
                    activityFreeGameVO.setCurrencyCode(currencyCode);
                    activityFreeGameVO.setActivityId(activityAssignDayRespVO.getId());
                    activityFreeGameVO.setActivityNo(activityAssignDayRespVO.getActivityNo());
                    activityFreeGameVO.setActivityTemplate(activityTemplateCode);
                    activityFreeGameVO.setActivityTemplateName(ActivityTemplateEnum.parseNameByCode(activityTemplateCode));
                    activityFreeGameVO.setAcquireNum(activityAssignDayCondVO.getAcquireNum());

                    activityFreeGameVO.setWashRatio(activityAssignDayRespVO.getWashRatio());
                    activityFreeGameVO.setVenueCode(activityAssignDayRespVO.getVenueCode());
                    activityFreeGameVO.setAccessParameters(activityAssignDayRespVO.getAccessParameters());
                    activityFreeGameVO.setBetLimitAmount(activityAssignDayRespVO.getBetLimitAmount());

                    String activityId = String.valueOf(activityAssignDayRespVO.getId());
                    String userId = reportUserRechargeResponseVO.getUserId();
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
                    ResponseVO<ToActivityVO> responseVO = activityParticipateApi.checkToActivity(userBaseReqVO);
                    ToActivityVO toActivityVO = responseVO.getData();
                    if (responseVO.isOk()) {
                        activitySendMqVOS.add(activitySendMqVO);
                        activityFreeGameVOS.add(activityFreeGameVO);
                    } else {
                        log.info("站点:{},指定存款日期:{}不符合发放条件", activityAssignDayRespVO.getSiteCode(), responseVO);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(activitySendMqVOS)) {
            ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
            activitySendListMqVO.setList(activitySendMqVOS);
            log.info("站点:{},指定存款日期开始发放条数:{}", activityAssignDayRespVO.getSiteCode(), activitySendMqVOS.size());
            KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
        }

        if (!CollectionUtils.isEmpty(activityFreeGameVOS)) {
            ActivityFreeGameTriggerVO activityFreeGameTriggerVO = new ActivityFreeGameTriggerVO();
            activityFreeGameTriggerVO.setFreeGameVOList(activityFreeGameVOS);
            log.info("站点:{},指定存款日期开始 赠送免费旋转次数:{}", activityAssignDayRespVO.getSiteCode(), activityFreeGameVOS.size());

            KafkaUtil.send(TopicsConstants.FREE_GAME, activityFreeGameTriggerVO);
        }
    }


    /**
     * 判断条件是否匹配
     *
     * @param activityAssignDayRespVO      活动配置
     * @param reportUserRechargeResponseVO 充值金额
     * @param activityAssignDayCondVOList  条件 查询循转次数 赠送金额
     * @param finalRateMap                 汇率
     * @param venueType                    游戏大类
     * @return 指定日期存款活动条件
     */
    private ActivityAssignDayCondVO rechargeAutoMatch(ActivityAssignDayRespVO activityAssignDayRespVO,
                                                      ReportUserRechargeResponseVO reportUserRechargeResponseVO,
                                                      List<ActivityAssignDayCondVO> activityAssignDayCondVOList,
                                                      Map<String, BigDecimal> finalRateMap,
                                                      String venueType) {
        String rewardCurrencyCode = reportUserRechargeResponseVO.getCurrency();
        BigDecimal rechargeAmount = reportUserRechargeResponseVO.getRechargeAmount();
        BigDecimal washRatio;
        Integer discountType;
        ActivityAssignDayVenueVO depositConfigDTO = null;
        if (!StringUtils.hasText(venueType)) {
            washRatio = activityAssignDayRespVO.getWashRatio();
            discountType = activityAssignDayRespVO.getDiscountType();
        } else {
            // 针对每个游戏大类的配置
            List<ActivityAssignDayVenueVO> depositConfigDTOS = activityAssignDayRespVO.getActivityAssignDayVenueVOS();
            depositConfigDTO = depositConfigDTOS.stream()
                    .filter(e -> e.getVenueType().equals(venueType))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.info("指定日活动配置异常, venueType：{},  depositConfigDTOS: {}", venueType, depositConfigDTOS);
                        return new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                    });
            washRatio = depositConfigDTO.getWashRatio();
            discountType = depositConfigDTO.getDiscountType();

        }
        BigDecimal transferRate = finalRateMap.get(rewardCurrencyCode);
        BigDecimal rechargeToPlatFormAmount = AmountUtils.divide(rechargeAmount, transferRate);
        log.info("指定日期存款,用户:{},充值金额转换为平台币:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount);
        if (Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), discountType)) {
            ///按照主货币派发
            // 是否按照通用还是游戏大类的配置
            ActivityAssignDayCondVO activityAssignDayCondVO = new ActivityAssignDayCondVO();
            if (!StringUtils.hasText(venueType)) {
                activityAssignDayCondVO = activityAssignDayCondVOList.get(0);
            } else {
                activityAssignDayCondVO = depositConfigDTO.getActivityAssignDayCondVOList().get(0);
            }
            //activityAssignDayCondVO = activityAssignDayCondVOList.get(0);

            activityAssignDayCondVO.setWashRatio(washRatio);
            activityAssignDayCondVO.setCurrencyCode(rewardCurrencyCode);
            BigDecimal acquireAmountMainCurrency = BigDecimal.ZERO;
            Integer acquireNum = 0;
            // 配置的是平台币金额,对比的时候也是平台币金额进行对比, 赠送的时候需要将奖励金额转化为主货币
            if (rechargeToPlatFormAmount.compareTo(activityAssignDayCondVO.getMinDepositAmt()) >= 0) {
                BigDecimal acquirePlatFormAmountPercent = AmountUtils.multiplyPercent(rechargeToPlatFormAmount, activityAssignDayCondVO.getDepositPercent());//平台币
                BigDecimal acquirePlatFormAmountMax = activityAssignDayCondVO.getAcquireAmountMax();//平台币
                log.info("指定日期存款,配置为百分比,用户:{},充值金额转换为平台币:{},平台币奖励:{},奖励最大值平台币:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount, acquirePlatFormAmountPercent, acquirePlatFormAmountMax);
                if (acquirePlatFormAmountPercent.compareTo(acquirePlatFormAmountMax) > 0) {
                    //平台币转法币
                    acquireAmountMainCurrency = AmountUtils.multiply(acquirePlatFormAmountMax, transferRate);
                } else {
                    acquireAmountMainCurrency = AmountUtils.multiplyPercent(rechargeAmount, activityAssignDayCondVO.getDepositPercent());
                }
                acquireNum = activityAssignDayCondVO.getAcquireNum();
            }
            //充本金的法币 + 彩金平台币(转法币)
            BigDecimal totalAmount = rechargeAmount.add(acquireAmountMainCurrency);
            BigDecimal requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
            if (requiredTurnover.compareTo(BigDecimal.ZERO) > 0) {
                requiredTurnover = requiredTurnover.subtract(rechargeAmount);
            }
            log.info("指定日期存款,配置为百分比,用户:{},充值金额转换为平台币:{},奖励金额:{}{},奖励次数:{}", reportUserRechargeResponseVO.getUserId(), rechargeToPlatFormAmount, acquireAmountMainCurrency, rewardCurrencyCode, acquireNum);
            activityAssignDayCondVO.setAcquireNum(acquireNum);
            activityAssignDayCondVO.setAcquireAmount(acquireAmountMainCurrency);
            activityAssignDayCondVO.setRequiredTurnover(requiredTurnover);
            return activityAssignDayCondVO;
        } else {
            BigDecimal acquireAmount = BigDecimal.ZERO;
            Integer acquireNum = 0;
            rewardCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
            ActivityAssignDayCondVO activityAssignDayCondVOResult = new ActivityAssignDayCondVO();
            List<ActivityAssignDayCondVO> activityAssignDayCondVOSConfig = new ArrayList<>();
            if (!StringUtils.hasText(venueType)) {
                activityAssignDayCondVOSConfig = activityAssignDayCondVOList;
            } else {
                activityAssignDayCondVOSConfig = depositConfigDTO.getActivityAssignDayCondVOList();
            }
            for (ActivityAssignDayCondVO activityAssignDayCondVO : activityAssignDayCondVOSConfig) {
                //判断一下是否满足某个区间
                BigDecimal minDeposit = activityAssignDayCondVO.getMinDepositAmt();
                BigDecimal maxDeposit = activityAssignDayCondVO.getMaxDepositAmt();
                //只要大于最大值 奖励金额取配置金额
                log.debug("指定日期存款,平台币金额:{},最小充值:{},最大充值:{},当前配置奖励金额:{}", rechargeToPlatFormAmount, minDeposit, maxDeposit, activityAssignDayCondVO.getAcquireAmount());
                if (rechargeToPlatFormAmount.compareTo(maxDeposit) >= 0) {
                    BeanUtils.copyProperties(activityAssignDayCondVO, activityAssignDayCondVOResult);
                    acquireAmount = activityAssignDayCondVO.getAcquireAmount();
                    acquireNum = activityAssignDayCondVO.getAcquireNum();
                } else {
                    //需要大于最小值
                    if (rechargeToPlatFormAmount.compareTo(minDeposit) >= 0) {
                        BeanUtils.copyProperties(activityAssignDayCondVO, activityAssignDayCondVOResult);
                        acquireAmount = activityAssignDayCondVO.getAcquireAmount();
                        acquireNum = activityAssignDayCondVO.getAcquireNum();
                    }
                    break;
                }
            }
            //彩金法币金额
            BigDecimal activityAmountCurrency = AmountUtils.multiply(acquireAmount, transferRate);
            log.info("指定日期存款,平台币金额:{},实际奖励金额:{}转换为法币金额:{}", rechargeToPlatFormAmount, acquireAmount, activityAmountCurrency);
            //充本金的法币 + 彩金平台币(转法币)
            BigDecimal totalAmount = rechargeAmount.add(activityAmountCurrency);
            BigDecimal requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
            if (requiredTurnover.compareTo(BigDecimal.ZERO) > 0) {
                requiredTurnover = requiredTurnover.subtract(rechargeAmount);
            }
            activityAssignDayCondVOResult.setAcquireNum(acquireNum);
            activityAssignDayCondVOResult.setAcquireAmount(acquireAmount);
            activityAssignDayCondVOResult.setRequiredTurnover(requiredTurnover);
            activityAssignDayCondVOResult.setCurrencyCode(rewardCurrencyCode);
            return activityAssignDayCondVOResult;
        }

    }

    /**
     * 活动保存，下一步，各个活动自己校验参数
     *
     * @param activityConfigVO
     */
    @Override
    public void checkSecond(ActivityConfigVO activityConfigVO) {
        ActivityAssignDayVO activityAssignDayVO = activityConfigVO.getActivityAssignDayVO();
        if (ObjectUtil.isEmpty(activityAssignDayVO.getVenueType())) {
            boolean checkParamFlag = activityAssignDayVO.validate();
            if (!checkParamFlag) {
                log.info("{},指定存款日 详细校验失败", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        // 游戏大类配置存款奖励校验
        if (Objects.nonNull(activityAssignDayVO.getActivityAssignDayVenueVOS()) && activityAssignDayVO.getActivityAssignDayVenueVOS().size() > 0) {
            for (ActivityAssignDayVenueVO depositConfigDTO : activityAssignDayVO.getActivityAssignDayVenueVOS()) {
                if (!depositConfigDTO.validate()) {
                    log.info("{},指定存款日 详细校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

            }
        }

        // 判断是否配置了游戏大类
        if (ObjectUtil.isNotEmpty(activityAssignDayVO.getVenueType())) {
            List<GameSelectVO> gameSelect = gameService.getGameSelect(ActivityTemplateEnum.ASSIGN_DAY.getType(), activityConfigVO.getSiteCode());
            for (GameSelectVO vo : gameSelect) {
                if (!DataUtils.checkStringSame(activityAssignDayVO.getVenueType(), vo.getVenueType())) {
                    log.info("{},指定存款日 详细校验失败", getActivity().getName());
                    throw new BaowangDefaultException(ResultCode.ADMIN_CENTER_ACTIVITY_GAME_TYPE_MISMATCH);
                }
            }
        } else {
            // 校验洗码倍率
            if (Objects.isNull(activityAssignDayVO.getWashRatio()) || activityAssignDayVO.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{},指定存款日 详细校验失败", getActivity().getName());
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
    }


    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        siteActivityAssignDayService.deleteByActivityId(vo.getId());
    }


    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBasePO> allValidBasePos) {
        if (CollectionUtils.isEmpty(allValidBasePos)) {
            log.info("指定日期活动,不存在已开启的,可以直接操作");
            return;
        }
        String activityId = vo.getId();
        SiteActivityAssignDayPO currentActivityPO = this.siteActivityAssignDayService.info(activityId);
        String weekDays = currentActivityPO.getWeekDays();
        List<String> originWeekDays = Arrays.stream(weekDays.split(",")).toList();
        List<String> activityIds = allValidBasePos.stream().map(SiteActivityBasePO::getId).toList();
        List<SiteActivityAssignDayPO> siteActivityAssignDayPOS = this.siteActivityAssignDayService.selectByActivityIds(activityIds);
        for (SiteActivityAssignDayPO siteActivityAssignDayPO : siteActivityAssignDayPOS) {
            List<String> compareWeekDays = Arrays.stream(siteActivityAssignDayPO.getWeekDays().split(",")).toList();
            boolean hasSameElement = Collections.disjoint(originWeekDays, compareWeekDays);
            //包含指定元素 返回false
            if (!hasSameElement) {
                log.info("指定日期存在相同配置已开启,此活动:{}无法开启", activityId);
                throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
            }
        }
    }


    public ToActivityVO toActivity(String activityBase, UserBaseReqVO userBaseReqVO) {
        String userId = userBaseReqVO.getUserId();
        String siteCode = userBaseReqVO.getSiteCode();
        ActivityAssignDayRespVO respVO = JSONObject.parseObject(activityBase, ActivityAssignDayRespVO.class);
        String activityTemplate = respVO.getActivityTemplate();
        Integer participationMode = respVO.getParticipationMode();
        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder()
                .activityTemplate(respVO.getActivityTemplate())
                .siteCode(userBaseReqVO.getSiteCode())
                .userId(userBaseReqVO.getUserId())
                .build();

//        String[] weekList = respVO.getWeekDays().split(",");
//        List<Long> dayList = Lists.newArrayList();

        //Long toDayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), userBaseReqVO.getTimezone());
        //Long toDayEndTime = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), userBaseReqVO.getTimezone());
        Long toDayStartTime = userBaseReqVO.getDayStartTime();
        Long toDayEndTime = userBaseReqVO.getDayEndTime();
        //查询出当天的开始时间戳
        queryParam.setDay(toDayStartTime);

//        for (String week : weekList) {
//            dayList.add(TimeZoneUtils.getWeekdayStartTimestamp(Integer.parseInt(week), CurrReqUtils.getTimezone()));
//        }
//        queryParam.setDayList(dayList);


        //是否有参与记录
        boolean activityCondition = siteActivityEventRecordService.toActivityEventRecordCount(queryParam) <= 0;
        ;
        //是否申请操作
        if (userBaseReqVO.isApplyFlag()) {
            // 人工点击参与
            if (!activityCondition) {
                log.info("指定日期存款 检查参与:{}活动,siteCoe:{},userId:{},已经参与,不能再参加", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
            }
        } else {
            // job或mq触发
            if (activityCondition && ActivityParticipationModeEnum.MANUAL.getCode().equals(participationMode)) {
                log.info("指定日期存款  检查参与:{}活动,siteCoe:{},userId:{},尚未参与不能派发", activityTemplate, userBaseReqVO.getSiteCode(), userBaseReqVO.getUserId());
                return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
            }
        }

        //是否发放过记录
        /*if (siteActivityOrderRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteActivityOrderRecordPO.class)
                .eq(SiteActivityOrderRecordPO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordPO::getUserId, userId)
                .between(SiteActivityOrderRecordPO::getCreatedTime, toDayStartTime, toDayEndTime)
                .eq(SiteActivityOrderRecordPO::getSiteCode, siteCode)) > 0) {
            log.info("指定日期存款  申请参与:{},活动,siteCoe:{}userId:{},被拒绝,重复参与", activityTemplate, siteCode, userId);
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
        }*/
        return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).message(ResultCode.SUCCESS.getMessageCode()).build();
    }


    public ActivityConfigDetailVO getConfigDetail(String activityBase, ActivityConfigDetailVO detailVO,
                                                  String siteCode, String timezone, String userId) {
        ActivityAssignDayRespVO activityAssignDayRespVO = JSONObject.parseObject(activityBase, ActivityAssignDayRespVO.class);
        SiteActivityEventRecordQueryParam queryParam = SiteActivityEventRecordQueryParam
                .builder().siteCode(siteCode)
                .activityTemplate(activityAssignDayRespVO.getActivityTemplate())
                .day(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone))
                .userId(userId).activityId(activityAssignDayRespVO.getActivityId())
                .timezone(timezone).build();
        //参加过的不能参与
        if (siteActivityEventRecordService.toActivityEventRecordCount(queryParam) > 0) {
            log.info("指定日期存款活动,siteCoe:{}userId:{},已经参与,不需要显示按钮", siteCode, userId);
            detailVO.setActivityCondition(false);
            detailVO.setStatus(ResultCode.ACTIVITY_REPEAT.getCode());
        }
        return detailVO;
    }
}

