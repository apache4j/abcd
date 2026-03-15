package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.userreview.EditAmountVO;
import com.cloud.baowang.wallet.api.vo.userreview.TwoSuccessVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserRechargeReviewApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员充值人工确认 服务")
public interface UserRechargeReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/userRechargeReview/api";

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = PREFIX + "/rechargeLock")
    ResponseVO<?> rechargeLock(@RequestBody WalletStatusVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "一审通过")
    @PostMapping(value = PREFIX + "/oneSuccess")
    ResponseVO<?> oneSuccess(@RequestBody WalletReviewVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    //editAmount
    @Operation(summary = "充值编辑确认")
    @PostMapping(value = PREFIX + "/editAmount")
    ResponseVO<?> editAmount(@RequestBody EditAmountVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);
    @Operation(summary = "一审/二审拒绝")
    @PostMapping(value = PREFIX + "/oneFail")
    ResponseVO<?> oneFail(@RequestBody WalletReviewVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "审核列表")
    @PostMapping(value = PREFIX + "/getReviewPage")
    Page<UserRechargeReviewResponseVO> getReviewPage(@RequestBody UserRechargeReviewPageVO vo,
                                                     @RequestParam("adminName") String adminName);

    @Operation(summary = "待入款-锁单或解锁")
    @PostMapping(value = PREFIX + "/rechargeLock2")
    ResponseVO<?> rechargeLock2(@RequestBody WalletStatusVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);


    @Operation(summary = "充值上分确认")
    @PostMapping(value = PREFIX + "/twoSuccess")
    ResponseVO<?> twoSuccess(@RequestBody TwoSuccessVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "查询-会员充值人工确认-未审核数量角标")
    @PostMapping(value = PREFIX + "/getNotReviewNum")
    ResponseVO<?> getNotReviewNum();



    // ----------------------------------------------------------------------------------
    /*@Operation(summary = "会员新手活动:完成首笔投注，插入活动记录")
    @PostMapping(value = "/handleNoviceActivityBet")
    public ResponseVO<?> handleNoviceActivityBet(@RequestParam Long userId,
                                                 @RequestParam String userAccount,
                                                 @RequestParam Long registerTime,
                                                 @RequestParam Integer deviceType);*/
}
