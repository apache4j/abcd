package com.cloud.baowang.admin.controller.verify;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.admin.vo.export.ChannelSendingEmailStatisticExportVO;
import com.cloud.baowang.admin.vo.export.ChannelSendingStatisticExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.verify.ChannelSendStatisticApi;
import com.cloud.baowang.system.api.vo.verify.ChannelSendDetailsTotalRspVO;
import com.cloud.baowang.system.api.vo.verify.ChannelSendStatisticQueryVO;
import com.cloud.baowang.system.api.vo.verify.ChannelSendStatisticRspVO;
import com.cloud.baowang.system.api.vo.verify.SiteInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@Tag(name = "总台-邮箱统计报表")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/email-statistic/api")
public class ChannelEmailStatisticController {
    private final ChannelSendStatisticApi statisticApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "分页查询")
    @PostMapping(value = "/pageQuery")
    public ResponseVO<ChannelSendStatisticRspVO> pageQuery(@RequestBody ChannelSendStatisticQueryVO queryVO) {
        queryVO.setChannelType(CommonConstant.business_two_str);
        return ResponseVO.success(statisticApi.pageQuery(queryVO));
    }

    @Operation(summary = "通道下email发送量明细")
    @PostMapping(value = "/getChannelSendDetails")
    public ResponseVO<ChannelSendDetailsTotalRspVO> getChannelSendDetails(@RequestBody SiteInfoVO queryVO) {
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        queryVO.setTimeZone(timeZoneId);
        queryVO.setTimeZoneDb(dbZone);
        queryVO.setChannelType(CommonConstant.business_two_str);
        return ResponseVO.success(statisticApi.getChannelSendDetails(queryVO));
    }




    @PostMapping("export")
    @Operation(summary = "邮箱统计报表导出")
    public ResponseVO<?> export(@RequestBody ChannelSendStatisticQueryVO vo){
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "emailExport::centerControl::emailStatistic::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        vo.setPageSize(10000);
        String timeZoneId= CurrReqUtils.getTimezone();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        vo.setTimeZone(timeZoneId);
        vo.setTimeZoneDb(dbZone);
        vo.setChannelType(CommonConstant.business_two_str);
        ChannelSendStatisticRspVO responseVO = statisticApi.pageQuery(vo);
        long total = responseVO.getPages().getTotal();
        if (responseVO.getPages().getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                ChannelSendingEmailStatisticExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), total),
                param -> ConvertUtil.entityListToModelList(statisticApi.pageQuery(param).getPages().getRecords(), ChannelSendingEmailStatisticExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.EMAIL_STATISTIC_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }


}
