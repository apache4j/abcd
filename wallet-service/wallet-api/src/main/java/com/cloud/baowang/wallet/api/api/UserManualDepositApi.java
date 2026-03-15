package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "userDepositReview", value = ApiConstants.NAME)
@Tag(name = "RPC 会员人工充值审核 服务")
public interface UserManualDepositApi {

    String PREFIX = ApiConstants.PREFIX + "/userDepositReview/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "pageList")
    Page<UserManualDepositPageResVO> pageList(@RequestBody UserManualDepositPageReqVO vo);


    @Operation(summary = "锁定/解锁")
    @PostMapping(value = PREFIX + "lockOrUnLock")
    ResponseVO<Boolean> lockOrUnLock(@RequestBody UserManualDepositLockOrUnLockVO vo);



    @Operation(summary = "待出款成功")
    @PostMapping(value = PREFIX + "paymentReviewSuccess")
    ResponseVO<Boolean> paymentReviewSuccess(@RequestBody UserDepositReviewReqVO vo);


    @Operation(summary = "三审拒绝")
    @PostMapping(value = PREFIX + "paymentReviewFail")
    ResponseVO<Boolean> paymentReviewFail(@RequestBody UserDepositReviewReqVO vo);

    @Operation(summary = "会员人工存款记录审核列表")
    @PostMapping(value = PREFIX + "userManualDepositRecordPage")
    Page<UserManualDepositRecordPageResVO> userManualDepositRecordPage(@RequestBody UserManualDepositRecordPageReqVO vo);

    @Operation(summary = "会员人工存款记录审核列表计数")
    @PostMapping(value = PREFIX + "userManualDepositReviewRecordExportCount")
    ResponseVO<Long> userManualDepositReviewRecordExportCount(@RequestBody UserManualDepositRecordPageReqVO vo);

    @Operation(summary = "会员人工存款审核列表计数")
    @PostMapping(value = PREFIX + "userManualDepositCount")
    ResponseVO<Long> userManualDepositCount(@RequestBody UserManualDepositPageReqVO vo);
}
