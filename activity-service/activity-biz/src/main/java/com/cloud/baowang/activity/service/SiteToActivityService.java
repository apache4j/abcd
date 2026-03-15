package com.cloud.baowang.activity.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.SiteActivityEventRecordPO;
import com.cloud.baowang.activity.service.base.ActivityBaseContext;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.user.api.api.UserInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@AllArgsConstructor
@Slf4j
@Service
public class SiteToActivityService {

    private final ActivityBaseContext activityContext;

    private final UserInfoApi userInfoApi;

    private final SiteActivityEventRecordService siteActivityEventRecordService;



    private ToActivityVO buildResponse(ResultCode resultCode) {
        return ToActivityVO.builder()
                .message(resultCode.getMessageCode())
                .status(resultCode.getCode())
                .build();
    }

    //校验手机号与邮箱
    public ToActivityVO checkUserBindingStatus(ActivityBaseRespVO baseRespVO, UserInfoVO userInfoVO) {
        if (EnableStatusEnum.ENABLE.getCode().equals(baseRespVO.getSwitchPhone()) && ObjectUtil.isEmpty(userInfoVO.getPhone())) {
            log.info("用户: {}, 手机号未绑定", userInfoVO.getUserId());
            return buildResponse(ResultCode.ACTIVITY_PHONE_NOT);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(baseRespVO.getSwitchEmail()) && ObjectUtil.isEmpty(userInfoVO.getEmail())) {
            log.info("用户: {}, 邮箱未绑定", userInfoVO.getUserId());
            return buildResponse(ResultCode.ACTIVITY_EMAIL_NOT);
        }
        return null;
    }


    /**
     * 该方法针对转盘检验
     */
    @DistributedLock(name = RedisConstants.TO_ACTIVITY_SPIN_WHEEL_LOCK, unique = "#id + ':' + #userId", waitTime = 3, leaseTime = 180)
    public ToActivityVO toActivitySpinWheel(String id, String userId) {
        String siteCode = CurrReqUtils.getSiteCode();
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

        ActivityConfigDetailReq activityConfigDetailReq = ActivityConfigDetailReq
                .builder()
                .id(id)
                .showTerminal(String.valueOf(deviceType))
                .accountType(userInfoVO.getAccountType())
                .activityStartTime(System.currentTimeMillis())
                .activityEndTime(System.currentTimeMillis())
                .accountType(userInfoVO.getAccountType())
                .status(EnableStatusEnum.ENABLE.getCode())
                .siteCode(CurrReqUtils.getSiteCode())
                .showTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()))
                .build();

        String activityBaseRespVO = activityContext.getActivityByTemplate(activityConfigDetailReq);

        if (ObjectUtils.isEmpty(activityBaseRespVO)) {
            log.info("转盘参与校验,活动未配置或未开启:{}", id);
            return buildResponse(ResultCode.ACTIVITY_NOT_OPEN);
        }


        ActivitySpinWheelRespVO spinWheelRespVO = JSONObject.parseObject(activityBaseRespVO, ActivitySpinWheelRespVO.class);
        String activityTemplate = spinWheelRespVO.getActivityTemplate();

        //该接口能转盘活动调用
        if (!ActivityTemplateEnum.SPIN_WHEEL.getType().equals(spinWheelRespVO.getActivityTemplate())) {
            return buildResponse(ResultCode.ACTIVITY_NOT);
        }


        LambdaQueryWrapper<SiteActivityEventRecordPO> eventWrappers = Wrappers.lambdaQuery(SiteActivityEventRecordPO.class)
                .eq(SiteActivityEventRecordPO::getSiteCode, siteCode)
                .eq(SiteActivityEventRecordPO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityEventRecordPO::getUserId, userId);
        Integer switchIp = spinWheelRespVO.getSwitchIp();


        //IP状态开启,验证该IP是否被参与
        if (EnableStatusEnum.ENABLE.getCode().equals(switchIp)) {
            eventWrappers.eq(SiteActivityEventRecordPO::getIp, userInfoVO.getLastLoginIp())
                    .ne(SiteActivityEventRecordPO::getUserId, userId);
            Long eventCount = siteActivityEventRecordService.getBaseMapper().selectCount(eventWrappers);
            if (eventCount > 0) {
                log.info("转盘参与校验:{},活动,userId:{},被拒绝,IP:{},已经被参与过活动", activityTemplate, userId, userInfoVO.getLastLoginIp());
                return buildResponse(ResultCode.ACTIVITY_IP_NOT);
            }
        }

        //开启手机判断的并且没有绑定手机
        ToActivityVO userBind = checkUserBindingStatus(spinWheelRespVO, userInfoVO);
        if (userBind != null) {
            return userBind;
        }

        SiteActivityEventRecordPO siteActivityEventRecordPO = SiteActivityEventRecordPO.builder()
                .activityId(spinWheelRespVO.getId())
                .code(String.valueOf(System.currentTimeMillis()))//因为转盘一天可以参加多次，所以针对这个字段用时间戳，每次都在变
                .ip(userInfoVO.getLastLoginIp())
                .siteCode(siteCode)
                .activityTemplate(activityTemplate)
                .status(ActivityEventStatusEnum.UNISSUED.getCode())
                .userAccount(userInfoVO.getUserAccount())
                .vipRank(userInfoVO.getVipRank())
                .day(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()))
                .userId(userId)
                .deviceNo(userInfoVO.getLastDeviceNo())
                .build();


        long count = siteActivityEventRecordService.getBaseMapper().insert(siteActivityEventRecordPO);
        if (count <= 0) {
            log.info("转盘参与校验:{},活动,userId:{},插入失败", activityTemplate, CurrReqUtils.getOneId());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return buildResponse(ResultCode.SUCCESS);
    }


}
