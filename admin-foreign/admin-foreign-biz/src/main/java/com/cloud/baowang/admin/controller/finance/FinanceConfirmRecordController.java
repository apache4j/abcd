package com.cloud.baowang.admin.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.admin.service.FinanceConfirmService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.FinanceConfirmApi;
import com.cloud.baowang.wallet.api.enums.wallet.UserWithdrawalReviewNumberEnum;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "资金-资金确认记录")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/finance-confirm-record/api")
public class FinanceConfirmRecordController {

    private final FinanceConfirmService financeConfirmService;

    private final SystemParamApi systemParamApi;
    private final FinanceConfirmApi financeConfirmApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 订单状态
        List<CodeValueVO> orderStatus = Lists.newArrayList();
        for (UserWithdrawalReviewNumberEnum value : UserWithdrawalReviewNumberEnum.values()) {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setCode(value.getCode().toString());
            codeValueVO.setValue(value.getName());
            orderStatus.add(codeValueVO);
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("orderStatus", orderStatus);
        return ResponseVO.success(result);
    }

    @Operation(summary = "会员提款人工确记录-分页查询")
    @PostMapping("/manualConfirmMemberWithdrawRecPage")
    public ResponseVO<Page<FinanceManualConfirmRecordVO>> manualConfirmMemberWithdrawRecPage(@Valid @RequestBody FinanceManualConfirmRecordQueryVO requestVO) {
        return financeConfirmService.manualConfirmMemberWithdrawRecPage(requestVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody FinanceManualConfirmRecordQueryVO vo, HttpServletResponse response) {
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::financeConfirm::" + CommonConstant.ADMIN_CENTER_SITE_CODE + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = financeConfirmApi.manualConfirmMemberWithdrawRecCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        byte[] byteArray = ExcelUtil.writeForParallel(FinanceManualConfirmRecordVO.class, vo, 4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> financeConfirmApi.manualConfirmMemberWithdrawRecPage(param).getData().getRecords());

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.MEMBER_WITHDRAWAL_MANUAL_CONFIRM_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }


}
