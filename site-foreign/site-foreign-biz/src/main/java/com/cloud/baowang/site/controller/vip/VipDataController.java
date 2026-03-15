package com.cloud.baowang.site.controller.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.ReportVIPDataApi;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataPage;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataReq;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordExportVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author : 小智
 * @Date : 2024/11/7 11:37
 * @Version : 1.0
 */
@Tag(name = "站点后台-业务报表-VIP数据报表")
@RestController
@RequestMapping("/vipData")
@AllArgsConstructor
public class VipDataController {

    private ReportVIPDataApi reportVIPDataApi;

    private MinioUploadApi minioUploadApi;

    @Operation(summary = "vip数据报表查询")
    @PostMapping(value = "/queryVIPData")
    public ResponseVO<ReportVIPDataVO> queryVIPData(@RequestBody ReportVIPDataReq req) {
        // 如果日期为空默认当前时区的今天
        if(null == req.getStartTime()){
            String timeZone = CurrReqUtils.getTimezone();
            req.setStartTime(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone));
        }
        req.setHandicapMode(CurrReqUtils.getHandicapMode());
        return reportVIPDataApi.pageVIPData(req);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody ReportVIPDataReq req) {
        String siteCode = CurrReqUtils.getSiteCode();
        req.setSiteCode(siteCode);
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::siteControl::vipData::" + siteCode + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        req.setPageSize(10000);
        ResponseVO<Long> responseVO = reportVIPDataApi.getTotalCount(req);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ReportVIPDataPage.class,
                req,
                4,
                ExcelUtil.getPages(req.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(queryVIPData(param).getData().getPageList().getRecords(), ReportVIPDataPage.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.REPORT_VIP_DATA)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());

    }
}
