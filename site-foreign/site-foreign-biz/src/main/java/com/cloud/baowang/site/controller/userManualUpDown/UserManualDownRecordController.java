package com.cloud.baowang.site.controller.userManualUpDown;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.UserManualDownRecordExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserManualDownRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author:qiqi
 */
@Tag(name = "资金-会员资金记录-会员人工扣除记录")
@RestController
@RequestMapping("/user-manual-down-record/api")
@AllArgsConstructor
@Slf4j
public class UserManualDownRecordController {
    private final SystemParamApi systemParamApi;
    private final UserManualDownRecordApi userManualDownRecordApi;
    private final MinioUploadApi minioUploadApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取发起申请下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getActivityTemplateDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.MANUAL_ADJUST_DOWN_TYPE);
        param.add(CommonConstant.BALANCE_CHANGE_STATUS);
        param.add(CommonConstant.ACTIVITY_TEMPLATE);
        return systemParamApi.getSystemParamsByList(param);
    }

    @PostMapping("listUserManualDownRecordPage")
    @Operation(summary = "会员人工减额记录分页列表")
    public ResponseVO<UserManualDownRecordResponseVO> listUserManualDownRecordPage(@RequestBody UserManualDownRecordRequestVO userCoinRecordRequestVO) {
        userCoinRecordRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return userManualDownRecordApi.listUserManualDownRecordPage(userCoinRecordRequestVO);
    }

    @PostMapping("export")
    @Operation(summary = "会员人工扣除记录导出")
    public ResponseVO<?> export(@RequestBody UserManualDownRecordRequestVO vo, HttpServletResponse response) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userManualDownRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = userManualDownRecordApi.listUserManualDownRecordPageExportCount(vo);

        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserManualDownRecordExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(listUserManualDownRecordPage(param).getData().getRecords(), UserManualDownRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_MANUAL_DOWN_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());


    }


}
