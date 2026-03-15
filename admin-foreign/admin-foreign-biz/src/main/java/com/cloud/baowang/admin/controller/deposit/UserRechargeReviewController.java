package com.cloud.baowang.admin.controller.deposit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.utils.CommonAdminUtils;
import com.cloud.baowang.system.api.constant.AdminPermissionApiConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.wallet.api.api.UserRechargeReviewApi;
import com.cloud.baowang.wallet.api.enums.usercoin.UserRechargeReviewNumberEnum;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.userreview.EditAmountVO;
import com.cloud.baowang.wallet.api.vo.userreview.TwoSuccessVO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @des 会员充值人工确认
 * @author: wade
 */
@AllArgsConstructor
@Tag(name = "会员充值人工确认")
@RestController
@RequestMapping("/user-recharge-review/api")
public class UserRechargeReviewController {

    private final UserRechargeReviewApi userRechargeReviewApi;

    private final SystemParamApi systemParamApi;




    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        boolean waitOneReview = false;
        boolean waitBringMoney = false;
        List<String> adminApis = CommonAdminUtils.getAdminApis();
        if (null == adminApis) {
            waitOneReview = true;
            waitBringMoney = true;
        } else {
            if (adminApis.contains(AdminPermissionApiConstant.WAIT_ONE_REVIEW_)) {
                waitOneReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.WAIT_BRING_MONEY)) {
                waitBringMoney = true;
            }
        }

        // 锁单状态-下拉框
        ResponseVO<Map<String, List<CodeValueVO>>> lockStatusResponseVO =
                systemParamApi.getSystemParamsByList(List.of(CommonConstant.USER_REVIEW_LOCK_STATUS, CommonConstant.COMMISSION_OPERATION_STATUS));
        // 待一审、待入款
        List<CodeValueVO> reviewList = new ArrayList<>(UserRechargeReviewNumberEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList());
        if (!waitOneReview) {
            reviewList.remove(new CodeValueVO(
                    null,
                    UserRechargeReviewNumberEnum.WAIT_ONE_REVIEW.getCode().toString(),
                    UserRechargeReviewNumberEnum.WAIT_ONE_REVIEW.getName()));
        }
        if (!waitBringMoney) {
            reviewList.remove(new CodeValueVO(
                    null,
                    UserRechargeReviewNumberEnum.WAIT_BRING_MONEY.getCode().toString(),
                    UserRechargeReviewNumberEnum.WAIT_BRING_MONEY.getName()));
        }

        List<CodeValueVO> lockStatus = Lists.newArrayList();
        List<CodeValueVO> commissionOperation = Lists.newArrayList();
        if (lockStatusResponseVO.getCode() == ResultCode.SUCCESS.getCode()) {
            lockStatus = lockStatusResponseVO.getData().get(CommonConstant.COMMISSION_OPERATION_STATUS);
            commissionOperation = lockStatusResponseVO.getData().get(CommonConstant.COMMISSION_OPERATION).stream().filter( item -> !item.getCode().equals(CommonConstant.business_two_str)).toList();
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("lockStatus", lockStatus);
        result.put("reviewList", reviewList);
        result.put("commissionOperation", commissionOperation);

        return ResponseVO.success(result);
    }

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = "/rechargeLock")
    public ResponseVO<?> rechargeLock(@Valid @RequestBody WalletStatusVO vo) {
        return userRechargeReviewApi.rechargeLock(vo, CurrReqUtils.getAccount(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审通过")
    @PostMapping(value = "/oneSuccess")
    public ResponseVO<?> oneSuccess(@Valid @RequestBody WalletReviewVO vo) {
        return userRechargeReviewApi.oneSuccess(vo, CurrReqUtils.getAccount(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "充值编辑确认")
    @PostMapping(value = "/editAmount")
    public ResponseVO<?> editAmount(@Valid @RequestBody EditAmountVO vo) {
        return userRechargeReviewApi.editAmount(vo, CurrReqUtils.getAccount(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审/二审拒绝")
    @PostMapping(value = "/oneFail")
    public ResponseVO<?> oneFail(@Valid @RequestBody WalletReviewVO vo) {
        return userRechargeReviewApi.oneFail(vo, CurrReqUtils.getAccount(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "审核列表")
    @PostMapping(value = "/getReviewPage")
    public ResponseVO<Page<UserRechargeReviewResponseVO>> getReviewPage(@Valid @RequestBody UserRechargeReviewPageVO vo) {
        LoginAdmin loginAdmin = CommonAdminUtils.getLoginAdmin();
        vo.setDataDesensitization(loginAdmin.getDataDesensitization());
        return ResponseVO.success(userRechargeReviewApi.getReviewPage(vo, CurrReqUtils.getAccount()));
    }

    @Operation(summary = "待入款-锁单或解锁")
    @PostMapping(value = "/rechargeLock2")
    public ResponseVO<?> rechargeLock2(@Valid @RequestBody WalletStatusVO vo) {
        return userRechargeReviewApi.rechargeLock2(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "充值上分确认")
    @PostMapping(value = "/twoSuccess")
    public ResponseVO<?> twoSuccess(@Valid @RequestBody TwoSuccessVO vo) {
        return userRechargeReviewApi.twoSuccess(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }
}
