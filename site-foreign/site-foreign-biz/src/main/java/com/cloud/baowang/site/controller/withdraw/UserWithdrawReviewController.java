package com.cloud.baowang.site.controller.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawReviewApi;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinUpReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("user-withdraw-review/api")
@Tag(name = "资金-资金审核-会员提款审核")

public class UserWithdrawReviewController {

    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SystemParamApi systemParamApi;

    private final UserWithdrawReviewApi userWithdrawReviewApi;


    @Operation(summary = "获取会员提现审核下拉框数据")
    @GetMapping("getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        ArrayList<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        param.add(CommonConstant.USER_WITHDRAW_REVIEW_OPERATION);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            String siteCode = CurrReqUtils.getSiteCode();
            List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(siteCode);
            currencyDownBox = currencyDownBox.stream().filter(item -> !CommonConstant.PLAT_CURRENCY_CODE.equals(item.getCode())).toList();
            Map<String, List<CodeValueVO>> data = resp.getData();
            data.put("currencyCode", currencyDownBox);
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.USER_WITHDRAW_REVIEW_OPERATION);
            Integer code = UserWithDrawReviewOperationEnum.CHECK.getCode();
            codeValueVOS.removeIf(codeValue -> codeValue.getCode().equals(String.valueOf(code)));
            data.put(CommonConstant.USER_WITHDRAW_REVIEW_OPERATION, codeValueVOS);
            resp.setData(data);
        }
        return resp;

    }


    @Operation(summary = "提款审核列表")
    @PostMapping("withdrawReviewPage")
    public ResponseVO<Page<UserWithdrawReviewPageResVO>> withdrawReviewPage(@Valid @RequestBody UserWithdrawReviewPageReqVO
                                                                                    userWithdrawReviewPageReqVO) {
        userWithdrawReviewPageReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userWithdrawReviewPageReqVO.setOperator(CurrReqUtils.getAccount());
        return ResponseVO.success(userWithdrawReviewApi.withdrawReviewPage(userWithdrawReviewPageReqVO));
    }

    @Operation(summary = "审核详情")
    @PostMapping("withdrawReviewDetail")
    public ResponseVO<UserWithdrawReviewDetailsVO> withdrawReviewDetail(@RequestBody WithdrawReviewDetailReqVO vo) {
        Boolean dataDesensity = CurrReqUtils.getDataDesensity();
        vo.setDataDesensitization(dataDesensity);
        return ResponseVO.success(userWithdrawReviewApi.withdrawReviewDetail(vo));
    }

    @Operation(summary = "一审锁定/解锁")
    @PostMapping("oneLockOrUnLock")
    public ResponseVO<Boolean> oneLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.oneLockOrUnLock(vo);
    }

    @Operation(summary = "挂单审核锁定/解锁")
    @PostMapping(value = "orderLockOrUnLock")
    public ResponseVO<Boolean> orderLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.orderLockOrUnLock(vo);
    }

    @Operation(summary = "待出款锁定/解锁")
    @PostMapping(value = "paymentLockOrUnLock")
    public ResponseVO<Boolean> paymentLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.paymentLockOrUnLock(vo);
    }


    @Operation(summary = "一审成功")
    @PostMapping(value = "oneReviewSuccess")
    public ResponseVO<Boolean> oneReviewSuccess(@RequestBody WithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.oneReviewSuccess(vo);
    }

    @Operation(summary = "一审拒绝")
    @PostMapping(value = "oneReviewFail")
    public ResponseVO<Boolean> oneReviewFail(@RequestBody WithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.oneReviewFail(vo);
    }

    @Operation(summary = "一审挂单")
    @PostMapping(value = "oneReviewOrder")
    public ResponseVO<Boolean> oneReviewOrder(@RequestBody WithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.oneReviewOrder(vo);
    }

    @Operation(summary = "挂单审核成功")
    @PostMapping(value = "orderReviewSuccess")
    public ResponseVO<Boolean> orderReviewSuccess(@RequestBody WithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.orderReviewSuccess(vo);
    }

    @Operation(summary = "挂单审核拒绝")
    @PostMapping(value = "orderReviewFail")
    public ResponseVO<Boolean> orderReviewFail(@RequestBody WithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.orderReviewFail(vo);
    }

    @GetMapping("getChannelByChannelTypeAndReviewId")
    @Operation(summary = "根据通道类型,system_param deposit_withdraw_channel 审核单据id获取通道列表")
    public ResponseVO<List<WithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(@RequestParam("depositWithdrawChannel") String depositWithdrawChannel,
                                                                                     @RequestParam("id") String id) {
        String siteCode = CurrReqUtils.getSiteCode();
        return userWithdrawReviewApi.getChannelByChannelTypeAndReviewId(depositWithdrawChannel, siteCode, id);

    }


    @Operation(summary = "(分配)-待出款成功")
    @PostMapping(value = "paymentReviewSuccess")
    public ResponseVO<Boolean> paymentReviewSuccess(@RequestBody WithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.paymentReviewSuccess(vo);
    }

    @Operation(summary = "待出款拒绝")
    @PostMapping(value = "paymentReviewFail")
    public ResponseVO<Boolean> paymentReviewFail(@RequestBody WithdrawReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.paymentReviewFail(vo);
    }
    @Operation(summary = "收款账户详情列表")
    @PostMapping(value = "/getAddressInfoList")
    public ResponseVO<Page<UserWithdrawReviewAddressResponseVO>> getAddressInfoList(@Valid @RequestBody WithdrawReviewAddressReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userWithdrawReviewApi.getAddressInfoList(vo);
    }

}
