package com.cloud.baowang.user.controller.activity;


import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.vo.ActivityDailyContestReqVO;
import com.cloud.baowang.activity.api.vo.ActivityPartDailyCompletionRespVO;
import com.cloud.baowang.activity.api.vo.ActivityPartDailyCompletionVenueRespVO;
import com.cloud.baowang.activity.api.vo.ActivityPartDailyRecordRespVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name = "每日竞赛-活动")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/activityParticipate/api")
public class ActivityDailyCompetitionController {

    private final ActivityParticipateApi activityParticipateApi;

    @PostMapping("/queryActivityDailyContest")
    @Operation(summary = "每日竞赛-初始信息")
    public ResponseVO<ActivityPartDailyCompletionRespVO> queryActivityDailyContest(@Valid @RequestBody ActivityDailyContestReqVO activityDailyContestReqVO) {
        if(activityDailyContestReqVO.getId() == null || StringUtils.isBlank(activityDailyContestReqVO.getId())){
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
        }
        return activityParticipateApi.queryActivityDailyContest(activityDailyContestReqVO.getId());
    }

    @PostMapping("/queryActivityDailyContestVenueCode")
    @Operation(summary = "每日竞赛-场馆列表")
    public ResponseVO<ActivityPartDailyCompletionVenueRespVO> queryActivityDailyContestVenueCode() {
        return activityParticipateApi.queryActivityDailyContestVenueCode();
    }

    @PostMapping("/queryActivityDailyPrizePool")
    @Operation(summary = "每日竞赛-奖池")
    public ResponseVO<BigDecimal> queryActivityDailyPrizePool(@Valid @RequestBody ActivityDailyContestReqVO activityDailyContestReqVO) {
        return activityParticipateApi.queryActivityDailyPrizePool(activityDailyContestReqVO.getId());
    }

    @PostMapping("/queryActivityDailyRecord")
    @Operation(summary = "每日竞赛-排名记录")
    public ResponseVO<ActivityPartDailyRecordRespVO> queryActivityDailyRecord(@Valid @RequestBody ActivityDailyContestReqVO activityDailyContestReqVO) {
        if(activityDailyContestReqVO.getId() == null || StringUtils.isBlank(activityDailyContestReqVO.getId())){
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
        }
        return activityParticipateApi.queryActivityDailyRecord(activityDailyContestReqVO);
    }


}
