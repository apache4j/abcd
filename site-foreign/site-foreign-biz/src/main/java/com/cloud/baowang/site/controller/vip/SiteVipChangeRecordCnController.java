package com.cloud.baowang.site.controller.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.vo.export.vip.SiteVipCnChangeRecordExcelVO;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.user.api.api.vip.SiteVipChangeRecordCnApi;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnReqVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author : mufan
 * @Date : 2024/8/7 11:24
 * @Version : 1.0
 */
@Tag(name = "站点后台大陆盘vip等级变更记录")
@RestController
@RequestMapping("/siteVipChangeRecordCn/api")
@AllArgsConstructor
public class SiteVipChangeRecordCnController {

    private final SiteVipChangeRecordCnApi siteVipOptionCurrencyConfigApi;
    private final MinioUploadApi minioUploadApi;

    public static final String VIP_RANK_OPERATION = "VIP等级变更记录";

    @Operation(summary = "VIP变更记录列表,下拉框升降级类型用 vip_level_change_type 关键字查询")
    @PostMapping(value = "/queryList")
    public ResponseVO<Page<SiteVipChangeRecordCnVO>>  queryList(@RequestBody SiteVipChangeRecordCnReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteVipOptionCurrencyConfigApi.getList(vo);
    }

    @Operation(summary = "导出VIP段位变更记录")
    @PostMapping(value = "/exportOperation")
    public ResponseVO<?> exportOperation(@RequestBody SiteVipChangeRecordCnReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::vipRankOperation::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Page<SiteVipChangeRecordCnVO>> responseVO =this.queryList(vo);
        Integer responseSize = responseVO.getData().getRecords().size();
        if (responseSize <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteVipCnChangeRecordExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), Long.parseLong(responseSize+"")),
                param -> ConvertUtil.entityListToModelList(responseVO.getData().getRecords(), SiteVipCnChangeRecordExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(VIP_RANK_OPERATION)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());

    }



}
