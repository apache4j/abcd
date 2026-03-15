package com.cloud.baowang.site.controller.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.cloud.baowang.user.api.api.medal.SiteMedalOperLogApi;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogRespVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 17:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "会员-会员勋章管理-勋章变更记录")
@RequestMapping("/siteMedalOperLog/api/")
@AllArgsConstructor
@Slf4j
public class SiteMedalOperLogController {

    private final SiteMedalOperLogApi siteMedalOperLogApi;

    private final MinioUploadApi minioUploadApi;


    @PostMapping("selectPage")
    @Operation(summary = "勋章变更记录分页查询")
    ResponseVO<Page<SiteMedalOperLogRespVO>> selectPage(@RequestBody @Validated SiteMedalOperLogReqVO siteMedalOperLogReqVO){
        siteMedalOperLogReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteMedalOperLogApi.listPage(siteMedalOperLogReqVO);
    }


   /* @Operation(summary = "下拉框 ")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        Map<String, Object> result = Maps.newHashMap();
        List<CodeValueVO> operationEnums = siteMedalOperLogApi.getMedalOperationEnums().getData();
        result.put("operationEnums", operationEnums);
        return ResponseVO.success(result);
    }*/

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody SiteMedalOperLogReqVO siteMedalOperLogReqVO) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::siteMeal::operateLog::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        siteMedalOperLogReqVO.setPageSize(10000);
        ResponseVO<Page<SiteMedalOperLogRespVO>> responseVO = siteMedalOperLogApi.listPage(siteMedalOperLogReqVO);
        if (!responseVO.isOk()||responseVO.getData().getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        byte[] byteArray = ExcelUtil.writeForParallel(SiteMedalOperLogRespVO.class, siteMedalOperLogReqVO, 4,
                ExcelUtil.getPages(siteMedalOperLogReqVO.getPageSize(), responseVO.getData().getTotal()),
                param -> ConvertUtil.entityListToModelList(selectPage(param).getData().getRecords(), SiteMedalOperLogRespVO.class));
        log.info("操作人:{}开始导出勋章变更操作记录,siteCode:{}", CurrReqUtils.getAccount(), CurrReqUtils.getSiteCode());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_MEDAL_OPERATOR_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
