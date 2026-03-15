package com.cloud.baowang.activity.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.free.FreeGameRecordReqVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "activityBaseApi", value = ApiConstants.NAME)
@Tag(name = "活动配置-接口")
public interface ActivityBaseApi {
    String PREFIX = ApiConstants.PREFIX + "/activityBase/api/";

    @Operation(summary = "添加活动")
    @PostMapping(PREFIX + "save")
    ResponseVO<Boolean> save(@RequestBody ActivityConfigVO activityConfigVO);

    @Operation(summary = "每日竞赛-新增机器人")
    @PostMapping(PREFIX + "saveDailyRobot")
    ResponseVO<Boolean> saveDailyRobot(@RequestBody ActivityDailyRobotAddVO robotAddVO);


    @Operation(summary = "每日竞赛-修改机器人")
    @PostMapping(PREFIX + "upDailyRobot")
    ResponseVO<Boolean> upDailyRobot(@RequestBody ActivityDailyRobotUpVO robotUpVO);

    @Operation(summary = "每日竞赛-删除机器人")
    @PostMapping(PREFIX + "deleteDailyRobot")
    ResponseVO<Boolean> deleteDailyRobot(@RequestBody ActivityDelDailyRobotAddVO robotAddVO);

    @Operation(summary = "每日竞赛-活动配置")
    @PostMapping(PREFIX + "queryDailyRobot")
    ResponseVO<List<ActivityDailyRobotRespVO>> queryDailyRobot(@RequestBody ActivityDailyRobotListReqVO reqVO);

    @Operation(summary = "每日竞赛-竞赛详情")
    @PostMapping(PREFIX + "queryDailyDetailList")
    ResponseVO<List<ActivityDailyCompetitionDetailNameRespVO>> queryDailyDetailList(@RequestBody ActivityDailyRobotListReqVO reqVO);

    @Operation(summary = "保存活动-第一步基础信息校验")
    @PostMapping(PREFIX + "checkFirst")
    ResponseVO<Boolean> checkFirst(@RequestBody ActivityConfigVO activityConfigVO);

    @Operation(summary = "保存活动-第二步骤 活动规则校验")
    @PostMapping(PREFIX + "checkSecond")
    ResponseVO<Boolean> checkSecond(@RequestBody ActivityConfigVO activityConfigVO);


    @Operation(summary = "编辑活动")
    @PostMapping(PREFIX + "update")
    ResponseVO<Boolean> update(@RequestBody ActivityConfigVO activityConfigVO);

    @Operation(summary = "活动详情")
    @PostMapping(PREFIX + "info")
    ResponseVO<ActivityConfigRespVO> info(@RequestBody ActivityIdReqVO activityIdReqVO);

    @Operation(summary = "活动列表详情")
    @PostMapping(PREFIX + "siteActivityPageList")
    ResponseVO<Page<ActivityBaseRespVO>> siteActivityPageList(@RequestBody ActivityBaseReqVO vo);

    @Operation(summary = "活动浮标排序列表")
    @PostMapping(PREFIX + "floatIconSortList")
    ResponseVO<ActivityBaseV2FloatIconRespVO> floatIconSortListToSite(@RequestBody ActivityBaseReqVO vo);

    @Operation(summary = "活动浮标排序列表保存")
    @PostMapping(PREFIX + "floatIconSortListSave/{floatIconShowNumber}")
    ResponseVO<Boolean> floatIconSortListSave(@RequestBody List<ActivityBaseV2VO> requestVOList, @PathVariable("floatIconShowNumber") Integer floatIconShowNumber);

    @Operation(summary = "查询排序结果")
    @PostMapping(PREFIX + "getActiveTabSort")
    ResponseVO<List<ActivityBaseSortRespVO>> getActiveTabSort(@RequestParam("siteCode") String siteCode,
                                                              @RequestParam("labelId") String labelId);

    @Operation(summary = "排序")
    @PostMapping(PREFIX + "activeTabSort")
    ResponseVO<Boolean> activeTabSort(@RequestBody ActiveSortReqVO reqVO);

    @Operation(summary = "删除")
    @PostMapping(PREFIX + "delete")
    ResponseVO<?> delete(@RequestBody ActiveBaseOnOffVO vo);

    @Operation(summary = "启用与禁用")
    @PostMapping(PREFIX + "operateStatus")
    ResponseVO<?> operateStatus(@RequestBody ActiveBaseOnOffVO reqVO);

    @Operation(summary = "xxl-job-活动优惠过期-修改活动为过期")
    @PostMapping(PREFIX + "awardExpire")
    ResponseVO<Boolean> awardExpire();

    @Operation(summary = "xxl-job-会员活动优惠满足条件状态变更")
    @PostMapping(PREFIX + "awardActive")
    ResponseVO<Boolean> awardActive(@RequestParam("siteCode") String siteCode, @RequestParam("template") String template, @RequestParam("param") String param);

    @Operation(summary = "查询正在进行的活动")
    @PostMapping(PREFIX + "queryActivityListSwitch")
    ResponseVO<LobbyLabelActivitySwitchResVO> queryActivityListSwitch(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据ID查询站点的活动基础信息")
    @PostMapping(PREFIX + "queryActivityList")
    ResponseVO<List<ActivityBaseRespVO>> queryActivityList(@RequestBody ActivityBaseVO activityBaseVO);

    @GetMapping(PREFIX + "queryActivityByActivityNo")
    @Operation(summary = "根据活动id-短id 查询站点的活动信息")
    ResponseVO<ActivityBaseRespVO> queryActivityByActivityNoAndTemplate(@RequestParam("activityNo") String activityNo,
                                                                        @RequestParam("activityTemplate") String template,
                                                                        @RequestParam("siteCode") String siteCode);


}
