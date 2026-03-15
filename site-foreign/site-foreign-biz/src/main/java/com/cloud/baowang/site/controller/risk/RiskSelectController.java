package com.cloud.baowang.site.controller.risk;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.UserManualDownRecordExportVO;
import com.cloud.baowang.site.vo.export.withdraw.UserWithdrawRecordExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.risk.RiskWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static com.cloud.baowang.common.core.constants.RedisConstants.SITE_USER_DUPLICATE;

@RestController
@Tag(name = "风控查询")
@RequestMapping("/risk/inquiry")
@AllArgsConstructor
@Slf4j
public class RiskSelectController {

    private final UserWithdrawRecordApi userWithdrawRecordApi;
    private final MinioUploadApi minioUploadApi;




    @PostMapping("/selectWithdrawalRecordDuplicateList")
    @Operation(summary = "风控查询-会员提款重复项查询")
    public ResponseVO<Page<RiskWithdrawRecordVO>> selectWithdrawalRecordDuplicateList(@RequestBody UserWithdrawalRecordRequestVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String adminId = CurrReqUtils.getOneId();
        reqVO.setSiteCode(siteCode);
        String key = String.format(SITE_USER_DUPLICATE, siteCode, adminId);
        RedisUtil.setValue(key, reqVO.getDuplicate()+"");
        reqVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return ResponseVO.success(userWithdrawRecordApi.getWithdrawalRecordDuplicateList(reqVO));
    }

    @PostMapping("/withdrawalRecordDuplicateStatus")
    @Operation(summary = "风控查询-会员提款重复状态")
    public ResponseVO<Integer> withdrawalRecordDuplicateStatus(@RequestBody UserWithdrawalRecordRequestVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String adminId = CurrReqUtils.getOneId();
        String key = String.format(SITE_USER_DUPLICATE, siteCode, adminId);
        Object value = RedisUtil.getValue(key);
        if (value==null || value.toString().isEmpty()){
            return ResponseVO.success(0);
        }else {
            return ResponseVO.success(Integer.parseInt(value.toString()));
        }
    }

    @PostMapping("/withdrawalRecordDuplicateStatusSave")
    @Operation(summary = "风控查询-会员提款重复状态保存")
    public ResponseVO<Integer> withdrawalRecordDuplicateStatusSave(@RequestBody UserWithdrawalRecordRequestVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String adminId = CurrReqUtils.getOneId();
        String key = String.format(SITE_USER_DUPLICATE, siteCode, adminId);
        RedisUtil.setValue(key, reqVO.getDuplicate()+"");
        return ResponseVO.success(reqVO.getDuplicate());
    }


    @PostMapping("export")
    @Operation(summary = "风控查询-会员提款重复项查询导出")
    public ResponseVO<?> export(@RequestBody UserWithdrawalRecordRequestVO vo, HttpServletResponse response) {
        String adminId = CurrReqUtils.getOneId();
        vo.setDataDesensitization(CurrReqUtils.getDataDesensity());
        String uniqueKey = "tableExport::site::Control::selectWithdrawalRecordDuplicateList::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn("{}秒后才能导出下载", remain);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.QUERY_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setPageSize(10000);

        long withdrawalRecordDuplicateListCount = userWithdrawRecordApi.getWithdrawalRecordDuplicateListCount(vo);

        if (withdrawalRecordDuplicateListCount==0) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserWithdrawRecordExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), withdrawalRecordDuplicateListCount),
                param -> ConvertUtil.entityListToModelList(selectWithdrawalRecordDuplicateList(param).getData().getRecords(), UserWithdrawRecordExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_WITHDRAW_SUCCESS_RECORD_RICK)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());


    }

}
