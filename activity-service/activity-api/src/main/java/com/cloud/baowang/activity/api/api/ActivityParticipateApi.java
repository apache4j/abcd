package com.cloud.baowang.activity.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(contextId = "activityParticipateApi", value = ApiConstants.NAME)
@Tag(name = "参加活动-接口")
public interface ActivityParticipateApi {
    String PREFIX = ApiConstants.PREFIX + "/participate/api/";

    @Operation(summary = "获取活动详情")
    @PostMapping(PREFIX + "getConfigDetail")
    ResponseVO<ActivityConfigDetailVO> getConfigDetail(@RequestBody ActivityConfigDetailReq activityConfigDetailReq);

    @Operation(summary = "去参与活动")
    @PostMapping(PREFIX + "toActivity")
    ResponseVO<ToActivityVO> toActivity(@RequestBody UserBaseReqVO reqVO);

    @Operation(summary = "活动逻辑校验")
    @PostMapping(PREFIX + "checkToActivity")
    ResponseVO<ToActivityVO> checkToActivity(@RequestBody UserBaseReqVO reqVO);

    @Operation(summary = "领取奖励-单个领取")
    @PostMapping(PREFIX + "getActivityReward")
    ResponseVO<ActivityRewardVO> getActivityReward(@RequestParam("id") String id);

    @Operation(summary = "领取奖励-批量领取")
    @PostMapping(PREFIX + "getBatchActivityReward")
    ResponseVO<ActivityRewardVO> getBatchActivityReward(@RequestParam("userId") String userId);

    @Operation(summary = "活动列表详情-客户端")
    @PostMapping(PREFIX + "activityPagePartList")
    ResponseVO<Page<ActivityBasePartRespVO>> activityPagePartList(@RequestBody ActivityBasePartReqVO vo);

    @Operation(summary = "活动浮标排序列表")
    @PostMapping(PREFIX + "floatIconSortList")
    ResponseVO<List<ActivityBaseV2FloatIconVO>> floatIconSortListToApp(@RequestBody ActivityBasePartReqVO vo);

    @Operation(summary = "获取站点下的活动总金额")
    @PostMapping(PREFIX + "getActivityTotalAmount")
    ResponseVO<BigDecimal> getActivityTotalAmount(@RequestParam("activityTemplate") String activityTemplate, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "福利中心-获取活动礼金订单")
    @PostMapping(PREFIX + "queryActivityOrderRecord")
    ResponseVO<ActivityOrderRecordPartRespVO> queryActivityOrderRecord(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO);

    @Operation(summary = "福利中心-获取活动礼金订单-总条数")
    @PostMapping(PREFIX + "queryActivityOrderRecordCount")
    ResponseVO<Long> queryActivityOrderRecordCount(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO);

    @Operation(summary = "福利中心-获取活动礼金订单")
    @PostMapping(PREFIX + "querySpinWheelOrderRecord")
    ResponseVO<Page<ActivityOrderRecordForSpinWheelRespVO>> querySpinWheelOrderRecord(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO);


    @Operation(summary = "活动礼金记录")
    @PostMapping(PREFIX + "queryPageActivityOrderRecord")
    ResponseVO<Page<ActivityOrderRecordRespVO>> queryPageActivityOrderRecord(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO);

    @Operation(summary = "活动礼金记录")
    @PostMapping(PREFIX + "getActivityOrderRecordCount")
    ResponseVO<Long> getActivityOrderRecordCount(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO);


    @Operation(summary = "活动礼金记录-总条数")
    @PostMapping(PREFIX + "queryPageActivityOrderRecordCount")
    ResponseVO<Long> queryPageActivityOrderRecordCount(@RequestBody ActivityOrderRecordReqVO activityPartOrderRecordReqVO);


    @Operation(summary = "每日竞赛")
    @PostMapping(PREFIX + "queryActivityDailyContest")
    ResponseVO<ActivityPartDailyCompletionRespVO> queryActivityDailyContest(@RequestParam("id") String id);


    @Operation(summary = "每日竞赛-场馆列表")
    @PostMapping(PREFIX + "queryActivityDailyContestVenueCode")
    ResponseVO<ActivityPartDailyCompletionVenueRespVO> queryActivityDailyContestVenueCode();

    @Operation(summary = "每日竞赛-奖池")
    @PostMapping(PREFIX + "queryActivityDailyPrizePool")
    ResponseVO<BigDecimal> queryActivityDailyPrizePool(@RequestParam("id") String id);

    @Operation(summary = "每日竞赛-排行榜记录")
    @PostMapping(PREFIX + "queryActivityDailyRecord")
    ResponseVO<ActivityPartDailyRecordRespVO> queryActivityDailyRecord(@RequestBody ActivityDailyContestReqVO activityDailyContestReqVO);

    @Operation(summary = "活动参数校验")
    @PostMapping(PREFIX + "queryActivityCheck")
    ResponseVO<ToActivityVO> queryActivityCheck(@RequestBody ActivityTemplateCheckReqVO activityDailyContestReqVO);


    @Operation(summary = "每日竞赛-勋章发放")
    @PostMapping(PREFIX + "activityDailMedalAwardActive")
    ResponseVO<Void> activityDailMedalAwardActive(@RequestParam("venueType") Integer venueType);


    @Operation(summary = "场馆-勋章发放")
    @PostMapping(PREFIX + "activityVenueCodeMedalAwardActive")
    ResponseVO<Void> activityVenueCodeMedalAwardActive(@RequestParam("venueType") Integer venueType);


    @Operation(summary = "每日竞赛计算出前100名用户")
    @PostMapping(PREFIX + "toSetActivityDailyTop100")
    ResponseVO<Void> toSetActivityDailyTop100();

    @Operation(summary = "获取会员领取活动总金额（主货币）")
    @PostMapping(PREFIX + "getActivityTotalAmountByUserId")
    BigDecimal getActivityTotalAmountByUserId(@RequestParam("userId") String userId);


    @Operation(summary = "签到活动是否开启")
    @PostMapping(PREFIX + "checkInActivityInfo")
    ResponseVO<CheckInBasePartRespVO> checkInActivityInfo(@RequestBody UserBaseReqVO reqVO);

    @Operation(summary = "签到活动历史记录")
    @PostMapping(PREFIX + "checkInRecord")
    ResponseVO<CheckInRecordRespVO> checkInRecord(@RequestBody UserBaseReqVO build);

    @Operation(summary = "签到活动历史记录")
    @PostMapping(PREFIX + "checkIn")
    ResponseVO<CheckInRewardResultRespVO> checkIn(@RequestBody UserBaseReqVO build);

    @Operation(summary = "每日竞赛机器人流失计算")
    @PostMapping(PREFIX + "calculateActivityDailyRobot")
    ResponseVO<Void> calculateActivityDailyRobot();
}
