package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewDetailsVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewPageResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawCancelVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewAddressReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewDetailReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.WithdrawReviewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserWithdrawReviewApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员提款审核 服务")
public interface UserWithdrawReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/userWithdrawReview/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "withdrawalRecordPageList")
    Page<UserWithdrawReviewPageResVO> withdrawReviewPage(@RequestBody UserWithdrawReviewPageReqVO vo);


    @Operation(summary = "审核详情")
    @PostMapping(value = PREFIX + "withdrawReviewDetail")
    UserWithdrawReviewDetailsVO withdrawReviewDetail(@RequestBody WithdrawReviewDetailReqVO vo);


    @Operation(summary = "一审锁定/解锁")
    @PostMapping(value = PREFIX + "oneLockOrUnLock")
    ResponseVO<Boolean> oneLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo);

    @Operation(summary = "挂单审核锁定/解锁")
    @PostMapping(value = PREFIX + "twoLockOrUnLock")
    ResponseVO<Boolean> orderLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo);

    @Operation(summary = "待出款锁定/解锁")
    @PostMapping(value = PREFIX + "threeLockOrUnLock")
    ResponseVO<Boolean> paymentLockOrUnLock(@RequestBody UserWithdrawReviewLockOrUnLockVO vo);

    @Operation(summary = "一审成功")
    @PostMapping(value = PREFIX + "oneReviewSuccess")
    ResponseVO<Boolean> oneReviewSuccess(@RequestBody WithdrawReviewReqVO vo);

    @Operation(summary = "一审拒绝")
    @PostMapping(value = PREFIX + "oneReviewFail")
    ResponseVO<Boolean> oneReviewFail(@RequestBody WithdrawReviewReqVO vo);

    @Operation(summary = "一审挂单")
    @PostMapping(value = PREFIX + "oneReviewOrder")
    ResponseVO<Boolean> oneReviewOrder(@RequestBody WithdrawReviewReqVO vo);

    @Operation(summary = "挂单审核成功")
    @PostMapping(value = PREFIX + "twoReviewSuccess")
    ResponseVO<Boolean> orderReviewSuccess(@RequestBody WithdrawReviewReqVO vo);

    @Operation(summary = "挂单审核拒绝")
    @PostMapping(value = PREFIX + "twoReviewFail")
    ResponseVO<Boolean> orderReviewFail(@RequestBody WithdrawReviewReqVO vo);

    @Operation(summary = "(分配)-待出款成功")
    @PostMapping(value = PREFIX + "threeReviewSuccess")
    ResponseVO<Boolean> paymentReviewSuccess(@RequestBody WithdrawReviewReqVO vo);


    @Operation(summary = "待出款拒绝")
    @PostMapping(value = PREFIX + "threeReviewFail")
    ResponseVO<Boolean> paymentReviewFail(@RequestBody WithdrawReviewReqVO vo);

    @GetMapping("getChannelByChannelTypeAndReviewId")
    @Operation(summary = "根据通道类型,system_param deposit_withdraw_channel 审核单据id获取通道列表")
    ResponseVO<List<WithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(@RequestParam("depositWithdrawChannel") String depositWithdrawChannel,
                                                                              @RequestParam("siteCode") String siteCode,
                                                                              @RequestParam("id") String id);
    @GetMapping(PREFIX+"getTotalPendingReviewBySiteCode")
    @Operation(summary = "获取当前站点全部待审核会员提款记录")
    long getTotalPendingReviewBySiteCode(@RequestParam("siteCode") String siteCode);

    @PostMapping(PREFIX+"getAddressInfoList")
    @Operation(summary = "获取收款账户详情列表")
    ResponseVO<Page<UserWithdrawReviewAddressResponseVO>> getAddressInfoList(@RequestBody WithdrawReviewAddressReqVO vo);
}
