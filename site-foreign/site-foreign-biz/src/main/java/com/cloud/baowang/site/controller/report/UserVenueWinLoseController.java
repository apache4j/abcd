package com.cloud.baowang.site.controller.report;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.venuewinlose.ReportVenueWinLossPageReqVO;
import com.cloud.baowang.report.api.vo.venuewinlose.ReportVenueWinLossResVO;
import com.cloud.baowang.report.api.vo.venuewinlose.VenueWinLossDetailResVO;
import com.cloud.baowang.report.api.vo.venuewinlose.VenueWinLossInfoResVO;
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
@Tag(name = "报表-盈亏报表-场馆盈亏")
@RequestMapping("/venue-win-lose-report/api")
@AllArgsConstructor
public class UserVenueWinLoseController {
    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "场馆盈亏-分页查询")
    @PostMapping(value = "/pageList")
    public ResponseVO<ReportVenueWinLossResVO> pageList(@Valid @RequestBody ReportVenueWinLossPageReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return reportUserVenueWinLoseApi.pageList(vo);
    }

    @Operation(summary = "场馆盈亏-场馆详情")
    @PostMapping(value = "/info")
    public ResponseVO<Page<VenueWinLossInfoResVO>> info(@Valid @RequestBody ReportVenueWinLossPageReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        if (StrUtil.isBlank(vo.getVenueCode())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return reportUserVenueWinLoseApi.info(vo);
    }


    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@Valid @RequestBody ReportVenueWinLossPageReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::getUserVenueWinLosePage::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setExportFlag(true);
        Long responseVO = reportUserVenueWinLoseApi.getTotalCount(vo);
        responseVO = responseVO == null ? 0L : responseVO.intValue();
        vo.setPageSize(responseVO == null ? 0 : responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                VenueWinLossDetailResVO.class,
                vo,
                2,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> reportUserVenueWinLoseApi.pageList(param).getData().getPageList().getRecords()
        );

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.GET_VENUE_WINLOSE_PAGE)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }
}
