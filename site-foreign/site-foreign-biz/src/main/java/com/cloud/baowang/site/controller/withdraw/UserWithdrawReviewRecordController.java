package com.cloud.baowang.site.controller.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.withdraw.UserWithdrawReviewExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawReviewRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewDetailReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@AllArgsConstructor
@RequestMapping("user-withdraw-review-record/api")
@Tag(name = "资金-资金审核记录-会员提款审核记录")
public class UserWithdrawReviewRecordController {

    private final UserWithdrawReviewRecordApi userWithdrawReviewRecordApi;
    private final SystemParamApi systemParamApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        ResponseVO<List<CodeValueVO>> resp = systemParamApi.getSystemParamByType(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> result = new HashMap<>();
            List<CodeValueVO> currencyCodeDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
            List<String> statusParam = new ArrayList<>();
            List<CodeValueVO> statusData = resp.getData();
            statusParam.add(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode());
            statusParam.add(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT_REJECT.getCode());
            statusParam.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_REJECT.getCode());
            /*statusParam.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_SUCCESS.getCode());*/
            statusParam.add(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
            statusParam.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL.getCode());
            statusParam.add(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            //只返回记录列表需要的几个审核状态
            statusData = statusData.stream()
                    .filter(codeValue -> statusParam.contains(codeValue.getCode()))
                    .toList();
            result.put("status", statusData);
            result.put("currency_code", currencyCodeDownBox);
            return ResponseVO.success(result);
        }
        return ResponseVO.success();
    }


    @Operation(summary = "提款审核记录列表")
    @PostMapping("withdrawalReviewRecordPageList")
    public ResponseVO<Page<UserWithdrawReviewRecordVO>> withdrawalReviewRecordPageList(@RequestBody UserWithdrawReviewRecordPageReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        if (vo.getReviewOperation() == null) {
            vo.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        }
        return ResponseVO.success(userWithdrawReviewRecordApi.withdrawalReviewRecordPageList(vo));
    }

    @Operation(summary = "提款审核记录详情")
    @PostMapping("withdrawReviewRecordDetail")
    public ResponseVO<UserWithdrawReviewDetailsVO> withdrawReviewRecordDetail(@RequestBody WithdrawReviewDetailReqVO vo) {
        return ResponseVO.success(userWithdrawReviewRecordApi.withdrawReviewRecordDetail(vo));
    }


    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserWithdrawReviewRecordPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        if (vo.getReviewOperation() == null) {
            vo.setReviewOperation(UserWithDrawReviewOperationEnum.CHECK.getCode());
        }
        String uniqueKey = "tableExport::centerControl::userWithdrawReviewRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long responseVO = userWithdrawReviewRecordApi.withdrawalReviewRecordPageListCount(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserWithdrawReviewExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(withdrawalReviewRecordPageList(param).getData().getRecords(), UserWithdrawReviewExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_WITHDRAW_REVIEW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
