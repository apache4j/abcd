package com.cloud.baowang.activity.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author wade
 * @Date 2024-09-09
 * 转盘活动
 */
@FeignClient(contextId = "ActivitySpinWheelApi", value = ApiConstants.NAME)
@Tag(name = "活动配置-接口")
public interface ActivitySpinWheelApi {


    String PREFIX = ApiConstants.PREFIX + "/" + ApiConstants.PATH + "/activitySpinWheel/api/";

    @Operation(summary = "转盘抽奖次数获取记录")
    @PostMapping(PREFIX + "spinWheelPageList")
    ResponseVO<Page<SiteActivityLotteryRecordRespVO>> spinWheelPageList(@RequestBody SiteActivityLotteryRecordReqVO reqVO);

    @Operation(summary = "会员注册信息-总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    Long getTotalCount(@RequestBody SiteActivityLotteryRecordReqVO vo);


    @Operation(summary = "转盘详情")
    @PostMapping(PREFIX + "detail")
    ResponseVO<ActivitySpinWheelAppRespVO> detail(@RequestBody ActivitySpinWheelAppReqVO requestVO);


    @Operation(summary = "转盘抽奖结果")
    @PostMapping(PREFIX + "prizeResult")
    ResponseVO<SiteActivityRewardSpinAPPResponseVO> prizeResult(@RequestBody ActivitySpinWheelAppReqVO requestVO);


    @Operation(summary = "参与活动")
    @PostMapping(PREFIX + "toActivitySpinWheel")
    ResponseVO<ToActivityVO> toActivitySpinWheel(@RequestBody ActivitySpinWheelAppReqVO reqVO);


    @Operation(summary = "vip晋级奖励转盘次数")
    @PostMapping(PREFIX + "handleVipReward")
    ResponseVO<Void> handleVipReward(@RequestBody VipUpRewardResVO reqVO);


//    @Operation(summary = "test")
//    @PostMapping(PREFIX + "test")
//    ResponseVO<Void> test(@RequestBody RechargeTriggerVO requestVO);
}
