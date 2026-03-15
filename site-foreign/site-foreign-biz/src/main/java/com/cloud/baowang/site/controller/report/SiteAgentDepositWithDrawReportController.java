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
import com.cloud.baowang.report.api.api.ReportAgentDepositWithdrawApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawResult;
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
@Tag(name = "报表-业务报表-代理存取报表")
@RequestMapping("/agent-deposit-withdraw-report/api")
@AllArgsConstructor
public class SiteAgentDepositWithDrawReportController {

    private final ReportAgentDepositWithdrawApi reportAgentDepositWithdrawApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "代理存取报表分页")
    @PostMapping(value = "/listPage")
    public ResponseVO<ReportAgentDepositWithdrawResult> listPage(@Valid @RequestBody ReportAgentDepositWithdrawPageVO reportAgentDepositWithdrawPageVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportAgentDepositWithdrawPageVO.setTimeZone(timeZoneId);
        reportAgentDepositWithdrawPageVO.setTimeZoneDb(dbZone);
        reportAgentDepositWithdrawPageVO.setSiteCode(siteCode);
        String currentUserAccount = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::agentDepositWithDrawReportQuery::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException(remain + "秒后才能查询");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }

        return  reportAgentDepositWithdrawApi.listPage(reportAgentDepositWithdrawPageVO);
    }


    @Operation(summary = "代理存取报表导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@Valid @RequestBody ReportAgentDepositWithdrawPageVO reportAgentDepositWithdrawPageVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportAgentDepositWithdrawPageVO.setTimeZone(timeZoneId);
        reportAgentDepositWithdrawPageVO.setTimeZoneDb(dbZone);
        reportAgentDepositWithdrawPageVO.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::agentDepositWithDrawReport::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reportAgentDepositWithdrawPageVO.setPageSize(10000);
        ResponseVO<ReportAgentDepositWithdrawResult> dataRespVO = reportAgentDepositWithdrawApi.listPage(reportAgentDepositWithdrawPageVO);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        ReportAgentDepositWithdrawResult reportAgentDepositWithdrawResult=dataRespVO.getData();
        if (reportAgentDepositWithdrawResult==null) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        if (reportAgentDepositWithdrawResult.getPageList()==null) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        long totalNum=reportAgentDepositWithdrawResult.getPageList().getTotal();
        if (totalNum==0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportAgentDepositWithdrawResponseVO.class,
                reportAgentDepositWithdrawPageVO,
                4,
                ExcelUtil.getPages(reportAgentDepositWithdrawPageVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(reportAgentDepositWithdrawApi.listPage(param).getData().getPageList().getRecords(), ReportAgentDepositWithdrawResponseVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_DEPOSIT_WITHDRAW_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
