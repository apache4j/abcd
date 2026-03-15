package com.cloud.baowang.user.controller.activity;

import com.cloud.baowang.activity.api.api.ActivitySpinWheelApi;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @className: SpinWheelController
 * @author: wade
 * @description: 转盘活动
 * @date: 14/9/24 09:16
 */
@Tag(name = "转盘活动-详情")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/activity/spin/api")
public class SpinWheelController {

    private final ActivitySpinWheelApi activitySpinWheelApi;

    @PostMapping("/detail")
    @Operation(summary = "转盘活动详情")
    public ResponseVO<ActivitySpinWheelAppRespVO> detail(@RequestBody ActivitySpinWheelAppReqVO requestVO) {
        requestVO.setUserId(CurrReqUtils.getOneId());
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setDeviceType(CurrReqUtils.getReqDeviceType());
        requestVO.setTimezone(CurrReqUtils.getTimezone());
        return activitySpinWheelApi.detail(requestVO);
    }

    @PostMapping("/prizeResult")
    @Operation(summary = "转盘抽奖")
    public ResponseVO<SiteActivityRewardSpinAPPResponseVO> prizeResult(@RequestBody ActivitySpinWheelAppReqVO requestVO) {
        requestVO.setUserId(CurrReqUtils.getOneId());
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setUserAccount(CurrReqUtils.getAccount());
        requestVO.setDeviceType(CurrReqUtils.getReqDeviceType());
        requestVO.setTimezone(CurrReqUtils.getTimezone());
        return activitySpinWheelApi.prizeResult(requestVO);
    }
    @Operation(summary = "参与活动")
    @PostMapping("/toActivity")
    public ResponseVO<ToActivityVO> toActivity() {
        ActivitySpinWheelAppReqVO reqVO = new ActivitySpinWheelAppReqVO();
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setUserId(CurrReqUtils.getOneId());
        reqVO.setDeviceType(CurrReqUtils.getReqDeviceType());
        reqVO.setTimezone(CurrReqUtils.getTimezone());
        return activitySpinWheelApi.toActivitySpinWheel(reqVO);
    }


}
