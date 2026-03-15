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
import com.cloud.baowang.report.api.api.ReportGameApi;
import com.cloud.baowang.report.api.vo.game.ReportGameQuerySiteReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQuerySiteVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueTypeReqVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueTypeVO;
import com.cloud.baowang.report.api.vo.game.ReportGameQueryVenueVO;
import com.cloud.baowang.site.controller.base.ExportBaseController;
import com.cloud.baowang.site.vo.export.gamereport.ReportGameQuerySiteExportVO;
import com.cloud.baowang.site.vo.export.gamereport.ReportGameQueryVenueExportVO;
import com.cloud.baowang.site.vo.export.gamereport.ReportGameQueryVenueTypeExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@Tag(name = "站点-游戏报表")
@RequestMapping("/site-integrate/api")
@AllArgsConstructor
public class ReportGameController extends ExportBaseController {
    private static final String GAME_REPORT_SITE_EXPORT_UNIQUE_KEY = "center_site_game_report";
    private static final String GAME_REPORT_SITE_VENUETYPE_EXPORT_UNIQUE_KEY = "center_site_venuetype_game_report";
    private static final String GAME_REPORT_SITE_VENUE_EXPORT_UNIQUE_KEY = "center_site_venue_game_report";

    private static final String EXPORT_REDIS_UNIQUE_KEY = "tableExport:centerControl:%s:%s:%s";
    private final ReportGameApi reportGameApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "游戏报表-站点")
    @PostMapping(value = "/site/pageList")
    public ResponseVO<Page<ReportGameQuerySiteVO>> sitePageList(@Valid @RequestBody ReportGameQuerySiteReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return reportGameApi.sitePageList(vo);
    }

    @Operation(summary = "游戏报表-场馆类型")
    @PostMapping(value = "/venue-type/pageList")
    public ResponseVO<Page<ReportGameQueryVenueTypeVO>> venueTypePageList(@Valid @RequestBody ReportGameQueryVenueTypeReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return reportGameApi.venueTypePageList(vo);
    }

    @Operation(summary = "游戏报表-场馆")
    @PostMapping(value = "/venue/pageList")
    public ResponseVO<Page<ReportGameQueryVenueVO>> venuePageList(@Valid @RequestBody ReportGameQueryVenueReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return reportGameApi.venuePageList(vo);
    }


    @Operation(summary = "游戏报表-站点导出")
    @PostMapping(value = "/site/export")
    public ResponseVO<String> siteExport(@Valid @RequestBody ReportGameQuerySiteReqVO dto) {
        // 导出频率校验
        //checkExportFrequency(GAME_REPORT_SITE_EXPORT_UNIQUE_KEY);
        String uniqueMark = GAME_REPORT_SITE_EXPORT_UNIQUE_KEY + CurrReqUtils.getSiteCode();
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = String.format(EXPORT_REDIS_UNIQUE_KEY, uniqueMark, siteCode, oneId);
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            //throw new BaowangDefaultException(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dto.setPageSize(10000);
        dto.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Long> responseVO = reportGameApi.sitePageListCount(dto);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        byte[] byteArray = ExcelUtil.writeForParallel(ReportGameQuerySiteExportVO.class, dto, 1,
                ExcelUtil.getPages(dto.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(reportGameApi.sitePageList(param).getData().getRecords(), ReportGameQuerySiteExportVO.class));
        log.info("操作人:{}开始游戏报表-站点导出", CurrReqUtils.getAccount());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.GAME_REPORT_VENUE_TYPE)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    @Operation(summary = "游戏报表-场馆分类导出")
    @PostMapping(value = "/venue-type/export")
    public ResponseVO<String> venueTypeexport(@Valid @RequestBody ReportGameQueryVenueTypeReqVO dto) {
        // 导出频率校验
        //checkExportFrequency(GAME_REPORT_SITE_VENUETYPE_EXPORT_UNIQUE_KEY);
        String uniqueMark = GAME_REPORT_SITE_VENUETYPE_EXPORT_UNIQUE_KEY;
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = String.format(EXPORT_REDIS_UNIQUE_KEY, uniqueMark, siteCode, oneId);
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            //throw new BaowangDefaultException(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dto.setPageSize(10000);
        dto.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Long> responseVO = reportGameApi.venueTypePageListCount(dto);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        byte[] byteArray = ExcelUtil.writeForParallel(ReportGameQueryVenueTypeExportVO.class, dto, 1,
                ExcelUtil.getPages(dto.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(reportGameApi.venueTypePageList(param).getData().getRecords(), ReportGameQueryVenueTypeExportVO.class));
        log.info("操作人:{}开始游戏报表-总台导出", CurrReqUtils.getAccount());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.GAME_REPORT_VENUE)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    @Operation(summary = "游戏报表-场馆导出")
    @PostMapping(value = "/venue/export")
    public ResponseVO<String> venueExport(@Valid @RequestBody ReportGameQueryVenueReqVO dto) {
        // 导出频率校验
        //checkExportFrequency(GAME_REPORT_SITE_VENUE_EXPORT_UNIQUE_KEY);
        String uniqueMark = GAME_REPORT_SITE_VENUE_EXPORT_UNIQUE_KEY;
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = String.format(EXPORT_REDIS_UNIQUE_KEY, uniqueMark, siteCode, oneId);
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            //throw new BaowangDefaultException(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dto.setPageSize(10000);
        dto.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Long> responseVO = reportGameApi.venuePageListCount(dto);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        byte[] byteArray = ExcelUtil.writeForParallel(ReportGameQueryVenueExportVO.class, dto, 4,
                ExcelUtil.getPages(dto.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(reportGameApi.venuePageList(param).getData().getRecords(), ReportGameQueryVenueExportVO.class));
        log.info("操作人:{}开始游戏报表-总台导出", CurrReqUtils.getAccount());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.GAME_REPORT_GAME)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
