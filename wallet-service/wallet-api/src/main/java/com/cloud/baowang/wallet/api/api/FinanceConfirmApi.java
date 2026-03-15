package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteFinanceConfirmApi", value = ApiConstants.NAME)
@Tag(name = "RPC 资金确认 服务")
public interface FinanceConfirmApi {

    String PREFIX = ApiConstants.PREFIX + "/financeConfirm/api/";

    @Operation(summary = "会员提款人工确认-分页查询")
    @PostMapping(value = PREFIX + "manualConfirmMemberWithdrawPage")
    ResponseVO<Page<FinanceManualConfirmVO>> manualConfirmMemberWithdrawPage(@RequestBody FinanceManualConfirmQueryVO requestVO,@RequestParam("adminName") String adminName);

    @Operation(summary = "会员提款人工确认-解/锁单")
    @PostMapping(value = PREFIX + "withdrawManualLock")
    ResponseVO<Boolean> withdrawManualLock(@RequestBody WalletStatusVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "会员提款人工确认-一审通过")
    @PostMapping(value = PREFIX + "withdrawManualOneSuccess")
    ResponseVO<Boolean> withdrawManualOneSuccess(@RequestBody WalletReviewVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "会员提款人工确认-一审拒绝")
    @PostMapping(value = PREFIX + "withdrawManualOneFail")
    ResponseVO<Boolean> withdrawManualOneFail(@RequestBody WalletReviewVO vo, @RequestParam("adminId") String adminId,  @RequestParam("adminName") String adminName);

    @Operation(summary = "会员提款人工确认-分页查询")
    @PostMapping(value = PREFIX + "manualConfirmMemberWithdrawRecPage")
    ResponseVO<Page<FinanceManualConfirmRecordVO>> manualConfirmMemberWithdrawRecPage(@RequestBody FinanceManualConfirmRecordQueryVO requestVO);

    @Operation(summary = "会员提款人工确认-总数")
    @PostMapping(value = PREFIX + "manualConfirmMemberWithdrawRecCount")
    ResponseVO<Long> manualConfirmMemberWithdrawRecCount(@RequestBody FinanceManualConfirmRecordQueryVO vo);

}
