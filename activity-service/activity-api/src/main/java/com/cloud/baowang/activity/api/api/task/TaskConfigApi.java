package com.cloud.baowang.activity.api.api.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameVO;
import com.cloud.baowang.activity.api.vo.task.*;
import com.cloud.baowang.activity.api.vo.test.PPFreeGameRecordReqVOTest;
import com.cloud.baowang.activity.api.vo.test.UserVenueWinLossSendVOTest;
import com.cloud.baowang.activity.api.vo.test.UserWinLoseMqVOTest;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @className: UserTaskApi
 * @author: wade
 * @description: 中控后台，配置task页面
 * @date: 5/8/24 09:41
 */
@FeignClient(contextId = "remoteUserTaskConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 用户任务的Controller 服务")
public interface TaskConfigApi {
    String PREFIX = ApiConstants.PREFIX + "/taskConfigApi/api";

    @Operation(summary = "任务配置分页查询")
    @PostMapping(value = PREFIX + "/taskList")
    ResponseVO<List<SiteTaskConfigResVO>> taskList(@RequestBody TaskConfigReqVO taskConfigReqVO);

    @Operation(summary = "任务配置是否展示查询")
    @PostMapping(value = PREFIX + "/taskOverViewConfig")
    ResponseVO<SiteTaskOverViewConfigResVO> taskOverViewConfig(@RequestBody TaskConfigOverViewReqVO taskConfigReqVO);

    @Operation(summary = "任务配置是否展示更新")
    @PostMapping(value = PREFIX + "/updateTaskOverViewConfig")
    ResponseVO<Boolean> updateTaskOverViewConfig(@RequestBody TaskConfigOverViewReqVO taskConfigReqVO);


    @Operation(summary = "任务配置查询")
    @PostMapping(value = PREFIX + "/taskDetail")
    ResponseVO<SiteTaskConfigResVO> taskDetail(@RequestBody TaskConfigDetailReqVO reqVO);


    @Operation(summary = "查询全部任务")
    @PostMapping(value = PREFIX + "/taskAllList")
    List<ReportSiteTaskConfigResVO> taskAllList(@RequestBody TaskConfigReqVO taskConfigReqVO);

    @Operation(summary = "根据名称查询id")
    @PostMapping(value = PREFIX + "/taskAllListByTaskName")
    List<String> taskAllListByTaskName(@RequestBody ReportTaskConfigReqVO vo);


    @Operation(summary = "任务配置保存")
    @PostMapping(value = PREFIX + "/save")
    ResponseVO<Void> save(@RequestBody SiteTaskConfigReqVO vo);

    @Operation(summary = "任务配置保存")
    @PostMapping(value = PREFIX + "/saveTaskFlashCard")
    ResponseVO<Void> saveTaskFlashCard(@RequestBody SiteTaskFlashCardSaveVO vo);

    @Operation(summary = "启用与禁用")
    @PostMapping(PREFIX + "/operateStatus")
    ResponseVO<Boolean> operateStatus(@RequestBody SiteTaskOnOffVO reqVO);

    @Operation(summary = "查询排序结果")
    @PostMapping(PREFIX + "/getActiveTabSort")
    ResponseVO<List<SiteTaskConfigSortRespVO>> getTaskTabSort(@RequestParam("siteCode") String siteCode,
                                                              @RequestParam("taskType") String taskType);

    @Operation(summary = "排序")
    @PostMapping(PREFIX + "/activeTabSort")
    ResponseVO<Boolean> taskTabSort(@RequestBody SiteTasKConfigSortReqVO reqVO);

    @Operation(summary = "领取记录")
    @PostMapping(PREFIX + "/recordPageList")
    ResponseVO<Page<SiteTaskOrderRecordResVO>> recordPageList(@RequestBody SiteTaskOrderRecordReqVO reqVO);

    @Operation(summary = "领取记录-总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    Long getTotalCount(@RequestBody SiteTaskOrderRecordReqVO reqVO);

    @Operation(summary = "任务详情")
    @PostMapping(PREFIX + "/detail")
    ResponseVO<APPTaskResponseVO> detail(@RequestBody APPTaskReqVO reqVO);


    @Operation(summary = "任务列表-客户端")
    @PostMapping(PREFIX + "/config")
    ResponseVO<APPTaskConfigResponseVO> config(@RequestBody APPTaskReqVO reqVO);


    @Operation(summary = "任务领取")
    @PostMapping(PREFIX + "/receive")
    ResponseVO<TaskReceiveAppResVO> receive(@RequestBody TaskReceiveAppReqVO requestVO);

    @Operation(summary = "任务领取,支持批量领取")
    @PostMapping(PREFIX + "/receiveTask")
    ResponseVO<Boolean> receiveTask(@RequestBody TaskReceiveBatchAppReqVO requestVO);

    @Operation(summary = "定时任务处理任务过期")
    @PostMapping(PREFIX + "/taskAwardExpire")
    ResponseVO<Void> taskAwardExpire();


    @Operation(summary = "定时任务发放任务大师勋章")
    @PostMapping(PREFIX + "/processSendMealJob")
    void processSendMealJob(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "下一期任务生效")
    @PostMapping(value = PREFIX + "/updateEffective")
    ResponseVO<Void> updateEffective();


    @Operation(summary = "触发任务")
    @PostMapping(PREFIX + "/test")
    ResponseVO<Void> test(@RequestParam("userId") String userId);


    @Operation(summary = "触发任务")
    @PostMapping(PREFIX + "/testKafka")
    ResponseVO<Void> testKafka(@RequestBody UserWinLoseMqVOTest vo);

    @Operation(summary = "触发任务")
    @PostMapping(PREFIX + "/testKafkaPP")
    ResponseVO<Void> testKafkaPP(@RequestBody PPFreeGameRecordReqVOTest vo);

    @Operation(summary = "触发任务")
    @PostMapping(PREFIX + "/testKafkaPPActivity")
    ResponseVO<Void> testKafkaPPActivity(@RequestBody ActivityFreeGameVO vo);

    @Operation(summary = "触发任务")
    @PostMapping(PREFIX + "/taskKafka")
    ResponseVO<Void> taskKafka(@RequestBody UserVenueWinLossSendVOTest vo);

    @Operation(summary = "查询任务卡图")
    @PostMapping(PREFIX + "/queryFlashCard")
    ResponseVO<SiteTaskFlashCardBaseRespVO> queryFlashCard(@RequestBody SiteTaskFlashCardBaseReqVO vo);

    @Operation(summary = "更新任务卡图状态")
    @PostMapping(PREFIX + "/updateTaskFlashCardStatus")
    ResponseVO<Void> updateTaskFlashCardStatus(@RequestBody SiteTaskFlashCardStatusVO vo);

}
