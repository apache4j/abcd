package com.cloud.baowang.site.controller.report;

import cn.hutool.core.collection.CollectionUtil;
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
import com.cloud.baowang.report.api.api.ReportTaskOrderReportApi;
import com.cloud.baowang.report.api.vo.excel.ReportTaskResponseVOExportVO;
import com.cloud.baowang.report.api.vo.task.ReportTaskOrderRecordResult;
import com.cloud.baowang.report.api.vo.task.ReportTaskReportPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Desciption:
 * @Author: wade
 * @Date: 2024/7/29 16:36
 * @Version: V1.0
 **/
@RestController
@Tag(name = "任务报表")
@RequestMapping("/report-task-order/api")
@AllArgsConstructor
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReportTaskOrderRecordController {

    private final ReportTaskOrderReportApi taskOrderReportApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "分页列表")
    @PostMapping(value = "/getReportTaskOrderPage")
    public ResponseVO<ReportTaskOrderRecordResult> getReportTaskOrderPage(@Valid @RequestBody ReportTaskReportPageVO vo) {
        //移除10秒校验
        String uniqueKey = "getReportTaskOrderPage::pageList::"  + CurrReqUtils.getOneId();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException("查询的太频繁,请"+remain + "秒后再操作");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimezone(CurrReqUtils.getTimezone());
        String dbZone = CurrReqUtils.getTimezone().replace("UTC", "").concat(":00");
        vo.setTimezoneDb(dbZone);
        return taskOrderReportApi.listPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@Valid @RequestBody ReportTaskReportPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimezone(CurrReqUtils.getTimezone());
        String dbZone = CurrReqUtils.getTimezone().replace("UTC", "").concat(":00");
        vo.setTimezoneDb(dbZone);
        String adminId = CurrReqUtils.getOneId();
        // 1.
        String uniqueKey = "tableExport::centerControl::getReportTaskOrderPage::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        Long responseVO = taskOrderReportApi.getTotalCount(vo);//2.
        responseVO = responseVO == null ? 0L : responseVO.intValue();
        vo.setPageSize(responseVO == null ? 0 : responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组 3.
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportTaskResponseVOExportVO.class,//3.
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(getReportTaskOrderPage(param).getData().getPageList().getRecords(),
                        ReportTaskResponseVOExportVO.class));//3.

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.REPORT_TASK_ORDER_RECORD)//4.
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }


}
