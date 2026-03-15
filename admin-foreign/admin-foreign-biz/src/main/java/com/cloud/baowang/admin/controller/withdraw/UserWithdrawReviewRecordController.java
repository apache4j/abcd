/*
package com.cloud.baowang.admin.controller.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.FileExportApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawReviewRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewDetailReqVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@AllArgsConstructor
@RequestMapping("user-withdraw-review-record/api")
@Tag(name = "资金-资金审核记录-会员提款审核记录")
public class UserWithdrawReviewRecordController {

    private final UserWithdrawReviewRecordApi userWithdrawReviewRecordApi;

    private final MinioUploadApi minioUploadApi;
    private final FileExportApi fileExportApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 订单状态
        List<Map<String, Object>> status = DepositWithdrawalOrderStatusEnum.getWithdrawReviewRecordList();

        List<DepositWithdrawalOrderStatusEnum> reviewRecordList = Arrays.asList(new DepositWithdrawalOrderStatusEnum[] { DepositWithdrawalOrderStatusEnum.SUCCEED ,DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT,  DepositWithdrawalOrderStatusEnum.SECOND_AUDIT_REJECT,
                DepositWithdrawalOrderStatusEnum.THIRD_AUDIT_REJECT,DepositWithdrawalOrderStatusEnum.BACKSTAGE_CANCEL,DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL}) ;

        List<CodeValueVO> statusList = reviewRecordList.stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", statusList);
        return ResponseVO.success(result);
    }



    @Operation(summary = "提款审核记录列表")
    @PostMapping("withdrawalReviewRecordPageList")
    public ResponseVO<Page<UserWithdrawReviewRecordVO>> withdrawalReviewRecordPageList(@RequestBody UserWithdrawReviewRecordPageReqVO vo){
        return ResponseVO.success(userWithdrawReviewRecordApi.withdrawalReviewRecordPageList(vo));
    }

    @Operation(summary = "提款审核记录详情")
    @PostMapping("withdrawReviewRecordDetail")
    public ResponseVO<UserWithdrawReviewDetailsVO> withdrawReviewRecordDetail(@RequestBody WithdrawReviewDetailReqVO vo){
        return ResponseVO.success(userWithdrawReviewRecordApi.withdrawReviewRecordDetail(vo));
    }



    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserWithdrawReviewRecordPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
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
                UserWithdrawReviewRecordVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(withdrawalReviewRecordPageList(param).getData().getRecords(), UserWithdrawReviewRecordVO.class));

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
*/
