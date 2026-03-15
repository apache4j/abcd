package com.cloud.baowang.site.controller.user;

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
import com.cloud.baowang.site.vo.export.UserLabelRecordExportVO;
import com.cloud.baowang.user.api.api.SiteUserLabelRecordApi;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsReqVO;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 会员标签变更记录
 */
@Tag(name = "会员-会员管理-会员标签变更记录")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/user-label-record/api")
public class UserLabelRecordController {
    private final SiteUserLabelRecordApi siteUserLabelRecordApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "会员标签变更记录分页数据")
    @PostMapping("/selectLabelRecord")
    public ResponseVO<Page<UserLabelRecordsResVO>> getUserLabelRecords(@RequestBody UserLabelRecordsReqVO reqVO) {
        return siteUserLabelRecordApi.getUserLabelRecords(reqVO);

    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserLabelRecordsReqVO reqVO) {
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::userLabelRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> responseVO = siteUserLabelRecordApi.getTotalCount(reqVO);
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
                UserLabelRecordExportVO.class,
                reqVO,
                4,
                ExcelUtil.getPages(reqVO.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(getUserLabelRecords(param).getData().getRecords(), UserLabelRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_LABEL_RECORD)
                        .adminId(adminId)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
