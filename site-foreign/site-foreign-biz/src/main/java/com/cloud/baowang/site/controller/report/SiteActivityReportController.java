package com.cloud.baowang.site.controller.report;

import com.cloud.baowang.activity.api.api.ActivityReportApi;
import com.cloud.baowang.activity.api.vo.report.ActivityDataReportRespVO;
import com.cloud.baowang.activity.api.vo.report.DataReportReqVO;
import com.cloud.baowang.activity.api.vo.report.DataReportRespVO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Desciption: 活动相关报表
 * @Author: Ford
 * @Date: 2024/10/3 11:47
 * @Version: V1.0
 **/
@RestController
@Tag(name = "报表-业务报表-活动报表")
@RequestMapping("/activity-report/api")
@AllArgsConstructor
public class SiteActivityReportController {

    private final ActivityReportApi activityReportApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "数据报表")
    @PostMapping(value = "/getDataReportPage")
    public ResponseVO<DataReportRespVO> getDataReportPage(@Valid @RequestBody DataReportReqVO dataReportReqVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        dataReportReqVO.setTimeZone(timeZoneId);
        dataReportReqVO.setTimeZoneDb(dbZone);
        dataReportReqVO.setSiteCode(siteCode);
        String uniqueKey = "activityReport::centerControl::getDataReportPage::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException(remain + "秒后才能查询");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }
        return  activityReportApi.getDataReportPage(dataReportReqVO);
    }


    @Operation(summary = "数据报表导出")
    @PostMapping(value = "/getDataReportExport")
    public ResponseVO<String> getDataReportExport(@Valid @RequestBody DataReportReqVO dataReportReqVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        dataReportReqVO.setTimeZone(timeZoneId);
        dataReportReqVO.setTimeZoneDb(dbZone);
        dataReportReqVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::activityDataReport::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dataReportReqVO.setPageSize(10000);
        ResponseVO<DataReportRespVO> dataRespVO = activityReportApi.getDataReportPage(dataReportReqVO);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        DataReportRespVO dataReportRespVO=dataRespVO.getData();
        long totalNum=dataReportRespVO.getActivityDataReportRespVOPage().getTotal();
        if (totalNum==0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ActivityDataReportRespVO.class,
                dataReportReqVO,
                4,
                ExcelUtil.getPages(dataReportReqVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(activityReportApi.getDataReportPage(param).getData().getActivityDataReportRespVOPage().getRecords(), ActivityDataReportRespVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.ACTIVITY_DATA_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
