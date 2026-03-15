package com.cloud.baowang.site.controller.report;

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
import com.cloud.baowang.report.api.api.ReportAgentMerchantStaticsApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsResult;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Desciption: 商务报表
 * @Author: Ford
 * @Date: 2024/10/3 11:47
 * @Version: V1.0
 **/
@RestController
@Tag(name = "报表-业务报表-商务报表")
@RequestMapping("/agent-merchant-report/api")
@AllArgsConstructor
public class SiteAgentMerchantReportController {

    private final ReportAgentMerchantStaticsApi reportAgentMerchantStaticsApi;

    private final MinioUploadApi minioUploadApi;

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;


    @Operation(summary = "数据报表")
    @PostMapping(value = "/listPage")
    public ResponseVO<ReportAgentMerchantStaticsResult> listPage(@Valid @RequestBody ReportAgentMerchantStaticsPageVO reportAgentMerchantStaticsPageVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportAgentMerchantStaticsPageVO.setTimeZone(timeZoneId);
        reportAgentMerchantStaticsPageVO.setTimeZoneDb(dbZone);
        reportAgentMerchantStaticsPageVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::agentMerchantStaticReportQuery::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException(remain + "秒后才能查询");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }
        return  reportAgentMerchantStaticsApi.listPage(reportAgentMerchantStaticsPageVO);
    }


    @Operation(summary = "币种下拉框")
    @PostMapping("/currencyDownbox")
    ResponseVO<List<SystemCurrencyInfoRespVO>> selectAll() {
        List<SystemCurrencyInfoRespVO> systemCurrencyInfoRespVOS = systemCurrencyInfoApi.selectAll().getData();
        SystemCurrencyInfoRespVO systemCurrencyInfoRespVO=new SystemCurrencyInfoRespVO();
        systemCurrencyInfoRespVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        systemCurrencyInfoRespVO.setCurrencyName(CommonConstant.PLAT_CURRENCY_CODE);
        systemCurrencyInfoRespVOS.add(systemCurrencyInfoRespVO);
        return ResponseVO.success(systemCurrencyInfoRespVOS);
    }

    @Operation(summary = "数据报表导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@Valid @RequestBody ReportAgentMerchantStaticsPageVO reportAgentMerchantStaticsPageVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportAgentMerchantStaticsPageVO.setTimeZone(timeZoneId);
        reportAgentMerchantStaticsPageVO.setTimeZoneDb(dbZone);
        reportAgentMerchantStaticsPageVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::agentMerchantStaticReport::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reportAgentMerchantStaticsPageVO.setPageSize(10000);
        ResponseVO<ReportAgentMerchantStaticsResult> dataRespVO = reportAgentMerchantStaticsApi.listPage(reportAgentMerchantStaticsPageVO);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        ReportAgentMerchantStaticsResult reportAgentMerchantStaticsResult=dataRespVO.getData();
        long totalNum=reportAgentMerchantStaticsResult.getPageList().getTotal();
        if (totalNum==0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportAgentMerchantStaticsResponseVO.class,
                reportAgentMerchantStaticsPageVO,
                4,
                ExcelUtil.getPages(reportAgentMerchantStaticsPageVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(reportAgentMerchantStaticsApi.listPage(param).getData().getPageList().getRecords(), ReportAgentMerchantStaticsResponseVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_MERCHANT_STATIC_REPORT_SITE)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
