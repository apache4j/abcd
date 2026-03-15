package com.cloud.baowang.admin.controller.site;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.admin.vo.export.SiteSecurityAdjustReviewLogExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteSecurityAdjustReviewApi;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityAdjustReviewDetailVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityAdjustReviewLogVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityReviewLogPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "总台-保证金审核记录")
@RestController
@AllArgsConstructor
@RequestMapping("/site-security-review/log/api")
public class SiteSecurityAdjustReviewLogController {

    private final SiteSecurityAdjustReviewApi siteSecurityAdjustReviewApi;
    private final MinioUploadApi minioUploadApi;
    private final SystemParamApi systemParamApi;

    @Operation(summary = "保证金调整审核记录分页列表")
    @PostMapping("/logsPageList")
    public ResponseVO<Page<SiteSecurityAdjustReviewLogVO>> logsPageList(@RequestBody SiteSecurityReviewLogPageReqVO siteSecurityReviewLogPageReqVO) {
        return siteSecurityAdjustReviewApi.logsPageList(siteSecurityReviewLogPageReqVO, CurrReqUtils.getAccount());
    }

    @Operation(summary = "单个详情")
    @PostMapping("/detail")
    public ResponseVO<SiteSecurityAdjustReviewDetailVO> detail(@RequestBody IdVO idVO) {
        return siteSecurityAdjustReviewApi.detail(idVO);
    }

    @Operation(summary = "保证金调整审核记录导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody SiteSecurityReviewLogPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::site-security-review::userInfo::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = siteSecurityAdjustReviewApi.logsPageListTotalCount(vo,CurrReqUtils.getAccount());
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteSecurityAdjustReviewLogExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(logsPageList(param).getData().getRecords(), SiteSecurityAdjustReviewLogExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_SECURITY_ADJUST_REVIEW_LOG_EXPORT_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }


    @Operation(summary = "下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        param.add(CommonConstant.AUDIT_TIME_TYPE);
        param.add(CommonConstant.SITE_SECURITY_REVIEW);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> data = resp.getData();
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.USER_REVIEW_REVIEW_STATUS);
            codeValueVOS = codeValueVOS.stream().filter(item -> item.getCode().equals(String.valueOf(ReviewStatusEnum.REVIEW_PASS.getCode()))
                    || item.getCode().equals(String.valueOf(ReviewStatusEnum.REVIEW_REJECTED.getCode()))).toList();
            data.put(CommonConstant.USER_REVIEW_REVIEW_STATUS, codeValueVOS);
            resp.setData(data);
        }
        return resp;
    }

}
