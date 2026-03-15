package com.cloud.baowang.site.controller.report;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.daily.DailyWinLoseExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.ReportDailyWinLoseApi;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResult;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLosePageVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 **/
@RestController
@Tag(name = "报表-盈亏报表")
@RequestMapping("/win-lose-report/api")
@AllArgsConstructor
public class DailyWinLoseController {

    private final ReportDailyWinLoseApi reportDailyWinLoseApi;

    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "每日盈亏")
    @PostMapping(value = "/dailyWinLosePage")
    public ResponseVO<DailyWinLoseResult> dailyWinLosePage(@Valid @RequestBody DailyWinLosePageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return reportDailyWinLoseApi.dailyWinLosePage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping("export")
    public ResponseVO<?> export(@Valid @RequestBody DailyWinLosePageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::getDailyWinLose::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> resp = reportDailyWinLoseApi.dailyWinLosePageCount(vo);
        if (!resp.isOk() || resp.getData() <= 0) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        vo.setPageSize(10000);
        Long count = resp.getData();
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                DailyWinLoseExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), count),
                param -> ConvertUtil.entityListToModelList(dailyWinLosePage(param).getData().getPageList().getRecords(),
                        DailyWinLoseExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.DAILY_WIN_LOSE)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

    @Operation(summary = "每日盈亏总条数")
    @PostMapping(value = "/dailyWinLosePageCount")
    public ResponseVO<Long> dailyWinLosePageCount(@Valid @RequestBody DailyWinLosePageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return reportDailyWinLoseApi.dailyWinLosePageCount(vo);
    }
}
