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
import com.cloud.baowang.user.api.api.UserInformationChangeApi;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeReqVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Tag(name = "会员-会员信息变更记录")
@RestController
@RequestMapping(value = "/user-info-change/api")
@AllArgsConstructor
public class UserInformationChangeController {



    private final UserInformationChangeApi userInformationChangeApi;

    private final MinioUploadApi minioUploadApi;

    @Schema(description = "分页查询")
    @PostMapping("/getUserInformationChange")
    public ResponseVO<Page<UserInformationChangeResVO>> getUserInformationChange(@RequestBody UserInformationChangeReqVO userInformationChangeReqVO) {
        userInformationChangeReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userInformationChangeReqVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        //userInformationChangeReqVO.setDataDesensitization(CurrentRequestUtils.getDataDesensity());
        return userInformationChangeApi.getUserInformationChange(userInformationChangeReqVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/getUserInformationChangeExport")
    public ResponseVO<?> export(@RequestBody UserInformationChangeReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::userInformationChangeController::getUserInformationChange::"+ CurrReqUtils.getSiteCode()+ adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long responseVO = userInformationChangeApi.getUserInformationChangeCount(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserInformationChangeResVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(getUserInformationChange(param).getData().getRecords(), UserInformationChangeResVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_INFORMATION_CHANGE_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());

    }
}
