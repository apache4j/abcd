package com.cloud.baowang.admin.controller.report;

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
import com.cloud.baowang.report.api.api.ReportAdminIntegrateDataApi;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataReportReqVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataTempRspVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateStaticRspVO;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Desciption: 综合数据报表
 **/
@RestController
@Tag(name = "综合数据报表")
@RequestMapping("/complex-data/api")
@AllArgsConstructor
public class ReportAdminIntegrateDataController {

    private final ReportAdminIntegrateDataApi reportAdminIntegrateDataApi;

    private final MinioUploadApi minioUploadApi;

    private final SiteApi siteApi;

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;

    @Operation(summary = "下拉框-币种列表,站点列表")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String,List<CodeValueVO>>> getDownBox() {
        Map<String,List<CodeValueVO>> map = new HashMap<>();
        map.put("siteCode",siteApi.getSiteDownBox().getData());
        List<SystemCurrencyInfoRespVO> data = systemCurrencyInfoApi.selectAll().getData();
        List<CodeValueVO> currencyList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            currencyList.add(CodeValueVO.builder().code(String.valueOf(i)).value(data.get(i).getCurrencyCode()).build());
        }
        map.put("currencyCode",currencyList);
        return ResponseVO.success(map);
    }


    @Operation(summary = "数据报表")
    @PostMapping(value = "/getDataReportPage")
    public ResponseVO<AdminIntegrateStaticRspVO> getDataReportPage(@RequestBody AdminIntegrateDataReportReqVO dataReportReqVO) {
        boolean checked = checkTimeLimit(dataReportReqVO.getBeginTime(), dataReportReqVO.getEndTime());
        if (checked) {
            throw new BaowangDefaultException(ResultCode.DATE_MAX_SPAN_31);
        }
        String currentUserAccount = CurrReqUtils.getAccount();
        String uniqueKey = "adminIntegratePageQuery::adminControl::adminIntegrateDataQuery::"  + currentUserAccount;
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
        if(!StringUtils.isNotBlank(dataReportReqVO.getSiteCode())){
            dataReportReqVO.setSiteCode(null);
        }
        if(!StringUtils.isNotBlank(dataReportReqVO.getCurrencyCode())){
            dataReportReqVO.setCurrencyCode(null);
        }
        return  reportAdminIntegrateDataApi.getIntegrateDataReportPage(dataReportReqVO);
    }


    @Operation(summary = "数据报表导出")
    @PostMapping(value = "/getDataReportExport")
    public ResponseVO<String> getDataReportExport(@RequestBody AdminIntegrateDataReportReqVO dataReportReqVO) {
        boolean checked = checkTimeLimit(dataReportReqVO.getBeginTime(), dataReportReqVO.getEndTime());
        if (checked) {
            throw new BaowangDefaultException(ResultCode.DATE_MAX_SPAN_31);
        }
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        dataReportReqVO.setTimeZone(timeZoneId);
        dataReportReqVO.setTimeZoneDb(dbZone);
        if(!StringUtils.isNotBlank(dataReportReqVO.getSiteCode())){
            dataReportReqVO.setSiteCode(null);
        }
        if(!StringUtils.isNotBlank(dataReportReqVO.getCurrencyCode())){
            dataReportReqVO.setCurrencyCode(null);
        }
        String currentUserAccount = CurrReqUtils.getAccount();
        String uniqueKey = "adminIntegrateDataExport::adminControl::adminIntegrateDataReport::"  + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dataReportReqVO.setPageSize(10000);
        ResponseVO<Page<AdminIntegrateDataTempRspVO>> dataRespVO = reportAdminIntegrateDataApi.getIntegrateDataExportPage(dataReportReqVO,true);
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
                param -> ConvertUtil.entityListToModelList(reportAdminIntegrateDataApi.getIntegrateDataExportPage(dataReportReqVO,true).getData().getRecords(), AdminIntegrateDataTempRspVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.ADMIN_INTEGRATE_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    public boolean checkTimeLimit(long beginTime, long endTime) {
        long thirtyOneDaysInMillis = 31L * 24 * 60 * 60 * 1000;
        return endTime - beginTime >= thirtyOneDaysInMillis;
    }
}
