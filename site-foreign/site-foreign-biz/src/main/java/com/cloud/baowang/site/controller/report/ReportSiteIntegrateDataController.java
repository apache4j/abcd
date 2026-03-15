package com.cloud.baowang.site.controller.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.ReportAdminIntegrateDataApi;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataReportReqVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataTempRspVO;
import com.cloud.baowang.report.api.vo.user.complex.SiteIntegrateStaticRspVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Desciption: 综合数据报表
 **/
@RestController
@Tag(name = "站点-综合报表")
@RequestMapping("/site-integrate/api")
@AllArgsConstructor
public class ReportSiteIntegrateDataController {

    private final ReportAdminIntegrateDataApi reportAdminIntegrateDataApi;

    private final MinioUploadApi minioUploadApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SiteApi siteApi;


    @Operation(summary = "下拉框-币种列表")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return ResponseVO.success(siteCurrencyInfoApi.getCurrencyList(CurrReqUtils.getSiteCode()));
    }


    @Operation(summary = "数据报表")
    @PostMapping(value = "/getSiteIntegrateDataReportPage")
    public ResponseVO<SiteIntegrateStaticRspVO> getSiteIntegrateDataReportPage(@RequestBody AdminIntegrateDataReportReqVO dataReportReqVO) {
        boolean checked = checkTimeLimit(dataReportReqVO.getBeginTime(), dataReportReqVO.getEndTime());
        if (checked) {
            throw new BaowangDefaultException(ResultCode.DATE_MAX_SPAN_31);
        }
        String currentUserAccount = CurrReqUtils.getAccount();
        String uniqueKey = "siteIntegratePageQuery::siteControl::siteIntegrateDataQuery::"  + currentUserAccount;
//        if (RedisUtil.isKeyExist(uniqueKey)) {
//            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
//            throw new BaowangDefaultException("查询的太频繁,请"+remain + "秒后再操作");
//        } else {
//            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
//        }
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        dataReportReqVO.setTimeZone(timeZoneId);
        dataReportReqVO.setTimeZoneDb(dbZone);
        dataReportReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        if(!StringUtils.isNotBlank(dataReportReqVO.getCurrencyCode())){
            dataReportReqVO.setCurrencyCode(null);
        }
        return  reportAdminIntegrateDataApi.getSiteIntegrateDataReportPage(dataReportReqVO);
    }


    @Operation(summary = "数据报表导出")
    @PostMapping(value = "/getSiteDataReportExport")
    public ResponseVO<String> getSiteDataReportExport(@RequestBody AdminIntegrateDataReportReqVO dataReportReqVO) {
        boolean checked = checkTimeLimit(dataReportReqVO.getBeginTime(), dataReportReqVO.getEndTime());
        if (checked) {
            throw new BaowangDefaultException(ResultCode.DATE_MAX_SPAN_31);
        }
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        dataReportReqVO.setTimeZone(timeZoneId);
        dataReportReqVO.setTimeZoneDb(dbZone);
        dataReportReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        String currentUserAccount = CurrReqUtils.getAccount();
        if(!StringUtils.isNotBlank(dataReportReqVO.getCurrencyCode())){
            dataReportReqVO.setCurrencyCode(null);
        }
        String uniqueKey = "siteIntegrateDataExport::siteControl::siteIntegrateDataReport::"  + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dataReportReqVO.setPageSize(10000);
        ResponseVO<Page<AdminIntegrateDataTempRspVO>> dataRespVO = reportAdminIntegrateDataApi.getIntegrateDataExportPage(dataReportReqVO,false);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        Page<AdminIntegrateDataTempRspVO> dataReportRespVO=dataRespVO.getData();
        long totalNum=dataReportRespVO.getTotal();
        if (totalNum==0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        List<String> exportFields = reportAdminIntegrateDataApi.convertFieldToExportFields(dataReportReqVO.getIncludeColumnList());
        dataReportReqVO.setIncludeColumnList(exportFields);
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AdminIntegrateDataTempRspVO.class,
                dataReportReqVO,
                4,
                ExcelUtil.getPages(dataReportReqVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(reportAdminIntegrateDataApi.getIntegrateDataExportPage(dataReportReqVO,false).getData().getRecords(), AdminIntegrateDataTempRspVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_INTEGRATE_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
    public boolean checkTimeLimit(long beginTime, long endTime) {
        long thirtyOneDaysInMillis = 31L * 24 * 60 * 60 * 1000;
        return endTime - beginTime >= thirtyOneDaysInMillis;
    }
}
