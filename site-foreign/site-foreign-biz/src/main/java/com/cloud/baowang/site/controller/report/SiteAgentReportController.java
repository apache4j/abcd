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
import com.cloud.baowang.report.api.api.ReportAgentStaticsApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsResult;
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
 * @Desciption: 代理报表
 * @Author: Ford
 * @Date: 2024/10/3 11:47
 * @Version: V1.0
 **/
@RestController
@Tag(name = "报表-业务报表-代理报表")
@RequestMapping("/agent-report/api")
@AllArgsConstructor
public class SiteAgentReportController {

    private final ReportAgentStaticsApi reportAgentStaticsApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "数据报表")
    @PostMapping(value = "/listPage")
    public ResponseVO<ReportAgentStaticsResult> listPage(@Valid @RequestBody ReportAgentStaticsPageVO reportAgentStaticsPageVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportAgentStaticsPageVO.setTimeZone(timeZoneId);
        reportAgentStaticsPageVO.setTimeZoneDb(dbZone);
        reportAgentStaticsPageVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::agentStaticReportQuery::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException(remain + "秒后才能查询");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }

        return  reportAgentStaticsApi.listPage(reportAgentStaticsPageVO);
    }


    @Operation(summary = "数据报表导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@Valid @RequestBody ReportAgentStaticsPageVO reportAgentStaticsPageVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportAgentStaticsPageVO.setTimeZone(timeZoneId);
        reportAgentStaticsPageVO.setTimeZoneDb(dbZone);
        reportAgentStaticsPageVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::agentStaticReport::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reportAgentStaticsPageVO.setPageSize(10000);
        ResponseVO<ReportAgentStaticsResult> dataRespVO = reportAgentStaticsApi.listPage(reportAgentStaticsPageVO);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        ReportAgentStaticsResult reportAgentStaticsResult=dataRespVO.getData();
        long totalNum=reportAgentStaticsResult.getPageList().getTotal();
        if (totalNum==0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportAgentStaticsResponseVO.class,
                reportAgentStaticsPageVO,
                4,
                ExcelUtil.getPages(reportAgentStaticsPageVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(reportAgentStaticsApi.listPage(param).getData().getPageList().getRecords(), ReportAgentStaticsResponseVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_STATIC_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
