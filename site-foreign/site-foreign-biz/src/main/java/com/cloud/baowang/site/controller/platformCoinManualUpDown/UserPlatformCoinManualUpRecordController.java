package com.cloud.baowang.site.controller.platformCoinManualUpDown;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.UserManualUpRecordExportVO;
import com.cloud.baowang.site.vo.export.platformCoinManual.UserPlatformCoinManualUpRecordExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserManualUpRecordApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualUpRecordApi;
import com.cloud.baowang.wallet.api.enums.PlatformCoinReviewStatusEnum;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordResult;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: kimi
 */
@Slf4j
@AllArgsConstructor
@Tag(name = "资金-会员资金记录-会员平台币上分记录")
@RestController
@RequestMapping("/user-platform-coin-manual-up-record/api")
public class UserPlatformCoinManualUpRecordController {
    private final SystemParamApi systemParamApi;
    private final UserPlatformCoinManualUpRecordApi userPlatformCoinManualUpRecordApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        ArrayList<String> types = new ArrayList<>();
        types.add(CommonConstant.PLATFORM_COIN_REVIEW_STATUS);
        types.add(CommonConstant.PLATFORM_COIN_MANUAL_ADJUST_UP_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(types);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> data = resp.getData();
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.PLATFORM_COIN_REVIEW_STATUS);
            List<Integer> needSave = new ArrayList<>();
            needSave.add(PlatformCoinReviewStatusEnum.REVIEW_PROGRESS.getCode());
            needSave.add(PlatformCoinReviewStatusEnum.REVIEW_PASS.getCode());
            needSave.add(PlatformCoinReviewStatusEnum.REVIEW_REJECTED.getCode());
            codeValueVOS = codeValueVOS.stream()
                    .filter(cv -> needSave.contains(Integer.parseInt(cv.getCode()))).toList();
            data.put(CommonConstant.PLATFORM_COIN_REVIEW_STATUS, codeValueVOS);
            resp.setData(data);
        }
        return resp;
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getUpRecordPage")
    public ResponseVO<UserPlatformCoinManualUpRecordResult> getUpRecordPage(@RequestBody UserPlatformCoinManualUpRecordPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userPlatformCoinManualUpRecordApi.getUpRecordPage(vo));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserPlatformCoinManualUpRecordPageVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::UserPlatformCoinManualUpRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = userPlatformCoinManualUpRecordApi.getUpRecordPageCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserPlatformCoinManualUpRecordExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(userPlatformCoinManualUpRecordApi.getUpRecordPage(param).getPageList().getRecords(), UserPlatformCoinManualUpRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_PLATFORM_COIN_MANUAL_UP_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }
}
