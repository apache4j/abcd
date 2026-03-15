package com.cloud.baowang.admin.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.admin.service.FinanceConfirmService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.api.vo.StatusVO;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "资金-资金确认")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/finance-confirm/api")
public class FinanceConfirmController {

    private final FinanceConfirmService financeConfirmService;

    private final SystemParamApi systemParamApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 锁单状态
        ResponseVO<List<CodeValueVO>> lockStatusResponseVO =
                systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_LOCK_STATUS);
        // 币种
        ResponseVO<List<CodeValueVO>> currencyVO =
                systemParamApi.getSystemParamByType(CommonConstant.COIN_CODE);
        List<CodeValueVO> lockStatus = Lists.newArrayList();
        if (lockStatusResponseVO.getCode() == ResultCode.SUCCESS.getCode()) {
            lockStatus = lockStatusResponseVO.getData();
        }
        List<CodeValueVO> currency = Lists.newArrayList();
        if (currencyVO.getCode() == ResultCode.SUCCESS.getCode()) {
            currency = currencyVO.getData();
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("lockStatus", lockStatus);
        result.put("currency", currency);

        return ResponseVO.success(result);
    }


    @Operation(summary = "会员提款人工确认-分页查询")
    @PostMapping("/manualConfirmOfMemberWithdrawPage")
    public ResponseVO<Page<FinanceManualConfirmVO>> manualConfirmMemberWithdrawPage(@Valid @RequestBody FinanceManualConfirmQueryVO requestVO) {
        return financeConfirmService.manualConfirmMemberWithdrawPage(requestVO, CurrReqUtils.getAccount());
    }

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = "/withdrawManualLock")
    public ResponseVO<Boolean> withdrawManualLock(@Valid @RequestBody WalletStatusVO vo) {
        return financeConfirmService.withdrawManualLock(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审通过")
    @PostMapping(value = "/withdrawManualOneSuccess")
    public ResponseVO<Boolean> withdrawManualOneSuccess(@Valid @RequestBody WalletReviewVO vo) {
        return financeConfirmService.withdrawManualOneSuccess(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }


    @Operation(summary = "一审拒绝")
    @PostMapping(value = "/withdrawManualOneFail")
    public ResponseVO<Boolean> withdrawManualOneFail(@Valid @RequestBody WalletReviewVO vo) {
        return financeConfirmService.withdrawManualOneFail(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }


}
