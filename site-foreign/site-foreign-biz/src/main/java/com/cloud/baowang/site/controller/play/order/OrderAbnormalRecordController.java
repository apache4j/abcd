package com.cloud.baowang.site.controller.play.order;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.vo.export.ChinaOrderAbnormalRecordExportVO;
import com.cloud.baowang.site.vo.export.OrderAbnormalRecordExportVO;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.play.api.api.order.OrderAbnormalRecordApi;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalDetailLabelVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordAdminResVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordPageRespVO;
import com.cloud.baowang.site.vo.export.OrderRecordExportVO;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Tag(name = "站点后台异常注单")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/order-abnormal-record/api")
public class OrderAbnormalRecordController {

    private final OrderAbnormalRecordApi orderAbnormalRecordApi;
    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "异常注单列表查询-站点后台")
    @PostMapping("admin/page")
    public ResponseVO<Page<OrderAbnormalRecordPageRespVO>> adminPage(@RequestBody OrderAbnormalRecordAdminResVO dto) {
        String siteCode = CurrReqUtils.getSiteCode();
        dto.setSiteCode(siteCode);
        return orderAbnormalRecordApi.adminAbnormalPage(dto);
    }

    /**
     * 获取异常订单详情
     */
    @Operation(summary = "异常订单详情")
    @PostMapping("/info")
    public ResponseVO<OrderAbnormalDetailLabelVO> findOrderAbnormalDetailByOrderId(@RequestBody IdVO idVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        idVO.setSiteCode(siteCode);

        return ResponseVO.success(orderAbnormalRecordApi.findOrderAbnormalDetailByOrderId(idVO));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@RequestBody OrderAbnormalRecordAdminResVO vo, HttpServletResponse response) {
        String adminId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();

        vo.setSiteCode(siteCode);
        String uniqueKey = "tableExport::centerControl::abnormalRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = orderAbnormalRecordApi.getTotalCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        String fileName = "异常注单列表列表" + DateUtils.dateToyyyyMMddHHmmss(new Date());

        byte[] byteArray = null;


        Integer handicapModel = CurrReqUtils.getHandicapMode();

        //国内盘的导出实体
        if(ObjectUtil.isNotEmpty(handicapModel) && SiteHandicapModeEnum.China.getCode().equals(handicapModel)){
            byteArray = ExcelUtil.writeForParallel(ChinaOrderAbnormalRecordExportVO.class, vo, 4,
                    ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                    param -> ConvertUtil.entityListToModelList(orderAbnormalRecordApi.adminAbnormalPage(param).getData().getRecords(), ChinaOrderAbnormalRecordExportVO.class));

        }else{
            byteArray = ExcelUtil.writeForParallel(OrderAbnormalRecordExportVO.class, vo, 4,
                    ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                    param -> ConvertUtil.entityListToModelList(orderAbnormalRecordApi.adminAbnormalPage(param).getData().getRecords(), OrderAbnormalRecordExportVO.class));
        }
        log.info("操作人:{}开始导出游戏注单记录", CurrReqUtils.getAccount());


        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.ORDER_ABNORMAL_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
