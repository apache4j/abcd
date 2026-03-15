package com.cloud.baowang.activity.api;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivitySpinWheelApi;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.service.ActivitySpinWheelService;
import com.cloud.baowang.activity.service.SiteActivityLotteryRecordService;
import com.cloud.baowang.activity.service.base.ActivityActionContext;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * {@code @Desciption:} 转盘活动活动相关
 * @Author: Ford
 * @Date: 2024/9/9 21:06
 * @Version: V1.0
 **/
@Slf4j
@RestController
@AllArgsConstructor
public class ActivitySpinWheelApiImpl implements ActivitySpinWheelApi {


    private final SiteActivityLotteryRecordService lotteryRecordService;


    private final ActivitySpinWheelService activitySpinWheelService;


    private final ActivityActionContext activityActionContext;


    @Override
    public ResponseVO<Page<SiteActivityLotteryRecordRespVO>> spinWheelPageList(SiteActivityLotteryRecordReqVO reqVO) {
        return ResponseVO.success(lotteryRecordService.activityPageList(reqVO));
    }

    @Override
    public Long getTotalCount(SiteActivityLotteryRecordReqVO vo) {
        return lotteryRecordService.getTotalCount(vo);
    }

    @Override
    public ResponseVO<ActivitySpinWheelAppRespVO> detail(ActivitySpinWheelAppReqVO requestVO) {

        return activitySpinWheelService.detail(requestVO);
    }

    @Override
    public ResponseVO<SiteActivityRewardSpinAPPResponseVO> prizeResult(ActivitySpinWheelAppReqVO requestVO) {
        SiteActivityBasePO siteActivityBasePO = activitySpinWheelService.getSiteActivityBasePORecord(requestVO.getSiteCode());
        if (siteActivityBasePO == null) {
            log.warn("未能领取转盘活动奖励，转盘活动已经结束。 用户:{}", requestVO.getUserId());
            throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NULL_END);
        }

        UserBaseReqVO userBaseReqVO = new UserBaseReqVO();
        userBaseReqVO.setActivityId(siteActivityBasePO.getId());
        userBaseReqVO.setUserId(requestVO.getUserId());
        userBaseReqVO.setSiteCode(requestVO.getSiteCode());
        userBaseReqVO.setDeviceType(requestVO.getDeviceType());
        userBaseReqVO.setTimezone(requestVO.getTimezone());
        ToActivityVO toActivityVO = activityActionContext.toActivity(userBaseReqVO);
        //转盘检验
        if (!toActivityVO.getStatus().equals(ResultCode.SUCCESS.getCode()) && !toActivityVO.getStatus().equals(ResultCode.APPLY_SUCCESS.getCode())) {
            throw new BaowangDefaultException(Objects.requireNonNull(ResultCode.of(toActivityVO.getStatus())));
        }
        return activitySpinWheelService.prizeResult(requestVO);
    }

    @Override
    public ResponseVO<ToActivityVO> toActivitySpinWheel(ActivitySpinWheelAppReqVO requestVO) {
        SiteActivityBasePO siteActivityBasePO = activitySpinWheelService.getSiteActivityBasePORecord(requestVO.getSiteCode());
        if (siteActivityBasePO == null) {
            log.warn("未能领取转盘活动奖励， 用户:{}", requestVO.getUserId());
            throw new BaowangDefaultException(ResultCode.ACTIVITY_IS_NULL_END);
        }
        UserBaseReqVO userBaseReqVO = new UserBaseReqVO();
        userBaseReqVO.setActivityId(siteActivityBasePO.getId());
        userBaseReqVO.setUserId(requestVO.getUserId());
        userBaseReqVO.setSiteCode(requestVO.getSiteCode());
        userBaseReqVO.setDeviceType(requestVO.getDeviceType());
        userBaseReqVO.setTimezone(requestVO.getTimezone());
        ToActivityVO toActivityVO = activityActionContext.toActivity(userBaseReqVO);
        if (toActivityVO.getStatus() == ResultCode.APPLY_SUCCESS.getCode() || toActivityVO.getStatus() == ResultCode.SUCCESS.getCode()) {
            toActivityVO.setStatus(ResultCode.SUCCESS.getCode());
        }
        return ResponseVO.success(toActivityVO);
    }

    @Override
    public ResponseVO<Void> handleVipReward(VipUpRewardResVO reqVO) {
        log.info("vip晋级奖励转盘次数:{}", JSONObject.toJSONString(reqVO));
        activitySpinWheelService.handleVipReward(reqVO);
        return ResponseVO.success();
    }

//    @Override
//    public ResponseVO<Void> test(RechargeTriggerVO triggerVO) {
//        //
//        activitySpinWheelService.handleJob(triggerVO.getSiteCode());
//        //RechargeTriggerVO triggerVO = new RechargeTriggerVO();
//       /* triggerVO.setUserId("497818296");
//        triggerVO.setSiteCode("10002");
//        triggerVO.setOrderNumber("order-202222211-14241");
//        triggerVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
//        triggerVO.setPlatformCurrency(new BigDecimal(5000));*/
//        activitySpinWheelService.handleDepositReward(triggerVO);
//        return null;
//    }
}
