package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.vo.export.UserLabelConfigRecordExportVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigRecordApi;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageReqVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 标签配置变更记录
 */
@Tag(name = "会员-会员管理-标签配置变更记录")
@RestController
@AllArgsConstructor
@RequestMapping("/user-label-config-record/api")
public class UserLabelConfigRecordController {
    private final SiteUserLabelConfigRecordApi siteUserLabelConfigRecordApi;
    private final SystemParamApi systemParamApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<CodeValueResVO> getDownBox() {
        CodeValueResVO vo = new CodeValueResVO();
        vo.setCodeValues(systemParamApi.getSystemParamByType(CommonConstant.USER_LABEL_CHANGE_TYPE).getData());
        vo.setType("labelChangeType");
        return ResponseVO.success(vo);
    }

    @Operation(summary = "标签配置变更记录分页查询")
    @PostMapping(value = "/getLabelConfigRecordPage")
    public ResponseVO<Page<UserLabelConfigRecordPageResVO>> getLabelConfigRecordPage(@RequestBody UserLabelConfigRecordPageReqVO vo) {
        return ResponseVO.success(siteUserLabelConfigRecordApi.getLabelConfigRecordPage(vo));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserLabelConfigRecordPageReqVO reqVO) {
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::userLabelConfigRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> responseVO = siteUserLabelConfigRecordApi.getTotalCount(reqVO);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        reqVO.setPageSize(responseVO.getData().intValue());
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserLabelConfigRecordExportVO.class,
                reqVO,
                4,
                ExcelUtil.getPages(reqVO.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(getLabelConfigRecordPage(param).getData().getRecords(), UserLabelConfigRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.LABEL_CONFIG_CHANGE_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(adminId)
                        .build());
    }
}
