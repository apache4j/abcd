package com.cloud.baowang.site.controller.activity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.vo.task.*;
import com.cloud.baowang.activity.api.vo.test.UserVenueWinLossSendVOTest;
import com.cloud.baowang.activity.api.vo.test.UserWinLoseMqVOTest;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.vo.export.SiteTaskOrderRecordExcelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Tag(name = "任务配置")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/task/api")
public class TaskConfigController {

    private final TaskConfigApi taskConfigApi;

    private final MinioUploadApi minioUploadApi;

    @PostMapping("/taskList")
    @Operation(summary = "任务配置分页查询")
    public ResponseVO<List<SiteTaskConfigResVO>> taskList(@Valid @RequestBody TaskConfigReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        ResponseVO<List<SiteTaskConfigResVO>> result = taskConfigApi.taskList(vo);
        return result;
    }

    @PostMapping("/taskDetail")
    @Operation(summary = "任务配置分页查询")
    public ResponseVO<SiteTaskConfigResVO> taskDetail(@Valid @RequestBody TaskConfigDetailReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return taskConfigApi.taskDetail(vo);
    }


    @PostMapping("/save")
    @Operation(summary = "任务配置保存")
    public ResponseVO<Void> save(@Valid @RequestBody SiteTaskConfigReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return taskConfigApi.save(vo);
    }

    @PostMapping("/saveTaskFlashCard")
    @Operation(summary = "卡图配置保存")
    public ResponseVO<Void> saveTaskFlashCard(@Valid @RequestBody SiteTaskFlashCardSaveVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return taskConfigApi.saveTaskFlashCard(vo);
    }
    @PostMapping("/updateTaskFlashCardStatus")
    @Operation(summary = "卡图配置-开启禁用")
    public ResponseVO<Void> updateTaskFlashCardStatus(@Valid @RequestBody SiteTaskFlashCardStatusVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return taskConfigApi.updateTaskFlashCardStatus(vo);
    }

    @PostMapping("/queryFlashCard")
    @Operation(summary = "卡图查询")
    public ResponseVO<SiteTaskFlashCardBaseRespVO> queryFlashCard(@Valid @RequestBody SiteTaskFlashCardBaseReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return taskConfigApi.queryFlashCard(vo);
    }



    @PostMapping("/operateStatus")
    @Operation(summary = "启用与禁用")
    public ResponseVO<Boolean> operateStatus(@Valid @RequestBody SiteTaskOnOffVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return taskConfigApi.operateStatus(vo);
    }

    @GetMapping("/getTaskTabSort")
    @Operation(summary = "查询排序数据")
    public ResponseVO<List<SiteTaskConfigSortRespVO>> getTaskTabSort(@RequestParam("taskType") String taskType) {
        return taskConfigApi.getTaskTabSort(CurrReqUtils.getSiteCode(), taskType);
    }

    @PostMapping("/taskTabSort")
    @Operation(summary = "排序")
    public ResponseVO<Boolean> taskTabSort(@Valid @RequestBody SiteTasKConfigSortReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return taskConfigApi.taskTabSort(vo);
    }

    @PostMapping("/recordPageList")
    @Operation(summary = "任务领取记录")
    public ResponseVO<Page<SiteTaskOrderRecordResVO>> recordPageList(@Valid @RequestBody SiteTaskOrderRecordReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return taskConfigApi.recordPageList(vo);
    }


    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@Valid @RequestBody SiteTaskOrderRecordReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "taskExport::centerControl::userInfo::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        Long responseVO = taskConfigApi.getTotalCount(vo);

        vo.setPageSize(responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteTaskOrderRecordExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(recordPageList(param).getData().getRecords(), SiteTaskOrderRecordExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_TASK_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

    /*@PostMapping("/test")
    @Operation(summary = "test")
    public ResponseVO<Void> test(@RequestParam("userId") String userId) {
        return taskConfigApi.test(userId);
    }

    @PostMapping("/testKafka")
    @Operation(summary = "testKafka")
    public ResponseVO<Void> testKafka(@RequestBody UserWinLoseMqVOTest vo) {
        return taskConfigApi.testKafka(vo);
    }

    @PostMapping("/taskKafka")
    @Operation(summary = "taskKafka")
    public ResponseVO<Void> taskKafka(@RequestBody UserVenueWinLossSendVOTest vo) {
        return taskConfigApi.taskKafka(vo);
    }*/



    @PostMapping("/taskOverViewConfig")
    @Operation(summary = "任务配置展开与折叠配置")
    public ResponseVO<SiteTaskOverViewConfigResVO> taskOverViewConfig() {
        TaskConfigOverViewReqVO vo = new TaskConfigOverViewReqVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return taskConfigApi.taskOverViewConfig(vo);
    }

    @PostMapping("/updateTaskOverViewConfig")
    @Operation(summary = "更新任务配置是否展开与折叠")
    public ResponseVO<Boolean> updateTaskOverViewConfig(@Valid @RequestBody TaskConfigOverViewReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return taskConfigApi.updateTaskOverViewConfig(vo);
    }

}
