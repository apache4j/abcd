package com.cloud.baowang.site.controller.report;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.ReportTopAgentStaticsApi;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsResult;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
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
 * @Desciption: 总代报表
 * @Author: Ford
 * @Date: 2024/10/3 11:47
 * @Version: V1.0
 **/
@RestController
@Tag(name = "商务后台-总代报表")
@RequestMapping("/business-agent-report/api")
@AllArgsConstructor
public class ReportTopAgentController {

    private final ReportTopAgentStaticsApi reportTopAgentStaticsApi;

    private final MinioUploadApi minioUploadApi;

    private final SystemConfigApi systemConfigApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;


    @Operation(summary = "总代报表分页查询")
    @PostMapping(value = "/listPage")
    public ResponseVO<ReportTopAgentStaticsResult> listPage(@Valid @RequestBody ReportTopAgentStaticsPageVO reportTopAgentStaticsPageVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportTopAgentStaticsPageVO.setTimeZone(timeZoneId);
        reportTopAgentStaticsPageVO.setTimeZoneDb(dbZone);
        reportTopAgentStaticsPageVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        reportTopAgentStaticsPageVO.setMerchantAccount(currentUserAccount);
        Long startDay=reportTopAgentStaticsPageVO.getStartStaticDay();
        Long endDay=reportTopAgentStaticsPageVO.getEndStaticDay();
        if (DateUtils.checkTime(startDay, endDay)) {
            throw new BaowangDefaultException(ResultCode.FORTY_DAY_OVER);
        }

        String uniqueKey = "tableExport::centerControl::reportTopAgentQuery::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException(remain + "秒后才能查询");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }

        return  reportTopAgentStaticsApi.listPage(reportTopAgentStaticsPageVO);
    }

    @Operation(summary = "币种下拉框")
    @PostMapping("/currencyDownbox")
    ResponseVO<List<SiteCurrencyInfoRespVO>> selectAll() {
        String siteCode=CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS=siteCurrencyInfoApi.getBySiteCode(siteCode);
        SiteCurrencyInfoRespVO siteCurrencyInfoRespVO=new SiteCurrencyInfoRespVO();
        siteCurrencyInfoRespVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        siteCurrencyInfoRespVO.setCurrencyName(CommonConstant.PLAT_CURRENCY_CODE);
        siteCurrencyInfoRespVOS.add(siteCurrencyInfoRespVO);
        return ResponseVO.success(siteCurrencyInfoRespVOS);
    }


    @Operation(summary = "总代报表导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@Valid @RequestBody ReportTopAgentStaticsPageVO reportTopAgentStaticsPageVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportTopAgentStaticsPageVO.setTimeZone(timeZoneId);
        reportTopAgentStaticsPageVO.setTimeZoneDb(dbZone);
        reportTopAgentStaticsPageVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        reportTopAgentStaticsPageVO.setMerchantAccount(currentUserAccount);

        String uniqueKey = "tableExport::centerControl::reportTopAgent:" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reportTopAgentStaticsPageVO.setPageSize(10000);
        ResponseVO<ReportTopAgentStaticsResult> dataRespVO = reportTopAgentStaticsApi.listPage(reportTopAgentStaticsPageVO);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        ReportTopAgentStaticsResult reportTopAgentStaticsResult=dataRespVO.getData();
        long totalNum=reportTopAgentStaticsResult.getPageList().getTotal();
        if (totalNum==0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportTopAgentStaticsResponseVO.class,
                reportTopAgentStaticsPageVO,
                4,
                ExcelUtil.getPages(reportTopAgentStaticsPageVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(reportTopAgentStaticsApi.listPage(param).getData().getPageList().getRecords(), ReportTopAgentStaticsResponseVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        ResponseVO<String> upload =  minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.TOP_AGENT_STATIC_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());

        String fileName = upload.getData();
        String fileKey = ExcelUtil.BAOWANG_BUCKET + "/" + fileName;
        String domain =  systemConfigApi.queryMinioDomain().getData();
        String downLoadUrl = domain + "/" + fileKey;

        return ResponseVO.success(downLoadUrl);
    }
}
