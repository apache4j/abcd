package com.cloud.baowang.site.controller.deposit;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.userManual.UserManualDepositReviewRecordExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.api.UserManualDepositApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserManualDepositRecordPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserManualDepositRecordPageResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: qiqi
 */
@Tag(name = "资金-资金审核记录-会员人工存款审核记录")
@AllArgsConstructor
@RestController
@RequestMapping("/userDepositReviewRecord/api")
public class UserDepositReviewRecordController {

    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SystemParamApi systemParamApi;
    private final SystemRechargeWayApi wayApi;
    private final UserManualDepositApi userManualDepositApi;

    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.DEPOSIT_WITHDRAW_STATUS);

        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi.getSystemParamsByList(param);
        if (responseVO.isOk()) {
            Map<String, List<CodeValueVO>> data = responseVO.getData();
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
            if (CollectionUtil.isNotEmpty(codeValueVOS)) {
                //订单状态筛选 失败100 成功101
                List<String> statusList = new ArrayList<>();
                statusList.add(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
                Set<String> statusSet = Set.copyOf(statusList);
                codeValueVOS = codeValueVOS.stream()
                        .filter(codeValue -> statusSet.contains(codeValue.getCode()))
                        .toList();
                data.put(CommonConstant.DEPOSIT_WITHDRAW_STATUS, codeValueVOS);
            }
            List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
            currencyDownBox = currencyDownBox.stream()
                    //过滤平台币
                    .filter(codeValueVO -> !CommonConstant.PLAT_CURRENCY_CODE.equals(codeValueVO.getCode()))
                    .collect(Collectors.toList());
            data.put("currency_code", currencyDownBox);
            //充值方式
            List<CodeValueVO> rechargeWayListBySiteCode = wayApi.getRechargeWayListBySiteCode(CurrReqUtils.getSiteCode());
            if (CollectionUtil.isNotEmpty(rechargeWayListBySiteCode)) {
                data.put("deposit_withdraw_way", rechargeWayListBySiteCode);
            }
        }
        return responseVO;
    }

    @Operation(summary = "人工充值审核记录列表")
    @PostMapping("userManualDepositRecordPage")
    public ResponseVO<Page<UserManualDepositRecordPageResVO>> userManualDepositRecordPage(@Valid @RequestBody UserManualDepositRecordPageReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return ResponseVO.success(userManualDepositApi.userManualDepositRecordPage(vo));
    }
    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserManualDepositRecordPageReqVO vo) {
        String uniqueKey = "tableExport::user::userManualDepositReviewRecord::"+CurrReqUtils.getAccount();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = userManualDepositApi.userManualDepositReviewRecordExportCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserManualDepositReviewRecordExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(userManualDepositApi.userManualDepositRecordPage(param).getRecords(), UserManualDepositReviewRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_MANUAL_DEPOSIT_REVIEW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }


}
