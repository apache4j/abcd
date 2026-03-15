package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.user.UserRegistrationInfoResVO;
import com.cloud.baowang.user.api.vo.user.request.UserRegistrationInfoReqVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.user.api.api.UserRegistrationInfoApi;
import com.cloud.baowang.user.api.vo.user.excel.UserRegistrationInfoExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author kimi
 */
@Tag(name = "会员注册信息")
@RestController
@RequestMapping(value = "/user-registration-info/api")
@AllArgsConstructor
public class UserRegistrationInfoController {

    private final UserRegistrationInfoApi userRegistrationInfoApi;

    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "会员注册信息")
    @PostMapping("/getRegistrationInfo")
    public ResponseVO<Page<UserRegistrationInfoResVO>> getRegistrationInfo(@RequestBody UserRegistrationInfoReqVO userRegistrationInfoReqVO) {
        userRegistrationInfoReqVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        userRegistrationInfoReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return userRegistrationInfoApi.getRegistrationInfo(userRegistrationInfoReqVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserRegistrationInfoReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::getRegistrationInfo::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            //return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        Long responseVO = userRegistrationInfoApi.getTotalCount(vo);
        responseVO = responseVO == null ? 0L : responseVO;

        vo.setPageSize(responseVO.intValue());
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserRegistrationInfoExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(getRegistrationInfo(param).getData().getRecords(), UserRegistrationInfoExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_REGISTRATION_LIST)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

}
