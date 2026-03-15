package com.cloud.baowang.site.controller.platformCoinManualUpDown;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.platformCoinManual.UserPlatformCoinManualReviewRecordExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualUpReviewApi;
import com.cloud.baowang.wallet.api.api.UserPlatformManualUpReviewRecordApi;
import com.cloud.baowang.wallet.api.enums.PlatformCoinReviewStatusEnum;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewRecordResponseResultVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinUpReviewDetailsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: kimi
 */
@Tag(name = "资金-资金审核记录-会员平台币上分审核记录")
@RestController
@RequestMapping("/user-platform-coin-manual-up-review-record/api")
@RequiredArgsConstructor
public class UserPlatformCoinManualUpReviewRecordController {
    private final SystemParamApi systemParamApi;
    private final UserPlatformManualUpReviewRecordApi userPlatformManualUpReviewRecordApi;
    private final UserPlatformCoinManualUpReviewApi userPlatformCoinManualUpReviewApi;
    private final MinioUploadApi minioUploadApi;



    @Operation(summary = "审核状态下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        ResponseVO<List<CodeValueVO>> resp = systemParamApi.getSystemParamByType(CommonConstant.PLATFORM_COIN_REVIEW_STATUS);
        if (resp.isOk()) {
            List<CodeValueVO> codeValueVOS = resp.getData();
            List<Integer> needSave = new ArrayList<>();
            needSave.add(PlatformCoinReviewStatusEnum.REVIEW_PASS.getCode());
            needSave.add(PlatformCoinReviewStatusEnum.REVIEW_REJECTED.getCode());
            codeValueVOS = codeValueVOS.stream()
                    .filter(cv -> needSave.contains(Integer.parseInt(cv.getCode()))).toList();
            resp.setData(codeValueVOS);
        }
        return resp;
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getRecordPage")
    public ResponseVO<Page<UserPlatformCoinManualUpReviewRecordResponseResultVO>> getRecordPage(@RequestBody UserPlatformCoinManualUpReviewRecordPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userPlatformManualUpReviewRecordApi.getRecordPage(vo));
    }
    @Operation(summary = "审核详情")
    @PostMapping(value = "/getReviewDetail")
    public ResponseVO<UserPlatformCoinUpReviewDetailsVO> getReviewDetail(@Valid @RequestBody IdVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userPlatformCoinManualUpReviewApi.getUpReviewDetails(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserPlatformCoinManualUpReviewRecordPageVO vo, HttpServletResponse response) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::UserPlatformCoinManualUpReviewRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);

            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = userPlatformManualUpReviewRecordApi.getTotalCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        Long data = responseVO.getData();
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserPlatformCoinManualReviewRecordExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), data),
                param -> ConvertUtil.entityListToModelList(getRecordPage(param).getData().getRecords(), UserPlatformCoinManualReviewRecordExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_PLATFORM_COIN_MANUAL_UP_REVIEW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
