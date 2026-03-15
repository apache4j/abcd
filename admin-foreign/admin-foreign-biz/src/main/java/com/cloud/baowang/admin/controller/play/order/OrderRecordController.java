package com.cloud.baowang.admin.controller.play.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.controller.base.ExportBaseController;
import com.cloud.baowang.admin.vo.export.OrderRecordExportVO;
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
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.order.OrderRecordAdminInfoResVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAdminInfoRespVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAdminResVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAdminTotalRespVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordPageRespVO;
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
@Tag(name = "游戏注单")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/order-record/api")
public class OrderRecordController extends ExportBaseController {

    private final OrderRecordApi orderRecordApi;
    private final MinioUploadApi minioUploadApi;

    private static final String ORDER_RECORD_EXPORT_UNIQUE_KEY = "order_record";

    private static final String EXPORT_REDIS_UNIQUE_KEY = "tableExport:centerControl:%s:%s:%s";


    @Operation(summary = "注单列表查询-中控后台")
    @PostMapping("admin/page")
    public ResponseVO<Page<OrderRecordPageRespVO>> adminPage(@RequestBody OrderRecordAdminResVO dto) {
        dto.setCurSiteCode(CurrReqUtils.getSiteCode());
        return orderRecordApi.adminPage(dto);
    }

    @Operation(summary = "注单列表总计-中控后台")
    @PostMapping("admin/total")
    public ResponseVO<OrderRecordAdminTotalRespVO> adminTotal(@RequestBody OrderRecordAdminResVO dto) {
        return orderRecordApi.adminTotal(dto);
    }


    @Operation(summary = "注单详情")
    @PostMapping("admin/info")
    public ResponseVO<OrderRecordAdminInfoRespVO> orderInfo(@Valid @RequestBody OrderRecordAdminInfoResVO vo) {
        return orderRecordApi.orderInfo(vo);
    }

    @Operation(summary ="导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@RequestBody OrderRecordAdminResVO dto) {
        // 导出频率校验
        //checkExportFrequency(ORDER_RECORD_EXPORT_UNIQUE_KEY + CurrReqUtils.getSiteCode());
        String uniqueMark = ORDER_RECORD_EXPORT_UNIQUE_KEY + CurrReqUtils.getSiteCode();
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = String.format(EXPORT_REDIS_UNIQUE_KEY, uniqueMark, siteCode, oneId);
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        dto.setPageSize(10000);
        dto.setExportFlag(true);
        ResponseVO<Long> responseVO = orderRecordApi.orderExportCount(dto);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        dto.setCurSiteCode(CurrReqUtils.getSiteCode());
        byte[] byteArray = ExcelUtil.writeForParallel(OrderRecordExportVO.class, dto, 4,
                ExcelUtil.getPages(dto.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(orderRecordApi.adminPage(param).getData().getRecords(), OrderRecordExportVO.class));
        log.info("操作人:{}开始导出游戏注单记录", CurrReqUtils.getAccount());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.ORDER_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
