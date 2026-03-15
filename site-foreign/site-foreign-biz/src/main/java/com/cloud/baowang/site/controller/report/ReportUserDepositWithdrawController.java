package com.cloud.baowang.site.controller.report;
import cn.hutool.core.date.DateUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.report.ReportUserDepositWithdrawExportVO;
import com.cloud.baowang.site.vo.export.userCoin.UserCoinRecordExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.ReportUserDepositWithdrawApi;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawRequestVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


/**
 * @author qiqi
 */
@RestController
@Tag(name= "报表-会员存取报表")
@RequestMapping("/report_user_deposit_withdraw/api")
@Slf4j
@AllArgsConstructor
public class ReportUserDepositWithdrawController {

    private final ReportUserDepositWithdrawApi reportUserDepositWithdrawApi;

    private final MinioUploadApi minioUploadApi;


    @PostMapping("listUserReportDepositWithdrawPage")
    @Operation(summary = "存取报表分页列表")
    public ResponseVO<ReportUserDepositWithdrawResponseVO> listReportDepositWithdrawPage(@RequestBody ReportUserDepositWithdrawRequestVO reportDepositWithdrawRequestVO) {
        int between = TimeZoneUtils.getDaysBetweenInclusive(reportDepositWithdrawRequestVO.getStartDay(), reportDepositWithdrawRequestVO.getEndDay(), CurrReqUtils.getTimezone());
        if (between > 31) {
            return ResponseVO.fail(ResultCode.DATE_MAX_SPAN_31);
        }


        /*Long adminId = commonAdminService.getAdminId();
        String uniqueKey = "selectPage::centerControl::report_deposit_withdraw::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            throw new BaowangDefaultException("你查询的太频繁,"+remain + "秒后才能查询");

        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.SELECT_PAGE_EXPIRE_TIME, TimeUnit.SECONDS);
        }*/
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        reportDepositWithdrawRequestVO.setTimeZone(timeZoneId);
        reportDepositWithdrawRequestVO.setTimeZoneDb(dbZone);
        reportDepositWithdrawRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return reportUserDepositWithdrawApi.listReportDepositWithdrawPage(reportDepositWithdrawRequestVO);
    }

    @PostMapping("export")
    @Operation(summary = "会员存取报表导出")
    public ResponseVO<?> export(@RequestBody ReportUserDepositWithdrawRequestVO vo){
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userUserReportDepositWithdraw::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        vo.setPageSize(10000);
        vo.setExportFlag(true);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Long> responseVO = reportUserDepositWithdrawApi.userReportDepositWithdrawPageCount(vo);
        if(!responseVO.isOk()){
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportUserDepositWithdrawExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(reportUserDepositWithdrawApi.listReportDepositWithdrawPage(param).getData().getPage().getRecords(), ReportUserDepositWithdrawExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_DEPOSIT_WITHDRAW_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }


}
