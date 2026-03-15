package com.cloud.baowang.admin.controller.report;

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
import com.cloud.baowang.wallet.api.api.WithdrawChannelStaticReportApi;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelDataReportRespVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportRespVO;
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
 * @Desciption: 提现渠道报表
 * @Author: Ford
 * @Date: 2024/10/10s 11:47
 * @Version: V1.0
 **/
@RestController
@Tag(name = "提现渠道报表")
@RequestMapping("/withdraw-report/api")
@AllArgsConstructor
public class WithdrawChannelReportController {

    private final WithdrawChannelStaticReportApi withdrawChannelStaticReportApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "数据报表")
    @PostMapping(value = "/getDataReportPage")
    public ResponseVO<WithdrawChannelStaticReportRespVO> getDataReportPage(@Valid @RequestBody WithdrawChannelStaticReportReqVO dataReportReqVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        dataReportReqVO.setTimeZone(timeZoneId);
        dataReportReqVO.setTimeZoneDb(dbZone);
        return  withdrawChannelStaticReportApi.getDataReportPage(dataReportReqVO);
    }


    @Operation(summary = "数据报表导出")
    @PostMapping(value = "/getDataReportExport")
    public ResponseVO<String> getDataReportExport(@Valid @RequestBody WithdrawChannelStaticReportReqVO dataReportReqVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        dataReportReqVO.setTimeZone(timeZoneId);
        dataReportReqVO.setTimeZoneDb(dbZone);
        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::withdrawChannelDataReport::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dataReportReqVO.setPageSize(10000);
        ResponseVO<WithdrawChannelStaticReportRespVO> dataRespVO = withdrawChannelStaticReportApi.getDataReportPage(dataReportReqVO);
        if (!dataRespVO.isOk()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        WithdrawChannelStaticReportRespVO dataReportRespVO=dataRespVO.getData();
        long totalNum=dataReportRespVO.getWithdrawChannelDataReportRespVOPage().getTotal();
        if (totalNum==0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                WithdrawChannelDataReportRespVO.class,
                dataReportReqVO,
                4,
                ExcelUtil.getPages(dataReportReqVO.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(withdrawChannelStaticReportApi.getDataReportPage(param).getData().getWithdrawChannelDataReportRespVOPage().getRecords(), WithdrawChannelDataReportRespVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.WITHDRAW_STATIC_DATA_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
