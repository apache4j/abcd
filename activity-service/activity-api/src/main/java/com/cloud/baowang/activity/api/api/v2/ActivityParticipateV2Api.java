package com.cloud.baowang.activity.api.api.v2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.v2.ActivityBasePartV2RespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2AppRespVO;
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

@FeignClient(contextId = "activityParticipateV2Api", value = ApiConstants.NAME)
@Tag(name = "参加活动-接口")
public interface ActivityParticipateV2Api {
    String PREFIX = ApiConstants.PREFIX + "/participateV2/api/";

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
    ResponseVO<Page<ActivityBasePartV2RespVO>> activityPagePartList(@RequestBody ActivityBasePartReqVO vo);

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

    @Operation(summary = "活动参数校验")
    @PostMapping(PREFIX + "queryActivityCheck")
    ResponseVO<ToActivityVO> queryActivityCheck(@RequestBody ActivityTemplateCheckReqVO activityDailyContestReqVO);

    @Operation(summary = "获取会员领取活动总金额（主货币）")
    @PostMapping(PREFIX + "getActivityTotalAmountByUserId")
    BigDecimal getActivityTotalAmountByUserId(@RequestParam("userId") String userId);

    @Operation(summary = "推荐活动（只能推荐一个）")
    @PostMapping(PREFIX + "recommended")
    ResponseVO<ActivityBaseV2AppRespVO> recommended();



}
