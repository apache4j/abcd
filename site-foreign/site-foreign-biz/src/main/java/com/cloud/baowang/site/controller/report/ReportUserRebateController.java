package com.cloud.baowang.site.controller.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.cloud.baowang.system.api.api.site.rebate.ReportUserRebateRecordApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsResponseVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateInfoVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateRspVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@RestController
@Tag(name = "返水报表")
@RequestMapping("/site-rebate/api")
@AllArgsConstructor
public class ReportUserRebateController {

    private final ReportUserRebateRecordApi reportUserRebateRecordApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "返水报表")
    @PostMapping(value = "/listPage")
    public ResponseVO<ReportUserRebateRspVO> listPage(@Valid @RequestBody ReportUserRebateQueryVO reqVo) {
        reqVo.setSiteCode(CurrReqUtils.getSiteCode());

        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::siteControl::userRebateList::" + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException(remain + "秒后才能查询");
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_LIMIT_TIME, TimeUnit.SECONDS);
        }
        reqVo.setSiteCode(siteCode);
        return reportUserRebateRecordApi.listPage(reqVo);
    }


    @Operation(summary = "场馆-返水明细")
    @PostMapping(value = "/venueRebateDetails")
    public ResponseVO<Page<ReportUserRebateInfoVO>> venueRebateDetails(@Valid @RequestBody ReportUserRebateQueryVO reqVo) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reqVo.setTimeZone(timeZoneId);
        reqVo.setTimeZoneDb(dbZone);
        String siteCode = CurrReqUtils.getSiteCode();
        reqVo.setSiteCode(siteCode);
        return reportUserRebateRecordApi.venueRebateDetails(reqVo);
    }


    @Operation(summary = "返水报表导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@Valid @RequestBody ReportUserRebateQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::siteControl::userRebateReport::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reqVO.setPageSize(10000);
        ResponseVO<ReportUserRebateRspVO> dataRespVO = reportUserRebateRecordApi.listPage(reqVO);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        ReportUserRebateRspVO reportAgentStaticsResult = dataRespVO.getData();
        long totalNum = reportAgentStaticsResult.getPageInfo().getTotal();
        if (totalNum == 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportUserRebateInfoVO.class,
                reqVO,
                4,
                ExcelUtil.getPages(reqVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(reportUserRebateRecordApi.listPage(param).getData().getPageInfo().getRecords(), ReportUserRebateInfoVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_REBATE_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
