package com.cloud.baowang.activity.api.api.v2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "activityV2BaseApi", value = ApiConstants.NAME)
@Tag(name = "活动配置-接口V2")
public interface ActivityBaseV2Api {

    String PREFIX = ApiConstants.PREFIX + "/activityV2Base/api/";

    @Operation(summary = "添加活动")
    @PostMapping(PREFIX + "save")
    ResponseVO<Boolean> save(@RequestBody ActivityConfigV2VO activityConfigVO);


    @Operation(summary = "保存活动-第一步基础信息校验")
    @PostMapping(PREFIX + "checkFirst")
    ResponseVO<Boolean> checkFirst(@RequestBody ActivityConfigV2VO activityConfigVO);

    @Operation(summary = "保存活动-第二步骤 活动规则校验")
    @PostMapping(PREFIX + "checkSecond")
    ResponseVO<Boolean> checkSecond(@RequestBody ActivityConfigV2VO activityConfigVO);


    @Operation(summary = "编辑活动")
    @PostMapping(PREFIX + "update")
    ResponseVO<Boolean> update(@RequestBody ActivityConfigV2VO activityConfigVO);

    @Operation(summary = "活动详情")
    @PostMapping(PREFIX + "info")
    ResponseVO<ActivityConfigV2RespVO> info(@RequestBody ActivityIdReqVO activityIdReqVO);

    @Operation(summary = "活动列表")
    @PostMapping(PREFIX + "siteActivityPageList")
    ResponseVO<Page<ActivityBaseV2RespVO>> siteActivityPageList(@RequestBody ActivityBaseReqVO vo);

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
