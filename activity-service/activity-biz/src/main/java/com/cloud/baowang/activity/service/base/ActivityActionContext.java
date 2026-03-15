package com.cloud.baowang.activity.service.base;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.SiteActivityEventRecordPO;
import com.cloud.baowang.activity.service.SiteActivityDetailService;
import com.cloud.baowang.activity.service.SiteActivityEventRecordService;
import com.cloud.baowang.activity.service.base.activity.ActivityBaseInterface;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.AgentUserBenefitEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelIdReqVO;
import com.cloud.baowang.wallet.api.api.UserActivityTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.activity.UserActivityTypingAmountResp;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingAmountVO;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;


@Slf4j
@Component
@AllArgsConstructor
public class ActivityActionContext {

    private final List<ActivityBaseInterface> interfacesList;
    private final ActivityBaseContext activityBaseContext;
    private final SiteActivityDetailService siteActivityDetailService;
    private final UserInfoApi userInfoApi;
    private final SiteUserLabelConfigApi siteUserLabelConfigApi;
    private final SiteActivityEventRecordService siteActivityEventRecordService;
    private final SiteApi siteApi;
    private final AgentInfoApi agentInfoApi;
    private final UserActivityTypingAmountApi userActivityTypingAmountApi;
    private Map<ActivityTemplateEnum, ActivityBaseInterface> interfaceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (ActivityBaseInterface baseInterface : interfacesList) {
            interfaceMap.put(baseInterface.getActivity(), baseInterface);
        }
    }

    public ActivityBaseInterface getInterface(String template) {
        ActivityBaseInterface activityBaseInterface = interfaceMap.get(ActivityTemplateEnum.nameOfCode(template));
        if (ObjectUtil.isEmpty(activityBaseInterface)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return activityBaseInterface;
    }


    /***
     * 只要在 展示时间 范围内 就显示按钮
     * @param activityConfigDetailReq 查询参数
     * @return 活动详情
     */
    public ResponseVO<ActivityConfigDetailVO> getConfigDetail(ActivityConfigDetailReq activityConfigDetailReq) {
        long nowTime = System.currentTimeMillis();
        Integer deviceType = activityConfigDetailReq.getReqDeviceType();
        String activityId = activityConfigDetailReq.getId();
        String timeZone = activityConfigDetailReq.getTimezone();
        String activityBaseRespVO = activityBaseContext.getActivityByTemplate(ActivityConfigDetailReq.builder()
                .showStartTime(nowTime)
                .showEndTime(nowTime)
                .id(activityConfigDetailReq.getId())
                .showTerminal(String.valueOf(deviceType))
                .status(EnableStatusEnum.ENABLE.getCode())
                .siteCode(activityConfigDetailReq.getSiteCode())
                .build());
        if (ObjectUtils.isEmpty(activityBaseRespVO)) {
            log.info("活动未配置或未开启:{}", activityId);
            return ResponseVO.success();
        }
        ActivityBaseRespVO baseRespVO = JSONObject.parseObject(activityBaseRespVO, ActivityBaseRespVO.class);
        ActivityConfigDetailVO activityConfigDetailVO = ActivityConfigDetailVO
                .builder()
                .build();
        // 所有的子类
        ActivityFirstRechargeRespVO firstRechargeRespVO = JSONObject.parseObject(activityBaseRespVO, ActivityFirstRechargeRespVO.class);
        BeanUtils.copyProperties(firstRechargeRespVO, activityConfigDetailVO);
        activityConfigDetailVO.setActivityCondition(true);
        activityConfigDetailVO.setStatus(ResultCode.SUCCESS.getCode());
        activityConfigDetailVO.setUserType(ActivityUserTypeEnum.ALL_USER.getCode());
        activityConfigDetailVO.setUserId(activityConfigDetailReq.getUserId());
        activityConfigDetailVO.setUserAccount(activityConfigDetailReq.getUserAccount());
        activityConfigDetailVO.setSiteCode(activityConfigDetailReq.getSiteCode());
        //配置了指定 周期的活动 要当前的周期校验
        String activityTemplate = baseRespVO.getActivityTemplate();
      /*  if (ActivityTemplateEnum.getToActivityTemplateWeek().contains(activityTemplate)) {
            Boolean spinBool = siteActivityDetailService.getWeekConfigDetailCheck(activityBaseRespVO,timeZone);
            if (!spinBool) {
                activityConfigDetailVO.setActivityCondition(false);
                return ResponseVO.success(activityConfigDetailVO);
            }
        }*/

        //自动参与直接不显示按钮
       /* if (ActivityParticipationModeEnum.AUTO.getCode().equals(activityConfigDetailVO.getParticipationMode())) {
            activityConfigDetailVO.setActivityCondition(false);
            return ResponseVO.success(activityConfigDetailVO);
        }*/

        // 查看会员是否申请了，如果申请，前端不需要显示
        UserInfoVO userInfoVO = UserInfoVO.builder().userId(activityConfigDetailReq.getUserId()).siteCode(activityConfigDetailReq.getSiteCode()).build();
        UserActivityTypingAmountResp typingLimit = userActivityTypingAmountApi.getUserActivityTypingLimit(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        if (typingLimit != null) {
            activityConfigDetailVO.setSelectVenueType(typingLimit.getLimitGameType());
            activityConfigDetailVO.setSelectFlag("1");
        } else {
            activityConfigDetailVO.setSelectFlag("0");
        }
        List<VenueValueVO> venueValueVOS = getVenueTypeList(activityConfigDetailVO.getVenueType(), activityConfigDetailVO.getSelectVenueType(), firstRechargeRespVO, activityConfigDetailVO);
        activityConfigDetailVO.setVenueTypeList(venueValueVOS);

        //用户未登录,直接返回可以参与按钮
        processVenueType(activityConfigDetailVO, firstRechargeRespVO.getActivityTemplate());
        if (ObjectUtil.isEmpty(activityConfigDetailReq.getUserId())) {
            activityConfigDetailVO.setActivityCondition(true);
            return ResponseVO.success(activityConfigDetailVO);
        }

        getInterface(activityTemplate).getConfigDetail(activityBaseRespVO, activityConfigDetailVO,
                activityConfigDetailReq.getSiteCode(), activityConfigDetailReq.getTimezone(), activityConfigDetailReq.getUserId());


        //按钮默认 高亮展示
        Long activityStartTime = firstRechargeRespVO.getActivityStartTime();
        Long activityEndTime = firstRechargeRespVO.getActivityEndTime();

        //活动尚未开始时 按钮置灰
        if (activityStartTime != null && nowTime < activityStartTime) {
            activityConfigDetailVO.setActivityCondition(false);
            log.info("{},活动尚未开始{}", nowTime, activityStartTime);
        }
        if (activityEndTime != null && nowTime > activityEndTime) {
            activityConfigDetailVO.setActivityCondition(false);
            log.info("{},活动已经结束{}", nowTime, activityEndTime);
        }
        // 设置默认，如果选中，则外层使用选中，如果没有选中游戏大类，则使用第一个
        processVenueType(activityConfigDetailVO, firstRechargeRespVO.getActivityTemplate());
        return ResponseVO.success(activityConfigDetailVO);
    }

    private void processVenueType(ActivityConfigDetailVO activityConfigDetailVO, String template) {
        // 仅对于首存，次存，指定日
        if (!ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(template)
                && !ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(template)
                && !ActivityTemplateEnum.ASSIGN_DAY.getType().equals(template)) {
            return;
        }
        List<VenueValueVO> venueTypeList = activityConfigDetailVO.getVenueTypeList();
        if (CollectionUtils.isEmpty(venueTypeList)) {
            return;
        }
        VenueValueVO firstVenue = venueTypeList.stream().filter(e -> e.getSelectFlag().equals("1")).findFirst().orElse(venueTypeList.get(0));
        if (firstVenue != null) {
            activityConfigDetailVO.setActivityAmount(firstVenue.getActivityAmount());
            activityConfigDetailVO.setActivityAmountCurrencyCode(firstVenue.getActivityAmountCurrencyCode());
            activityConfigDetailVO.setRunningWater(firstVenue.getRunningWater());
            activityConfigDetailVO.setRunningWaterCurrencyCode(firstVenue.getRunningWaterCurrencyCode());
            activityConfigDetailVO.setActivityRuleI18nCode(firstVenue.getActivityRuleI18nCode());
        }
    }

    /**
     * @param venueType
     * @param selectVenueType
     * @param firstRechargeRespVO
     * @param activityConfigDetailVO 返回结果
     * @return
     */
    public List<VenueValueVO> getVenueTypeList(String venueType, String selectVenueType, ActivityFirstRechargeRespVO firstRechargeRespVO, ActivityConfigDetailVO activityConfigDetailVO) {
        List<VenueValueVO> strings = new ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isBlank(venueType)) {
            return strings;
        }
        String[] arrL = venueType.split(CommonConstant.COMMA);
        //String[] value = venueTypeText.split(CommonConstant.COMMA);
        List<DepositConfigDTO> depositConfigDTOS = new ArrayList<>();
        List<ActivityAssignDayVenueVO> activityAssignDayVenueVOS = new ArrayList<>();
        if (ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(firstRechargeRespVO.getActivityTemplate())
                || ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(firstRechargeRespVO.getActivityTemplate())) {
            depositConfigDTOS = firstRechargeRespVO.getDepositConfigDTOS();
        } else if (ActivityTemplateEnum.ASSIGN_DAY.getType().equals(firstRechargeRespVO.getActivityTemplate())) {
            activityAssignDayVenueVOS = firstRechargeRespVO.getActivityAssignDayVenueVOS();
        }
        for (int i = 0; i < arrL.length; i++) {
            String venueTypeTemp = arrL[i];
            VenueValueVO arrMap = new VenueValueVO();
            arrMap.setType("venue_type");
            arrMap.setCode(venueTypeTemp);
            //arrMap.setValue(value[i]);

            if (ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(firstRechargeRespVO.getActivityTemplate())
                    || ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(firstRechargeRespVO.getActivityTemplate())) {
                if (!CollectionUtils.isEmpty(depositConfigDTOS)) {
                    DepositConfigDTO depositConfigDTO = depositConfigDTOS.stream().filter(e -> ObjectUtil.equals(e.getVenueType(), venueTypeTemp)).findFirst().orElse(null);
                    if (ObjectUtil.isNotNull(depositConfigDTO)) {
                        arrMap.setActivityRuleI18nCode(depositConfigDTO.getActivityRuleI18nCode());
                    }
                }
            } else if (ActivityTemplateEnum.ASSIGN_DAY.getType().equals(firstRechargeRespVO.getActivityTemplate())) {
                if (!CollectionUtils.isEmpty(activityAssignDayVenueVOS)) {
                    ActivityAssignDayVenueVO activityAssignDayVenueVO = activityAssignDayVenueVOS.stream().filter(e -> ObjectUtil.equals(e.getVenueType(), venueTypeTemp)).findFirst().orElse(null);
                    if (ObjectUtil.isNotNull(activityAssignDayVenueVO)) {
                        arrMap.setActivityRuleI18nCode(activityAssignDayVenueVO.getActivityRuleI18nCode());
                    }
                }
            }
            if (arrL[i].equals(selectVenueType)) {
                arrMap.setSelectFlag("1");

            } else {
                arrMap.setSelectFlag("0");
            }

            strings.add(arrMap);

        }

        return strings;
    }


    /**
     * 活动统一校验入口
     * <p>
     * 参与活动的先后判断顺序
     * 展示时间
     * 活动时间
     * 验证登录
     * 账号类型
     * 代理会员福利
     * 会员标签（不可参与的情况） 不能参加活动;无存款优惠
     * 参与活动条件
     * 绑定手机号、邮箱
     * 参与IP
     */
    public ToActivityVO checkToActivity(UserBaseReqVO userBaseReqVO) {
        String userId = userBaseReqVO.getUserId();
        String siteCode = userBaseReqVO.getSiteCode();
        String activityId = userBaseReqVO.getActivityId();
        String timeZone = userBaseReqVO.getTimezone();
        // 需要考虑是不是 申请操作 还是 派发操作
        boolean applyFlag = userBaseReqVO.isApplyFlag();

        // || UserLabelEnum.NO_DEPOSIT_BONUS.getLabelId().equals(o.getLabelId())
        Integer reqDeviceType = userBaseReqVO.getDeviceType();
        String reqDeviceTypeStr = "";
        if (reqDeviceType != null) {
            reqDeviceTypeStr = String.valueOf(reqDeviceType);
        }
        ActivityConfigDetailReq activityConfigDetailReq = ActivityConfigDetailReq
                .builder()
                .id(activityId)
                .status(EnableStatusEnum.ENABLE.getCode())
                .siteCode(siteCode)
                .showTerminal(reqDeviceTypeStr)
                .applyFlag(applyFlag)
                .build();
        String activityBaseRespVO=activityBaseContext.getActivityByTemplate(activityConfigDetailReq);
        /*if (applyFlag) {
            // 申请
            activityBaseRespVO = activityBaseContext.getActivityByTemplate(activityConfigDetailReq);
        } else {
            // 派发
            activityBaseRespVO = activityBaseContext.getActivityByTemplateForSend(activityConfigDetailReq);
        }*/


        // 派发不校验
        if (ObjectUtils.isEmpty(activityBaseRespVO) && applyFlag) {
            log.info("执行参与活动校验失败,活动未配置或未开启:{}", activityId);
            return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
        }
        // 代表所有的活动
        ActivityFirstRechargeRespVO firstRechargeRespVO = JSONObject.parseObject(activityBaseRespVO, ActivityFirstRechargeRespVO.class);
        long nowTime = System.currentTimeMillis();

        Long showStartTime = firstRechargeRespVO.getShowStartTime();
        Long showEndTime = firstRechargeRespVO.getShowEndTime();
        if (showStartTime != null && nowTime < showStartTime) {
            return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
        }
        if (showEndTime != null && nowTime > showEndTime) {
            return buildResponse(ResultCode.ACTIVITY_HAS_END);
        }

        Long activityStartTime = firstRechargeRespVO.getActivityStartTime();
        Long activityEndTime = firstRechargeRespVO.getActivityEndTime();

        //长期活动
        if (ActivityDeadLineEnum.LONG_TERM.getType().equals(firstRechargeRespVO.getActivityDeadline())) {
            //长期活动没有开始时间
            if (ObjectUtil.isEmpty(activityStartTime)) {
                log.info("执行参与活动失败,长期活动没有开始时间:{}", activityId);
                return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
            }

            //活动不满足条件
            if (nowTime < activityStartTime) {
                log.info("执行参与活动失败,长期活动开始时间未到:{}", activityId);
                return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
            }
        }

        //限时
        if (ActivityDeadLineEnum.LIMITED_TIME.getType().equals(firstRechargeRespVO.getActivityDeadline())) {

            //限时活动没有开始时间
            if (ObjectUtil.isEmpty(activityStartTime)) {
                log.info("执行参与活动失败,限时活动没有开始时间:{}", activityId);
                return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
            }

            //限时活动没有结束时间
            if (ObjectUtil.isEmpty(activityEndTime)) {
                log.info("执行参与活动失败,限时活动没有结束时间:{}", activityId);
                return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
            }

            //限时活动开始时间未到
            if (nowTime < activityStartTime) {
                log.info("执行参与活动失败,限时活动开始时间未到:{}", activityId);
                return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
            }

            //限时活动结束时间超出当前时间,申请校验，发放不校验
            if (nowTime > activityEndTime && applyFlag) {
                log.info("执行参与活动失败,限时活动结束时间超出当前时间:{}", activityId);
                return buildResponse(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED);
            }
        }

        String activityTemplate = firstRechargeRespVO.getActivityTemplate();
        //免费旋转 || 指定日存款 周期校验 并且是 申请操作
        if (ActivityTemplateEnum.getToActivityTemplateWeek().contains(firstRechargeRespVO.getActivityTemplate()) && applyFlag) {
            Boolean bool = siteActivityDetailService.getWeekConfigDetailCheck(activityBaseRespVO, timeZone);
            if (!bool) {
                log.info("申请参与:{},活动,siteCoe:{},userId:{},被拒绝,不符合条件", activityTemplate, siteCode, userId);
                return buildResponse(ResultCode.ACTIVITY_NOT);
            }
        }


        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        /*if(Objects.equals(UserAccountTypeEnum.TEST_ACCOUNT.getCode(), Integer.valueOf(userInfoVO.getAccountType()))){
            log.info("当前用户:{} 为测试账号不能参与活动:{}", userId, activityId);
            return buildResponse(ResultCode.ACTIVITY_CAN_NOT_JOIN);
        }*/

        String agentId = userInfoVO.getSuperAgentId();
        if (StringUtils.hasText(agentId)) {
            AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(agentId);
            if (agentInfoVO != null) {
                String userBenefit = agentInfoVO.getUserBenefit();
                if (StringUtils.hasText(userBenefit) && !userBenefit.contains(AgentUserBenefitEnum.DISCOUNT_ACTIVITY.getCode().toString())) {
                    log.info("当前用户:{}上级代理:{}设置会员福利不能参与活动:{}", userId, agentId, activityId);
                    return buildResponse(ResultCode.ACTIVITY_CAN_NOT_JOIN);
                }
            }
        }

        UserLabelIdReqVO userLabelIdReqVO = new UserLabelIdReqVO();
        userLabelIdReqVO.setLabelIds(userInfoVO.getUserLabelId());
        userLabelIdReqVO.setSiteCode(userInfoVO.getSiteCode());
        List<GetUserLabelByIdsVO> getUserLabelByIdsVOS = siteUserLabelConfigApi.getUserLabel(userLabelIdReqVO);
        if (!CollectionUtils.isEmpty(getUserLabelByIdsVOS)) {
            Optional<GetUserLabelByIdsVO> getUserLabelByIdsVOOptional = getUserLabelByIdsVOS.stream().filter(o -> {
                // 标签:不参加活动 不要参加活动
                return UserLabelEnum.NO_PARTICIPATION_ACTIVITY.getLabelId().equals(o.getLabelId());
            }).findFirst();
            if (getUserLabelByIdsVOOptional.isPresent()) {
                log.info("当前用户:{}被打标签,不能参与活动:{}", userId, activityId);
                return buildResponse(ResultCode.ACTIVITY_CAN_NOT_JOIN);
            }
        }


        if (!CollectionUtils.isEmpty(getUserLabelByIdsVOS)) {
            Optional<GetUserLabelByIdsVO> getUserLabelByIdsVOOptional = getUserLabelByIdsVOS.stream().filter(o -> {
                // 标签:无存款优惠 不需要参加活动
                return UserLabelEnum.NO_DEPOSIT_BONUS.getLabelId().equals(o.getLabelId());
            }).findFirst();
            boolean checkFlag = ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(activityTemplate) ||
                    ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(activityTemplate) ||
                    ActivityTemplateEnum.ASSIGN_DAY.getType().equals(activityTemplate) ||
                    ActivityTemplateEnum.FREE_WHEEL.getType().equals(activityTemplate);
            ;
            if (checkFlag && getUserLabelByIdsVOOptional.isPresent()) {
                log.info("当前用户:{}被打标签:无存款优惠,不能参与活动:{},{}", userId, activityId, activityTemplate);
                return buildResponse(ResultCode.ACTIVITY_CAN_NOT_JOIN);
            }
        }

        if (EnableStatusEnum.ENABLE.getCode().equals(firstRechargeRespVO.getSwitchPhone()) && ObjectUtil.isEmpty(userInfoVO.getPhone())
                && EnableStatusEnum.ENABLE.getCode().equals(firstRechargeRespVO.getSwitchEmail()) && ObjectUtil.isEmpty(userInfoVO.getEmail())) {
            log.info("用户: {}, 手机号与邮箱都没绑定", userInfoVO.getUserId());
            return buildResponse(ResultCode.ACTIVITY_AND_EMAIL_NOT);
        }

        if (EnableStatusEnum.ENABLE.getCode().equals(firstRechargeRespVO.getSwitchPhone()) && ObjectUtil.isEmpty(userInfoVO.getPhone())) {
            log.info("用户: {}, 手机号未绑定", userInfoVO.getUserId());
            return buildResponse(ResultCode.ACTIVITY_PHONE_NOT);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(firstRechargeRespVO.getSwitchEmail()) && ObjectUtil.isEmpty(userInfoVO.getEmail())) {
            log.info("用户: {}, 邮箱未绑定", userInfoVO.getUserId());
            return buildResponse(ResultCode.ACTIVITY_EMAIL_NOT);
        }

        Integer switchIp = firstRechargeRespVO.getSwitchIp();

        //IP状态开启,验证该IP是否被参与 ;申请按钮试校验
        if (EnableStatusEnum.ENABLE.getCode().equals(switchIp) && applyFlag) {
            //NOTE 领取当天时间戳
            Long todayStartTime = DateUtils.getTodayStartTime(timeZone);
            LambdaQueryWrapper<SiteActivityEventRecordPO> eventWrappers = Wrappers.lambdaQuery(SiteActivityEventRecordPO.class)
                    .eq(SiteActivityEventRecordPO::getSiteCode, siteCode)

                    .ge(SiteActivityEventRecordPO::getDay, todayStartTime)
                    .lt(SiteActivityEventRecordPO::getDay, todayStartTime + (1000 * 3600 * 24))

                    .eq(SiteActivityEventRecordPO::getActivityTemplate, activityTemplate).
                    eq(SiteActivityEventRecordPO::getIp, userInfoVO.getLastLoginIp());
            //转盘活动同一个IP 是同一个人 可以参加多次,
            if (activityTemplate.equals(ActivityTemplateEnum.SPIN_WHEEL.getType())) {
                eventWrappers.ne(SiteActivityEventRecordPO::getUserId, userId);
            }

            Long eventCount = siteActivityEventRecordService.getBaseMapper().selectCount(eventWrappers);
            if (eventCount > 0) {
                log.info("申请参与:{},活动,userId:{},开启IP校验:{},已经被参与过活动", activityTemplate, userId, userInfoVO.getLastLoginIp());
                return buildResponse(ResultCode.ACTIVITY_IP_NOT);
            }
        }

        ToActivityVO toActivityVO = getInterface(activityTemplate).toActivity(activityBaseRespVO, userBaseReqVO);
        if (ResultCode.SUCCESS.getCode() != toActivityVO.getStatus()) {
            return toActivityVO;
        }
        // 校验
        if (!ObjectUtils.isEmpty(userBaseReqVO.getVenueType())) {
            // 三种活动

            if (!isSingleValidNumber(userBaseReqVO.getVenueType())) {
                log.info("申请参与:{},活动,userId:{},游戏大类校验失败:{}", userId, userBaseReqVO.getVenueType());
                return buildResponse(ResultCode.PARAM_ERROR);
            }
            String recordType = firstRechargeRespVO.getVenueType();
            // 选择的要在 活动配置种
            if (!ObjectUtils.isEmpty(recordType)) {
                if (!recordType.contains(userBaseReqVO.getVenueType())) {
                    log.info("申请参与:{},活动,userId:{},游戏大类校验失败:{}", userId, userBaseReqVO.getVenueType());
                    return buildResponse(ResultCode.PARAM_ERROR);
                }
                // 判断是否被与之前选择的相同
                UserActivityTypingAmountResp typingLimit = userActivityTypingAmountApi.getUserActivityTypingLimit(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                // 在其他活动已经申请游戏大类，判断这次的游戏大类与所选择的是一样的
                if (typingLimit != null) {
                    // 如果活动配置包括个人已经选择的，则要判断与这次申请的是否一致，不一致则提示
                    if (!typingLimit.getLimitGameType().equals(userBaseReqVO.getVenueType())) {
                        // 如果活动配置不包括个人已经选择的，则说明活动去掉了，需要判断这个人之前的流水是否打完了，如果打完了，则可以申请
                        BigDecimal typingAmount = typingLimit.getTypingAmount() == null ? BigDecimal.ZERO : typingLimit.getTypingAmount();
                        if (typingAmount.compareTo(BigDecimal.ZERO) > 0) {
                            return buildResponse(ResultCode.ADMIN_CENTER_ACTIVITY_PARTICIPATION_LIMIT);
                        }
                    }
                }
            }

        }

        return buildResponse(ResultCode.SUCCESS);
    }


    /**
     * 参与活动的先后判断顺序
     * 展示时间
     * 活动时间
     * 验证登录
     * 账号类型
     * 代理会员福利
     * 活动标签（不可参与的情况）
     * 参与活动条件
     * 绑定手机号、邮箱
     * 参与IP
     *
     * @param baseReqVO
     * @return
     */
    @DistributedLock(name = RedisConstants.TO_ACTIVITY_LOCK, unique = "#baseReqVO.userId + ':' + #baseReqVO.activityId", waitTime = 3, leaseTime = 180)
    public ToActivityVO toActivity(UserBaseReqVO baseReqVO) {

        String activityId = baseReqVO.getActivityId();
        String siteCode = baseReqVO.getSiteCode();
        String timezone = baseReqVO.getTimezone();
        String userId = baseReqVO.getUserId();
        Integer deviceType = baseReqVO.getDeviceType();
        String venueType = baseReqVO.getVenueType();


        // 各个活动实体
        ActivityConfigDetailReq activityConfigDetailReq = ActivityConfigDetailReq
                .builder()
                .id(activityId)
                .showTerminal(String.valueOf(deviceType))
                .status(EnableStatusEnum.ENABLE.getCode())
                .siteCode(siteCode)
                .build();
        String activityBaseRespVO = activityBaseContext.getActivityByTemplate(activityConfigDetailReq);
        // 所有活动的校验逻辑
        ToActivityVO toActivityVO = checkToActivity(baseReqVO);

        if (toActivityVO.getStatus() != ResultCode.SUCCESS.getCode()) {
            return toActivityVO;
        }


        //
        if (ObjectUtils.isEmpty(activityBaseRespVO)) {
            log.info("执行参与活动失败,活动未配置或未开启:{}", activityId);
            return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
        }

        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();

        ActivityFirstRechargeRespVO firstRechargeRespVO = JSONObject.parseObject(activityBaseRespVO, ActivityFirstRechargeRespVO.class);
        String activityTemplate = firstRechargeRespVO.getActivityTemplate();

        //如果不是转盘的活动,其他所有的活动只能是手动参与的才可以调用这个接口
        if (!ActivityTemplateEnum.SPIN_WHEEL.getType().equals(activityTemplate) &&
                !ActivityParticipationModeEnum.MANUAL.getCode().equals(firstRechargeRespVO.getParticipationMode())) {

            log.info("用户:{},siteCode:{},自动参与的活动详情被手动调用", userId, siteCode);
            return buildResponse(ResultCode.ACTIVITY_NOT);
        }

        String code = activityTemplate;
        //因为转盘一天可以参加多次，所以针对这个字段用时间戳，每次都在变
        if (activityTemplate.equals(ActivityTemplateEnum.SPIN_WHEEL.getType())) {
            code = String.valueOf(System.currentTimeMillis());
        }

        SiteActivityEventRecordPO siteActivityEventRecordPO = SiteActivityEventRecordPO.builder()
                .activityId(activityId)
                .code(code)
                .ip(userInfoVO.getLastLoginIp())
                .siteCode(siteCode)
                .activityTemplate(activityTemplate)
                .status(ActivityEventStatusEnum.UNISSUED.getCode())
                .userAccount(userInfoVO.getUserAccount())
                .vipRank(userInfoVO.getVipRank())
                .day(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone))
                .userId(userId)
                .deviceNo(userInfoVO.getLastDeviceNo())
                .calculateType(firstRechargeRespVO.getCalculateType())
                .build();

        log.info("用户:{}参与活动:{}成功", userId, activityTemplate);
        long count = siteActivityEventRecordService.getBaseMapper().insert(siteActivityEventRecordPO);
        if (count <= 0) {
            log.info("申请参与:{},活动,userId:{},插入失败", activityTemplate, userId);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        RechargeTriggerVO triggerVO = new RechargeTriggerVO();
        triggerVO.setSiteCode(userInfoVO.getSiteCode());
        triggerVO.setUserAccount(userInfoVO.getUserAccount());
        triggerVO.setUserId(userInfoVO.getUserId());
        triggerVO.setCurrencyCode(userInfoVO.getMainCurrency());
        triggerVO.setApplyFlag(baseReqVO.isApplyFlag());

        // 申请是否添加活动的游戏大类,判断是否选择了，选择了就需要配置游戏大类
        if (!ObjectUtils.isEmpty(baseReqVO.getVenueType())) {

            UserActivityTypingAmountVO userActivityTypingAmountVO = UserActivityTypingAmountVO.builder().build();
            BeanUtils.copyProperties(userInfoVO, userActivityTypingAmountVO);
            userActivityTypingAmountVO.setLimitGameType(venueType);
            userActivityTypingAmountVO.setCurrency(userInfoVO.getMainCurrency());
            userActivityTypingAmountVO.setSiteCode(userInfoVO.getSiteCode());
            userActivityTypingAmountVO.setTypingAmount(BigDecimal.ZERO);
            userActivityTypingAmountApi.initUserActivityTypingAmountLimit(userActivityTypingAmountVO);
        }
        //首充发送发奖消息
        if (activityTemplate.equals(ActivityTemplateEnum.FIRST_DEPOSIT.getType())) {
            triggerVO.setDepositType(DepositTypeEnum.FIRST_DEPOSIT.getValue());
            triggerVO.setRechargeTime(userInfoVO.getFirstDepositTime());
            triggerVO.setRechargeAmount(userInfoVO.getFirstDepositAmount());
            log.info("首次充值参与条件满足,发送消息:{}", triggerVO);
            KafkaUtil.send(TopicsConstants.MEMBER_RECHARGE, triggerVO);
        }


        //次充发送发奖消息
        if (activityTemplate.equals(ActivityTemplateEnum.SECOND_DEPOSIT.getType())) {
            triggerVO.setDepositType(DepositTypeEnum.SECOND_DEPOSIT.getValue());
            triggerVO.setRechargeTime(userInfoVO.getSecondDepositTime());
            triggerVO.setRechargeAmount(userInfoVO.getSecondDepositAmount());
            log.info("次充参与条件满足,发送消息:{}", triggerVO);
            KafkaUtil.send(TopicsConstants.MEMBER_RECHARGE, triggerVO);
        }


        //负盈利发奖
        if (activityTemplate.equals(ActivityTemplateEnum.LOSS_IN_SPORTS.getType())) {
            ActivityLossInSportsRespVO respVO = JSONObject.parseObject(activityBaseRespVO, ActivityLossInSportsRespVO.class);
            ActivityBaseInterface<ActivityLossInSportsRespVO> anInterface = activityBaseContext.getInterface(respVO.getActivityTemplate());
            Boolean booleanOrder = anInterface.sendActivityOrder(siteVO, Lists.newArrayList(userId), respVO);
            log.info("负盈利参与条件满足:{}", booleanOrder);
        }


        return buildResponse(ResultCode.APPLY_SUCCESS);
    }

    /**
     * 校验输入字符串是否是一个合法的正整数，并且能匹配到已定义的 VenueTypeEnum 枚举类型。
     * <p>
     * 规则如下：
     * 1. 空字符串（null 或 ""）被视为有效，返回 true。
     * 2. 字符串必须是一个正整数（不包含前导 0），如 "1"、"23" 等。
     * 3. 字符串转换成整数后，必须能匹配到对应的 VenueTypeEnum 枚举实例。
     * 4. 非数字或无法匹配的值返回 false。
     *
     * @param str 输入的字符串
     * @return 如果为空或匹配合法数字且存在对应枚举，则返回 true，否则返回 false。
     */
    public boolean isSingleValidNumber(String str) {
        // 如果为空，视为有效
        if (ObjectUtil.isEmpty(str)) {
            return true;
        }

        // 非正整数直接返回 false
        if (!str.matches("^[1-9]\\d*$")) {
            return false;
        }

        // 转换为整数并尝试匹配枚举
        Integer integer = Integer.valueOf(str);
        VenueTypeEnum venueTypeEnum = VenueTypeEnum.of(integer);
        return venueTypeEnum != null;
    }


    private ToActivityVO buildResponse(ResultCode resultCode) {
        return ToActivityVO.builder()
                .message(resultCode.getMessageCode())
                .status(resultCode.getCode())
                .build();
    }
}
