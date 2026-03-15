package com.cloud.baowang.admin.controller.site;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.admin.vo.export.SiteStatisticsViewExcelVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.SiteReportApi;
import com.cloud.baowang.report.api.vo.SiteReportSyncDataVO;
import com.cloud.baowang.report.api.vo.SiteStatisticsRecordVO;
import com.cloud.baowang.report.api.vo.site.SiteReportStatisticsQueryPageQueryVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Tag(name = "总台-平台报表")
@RestController
@AllArgsConstructor
@RequestMapping("/site-report/api")
public class SiteReportController {
    private final SiteReportApi siteReportApi;
    private final SystemParamApi systemParamApi;
    private final SystemCurrencyInfoApi currencyInfoApi;
    private final MinioUploadApi minioUploadApi;

    @GetMapping("getDownBox")
    @Operation(summary = "平台类型,币种下拉")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.SITE_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> data = resp.getData();
            ResponseVO<List<SystemCurrencyInfoRespVO>> currResp = currencyInfoApi.selectAll();
            if (currResp.isOk()) {
                List<SystemCurrencyInfoRespVO> cData = currResp.getData();
                List<CodeValueVO> currencyList = cData.stream()
                        .map(currencyInfo -> new CodeValueVO(currencyInfo.getCurrencyCode(), currencyInfo.getCurrencyNameI18()))
                        .toList();
                data.put("currency_code", currencyList);
            }
        }
        return resp;
    }

    @PostMapping("syncData")
    @Operation(summary = "测试-同步数据")
    public ResponseVO<Boolean> syncData(@RequestBody SiteReportSyncDataVO dataVO) {
        if (dataVO.getStartTime() == null) {
            dataVO = new SiteReportSyncDataVO();
            ZoneId systemZone = ZoneId.of("UTC-5");
            LocalDateTime now = LocalDateTime.now(systemZone);
            LocalDateTime startOfPreviousHour = now.minusHours(1).truncatedTo(ChronoUnit.HOURS);
            LocalDateTime endOfPreviousHour = startOfPreviousHour.plusMinutes(59).plusSeconds(59).plusNanos(999000000);

            long startTimestamp = startOfPreviousHour.atZone(systemZone).toInstant().toEpochMilli();
            long endTimestamp = endOfPreviousHour.atZone(systemZone).toInstant().toEpochMilli();

            dataVO.setStartTime(startTimestamp);
            dataVO.setEndTime(endTimestamp);
            dataVO.setRerun(false);
        }

        return siteReportApi.syncData(dataVO);
    }

    @PostMapping("pageQuery")
    @Operation(summary = "平台报表")
    public ResponseVO<SiteStatisticsRecordVO> getPage(@RequestBody SiteReportStatisticsQueryPageQueryVO queryVO) {
        if(Objects.nonNull(queryVO.getStartTime()) && Objects.nonNull(queryVO.getEndTime())) {
            List<String> betweenDates = DateUtils.getBetweenDates(queryVO.getStartTime(), queryVO.getEndTime(), CurrReqUtils.getTimezone());
            if (CollectionUtil.isNotEmpty(betweenDates) && betweenDates.size() > 31) {
                throw new BaowangDefaultException(ResultCode.LIMIT_TIME_RANGE);
            }
        }
        return siteReportApi.getSiteReport(queryVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody SiteReportStatisticsQueryPageQueryVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::siteReport::export::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long total = siteReportApi.getTotal(vo);
        if (total == null || total <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteStatisticsViewExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), total),
                param -> ConvertUtil.entityListToModelList(siteReportApi.getPage(param).getData().getRecords(), SiteStatisticsViewExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
