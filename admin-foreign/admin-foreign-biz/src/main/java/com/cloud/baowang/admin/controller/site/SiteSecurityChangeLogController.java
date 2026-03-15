package com.cloud.baowang.admin.controller.site;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteSecurityChangeLogApi;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogAllRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogPageReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Desciption: 保证金帐变记录
 * @Author: Ford
 * @Date: 2025/6/27 17:43
 * @Version: V1.0
 **/
@Tag(name = "总台-保证金帐变记录")
@RestController
@AllArgsConstructor
@RequestMapping("/site-security-changeLog/api")
public class SiteSecurityChangeLogController {

    private final SiteSecurityChangeLogApi siteSecurityChangeLogApi;

    private final SystemParamApi systemParamApi;

    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "分页查询接口")
    @PostMapping("listPage")
    ResponseVO<SiteSecurityChangeLogAllRespVO> listPage(@RequestBody SiteSecurityChangeLogPageReqVO siteSecurityChangeLogPageReqVO){
        return siteSecurityChangeLogApi.listPage(siteSecurityChangeLogPageReqVO);
    }


    @Operation(summary = "下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.SITE_TYPE);
        param.add(CommonConstant.SECURITY_SOURCE_COIN_TYPE);
        param.add(CommonConstant.SECURITY_COIN_TYPE);
        param.add(CommonConstant.SECURITY_USER_TYPE);
        param.add(CommonConstant.SECURITY_AMOUNT_DIRECT);
        param.add(CommonConstant.SITE_SECURITY_BALANCE_ACCOUNT);
        return systemParamApi.getSystemParamsByList(param);

    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody SiteSecurityChangeLogPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::siteSecurityChangeLogReport::export::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        SiteSecurityChangeLogAllRespVO siteSecurityChangeLogAllRespVO = siteSecurityChangeLogApi.listPage(vo).getData();
        if (siteSecurityChangeLogAllRespVO == null || siteSecurityChangeLogAllRespVO.getSiteSecurityChangeLogRespVOPage().getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteSecurityChangeLogRespVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), siteSecurityChangeLogAllRespVO.getSiteSecurityChangeLogRespVOPage().getTotal()),
                param -> ConvertUtil.entityListToModelList(siteSecurityChangeLogApi.listPage(vo).getData().getSiteSecurityChangeLogRespVOPage().getRecords(), SiteSecurityChangeLogRespVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_SECURITY_CHANGE_LOG_REPORT)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
