package com.cloud.baowang.site.controller.activity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivitySpinWheelApi;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.excel.SiteActivityLotteryRecordRespExcelVO;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


@Tag(name = "转盘活动")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/spinWheel/api")
public class ActivitySpinWheelController {

    private final ActivitySpinWheelApi activitySpinWheelApi;

    private final MinioUploadApi minioUploadApi;

    @PostMapping("/spinWheelPageList")
    @Operation(summary = "转盘抽奖次数获取记录")
    public ResponseVO<Page<SiteActivityLotteryRecordRespVO>> spinWheelPageList(@Valid @RequestBody SiteActivityLotteryRecordReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return activitySpinWheelApi.spinWheelPageList(vo);
    }

//    @PostMapping("/test")
//    @Operation(summary = "转盘抽奖次数获取记录")
//    public ResponseVO<Void> test(@Valid @RequestBody RechargeTriggerVO vo) {
//        vo.setSiteCode(CurrentRequestUtils.getSiteCode());
//        return activitySpinWheelApi.test(vo);
//    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody SiteActivityLotteryRecordReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::siteControl::spinWheelPageList::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            //return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        Long responseVO = activitySpinWheelApi.getTotalCount(vo);
        responseVO = responseVO == null ? 0L : responseVO;

        vo.setPageSize(responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteActivityLotteryRecordRespExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(spinWheelPageList(param).getData().getRecords(), SiteActivityLotteryRecordRespExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SPIN_WHEEL_LIST)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }


}
