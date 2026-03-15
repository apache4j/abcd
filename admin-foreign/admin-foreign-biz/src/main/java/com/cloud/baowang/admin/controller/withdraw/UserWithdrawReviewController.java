/*
package com.cloud.baowang.admin.controller.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.utils.CommonAdminUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.constant.AdminPermissionApiConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.usercoin.UserWithdrawReviewNumberEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.wallet.api.api.UserWithdrawReviewApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewPageResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawCancelVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewDetailReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewReqVO;
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

*/
/**
 * @author qiqi
 *//*

@RestController
@AllArgsConstructor
@RequestMapping("user-withdraw-review/api")
@Tag(name = "资金-资金审核-会员提款审核")

public class UserWithdrawReviewController {

    private final SystemParamApi systemParamApi;

    private final UserWithdrawReviewApi userWithdrawReviewApi;


    @Operation(summary = "获取会员提现审核下拉框数据")
    @PostMapping("getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox(){

        ResponseVO<Map<String, List< CodeValueVO>>> responseVO = systemParamApi
                .getSystemParamsByList(List.of(CommonConstant.USER_REVIEW_LOCK_STATUS,CommonConstant.DEPOSIT_WITHDRAW_CHANNEL,CommonConstant.THREE_PARTY_MESSAGE_STATUS));

        List<String> adminApis = CommonAdminUtils.getAdminApis();
        boolean waitOneReview = false;
        boolean waitTwoReview = false;
        boolean waitThreeReview = false;
        boolean waitWithdraw = false;
        if(null == adminApis ){
             waitOneReview = true;
             waitTwoReview = true;
             waitThreeReview = true;
             waitWithdraw = true;
        }else{
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_ONE_REVIEW)) {
                waitOneReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_TWO_REVIEW)) {
                waitTwoReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_THIRD_REVIEW)) {
                waitThreeReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.WITHDRAW_WAIT_WITHDRAW)) {
                waitWithdraw = true;
            }
        }
        // 待一审、待二审，待三审，待出款
        List<CodeValueVO> reviewList = new ArrayList<>(UserWithdrawReviewNumberEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList());
        if (!waitOneReview) {
            reviewList.remove(new CodeValueVO(
                    null,
                    UserWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode().toString(),
                    UserWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getName()));
        }
        if (!waitTwoReview) {
            reviewList.remove(new CodeValueVO(
                    null,
                    UserWithdrawReviewNumberEnum.WAIT_TWO_REVIEW.getCode().toString(),
                    UserWithdrawReviewNumberEnum.WAIT_TWO_REVIEW.getName()));
        }

        if (!waitThreeReview) {
            reviewList.remove(new CodeValueVO(
                    null,
                    UserWithdrawReviewNumberEnum.WAIT_THIRD_REVIEW.getCode().toString(),
                    UserWithdrawReviewNumberEnum.WAIT_THIRD_REVIEW.getName()));
        }
        if (!waitWithdraw) {
            reviewList.remove(new CodeValueVO(
                    null,
                    UserWithdrawReviewNumberEnum.WAIT_PAY_OUT.getCode().toString(),
                    UserWithdrawReviewNumberEnum.WAIT_PAY_OUT.getName()));
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("review_list", reviewList);
        map.putAll(responseVO.getData());
        return ResponseVO.success(map);
    }

    @Operation(summary = "提款审核列表")
    @PostMapping("withdrawReviewPage")
    public ResponseVO<Page<UserWithdrawReviewPageResVO>> withdrawReviewPage(@Valid @RequestBody UserWithdrawReviewPageReqVO
                                                                                    userWithdrawReviewPageReqVO){
        userWithdrawReviewPageReqVO.setCurrentAdminId(CurrReqUtils.getAccount());
        userWithdrawReviewPageReqVO.setCurrentAdminName(CurrReqUtils.getAccount());
        return  ResponseVO.success(userWithdrawReviewApi.withdrawReviewPage(userWithdrawReviewPageReqVO));
    }

    @Operation(summary = "审核详情")
    @PostMapping("withdrawReviewDetail")
    public ResponseVO<UserWithdrawReviewDetailsVO> withdrawReviewDetail(@RequestBody WithdrawReviewDetailReqVO vo){
        LoginAdmin loginAdmin = CommonAdminUtils.getLoginAdmin();
        vo.setDataDesensitization(loginAdmin.getDataDesensitization());
        return ResponseVO.success(userWithdrawReviewApi.withdrawReviewDetail(vo));
    }

    @Operation(summary = "一审锁定/解锁")
    @PostMapping("oneLockOrUnLock")
    public ResponseVO<Boolean> oneLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(userWithdrawReviewApi.oneLockOrUnLock(vo));
    }

    @Operation(summary = "二审锁定/解锁")
    @PostMapping(value =  "twoLockOrUnLock")
    public ResponseVO<Boolean> twoLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(userWithdrawReviewApi.twoLockOrUnLock(vo));
    }

    @Operation(summary = "三审锁定/解锁")
    @PostMapping(value =  "threeLockOrUnLock")
    public ResponseVO<Boolean> threeLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(userWithdrawReviewApi.threeLockOrUnLock(vo));
    }

    @Operation(summary = "待出款锁定/解锁")
    @PostMapping(value = "withdrawAuditLockOrUnLock")
    public ResponseVO<Boolean> withdrawAuditLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(userWithdrawReviewApi.withdrawAuditLockOrUnLock(vo));
    }

    @Operation(summary = "一审成功")
    @PostMapping(value =  "oneReviewSuccess")
    public ResponseVO oneReviewSuccess(@RequestBody WithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.oneReviewSuccess(vo);
    }

    @Operation(summary = "二审成功")
    @PostMapping(value =  "twoReviewSuccess")
    public ResponseVO twoReviewSuccess(@RequestBody WithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.twoReviewSuccess(vo);
    }

    @Operation(summary = "三审成功")
    @PostMapping(value =  "threeReviewSuccess")
    public ResponseVO threeReviewSuccess(@RequestBody WithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.threeReviewSuccess(vo);
    }

    @Operation(summary = "一审拒绝")
    @PostMapping(value =  "oneReviewFail")
    public ResponseVO oneReviewFail(@RequestBody WithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.oneReviewFail(vo);
    }


    @Operation(summary = "二审拒绝")
    @PostMapping(value =  "twoReviewFail")
    public ResponseVO twoReviewFail(@RequestBody WithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.twoReviewFail(vo);
    }

    @Operation(summary = "三审拒绝")
    @PostMapping(value =  "threeReviewFail")
    public ResponseVO threeReviewFail(@RequestBody WithdrawReviewReqVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return userWithdrawReviewApi.threeReviewFail(vo);
    }

    @Operation(summary = "出款取消")
    @PostMapping(value =  "withdrawCancel")
    public ResponseVO<Boolean> withdrawCancel(@RequestBody WithdrawCancelVO vo){
        vo.setCurrentAdminId(CurrReqUtils.getAccount());
        vo.setCurrentAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(userWithdrawReviewApi.withdrawCancel(vo));
    }

}
*/
